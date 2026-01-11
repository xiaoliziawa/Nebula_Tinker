package top.nebula.nebula_tinker.common.register;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.nebula.nebula_tinker.NebulaTinker;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModAttributes {
	public static final DeferredRegister<Attribute> ATTRIBUTES;

	// 使用 Supplier 延迟获取，避免在注册完成前访问
	private static final Map<String, Supplier<Attribute>> CUSTOM_ATTRIBUTE_SUPPLIERS = new HashMap<>();

	// 战斗属性
	public static final Supplier<Attribute> CRITICAL_CHANCE;
	public static final Supplier<Attribute> CRITICAL_DAMAGE;

	// 元素属性
	public static final Supplier<Attribute> FIRE_ASPECT;
	public static final Supplier<Attribute> FROST_ASPECT;
	public static final Supplier<Attribute> LIGHTNING_ASPECT;

	// 工具属性
	public static final Supplier<Attribute> DURABILITY;
	public static final Supplier<Attribute> HARVEST_LEVEL;
	public static final Supplier<Attribute> EFFICIENCY;
	public static final Supplier<Attribute> MINING_SPEED;

	// 远程属性
	public static final Supplier<Attribute> ARROW_ACCURACY;
	public static final Supplier<Attribute> ARROW_SPEED;

	// 防御属性
	public static final Supplier<Attribute> FEATHER_FALLING;
	public static final Supplier<Attribute> PROTECTION;

	// 特殊属性
	public static final Supplier<Attribute> DRAW_SPEED;
	public static final Supplier<Attribute> PROJECTILE_DAMAGE;

	static {
		ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, NebulaTinker.MODID);

		// 战斗
		CRITICAL_CHANCE = registerAttribute("critical_chance", () -> {
			return new RangedAttribute(
					"attribute.nebula_tinker.critical_chance",
					0.0,
					0.0,
					1.0
			).setSyncable(true);
		}, "CRITICAL_CHANCE");

		CRITICAL_DAMAGE = registerAttribute("critical_damage", () -> {
			return new RangedAttribute(
					"attribute.nebula_tinker.critical_damage",
					1.5,
					1.0,
					5.0
			).setSyncable(true);
		}, "CRITICAL_DAMAGE");

		// 元素
		FIRE_ASPECT = registerAttribute("fire_aspect", () -> {
			return new RangedAttribute(
					"attribute.nebula_tinker.fire_aspect",
					0.0,
					0.0,
					10.0
			).setSyncable(true);
		}, "FIRE_ASPECT");

		FROST_ASPECT = registerAttribute("frost_aspect", () -> {
			return new RangedAttribute(
					"attribute.nebula_tinker.frost_aspect",
					0.0,
					0.0,
					10.0
			).setSyncable(true);
		}, "FROST_ASPECT");

		LIGHTNING_ASPECT = registerAttribute("lightning_aspect", () -> {
			return new RangedAttribute(
					"attribute.nebula_tinker.lightning_aspect",
					0.0,
					0.0,
					10.0
			).setSyncable(true);
		}, "LIGHTNING_ASPECT");

		// 工具
		DURABILITY = registerAttribute("durability", () -> {
			return new RangedAttribute(
					"attribute.nebula_tinker.durability",
					0.0,
					-500.0,
					500.0
			).setSyncable(true);
		}, "DURABILITY");

		HARVEST_LEVEL = registerAttribute("harvest_level", () -> {
			return new RangedAttribute(
					"attribute.nebula_tinker.harvest_level",
					0.0,
					-5.0,
					10.0
			).setSyncable(true);
		}, "HARVEST_LEVEL");

		EFFICIENCY = registerAttribute("efficiency", () -> {
			return new RangedAttribute(
					"attribute.nebula_tinker.efficiency",
					0.0,
					-5.0,
					10.0
			).setSyncable(true);
		}, "EFFICIENCY");

		MINING_SPEED = registerAttribute("mining_speed", () -> {
			return new RangedAttribute(
					"attribute.nebula_tinker.mining_speed",
					0.0,
					-5.0,
					20.0
			).setSyncable(true);
		}, "MINING_SPEED");

		// 远程
		ARROW_ACCURACY = registerAttribute("arrow_accuracy", () -> {
			return new RangedAttribute(
					"attribute.nebula_tinker.arrow_accuracy",
					0.0,
					-1.0,
					1.0
			).setSyncable(true);
		}, "ARROW_ACCURACY");

		ARROW_SPEED = registerAttribute("arrow_speed", () -> {
			return new RangedAttribute(
					"attribute.nebula_tinker.arrow_speed",
					0.0,
					-2.0,
					5.0
			).setSyncable(true);
		}, "ARROW_SPEED");

		// 防御
		FEATHER_FALLING = registerAttribute("feather_falling", () -> {
			return new RangedAttribute(
					"attribute.nebula_tinker.feather_falling",
					0.0,
					0.0,
					10.0
			).setSyncable(true);
		}, "FEATHER_FALLING");

		PROTECTION = registerAttribute("protection", () -> {
			return new RangedAttribute(
					"attribute.nebula_tinker.protection",
					0.0,
					0.0,
					5.0
			).setSyncable(true);
		}, "PROTECTION");

		// 特殊
		DRAW_SPEED = registerAttribute("draw_speed", () -> {
			return new RangedAttribute(
					"attribute.nebula_tinker.draw_speed",
					0.0,
					-0.5,
					1.0
			).setSyncable(true);
		}, "DRAW_SPEED");

		PROJECTILE_DAMAGE = registerAttribute("projectile_damage", () -> {
			return new RangedAttribute(
					"attribute.nebula_tinker.projectile_damage",
					0.0,
					-1.0,
					5.0
			).setSyncable(true);
		}, "PROJECTILE_DAMAGE");
	}

	// 属性映射表, 延迟初始化
	private static final Map<String, Attribute> CUSTOM_ATTRIBUTE_MAP = new HashMap<>();
	private static boolean mapInitialized = false;

	// 辅助注册方法
	private static Supplier<Attribute> registerAttribute(String name, Supplier<Attribute> supplier, String enumName) {
		Supplier<Attribute> regObj = ATTRIBUTES.register(name, supplier);
		CUSTOM_ATTRIBUTE_SUPPLIERS.put(enumName, regObj);
		return regObj;
	}

	public static Attribute getCustomAttribute(String enumName) {
		// 如果映射表未初始化，尝试从 Supplier 获取
		if (!mapInitialized) {
			Supplier<Attribute> supplier = CUSTOM_ATTRIBUTE_SUPPLIERS.get(enumName);
			if (supplier != null) {
				return supplier.get();
			}
			return null;
		}

		Attribute attr = CUSTOM_ATTRIBUTE_MAP.get(enumName);
		if (attr == null) {
			// 后备机制：从 Supplier 获取
			Supplier<Attribute> supplier = CUSTOM_ATTRIBUTE_SUPPLIERS.get(enumName);
			if (supplier != null) {
				attr = supplier.get();
				CUSTOM_ATTRIBUTE_MAP.put(enumName, attr);
			}
		}
		return attr;
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			// 延迟初始化映射表，确保注册完成
			try {
				// 等待一小段时间确保注册完成
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			// 安全地填充属性映射表
			fillAttributeMapSafely();

			mapInitialized = true;
			NebulaTinker.LOGGER.info("Custom attributes initialized: {}", CUSTOM_ATTRIBUTE_MAP.size());
		});
	}

	private static void fillAttributeMapSafely() {
		// 使用安全的获取方式
		addAttributeSafely(CRITICAL_CHANCE, "CRITICAL_CHANCE");
		addAttributeSafely(CRITICAL_DAMAGE, "CRITICAL_DAMAGE");
		addAttributeSafely(FIRE_ASPECT, "FIRE_ASPECT");
		addAttributeSafely(FROST_ASPECT, "FROST_ASPECT");
		addAttributeSafely(LIGHTNING_ASPECT, "LIGHTNING_ASPECT");
		addAttributeSafely(DURABILITY, "DURABILITY");
		addAttributeSafely(HARVEST_LEVEL, "HARVEST_LEVEL");
		addAttributeSafely(EFFICIENCY, "EFFICIENCY");
		addAttributeSafely(ARROW_ACCURACY, "ARROW_ACCURACY");
		addAttributeSafely(MINING_SPEED, "MINING_SPEED");
		addAttributeSafely(ARROW_SPEED, "ARROW_SPEED");
		addAttributeSafely(FEATHER_FALLING, "FEATHER_FALLING");
		addAttributeSafely(PROTECTION, "PROTECTION");
		addAttributeSafely(DRAW_SPEED, "DRAW_SPEED");
		addAttributeSafely(PROJECTILE_DAMAGE, "PROJECTILE_DAMAGE");
	}

	private static void addAttributeSafely(Supplier<Attribute> supplier, String enumName) {
		try {
			Attribute attr = supplier.get();
			if (attr == null) {
				NebulaTinker.LOGGER.warn("Attribute {} supplier returned null", enumName);
				return;
			}

			CUSTOM_ATTRIBUTE_MAP.put(enumName, attr);
			NebulaTinker.LOGGER.debug("Successfully registered attribute: {}", enumName);
		} catch (Exception e) {
			NebulaTinker.LOGGER.error("Error registering attribute {}: {}", enumName, e.getMessage(), e);
		}
	}
}