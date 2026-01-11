package top.nebula.nebula_tinker;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.nebula.nebula_tinker.common.command.ModifierDebugCommand;
import top.nebula.nebula_tinker.common.register.ModAttributes;
import top.nebula.nebula_tinker.common.register.ModItem;
import top.nebula.nebula_tinker.common.register.ModModifier;

@Mod(NebulaTinker.MODID)
public class NebulaTinker {
	public static final String MODID = "nebula_tinker";
	public static final String NAME = "Nebula Tinker";
	public static final Logger LOGGER = LogManager.getLogger(NAME);

	/**
	 * 加载ResourceLocation资源
	 *
	 * @param path 命名空间下的资源路径
	 *             <p>
	 *             Resource path under namespace
	 * @return
	 */
	public static ResourceLocation loadResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public NebulaTinker(FMLJavaModLoadingContext context) {
		IEventBus bus = context.getModEventBus();

		// 注册修饰器
		ModModifier.MODIFIERS.register(bus);
		// 注册物品
		ModItem.ITEMS.register(bus);
		// 注册自定义属性
		ModAttributes.ATTRIBUTES.register(bus);

		LOGGER.info("Nebula Tinker mod initialized!");
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class ForgeEvents {
		@SubscribeEvent
		public static void onRegisterCommands(RegisterCommandsEvent event) {
			ModifierDebugCommand.register(event.getDispatcher());
			LOGGER.info("Registered Nebula Tinker debug commands");
		}
	}
}