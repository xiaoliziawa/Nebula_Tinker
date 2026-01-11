package top.nebula.nebula_tinker.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.common.modifier.AttributeApplicator;

@Mod.EventBusSubscriber(modid = NebulaTinker.MODID)
public class AttributeEventHandler {
	@SubscribeEvent
	public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		Player player = event.getEntity();
		AttributeApplicator.removeAllAttributes(player);
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		// 玩家重生时移除所有属性，新的装备会重新应用属性
		Player player = event.getEntity();
		AttributeApplicator.removeAllAttributes(player);
	}

	@SubscribeEvent
	public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		// 维度切换时，属性会被自动清除，这里确保清理缓存
		Player player = event.getEntity();
		AttributeApplicator.removeAllAttributes(player);
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		// 玩家死亡后克隆时，移除原版玩家的属性
		Player original = event.getOriginal();
		AttributeApplicator.removeAllAttributes(original);
		// 同时需要移除新玩家的属性（因为它们会被重新应用）
		Player newPlayer = event.getEntity();
		AttributeApplicator.removeAllAttributes(newPlayer);
	}

	/**
	 * 监听服务器tick，用于调试和监控属性系统
	 */
	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		// 可以添加调试逻辑，但非必需
		if (event.phase == TickEvent.Phase.END) {
			if (event.getServer().getTickCount() % 400 == 0) {
				AttributeApplicator.cleanupExpiredCache();
			}
		}
	}
}