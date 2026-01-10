package top.nebula.nebula_tinker.common.modifier;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import top.nebula.nebula_tinker.NebulaTinker;

@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RapidShot extends Modifier {

	@SubscribeEvent
	public static void onProjectileLaunch(ProjectileImpactEvent event) {
		Entity entity = event.getEntity();
		Projectile projectile = event.getProjectile();
		if (!(entity instanceof LivingEntity || entity instanceof Player player)) {
			return;
		}
	}
}
