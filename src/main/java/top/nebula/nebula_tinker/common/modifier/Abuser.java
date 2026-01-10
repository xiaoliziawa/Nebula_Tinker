package top.nebula.nebula_tinker.common.modifier;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.shared.TinkerEffects;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.utils.SimpleTConUtils;

@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Abuser extends Modifier {
	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		LivingEntity entity = event.getEntity();
		DamageSource source = event.getSource();

		if (!(source.getEntity() instanceof Player player)) {
			return;
		}

		boolean hasModifier = SimpleTConUtils.hasModifier(
				player.getItemInHand(InteractionHand.MAIN_HAND),
				NebulaTinker.loadResource("abuser").toString()
		);

		if (hasEffect(entity)) {
			event.setAmount(event.getAmount() * 1.5f);
		}
	}

	private static final boolean hasEffect(LivingEntity entity) {
		if (entity.hasEffect(MobEffects.POISON)
				|| entity.hasEffect(MobEffects.WITHER)
				|| entity.hasEffect(TinkerEffects.bleeding.get())) {
			return true;
		}
		return false;
	}
}