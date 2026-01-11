package top.nebula.nebula_tinker.common.modifier;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import top.nebula.nebula_tinker.utils.AttributeType;
import top.nebula.nebula_tinker.utils.SimpleTConUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeApplicator {
	private static final Map<UUID, Map<EquipmentSlot, List<AppliedModifier>>> appliedModifiers = new ConcurrentHashMap<>();

	// 新增：用于清理过期缓存
	private static final Map<UUID, Long> lastAccessTime = new ConcurrentHashMap<>();
	private static final long CACHE_TIMEOUT = 60000; // 60秒

	/**
	 * 为实体应用属性
	 */
	public static void applyAttributes(LivingEntity entity, List<DemonizationModifier.AttributeEntry> attributes, ItemStack stack, String modifierId) {
		if (entity == null || attributes == null || attributes.isEmpty()) {
			return;
		}

		UUID entityId = entity.getUUID();
		Map<EquipmentSlot, List<AppliedModifier>> entityModifiers =
				appliedModifiers.computeIfAbsent(entityId, (uuid) -> {
					return new ConcurrentHashMap<>();
				});

		for (DemonizationModifier.AttributeEntry entry : attributes) {
			AttributeType type = entry.type();
			double value = entry.value();
			EquipmentSlot slot = entry.slot();

			// 跳过没有映射到游戏属性的自定义属性
			if (type.getAttribute() == null) {
				continue;
			}

			// 生成唯一的UUID
			UUID modifierUuid = UUID.nameUUIDFromBytes((stack.getDescriptionId() + type.name() + modifierId + slot.getName()).getBytes());

			// 应用属性修饰符
			AttributeInstance attributeInstance = entity.getAttribute(type.getAttribute());
			if (attributeInstance != null) {
				// 移除旧的相同UUID的修饰符
				attributeInstance.removeModifier(modifierUuid);

				// 确定操作类型（加法或乘法）
				AttributeModifier.Operation operation = AttributeModifier.Operation.ADDITION;

				// 对于某些属性使用乘法操作
				if (type.name().contains("SPEED") && !type.isNegative()) {
					operation = AttributeModifier.Operation.MULTIPLY_TOTAL;
				}

				// 添加新的修饰符
				AttributeModifier modifier = new AttributeModifier(
						modifierUuid,
						"nebula_tinker_" + modifierId + "_" + type.name().toLowerCase(),
						value,
						operation
				);
				attributeInstance.addPermanentModifier(modifier);

				// 记录已应用的修饰符
				AppliedModifier appliedModifier = new AppliedModifier(
						modifierUuid, type.getAttribute(), slot, modifierId, type, value
				);
				List<AppliedModifier> slotModifiers = entityModifiers
						.computeIfAbsent(slot, (slot1) -> {
							return new ArrayList<>();
						});
				slotModifiers.add(appliedModifier);
			}
		}

		// 更新最后访问时间
		lastAccessTime.put(entityId, System.currentTimeMillis());
	}

	/**
	 * 移除指定槽位的所有属性
	 */
	public static void removeAttributes(Player player, EquipmentSlot slot) {
		UUID playerId = player.getUUID();
		if (!appliedModifiers.containsKey(playerId)) {
			return;
		}

		Map<EquipmentSlot, List<AppliedModifier>> playerModifiers = appliedModifiers.get(playerId);
		if (!playerModifiers.containsKey(slot)) {
			return;
		}

		List<AppliedModifier> slotModifiers = playerModifiers.get(slot);
		for (AppliedModifier modifier : slotModifiers) {
			AttributeInstance attributeInstance = player.getAttribute(modifier.attribute);
			if (attributeInstance != null) {
				attributeInstance.removeModifier(modifier.uuid);
			}
		}

		// 从缓存中移除
		playerModifiers.remove(slot);
		if (playerModifiers.isEmpty()) {
			appliedModifiers.remove(playerId);
			lastAccessTime.remove(playerId);
		}
	}

	/**
	 * 移除玩家的所有属性
	 */
	public static void removeAllAttributes(Player player) {
		UUID playerId = player.getUUID();
		if (!appliedModifiers.containsKey(playerId)) {
			return;
		}

		Map<EquipmentSlot, List<AppliedModifier>> playerModifiers = appliedModifiers.get(playerId);
		for (Map.Entry<EquipmentSlot, List<AppliedModifier>> entry : playerModifiers.entrySet()) {
			for (AppliedModifier modifier : entry.getValue()) {
				AttributeInstance attributeInstance = player.getAttribute(modifier.attribute);
				if (attributeInstance != null) {
					attributeInstance.removeModifier(modifier.uuid);
				}
			}
		}

		appliedModifiers.remove(playerId);
		lastAccessTime.remove(playerId);
	}

	/**
	 * 检查指定槽位是否有特定修饰符的属性
	 */
	public static boolean hasModifierAttributesInSlot(Player player, EquipmentSlot slot, String modifierId) {
		UUID playerId = player.getUUID();
		if (!appliedModifiers.containsKey(playerId)) {
			return false;
		}

		Map<EquipmentSlot, List<AppliedModifier>> playerModifiers = appliedModifiers.get(playerId);
		if (!playerModifiers.containsKey(slot)) {
			return false;
		}

		List<AppliedModifier> slotModifiers = playerModifiers.get(slot);
		for (AppliedModifier modifier : slotModifiers) {
			if (modifier.modifierId.equals(modifierId)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 清理过期缓存
	 */
	public static void cleanupExpiredCache() {
		long currentTime = System.currentTimeMillis();
		List<UUID> toRemove = new ArrayList<>();

		for (Map.Entry<UUID, Long> entry : lastAccessTime.entrySet()) {
			if (currentTime - entry.getValue() > CACHE_TIMEOUT) {
				toRemove.add(entry.getKey());
			}
		}

		for (UUID playerId : toRemove) {
			appliedModifiers.remove(playerId);
			lastAccessTime.remove(playerId);
		}
	}

	/**
	 * 获取物品的属性描述
	 */
	public static List<Component> getAttributeTooltips(ItemStack stack, Player player) {
		List<Component> tooltips = new ArrayList<>();

		if (stack.isEmpty() || player == null) {
			return tooltips;
		}

		// 检查是否有魔化或神化修饰符
		boolean hasDemonization = SimpleTConUtils.hasModifier(stack,
				top.nebula.nebula_tinker.NebulaTinker.loadResource("demonization").toString());
		boolean hasDivinization = SimpleTConUtils.hasModifier(stack,
				top.nebula.nebula_tinker.NebulaTinker.loadResource("divinization").toString());

		if (!hasDemonization && !hasDivinization) {
			return tooltips;
		}

		// 获取属性
		if (hasDemonization) {
			DemonizationModifier.AttributePack attributes = DemonizationModifier.getOrGenerateAttributes(stack, player);

			// 添加正面属性
			if (attributes.positive() != null && !attributes.positive().isEmpty()) {
				tooltips.add(Component.literal("§6§l魔化属性:").withStyle(ChatFormatting.GOLD));
				for (DemonizationModifier.AttributeEntry entry : attributes.positive()) {
					tooltips.add(Component.literal("  §a" + getAttributeDisplay(entry)).withStyle(ChatFormatting.GREEN));
				}
			}

			// 添加负面属性
			if (attributes.negative() != null && !attributes.negative().isEmpty()) {
				tooltips.add(Component.literal("§c§l负面效果:").withStyle(ChatFormatting.RED));
				for (DemonizationModifier.AttributeEntry entry : attributes.negative()) {
					tooltips.add(Component.literal("  §c" + getAttributeDisplay(entry)).withStyle(ChatFormatting.RED));
				}
			}
		} else {
			List<DivinizationModifier.AttributeEntry> attributes = DivinizationModifier.getOrGenerateAttributes(stack, player);

			if (!attributes.isEmpty()) {
				tooltips.add(Component.literal("§b§l神化属性:").withStyle(ChatFormatting.AQUA));
				for (DivinizationModifier.AttributeEntry entry : attributes) {
					tooltips.add(Component.literal("  §b" + getAttributeDisplay(entry)).withStyle(ChatFormatting.AQUA));
				}
			}
		}

		return tooltips;
	}

	/**
	 * 获取属性显示字符串
	 */
	private static String getAttributeDisplay(DemonizationModifier.AttributeEntry entry) {
		String displayName = getAttributeDisplayName(entry.type());
		String formattedValue = formatAttributeValue(entry.type(), entry.value());
		return String.format("%s §f%s", displayName, formattedValue);
	}

	private static String getAttributeDisplay(DivinizationModifier.AttributeEntry entry) {
		String displayName = getAttributeDisplayName(entry.type());
		String formattedValue = formatAttributeValue(entry.type(), entry.value());
		return String.format("%s §f%s", displayName, formattedValue);
	}

	/**
	 * 获取属性显示名称
	 */
	private static String getAttributeDisplayName(AttributeType type) {
		switch (type) {
			case ARMOR:
				return "护甲值";
			case MAX_HEALTH:
				return "生命值";
			case MOVEMENT_SPEED_SMALL:
			case MOVEMENT_SPEED_MEDIUM:
			case MOVEMENT_SPEED_LARGE:
				return "移动速度";
			case FEATHER_FALLING:
				return "安全摔落高度";
			case PROTECTION:
				return "保护";
			case ATTACK_DAMAGE:
				return "攻击伤害";
			case ATTACK_SPEED:
				return "攻击速度";
			case CRITICAL_CHANCE:
				return "暴击率";
			case CRITICAL_DAMAGE:
				return "暴击伤害";
			case FIRE_ASPECT:
				return "火焰附加";
			case FROST_ASPECT:
				return "冰霜附加";
			case LIGHTNING_ASPECT:
				return "闪电附加";
			case MINING_SPEED:
				return "挖掘速度";
			case DURABILITY:
				return "耐久度";
			case HARVEST_LEVEL:
				return "挖掘等级";
			case EFFICIENCY:
				return "效率";
			case KNOCKBACK_RESISTANCE:
				return "击退抗性";
			case DRAW_SPEED:
				return "拉弓速度";
			case ARROW_SPEED:
				return "箭矢速度";
			case ARROW_ACCURACY:
				return "箭矢精度";
			case PROJECTILE_DAMAGE:
				return "远程伤害";
			case ARMOR_TOUGHNESS:
				return "护甲韧性";
			default:
				// 负面属性
				if (type.name().contains("HEALTH_REDUCTION")) return "生命值减少";
				if (type.name().contains("ARMOR_REDUCTION")) return "护甲减少";
				if (type.name().contains("MOVEMENT_SLOW")) return "移动减速";
				if (type.name().contains("ATTACK_DAMAGE_REDUCTION")) return "攻击伤害减少";
				if (type.name().contains("ATTACK_SPEED_REDUCTION")) return "攻击速度减慢";
				if (type.name().contains("CRITICAL_REDUCTION")) return "暴击率减少";
				if (type.name().contains("CRITICAL_DAMAGE_REDUCTION")) return "暴击伤害减少";
				if (type.name().contains("MINING_SPEED_REDUCTION")) return "挖掘速度减慢";
				if (type.name().contains("DURABILITY_REDUCTION")) return "耐久减少";
				if (type.name().contains("HARVEST_LEVEL_REDUCTION")) return "挖掘等级降低";
				return type.name().toLowerCase().replace("_", " ");
		}
	}

	/**
	 * 格式化属性值
	 */
	private static String formatAttributeValue(AttributeType type, double value) {
		// 根据属性类型格式化显示
		if (type == AttributeType.CRITICAL_CHANCE ||
				type.name().contains("CRITICAL_REDUCTION") ||
				type == AttributeType.ARROW_ACCURACY) {
			// 百分比显示
			return String.format("%+.1f%%", value * 100);
		} else if (type == AttributeType.CRITICAL_DAMAGE ||
				type.name().contains("CRITICAL_DAMAGE_REDUCTION")) {
			// 暴击伤害倍数显示
			return String.format("%+.1f倍", value);
		} else if (type == AttributeType.FEATHER_FALLING) {
			// 整数值
			return String.format("+%.0f格", value);
		} else if (type == AttributeType.DURABILITY ||
				type.name().contains("DURABILITY_REDUCTION")) {
			// 整数值
			return String.format("%+.0f", value);
		} else if (type == AttributeType.HARVEST_LEVEL ||
				type.name().contains("HARVEST_LEVEL_REDUCTION")) {
			// 整数值
			return String.format("%+.0f", value);
		} else if (type.name().contains("MOVEMENT_SPEED") ||
				type.name().contains("MOVEMENT_SLOW")) {
			// 百分比显示
			return String.format("%+.1f%%", value * 100);
		} else if (type == AttributeType.ATTACK_SPEED ||
				type.name().contains("ATTACK_SPEED_REDUCTION")) {
			// 攻击速度特殊格式
			return String.format("%+.2f", value);
		} else if (type.getCategory() == AttributeType.AttributeCategory.ELEMENTAL) {
			// 元素伤害：显示秒数
			return String.format("%+.1f秒", Math.abs(value) / 2);
		} else if (type == AttributeType.PROTECTION) {
			// 保护
			return String.format("+%.1f", value);
		} else if (type == AttributeType.DRAW_SPEED ||
				type == AttributeType.ARROW_SPEED ||
				type == AttributeType.PROJECTILE_DAMAGE ||
				type == AttributeType.EFFICIENCY) {
			// 远程属性
			return String.format("%+.1f", value);
		} else if (type == AttributeType.ARMOR_TOUGHNESS ||
				type == AttributeType.KNOCKBACK_RESISTANCE) {
			// 护甲韧性和击退抗性
			return String.format("%+.1f", value);
		} else {
			// 默认格式
			return String.format("%+.1f", value);
		}
	}

	/**
	 * 已应用的修饰符记录（扩展）
	 */
	private record AppliedModifier(
			UUID uuid, Attribute attribute,
			EquipmentSlot slot,
			String modifierId,
			AttributeType attributeType,
			double value
	) {
	}
}