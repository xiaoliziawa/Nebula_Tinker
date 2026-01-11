package top.nebula.nebula_tinker.utils;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.common.modifier.CustomAttributes;

import java.util.*;

/**
 * 神魔化效果属性类型枚举
 * 定义所有可用的正面和负面属性
 */
public enum AttributeType {
    // ========== 正面属性 ==========

    // 通用战斗属性
    ATTACK_DAMAGE(Attributes.ATTACK_DAMAGE, 0.5, "attribute.modifier.nebula_tinker.attack_damage",
            EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND), AttributeCategory.COMBAT, 10, 2),
    ATTACK_SPEED(Attributes.ATTACK_SPEED, 0.1, "attribute.modifier.nebula_tinker.attack_speed",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.COMBAT, 12, 2),
    CRITICAL_CHANCE(null, 0.05,
            "attribute.modifier.nebula_tinker.critical_chance",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.COMBAT, 8, 3),
    CRITICAL_DAMAGE(null, 0.2, // 暴击伤害倍数增加
            "attribute.modifier.nebula_tinker.critical_damage",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.COMBAT, 7, 3),

    // 元素伤害属性
    FIRE_ASPECT(null, 2.0,
            "attribute.modifier.nebula_tinker.fire_aspect",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.ELEMENTAL, 6, 2),
    FROST_ASPECT(null, 2.0,
            "attribute.modifier.nebula_tinker.frost_aspect",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.ELEMENTAL, 6, 2),
    LIGHTNING_ASPECT(null, 2.0,
            "attribute.modifier.nebula_tinker.lightning_aspect",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.ELEMENTAL, 6, 2),

    // 远程属性
    DRAW_SPEED(null, 0.15, "attribute.modifier.nebula_tinker.draw_speed",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.RANGED, 10, 2),
    ARROW_SPEED(null, 0.2,
            "attribute.modifier.nebula_tinker.arrow_speed",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.RANGED, 8, 2),
    ARROW_ACCURACY(null, 0.1,
            "attribute.modifier.nebula_tinker.arrow_accuracy",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.RANGED, 9, 1),
    PROJECTILE_DAMAGE(null, 0.3, "attribute.modifier.nebula_tinker.projectile_damage",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.RANGED, 7, 2),

    // 工具属性
    MINING_SPEED(null, 0.5,
            "attribute.modifier.nebula_tinker.mining_speed",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.TOOL, 15, 1),
    DURABILITY(null, 100.0,
            "attribute.modifier.nebula_tinker.durability",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.TOOL, 12, 1),
    HARVEST_LEVEL(null, 1.0,
            "attribute.modifier.nebula_tinker.harvest_level",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.TOOL, 5, 3),
    EFFICIENCY(null, 0.2,
            "attribute.modifier.nebula_tinker.efficiency",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.TOOL, 10, 2),

    // 防御属性
    ARMOR(Attributes.ARMOR, 2.0, "attribute.modifier.nebula_tinker.armor",
            EnumSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET),
            AttributeCategory.DEFENSE, 14, 1),
    MAX_HEALTH(Attributes.MAX_HEALTH, 4.0, "attribute.modifier.nebula_tinker.max_health",
            EnumSet.of(EquipmentSlot.CHEST), AttributeCategory.DEFENSE, 6, 3),
    ARMOR_TOUGHNESS(Attributes.ARMOR_TOUGHNESS, 1.0, "attribute.modifier.nebula_tinker.armor_toughness",
            EnumSet.of(EquipmentSlot.CHEST), AttributeCategory.DEFENSE, 4, 3),

    // 移动属性
    MOVEMENT_SPEED_SMALL(Attributes.MOVEMENT_SPEED, 0.08,
            "attribute.modifier.nebula_tinker.movement_speed_small",
            EnumSet.of(EquipmentSlot.FEET), AttributeCategory.UTILITY, 10, 2),
    MOVEMENT_SPEED_MEDIUM(Attributes.MOVEMENT_SPEED, 0.15,
            "attribute.modifier.nebula_tinker.movement_speed_medium",
            EnumSet.of(EquipmentSlot.FEET), AttributeCategory.UTILITY, 8, 3),
    MOVEMENT_SPEED_LARGE(Attributes.MOVEMENT_SPEED, 0.25,
            "attribute.modifier.nebula_tinker.movement_speed_large",
            EnumSet.of(EquipmentSlot.FEET), AttributeCategory.UTILITY, 6, 4),

    KNOCKBACK_RESISTANCE(Attributes.KNOCKBACK_RESISTANCE, 0.15,
            "attribute.modifier.nebula_tinker.knockback_resistance",
            EnumSet.of(EquipmentSlot.CHEST), AttributeCategory.DEFENSE, 8, 2),

    // 特殊防御属性
    FEATHER_FALLING(null, 2.0,
            "attribute.modifier.nebula_tinker.feather_falling",
            EnumSet.of(EquipmentSlot.FEET), AttributeCategory.DEFENSE, 12, 2),
    PROTECTION(null, 1.0,
            "attribute.modifier.nebula_tinker.protection",
            EnumSet.allOf(EquipmentSlot.class), AttributeCategory.DEFENSE, 10, 2),

    // ========== 负面属性 ==========

    // 武器负面属性
    HEALTH_REDUCTION_SMALL(Attributes.MAX_HEALTH, -2.0,
            "attribute.modifier.nebula_tinker.health_reduction_small",
            EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND), AttributeCategory.NEGATIVE_WEAPON, 5, 1),
    HEALTH_REDUCTION_MEDIUM(Attributes.MAX_HEALTH, -4.0,
            "attribute.modifier.nebula_tinker.health_reduction_medium",
            EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND), AttributeCategory.NEGATIVE_WEAPON, 3, 2),

    ARMOR_REDUCTION_SMALL(Attributes.ARMOR, -1.0,
            "attribute.modifier.nebula_tinker.armor_reduction_small",
            EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND), AttributeCategory.NEGATIVE_WEAPON, 6, 1),
    ARMOR_REDUCTION_MEDIUM(Attributes.ARMOR, -2.0,
            "attribute.modifier.nebula_tinker.armor_reduction_medium",
            EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND), AttributeCategory.NEGATIVE_WEAPON, 4, 2),

    MOVEMENT_SLOW_SMALL(Attributes.MOVEMENT_SPEED, -0.05,
            "attribute.modifier.nebula_tinker.movement_slow_small",
            EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND), AttributeCategory.NEGATIVE_WEAPON, 7, 1),
    MOVEMENT_SLOW_MEDIUM(Attributes.MOVEMENT_SPEED, -0.10,
            "attribute.modifier.nebula_tinker.movement_slow_medium",
            EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND), AttributeCategory.NEGATIVE_WEAPON, 5, 2),

    // 盔甲负面属性
    ATTACK_DAMAGE_REDUCTION_SMALL(Attributes.ATTACK_DAMAGE, -0.3,
            "attribute.modifier.nebula_tinker.attack_damage_reduction_small",
            EnumSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET),
            AttributeCategory.NEGATIVE_ARMOR, 8, 1),
    ATTACK_DAMAGE_REDUCTION_MEDIUM(Attributes.ATTACK_DAMAGE, -0.6,
            "attribute.modifier.nebula_tinker.attack_damage_reduction_medium",
            EnumSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET),
            AttributeCategory.NEGATIVE_ARMOR, 6, 2),

    ATTACK_SPEED_REDUCTION_SMALL(Attributes.ATTACK_SPEED, -0.05,
            "attribute.modifier.nebula_tinker.attack_speed_reduction_small",
            EnumSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET),
            AttributeCategory.NEGATIVE_ARMOR, 9, 1),
    ATTACK_SPEED_REDUCTION_MEDIUM(Attributes.ATTACK_SPEED, -0.10,
            "attribute.modifier.nebula_tinker.attack_speed_reduction_medium",
            EnumSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET),
            AttributeCategory.NEGATIVE_ARMOR, 7, 2),

    CRITICAL_REDUCTION_SMALL(null, -0.03,
            "attribute.modifier.nebula_tinker.critical_reduction_small",
            EnumSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET),
            AttributeCategory.NEGATIVE_ARMOR, 4, 2),
    CRITICAL_REDUCTION_MEDIUM(null, -0.06,
            "attribute.modifier.nebula_tinker.critical_reduction_medium",
            EnumSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET),
            AttributeCategory.NEGATIVE_ARMOR, 2, 3),

    // 工具负面属性
    MINING_SPEED_REDUCTION_SMALL(null, -0.2,
            "attribute.modifier.nebula_tinker.mining_speed_reduction_small",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.NEGATIVE_TOOL, 10, 1),
    MINING_SPEED_REDUCTION_MEDIUM(null, -0.4,
            "attribute.modifier.nebula_tinker.mining_speed_reduction_medium",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.NEGATIVE_TOOL, 8, 2),

    DURABILITY_REDUCTION_SMALL(null, -50.0,
            "attribute.modifier.nebula_tinker.durability_reduction_small",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.NEGATIVE_TOOL, 8, 1),
    DURABILITY_REDUCTION_MEDIUM(null, -100.0,
            "attribute.modifier.nebula_tinker.durability_reduction_medium",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.NEGATIVE_TOOL, 6, 2),

    HARVEST_LEVEL_REDUCTION_SMALL(null, -0.5,
            "attribute.modifier.nebula_tinker.harvest_level_reduction_small",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.NEGATIVE_TOOL, 3, 2),
    HARVEST_LEVEL_REDUCTION_MEDIUM(null, -1.0,
            "attribute.modifier.nebula_tinker.harvest_level_reduction_medium",
            EnumSet.of(EquipmentSlot.MAINHAND), AttributeCategory.NEGATIVE_TOOL, 1, 3),

    // 暴击伤害负面属性
    CRITICAL_DAMAGE_REDUCTION_SMALL(null, -0.1,
            "attribute.modifier.nebula_tinker.critical_damage_reduction_small",
            EnumSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET),
            AttributeCategory.NEGATIVE_ARMOR, 5, 2),
    CRITICAL_DAMAGE_REDUCTION_MEDIUM(null, -0.2,
            "attribute.modifier.nebula_tinker.critical_damage_reduction_medium",
            EnumSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET),
            AttributeCategory.NEGATIVE_ARMOR, 3, 3);

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
        NEGATIVE_WEAPON, // 武器负面属性
        NEGATIVE_ARMOR,  // 盔甲负面属性
        NEGATIVE_TOOL    // 工具负面属性
    }

    private final Attribute attribute;
    private final double baseValue;
    private final String translationKey;
    private final Set<EquipmentSlot> applicableSlots;
    private final AttributeCategory category;
    private final int weight;  // 权重，用于随机选择
    private final int tier;    // 分级，用于稀有度系统

    AttributeType(Attribute attribute, double baseValue, String translationKey,
                  Set<EquipmentSlot> applicableSlots, AttributeCategory category) {
        this(attribute, baseValue, translationKey, applicableSlots, category, 10, 1);
    }

    AttributeType(Attribute attribute, double baseValue, String translationKey,
                  Set<EquipmentSlot> applicableSlots, AttributeCategory category, int weight, int tier) {
        this.attribute = attribute;
        this.baseValue = baseValue;
        this.translationKey = translationKey;
        this.applicableSlots = applicableSlots;
        this.category = category;
        this.weight = weight;
        this.tier = tier;
    }

    public Attribute getAttribute() {
        if (this.attribute != null) {
            return this.attribute;
        }

        // 对于自定义属性，从CustomAttributes获取
        try {
            Attribute customAttr = CustomAttributes.getCustomAttribute(this.name());
            if (customAttr != null) {
                return customAttr;
            }

            // 如果获取失败，记录警告并返回一个安全的占位符属性
            NebulaTinker.LOGGER.warn("Custom attribute {} not found, using placeholder", this.name());
            return Attributes.MAX_HEALTH; // 使用一个安全的默认属性
        } catch (Exception e) {
            NebulaTinker.LOGGER.error("Error getting custom attribute {}: {}", this.name(), e.getMessage(), e);
            return Attributes.MAX_HEALTH; // 使用一个安全的默认属性
        }
    }

    public double getBaseValue() { return baseValue; }
    public String getTranslationKey() { return translationKey; }
    public Set<EquipmentSlot> getApplicableSlots() { return applicableSlots; }
    public AttributeCategory getCategory() { return category; }
    public int getWeight() { return weight; }
    public int getTier() { return tier; }
    public boolean isNegative() {
        return category == AttributeCategory.NEGATIVE_WEAPON ||
                category == AttributeCategory.NEGATIVE_ARMOR ||
                category == AttributeCategory.NEGATIVE_TOOL;
    }

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
     * 检查两个属性是否冲突
     */
    public static boolean areConflicting(AttributeType type1, AttributeType type2) {
        // 相同的基础属性冲突（如增加攻击力和减少攻击力）
        if (type1.getAttribute() != null && type1.getAttribute().equals(type2.getAttribute())) {
            // 如果都是负面的或都是正面的，则不冲突（允许叠加）
            // 但如果一个是负面一个是正面，则冲突
            return (type1.isNegative() && !type2.isNegative()) ||
                    (!type1.isNegative() && type2.isNegative());
        }

        // 移动速度增加和减少冲突
        if ((type1.name().contains("MOVEMENT_SPEED") && type2.name().contains("MOVEMENT_SLOW")) ||
                (type1.name().contains("MOVEMENT_SLOW") && type2.name().contains("MOVEMENT_SPEED"))) {
            return true;
        }

        // 攻击伤害增加和减少冲突
        if ((type1.name().contains("ATTACK_DAMAGE") && type2.name().contains("ATTACK_DAMAGE_REDUCTION")) ||
                (type1.name().contains("ATTACK_DAMAGE_REDUCTION") && type2.name().contains("ATTACK_DAMAGE"))) {
            return true;
        }

        // 攻击速度增加和减少冲突
        if ((type1.name().contains("ATTACK_SPEED") && type2.name().contains("ATTACK_SPEED_REDUCTION")) ||
                (type1.name().contains("ATTACK_SPEED_REDUCTION") && type2.name().contains("ATTACK_SPEED"))) {
            return true;
        }

        return false;
    }
}