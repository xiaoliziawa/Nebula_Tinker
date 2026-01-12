package top.nebula.nebula_tinker.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.common.particle.provider.CrossChopParticleProvider;
import top.nebula.nebula_tinker.common.register.ModParticle;

@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientParticleRegister {
	@SubscribeEvent
	public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ModParticle.CROSS_CHOP.get(), CrossChopParticleProvider::new);
	}
}