package top.nebula.nebula_tinker.common.modifier;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.common.register.ModModifier;
import top.nebula.nebula_tinker.utils.AttackFeedback;
import top.nebula.nebula_tinker.utils.SimpleTConUtils;

@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DeathEcho extends Modifier {
	// 生命阈值
	private static final double LIFE_THRESHOLD = 0.35;
	// 基础暴击倍率(原本 1.5x 伤害)
	private static final float CRIT_MULTIPLIER = 1.5F;
	// 暴击率
	private static final double TRIGGER_PROBABILITY = 0.15;

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

		boolean hasModifier = SimpleTConUtils.hasModifier(
				player.getItemInHand(InteractionHand.MAIN_HAND),
				NebulaTinker.loadResource("death_echo").toString()
		);

		if (player.getHealth() <= player.getMaxHealth() * LIFE_THRESHOLD && hasModifier) {
			if (Math.random() < TRIGGER_PROBABILITY) {
				// 暴击
				AttackFeedback.spawnAbuserCritEffect(player);
				event.setResult(Event.Result.ALLOW);
				event.setDamageModifier(CRIT_MULTIPLIER);
			}
		}
	}
}