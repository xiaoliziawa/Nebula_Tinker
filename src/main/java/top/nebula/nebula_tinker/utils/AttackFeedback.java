package top.nebula.nebula_tinker.utils;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AttackFeedback {
	/**
	 * 生成粒子效果和挥刀暴击音效
	 *
	 * @param player 玩家
	 */
	public static void spawnAbuserCritEffect(Player player) {
		if (!(player.level() instanceof ServerLevel level)) {
			return;
		}

		level.sendParticles(
				ParticleTypes.SWEEP_ATTACK,
				player.getX(),
				player.getY() + player.getBbHeight() * 0.5,
				player.getZ(),
				3,
				0.5,
				0.2,
				0.5,
				0.0
		);

		level.playSound(
				null,
				player.blockPosition(),
				SoundEvents.PLAYER_ATTACK_SWEEP,
				SoundSource.PLAYERS,
				1.0F,
				1.5F
		);
	}
}