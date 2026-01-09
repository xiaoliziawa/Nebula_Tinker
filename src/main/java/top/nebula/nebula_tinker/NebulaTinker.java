package top.nebula.nebula_tinker;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

		ModModifier.MODIFIERS.register(bus);
	}
}