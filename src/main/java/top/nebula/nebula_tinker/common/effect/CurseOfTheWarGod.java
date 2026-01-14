package top.nebula.nebula_tinker.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;

import java.util.Random;

public class CurseOfTheWarGod extends MobEffect {
	private static final String LAST_HEALTH_KEY = "nebula_tinker:last_health";
	private static final Random RANDOM = new Random();
	// 触发概率
	private static final double PROBABILITY_OF_INJURY = 0.5;
	// 受到的伤害百分比
	private static final double PERCENTAGE_OF_DAMAGE = 0.75;

	public CurseOfTheWarGod() {
		super(MobEffectCategory.HARMFUL, 0x8B0000);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		// 每 tick 检查
		return true;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		if (entity.level().isClientSide()) {
			return;
		}

		CompoundTag data = entity.getPersistentData();

		float currentHealth = entity.getHealth();
		float lastHealth = data.contains(LAST_HEALTH_KEY)
				? data.getFloat(LAST_HEALTH_KEY)
				: currentHealth;

		// 检测刚刚受伤
		if (currentHealth < lastHealth) {
			// 受伤概率PROBABILITY_OF_INJURY
			if (RANDOM.nextFloat() < PROBABILITY_OF_INJURY) {
				double damage = currentHealth * PERCENTAGE_OF_DAMAGE;

				// 防止直接秒杀
				if (damage >= currentHealth) {
					damage = currentHealth - 1.0f;
				}

				entity.hurt(entity.damageSources().fellOutOfWorld(), (float) damage);
			}
		}

		// 记录本 tick 的血量
		data.putFloat(LAST_HEALTH_KEY, currentHealth);
	}
}