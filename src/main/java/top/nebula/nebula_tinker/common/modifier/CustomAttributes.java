package top.nebula.nebula_tinker.common.modifier;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.nebula.nebula_tinker.NebulaTinker;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CustomAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, NebulaTinker.MODID);

    // 使用 Supplier 延迟获取，避免在注册完成前访问
    private static final Map<String, Supplier<Attribute>> CUSTOM_ATTRIBUTE_SUPPLIERS = new HashMap<>();

    // ========== 战斗属性 ==========
    public static final RegistryObject<Attribute> CRITICAL_CHANCE = registerAttribute("critical_chance",
            () -> new RangedAttribute("attribute.nebula_tinker.critical_chance", 0.0, 0.0, 1.0)
                    .setSyncable(true), "CRITICAL_CHANCE");

    public static final RegistryObject<Attribute> CRITICAL_DAMAGE = registerAttribute("critical_damage",
            () -> new RangedAttribute("attribute.nebula_tinker.critical_damage", 1.5, 1.0, 5.0)
                    .setSyncable(true), "CRITICAL_DAMAGE");

    // ========== 元素属性 ==========
    public static final RegistryObject<Attribute> FIRE_ASPECT = registerAttribute("fire_aspect",
            () -> new RangedAttribute("attribute.nebula_tinker.fire_aspect", 0.0, 0.0, 10.0)
                    .setSyncable(true), "FIRE_ASPECT");

    public static final RegistryObject<Attribute> FROST_ASPECT = registerAttribute("frost_aspect",
            () -> new RangedAttribute("attribute.nebula_tinker.frost_aspect", 0.0, 0.0, 10.0)
                    .setSyncable(true), "FROST_ASPECT");

    public static final RegistryObject<Attribute> LIGHTNING_ASPECT = registerAttribute("lightning_aspect",
            () -> new RangedAttribute("attribute.nebula_tinker.lightning_aspect", 0.0, 0.0, 10.0)
                    .setSyncable(true), "LIGHTNING_ASPECT");

    // ========== 工具属性 ==========
    public static final RegistryObject<Attribute> DURABILITY = registerAttribute("durability",
            () -> new RangedAttribute("attribute.nebula_tinker.durability", 0.0, -500.0, 500.0)
                    .setSyncable(true), "DURABILITY");

    public static final RegistryObject<Attribute> HARVEST_LEVEL = registerAttribute("harvest_level",
            () -> new RangedAttribute("attribute.nebula_tinker.harvest_level", 0.0, -5.0, 10.0)
                    .setSyncable(true), "HARVEST_LEVEL");

    public static final RegistryObject<Attribute> EFFICIENCY = registerAttribute("efficiency",
            () -> new RangedAttribute("attribute.nebula_tinker.efficiency", 0.0, -5.0, 10.0)
                    .setSyncable(true), "EFFICIENCY");

    public static final RegistryObject<Attribute> MINING_SPEED = registerAttribute("mining_speed",
            () -> new RangedAttribute("attribute.nebula_tinker.mining_speed", 0.0, -5.0, 20.0)
                    .setSyncable(true), "MINING_SPEED");

    // ========== 远程属性 ==========
    public static final RegistryObject<Attribute> ARROW_ACCURACY = registerAttribute("arrow_accuracy",
            () -> new RangedAttribute("attribute.nebula_tinker.arrow_accuracy", 0.0, -1.0, 1.0)
                    .setSyncable(true), "ARROW_ACCURACY");

    public static final RegistryObject<Attribute> ARROW_SPEED = registerAttribute("arrow_speed",
            () -> new RangedAttribute("attribute.nebula_tinker.arrow_speed", 0.0, -2.0, 5.0)
                    .setSyncable(true), "ARROW_SPEED");

    // ========== 防御属性 ==========
    public static final RegistryObject<Attribute> FEATHER_FALLING = registerAttribute("feather_falling",
            () -> new RangedAttribute("attribute.nebula_tinker.feather_falling", 0.0, 0.0, 10.0)
                    .setSyncable(true), "FEATHER_FALLING");

    public static final RegistryObject<Attribute> PROTECTION = registerAttribute("protection",
            () -> new RangedAttribute("attribute.nebula_tinker.protection", 0.0, 0.0, 5.0)
                    .setSyncable(true), "PROTECTION");

    // ========== 特殊属性 ==========
    public static final RegistryObject<Attribute> DRAW_SPEED = registerAttribute("draw_speed",
            () -> new RangedAttribute("attribute.nebula_tinker.draw_speed", 0.0, -0.5, 1.0)
                    .setSyncable(true), "DRAW_SPEED");

    public static final RegistryObject<Attribute> PROJECTILE_DAMAGE = registerAttribute("projectile_damage",
            () -> new RangedAttribute("attribute.nebula_tinker.projectile_damage", 0.0, -1.0, 5.0)
                    .setSyncable(true), "PROJECTILE_DAMAGE");

    // 属性映射表 - 延迟初始化
    private static final Map<String, Attribute> CUSTOM_ATTRIBUTE_MAP = new HashMap<>();
    private static boolean mapInitialized = false;

    // 辅助注册方法
    private static RegistryObject<Attribute> registerAttribute(String name, Supplier<Attribute> supplier, String enumName) {
        RegistryObject<Attribute> regObj = ATTRIBUTES.register(name, supplier);
        CUSTOM_ATTRIBUTE_SUPPLIERS.put(enumName, () -> {
            Attribute attr = regObj.get();
            if (attr == null) {
                NebulaTinker.LOGGER.warn("Attribute {} not yet registered, returning placeholder", enumName);
                // 返回一个占位符属性，避免空指针
                return new RangedAttribute("attribute.nebula_tinker.placeholder_" + name, 0.0, 0.0, 1.0);
            }
            return attr;
        });
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

    private static void addAttributeSafely(RegistryObject<Attribute> regObj, String enumName) {
        try {
            if (regObj.isPresent()) {
                Attribute attr = regObj.get();
                if (attr != null) {
                    CUSTOM_ATTRIBUTE_MAP.put(enumName, attr);
                    NebulaTinker.LOGGER.debug("Successfully registered attribute: {}", enumName);
                } else {
                    NebulaTinker.LOGGER.warn("Attribute {} is present but get() returned null", enumName);
                }
            } else {
                NebulaTinker.LOGGER.warn("Attribute {} is not present in registry", enumName);
            }
        } catch (Exception e) {
            NebulaTinker.LOGGER.error("Error registering attribute {}: {}", enumName, e.getMessage(), e);
        }
    }
}