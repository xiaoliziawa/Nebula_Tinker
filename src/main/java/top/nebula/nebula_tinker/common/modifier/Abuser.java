package top.nebula.nebula_tinker.common.modifier;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.shared.TinkerEffects;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.utils.AttackFeedback;
import top.nebula.nebula_tinker.utils.SimpleTConUtils;

@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Abuser extends Modifier {
	/**
	 * 基础暴击倍率(原本 1.5x 伤害)
	 */
	private static final float CRIT_MULTIPLIER = 1.5F;

	@SubscribeEvent
	public static void onCriticalHit(CriticalHitEvent event) {
		Player player = event.getEntity();
		Entity target = event.getTarget();

		// 攻击冷却检查(防止连触发)
		if (player.getAttackStrengthScale(0.5F) < 0.9F) {
			return;
		}

		boolean hasModifier = SimpleTConUtils.hasModifier(
				player.getItemInHand(InteractionHand.MAIN_HAND),
				NebulaTinker.loadResource("abuser").toString()
		);

		if (!hasModifier) {
			return;
		}

		if (!(target instanceof LivingEntity entity)) {
			return;
		}

		if (!hasEffect(entity)) {
			return;
		}

		// 强制暴击
		event.setResult(Event.Result.ALLOW);
		event.setDamageModifier(CRIT_MULTIPLIER);

		AttackFeedback.spawnAbuserCritEffect(player);
	}

	/**
	 * 判断目标是否有可被剥削的异常状态
	 */
	private static boolean hasEffect(LivingEntity entity) {
		return entity.hasEffect(MobEffects.POISON)
				|| entity.hasEffect(MobEffects.WITHER)
				|| entity.hasEffect(TinkerEffects.bleeding.get());
	}
}