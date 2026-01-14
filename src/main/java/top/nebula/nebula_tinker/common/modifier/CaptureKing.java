package top.nebula.nebula_tinker.common.modifier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.utils.AttackFeedback;
import top.nebula.nebula_tinker.utils.SimpleTConUtils;

@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CaptureKing extends Modifier {
	// 基础暴击倍率(原本 1.5x 伤害)
	private static final float CRIT_MULTIPLIER = 1.5F;
	// 暴击率
	private static final double BASE_CRIT_CHANCE = 0.15;
	// 每级提升数
	private static final double CRIT_PER_LEVEL = 0.05d;

	@SubscribeEvent
	public static void onCriticalHit(CriticalHitEvent event) {
		Player player = event.getEntity();
		Entity target = event.getTarget();

		if (!(target instanceof LivingEntity entity)) {
			return;
		}

		// 攻击冷却检查(防止连触发)
		if (player.getAttackStrengthScale(0.5F) < 0.9F) {
			return;
		}

		int level = SimpleTConUtils.getModifierLevel(
				player.getItemInHand(InteractionHand.MAIN_HAND),
				NebulaTinker.loadResource("capture_king").toString()
		);

		boolean hasModfier = SimpleTConUtils.hasModifier(
				player.getItemInHand(InteractionHand.MAIN_HAND),
				NebulaTinker.loadResource("capture_king").toString()
		);

		boolean isBoss = entity.getType().is(Tags.EntityTypes.BOSSES);

		if (level <= 0) {
			return;
		}

		// 计算暴击率: 15% + 每级 5%
		double critChance = BASE_CRIT_CHANCE + level * CRIT_PER_LEVEL;
		critChance = Math.min(critChance, 1.0D);

		if (player.getRandom().nextDouble() > critChance) {
			return;
		}

		// 只对 BOSS 生效
		if (!isBoss && hasModfier) {
			return;
		}

		MutableComponent tranKey = Component.translatable("message.nebula_tinker.modifier.capture_king")
				.withStyle(ChatFormatting.RED)
				.withStyle(ChatFormatting.BOLD);

		player.displayClientMessage(tranKey, true);
		AttackFeedback.spawnAbuserCritEffect(player);
		event.setResult(Event.Result.ALLOW);
		event.setDamageModifier(CRIT_MULTIPLIER);
	}
}