package top.nebula.nebula_tinker.common.modifier;

import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class CausalTruncation extends Modifier implements MeleeDamageModifierHook {
	/**
	 * 如果通过实现钩子(Hook)来定义modifier的逻辑需要重写此方法来让逻辑生效似乎
	 * @param builder 构造器
	 */
	@Override
	protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
		builder.addHook(this, ModifierHooks.MELEE_DAMAGE);
	}

	@Override
	public float getMeleeDamage(@NotNull IToolStackView view, @NotNull ModifierEntry entry, @NotNull ToolAttackContext context, float baseDamage, float finalDamage) {
		if (context.getLivingTarget() == null || !context.getLivingTarget().isAlive()) {
			return finalDamage;
		}

		// 实际等级(指令 / debug 可超)
		int rawLevel = entry.getLevel();
		// 参与战斗计算的等级
		int cappedLevel = Math.min(rawLevel, 5);

		// 触发概率: 允许 debug 堆高, 但最多 75%
		float chance = Math.min(0.25f + rawLevel * 0.01f, 0.75f);
		if (Math.random() > chance) {
			return finalDamage;
		}

		float currentHealth = context.getLivingTarget().getHealth();
		if (currentHealth <= 0) {
			return finalDamage;
		}

		// 斩杀强度: 只吃封顶后的等级(目前是固定 33%)
		float damage = currentHealth * 0.33f;

		if (damage >= currentHealth) {
			damage = currentHealth - 1.0f;
		}

		return damage;
	}
}