package top.nebula.nebula_tinker.common.modifier;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.utils.SimpleTConUtils;

@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SwiftBlade extends Modifier {
    // 基础暴击伤害倍率
    private static final float BASE_CRIT_MULTIPLIER = 1.5F;
    // 每级速度效果额外增加的暴击伤害
    private static final float CRIT_BONUS_PER_SPEED_LEVEL = 0.1F;

    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        Player player = event.getEntity();

        // 攻击冷却检查，防止粒子小姑以及音效连续触发
        if (player.getAttackStrengthScale(0.5F) < 0.9F) {
            return;
        }

        // 检查玩家主手武器是否有此修饰符
        boolean hasModifier = SimpleTConUtils.hasModifier(
                player.getItemInHand(InteractionHand.MAIN_HAND),
                NebulaTinker.loadResource("swift_blade").toString()
        );

        if (!hasModifier) {
            return;
        }

        // 检查玩家是否有速度效果
        if (!player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
            return;
        }

        // 获取速度等级 (0 = Speed I, 1 = Speed II 以此类推)
        // 不喜欢数值的自己改一下
        int speedLevel = player.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1;

        // 设置暴击
        event.setResult(Event.Result.ALLOW);

        // 计算暴击伤害倍率：基础倍率 + 速度等级加成
        float critMultiplier = BASE_CRIT_MULTIPLIER + (speedLevel * CRIT_BONUS_PER_SPEED_LEVEL);
        event.setDamageModifier(critMultiplier);

        // 生成反馈
        spawnSwiftCritEffect(player);
    }

    /**
     * 生成粒子效果和挥刀暴击音效
     *
     * @param player 玩家
     */
    private static void spawnSwiftCritEffect(Player player) {
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
