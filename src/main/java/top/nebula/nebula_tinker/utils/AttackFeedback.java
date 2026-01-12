package top.nebula.nebula_tinker.utils;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

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
				ParticleTypes.CRIT,
				player.getX(),
				player.getY() + player.getBbHeight() * 0.5,
				player.getZ(),
				6,
				0.4,
				0.2,
				0.4,
				0.0
		);

		level.playSound(
				null,
				player.blockPosition(),
				SoundEvents.PLAYER_ATTACK_CRIT,
				SoundSource.PLAYERS,
				1.0F,
				1.2F
		);
	}
}