package top.nebula.nebula_tinker.common.modifier;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;

import java.util.*;

/**
 * 神魔化效果属性类型枚举
 * 定义所有可用的正面和负面属性
 */
public enum AttributeType {
    // ========== 正面属性 ==========

    // 通用战斗属性
    ATTACK_DAMAGE(Attributes.ATTACK_DAMAGE, 0.5, "attribute.modifier.nebula_tinker.attack_damage",
            EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND), AttributeCategory.COMBAT),
    ATTACK_SPEED(Attributes.ATTACK_SPEED, 0.1, "attribute.modifier.nebula_tinker.attack_speed",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.COMBAT),
    CRITICAL_CHANCE(null, 0.05, "attribute.modifier.nebula_tinker.critical_chance",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.COMBAT),

    // 元素伤害属性
    FIRE_ASPECT(null, 2.0, "attribute.modifier.nebula_tinker.fire_aspect",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.ELEMENTAL),
    FROST_ASPECT(null, 2.0, "attribute.modifier.nebula_tinker.frost_aspect",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.ELEMENTAL),
    LIGHTNING_ASPECT(null, 2.0, "attribute.modifier.nebula_tinker.lightning_aspect",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.ELEMENTAL),

    // 远程属性
    DRAW_SPEED(ForgeMod.ENTITY_REACH.get(), 0.15, "attribute.modifier.nebula_tinker.draw_speed",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.RANGED),
    ARROW_SPEED(Attributes.ATTACK_DAMAGE, 0.2, "attribute.modifier.nebula_tinker.arrow_speed",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.RANGED),
    ARROW_ACCURACY(null, 0.1, "attribute.modifier.nebula_tinker.arrow_accuracy",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.RANGED),
    PROJECTILE_DAMAGE(Attributes.ATTACK_DAMAGE, 0.3, "attribute.modifier.nebula_tinker.projectile_damage",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.RANGED),

    // 工具属性
    MINING_SPEED(Attributes.ATTACK_SPEED, 0.25, "attribute.modifier.nebula_tinker.mining_speed",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.TOOL),
    DURABILITY(null, 100.0, "attribute.modifier.nebula_tinker.durability",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.TOOL),
    HARVEST_LEVEL(null, 1.0, "attribute.modifier.nebula_tinker.harvest_level",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.TOOL),
    EFFICIENCY(null, 0.2, "attribute.modifier.nebula_tinker.efficiency",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.TOOL),

    // 防御属性
    ARMOR(Attributes.ARMOR, 1.0, "attribute.modifier.nebula_tinker.armor",
            EnumSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET), AttributeCategory.DEFENSE),
    MAX_HEALTH(Attributes.MAX_HEALTH, 2.0, "attribute.modifier.nebula_tinker.max_health",
            EnumSet.of(EquipmentSlot.CHEST), AttributeCategory.DEFENSE),
    ARMOR_TOUGHNESS(Attributes.ARMOR_TOUGHNESS, 0.5, "attribute.modifier.nebula_tinker.armor_toughness",
            EnumSet.of(EquipmentSlot.CHEST), AttributeCategory.DEFENSE),
    MOVEMENT_SPEED(Attributes.MOVEMENT_SPEED, 0.05, "attribute.modifier.nebula_tinker.movement_speed",
            EnumSet.of(EquipmentSlot.FEET), AttributeCategory.UTILITY),
    KNOCKBACK_RESISTANCE(Attributes.KNOCKBACK_RESISTANCE, 0.1, "attribute.modifier.nebula_tinker.knockback_resistance",
            EnumSet.of(EquipmentSlot.CHEST), AttributeCategory.DEFENSE),

    // ========== 负面属性 ==========

    // 生存负面属性（用于武器）
    HEALTH_REDUCTION(Attributes.MAX_HEALTH, -1.0, "attribute.modifier.nebula_tinker.health_reduction",
            EnumSet.allOf(EquipmentSlot.class), AttributeCategory.NEGATIVE),
    ARMOR_REDUCTION(Attributes.ARMOR, -0.5, "attribute.modifier.nebula_tinker.armor_reduction",
            EnumSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET), AttributeCategory.NEGATIVE),
    MOVEMENT_SLOW(Attributes.MOVEMENT_SPEED, -0.03, "attribute.modifier.nebula_tinker.movement_slow",
            EnumSet.of(EquipmentSlot.FEET), AttributeCategory.NEGATIVE),

    // 战斗负面属性（用于盔甲）
    ATTACK_DAMAGE_REDUCTION(Attributes.ATTACK_DAMAGE, -0.3, "attribute.modifier.nebula_tinker.attack_damage_reduction",
            EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND), AttributeCategory.NEGATIVE),
    ATTACK_SPEED_REDUCTION(Attributes.ATTACK_SPEED, -0.05, "attribute.modifier.nebula_tinker.attack_speed_reduction",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.NEGATIVE),
    CRITICAL_REDUCTION(null, -0.03, "attribute.modifier.nebula_tinker.critical_reduction",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.NEGATIVE),

    // 效率负面属性（用于工具）
    MINING_SPEED_REDUCTION(Attributes.ATTACK_SPEED, -0.1, "attribute.modifier.nebula_tinker.mining_speed_reduction",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.NEGATIVE),
    DURABILITY_REDUCTION(null, -50.0, "attribute.modifier.nebula_tinker.durability_reduction",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.NEGATIVE),
    HARVEST_LEVEL_REDUCTION(null, -0.5, "attribute.modifier.nebula_tinker.harvest_level_reduction",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.NEGATIVE);

    /**
     * 属性分类
     */
    public enum AttributeCategory {
        COMBAT,          // 战斗属性
        ELEMENTAL,       // 元素属性
        RANGED,          // 远程属性
        TOOL,            // 工具属性
        DEFENSE,         // 防御属性
        UTILITY,         // 通用属性
        NEGATIVE         // 负面属性
    }

    private final Attribute attribute;
    private final double baseValue;
    private final String translationKey;
    private final Set<EquipmentSlot> applicableSlots;
    private final AttributeCategory category;

    AttributeType(Attribute attribute, double baseValue, String translationKey,
                  Set<EquipmentSlot> applicableSlots, AttributeCategory category) {
        this.attribute = attribute;
        this.baseValue = baseValue;
        this.translationKey = translationKey;
        this.applicableSlots = applicableSlots;
        this.category = category;
    }

    // Getters
    public Attribute getAttribute() { return attribute; }
    public double getBaseValue() { return baseValue; }
    public String getTranslationKey() { return translationKey; }
    public Set<EquipmentSlot> getApplicableSlots() { return applicableSlots; }
    public AttributeCategory getCategory() { return category; }
    public boolean isNegative() { return category == AttributeCategory.NEGATIVE; }

    /**
     * 获取适用于指定装备槽位的属性类型
     */
    public static List<AttributeType> getApplicableAttributes(EquipmentSlot slot, boolean includeNegative) {
        List<AttributeType> result = new ArrayList<>();
        for (AttributeType type : values()) {
            if (type.applicableSlots.contains(slot) &&
                    (includeNegative || !type.isNegative())) {
                result.add(type);
            }
        }
        return result;
    }

    /**
     * 根据工具类型获取适用的属性池
     */
    public static List<AttributeType> getAttributePoolForTool(Object tool, boolean includeNegative) {
        List<AttributeType> pool = new ArrayList<>();

        // 检查是否为近战武器
        if (isMeleeWeapon(tool)) {
            pool.addAll(Arrays.asList(
                    ATTACK_DAMAGE, ATTACK_SPEED, CRITICAL_CHANCE,
                    FIRE_ASPECT, FROST_ASPECT, LIGHTNING_ASPECT
            ));
        }
        // 检查是否为远程武器
        else if (isRangedWeapon(tool)) {
            pool.addAll(Arrays.asList(
                    DRAW_SPEED, ARROW_SPEED, ARROW_ACCURACY, PROJECTILE_DAMAGE
            ));
        }
        // 检查是否为工具
        else if (isTool(tool)) {
            pool.addAll(Arrays.asList(
                    MINING_SPEED, DURABILITY, HARVEST_LEVEL, EFFICIENCY
            ));
        }
        // 检查是否为盔甲
        else if (isArmor(tool)) {
            pool.addAll(Arrays.asList(
                    ARMOR, MAX_HEALTH, ARMOR_TOUGHNESS,
                    MOVEMENT_SPEED, KNOCKBACK_RESISTANCE
            ));
        }

        if (includeNegative) {
            // 添加所有负面属性
            pool.addAll(Arrays.asList(
                    HEALTH_REDUCTION, ARMOR_REDUCTION, MOVEMENT_SLOW,
                    ATTACK_DAMAGE_REDUCTION, ATTACK_SPEED_REDUCTION, CRITICAL_REDUCTION,
                    MINING_SPEED_REDUCTION, DURABILITY_REDUCTION, HARVEST_LEVEL_REDUCTION
            ));
        }

        return pool;
    }

    /**
     * 判断是否为近战武器
     */
    private static boolean isMeleeWeapon(Object tool) {
        if (tool == null) return false;
        String className = tool.getClass().getName().toLowerCase();
        return className.contains("sword") ||
                className.contains("axe") ||
                className.contains("mace") ||
                className.contains("broadsword") ||
                className.contains("longsword") ||
                className.contains("cleaver") ||
                className.contains("scythe");
    }

    /**
     * 判断是否为远程武器
     */
    private static boolean isRangedWeapon(Object tool) {
        if (tool == null) return false;
        String className = tool.getClass().getName().toLowerCase();
        return className.contains("bow") ||
                className.contains("crossbow");
    }

    /**
     * 判断是否为工具
     */
    private static boolean isTool(Object tool) {
        if (tool == null) return false;
        String className = tool.getClass().getName().toLowerCase();
        return className.contains("pickaxe") ||
                className.contains("shovel") ||
                className.contains("mattock") ||
                className.contains("excavator") ||
                className.contains("hammer");
    }

    /**
     * 判断是否为盔甲
     */
    private static boolean isArmor(Object tool) {
        if (tool == null) return false;
        String className = tool.getClass().getName().toLowerCase();
        return className.contains("armor") ||
                className.contains("helmet") ||
                className.contains("chestplate") ||
                className.contains("leggings") ||
                className.contains("boots") ||
                className.contains("slimesuit");
    }

    /**
     * 获取相反的属性池（用于魔化的负面效果）
     */
    public static List<AttributeType> getOppositeAttributePool(Object tool) {
        List<AttributeType> pool = new ArrayList<>();

        if (isMeleeWeapon(tool) || isRangedWeapon(tool)) {
            // 武器：负面生存属性
            pool.addAll(Arrays.asList(
                    HEALTH_REDUCTION, ARMOR_REDUCTION, MOVEMENT_SLOW
            ));
        } else if (isTool(tool)) {
            // 工具：负面效率属性
            pool.addAll(Arrays.asList(
                    MINING_SPEED_REDUCTION, DURABILITY_REDUCTION, HARVEST_LEVEL_REDUCTION
            ));
        } else if (isArmor(tool)) {
            // 盔甲：负面战斗属性
            pool.addAll(Arrays.asList(
                    ATTACK_DAMAGE_REDUCTION, ATTACK_SPEED_REDUCTION, CRITICAL_REDUCTION
            ));
        }

        return pool;
    }
}