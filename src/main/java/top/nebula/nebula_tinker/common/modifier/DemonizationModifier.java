package top.nebula.nebula_tinker.common.modifier;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.utils.AttributeType;
import top.nebula.nebula_tinker.utils.SimpleTConUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DemonizationModifier extends Modifier {
	private static final ResourceLocation POSITIVE_ATTRIBUTES_KEY = NebulaTinker.loadResource("demonization_positive");
	private static final ResourceLocation NEGATIVE_ATTRIBUTES_KEY = NebulaTinker.loadResource("demonization_negative");
	private static final int MAX_LEVEL = 9;
	private static final int POSITIVE_ATTRIBUTES_COUNT = 2;
	private static final int NEGATIVE_ATTRIBUTES_COUNT = 1;
	private static final double BASE_MULTIPLIER = 1.2;
	private static final double PER_LEVEL_BONUS = 0.15;
	// 负面效果的基准值
	private static final double NEGATIVE_MULTIPLIER = 0.5;

	// 缓存修饰符物品的属性，避免重复计算
	private static final Map<UUID, Map<ItemStack, AttributePack>> attributeCache = new ConcurrentHashMap<>();
	// 计数器，减少tick处理频率
	private static final Map<UUID, Integer> tickCounter = new ConcurrentHashMap<>();
	// 效果冷却时间
	private static final int PARTICLE_COOLDOWN = 100; // 5秒
	private static final int DAMAGE_COOLDOWN = 100; // 5秒

	/**
	 * 获取或生成魔化属性
	 */
	public static AttributePack getOrGenerateAttributes(ItemStack stack, Player player) {
		if (stack.isEmpty() || player == null) {
			return new AttributePack(Collections.emptyList(), Collections.emptyList());
		}

		// 尝试从缓存获取
		UUID playerId = player.getUUID();
		Map<ItemStack, AttributePack> playerCache = attributeCache.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
		if (playerCache.containsKey(stack)) {
			return playerCache.get(stack);
		}

		CompoundTag tag = stack.getOrCreateTag();

		// 检查是否已生成属性
		if (tag.contains(POSITIVE_ATTRIBUTES_KEY.toString()) && tag.contains(NEGATIVE_ATTRIBUTES_KEY.toString())) {
			AttributePack pack = new AttributePack(
					deserializeAttributes(tag.getCompound(POSITIVE_ATTRIBUTES_KEY.toString())),
					deserializeAttributes(tag.getCompound(NEGATIVE_ATTRIBUTES_KEY.toString()))
			);
			playerCache.put(stack, pack);
			return pack;
		}

		// 生成新属性
		ToolStack tool = ToolStack.from(stack);
		if (tool.isBroken()) {
			return new AttributePack(Collections.emptyList(), Collections.emptyList());
		}

		int level = SimpleTConUtils.getModifierLevel(stack, NebulaTinker.loadResource("demonization").toString());
		if (level <= 0) {
			return new AttributePack(Collections.emptyList(), Collections.emptyList());
		}

		// 确定装备槽位
		EquipmentSlot slot = determineEquipmentSlot(stack, player);
		List<AttributeEntry> positiveAttributes = generatePositiveAttributes(tool, level, slot);
		List<AttributeEntry> negativeAttributes = generateNegativeAttributes(tool, level, slot);

		if (!positiveAttributes.isEmpty() || !negativeAttributes.isEmpty()) {
			saveAttributes(stack, positiveAttributes, negativeAttributes);

			// 缓存结果
			AttributePack pack = new AttributePack(positiveAttributes, negativeAttributes);
			playerCache.put(stack, pack);

			// 显示生成信息
			MutableComponent message = Component.translatable("message.nebula_tinker.demonization.generate").withStyle(ChatFormatting.DARK_RED);
			player.displayClientMessage(message, true);

			// 播放音效
			if (!player.level().isClientSide()) {
				player.level().playSound(null, player.blockPosition(),
						SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 0.5f, 0.8f);
			}
		}

		return new AttributePack(positiveAttributes, negativeAttributes);
	}

	/**
	 * 应用魔化属性到玩家
	 */
	private static void applyDemonizationAttributes(Player player, ItemStack stack, EquipmentSlot slot) {
		AttributePack attributes = getOrGenerateAttributes(stack, player);

		if (attributes.positive != null && !attributes.positive.isEmpty()) {
			AttributeApplicator.applyAttributes(player, attributes.positive, stack, "demonization_positive");
		}

		if (attributes.negative != null && !attributes.negative.isEmpty()) {
			AttributeApplicator.applyAttributes(player, attributes.negative, stack, "demonization_negative");
		}
	}

	/**
	 * 移除魔化属性
	 */
	private static void removeDemonizationAttributes(Player player, EquipmentSlot slot) {
		AttributeApplicator.removeAttributes(player, slot);
	}

	/**
	 * 确定装备槽位
	 */
	private static EquipmentSlot determineEquipmentSlot(ItemStack stack, Player player) {
		String itemName = stack.getItem().toString().toLowerCase();

		if (itemName.contains("helmet") || itemName.contains("head")) {
			return EquipmentSlot.HEAD;
		} else if (itemName.contains("chestplate") || itemName.contains("chest")) {
			return EquipmentSlot.CHEST;
		} else if (itemName.contains("leggings") || itemName.contains("leg")) {
			return EquipmentSlot.LEGS;
		} else if (itemName.contains("boots") || itemName.contains("feet")) {
			return EquipmentSlot.FEET;
		} else if (player.getOffhandItem() == stack) {
			return EquipmentSlot.OFFHAND;
		}
		return EquipmentSlot.MAINHAND;
	}

	/**
	 * 生成正面属性（比神化更强）
	 */
	public static List<AttributeEntry> generatePositiveAttributes(ToolStack tool, int level, EquipmentSlot slot) {
		List<AttributeEntry> attributes = new ArrayList<>();
		Random random = new Random();

		// 获取可用的属性池
		List<AttributeType> attributePool = getAttributePoolForSlot(tool, slot, true);
		if (attributePool.isEmpty()) {
			return attributes;
		}

		// 随机选择ATTRIBUTES_COUNT个不同的正面属性
		Set<AttributeType> selectedTypes = new HashSet<>();
		int maxAttempts = attributePool.size() * 2; // 防止死循环的最大尝试次数
		int attempts = 0;

		while (selectedTypes.size() < Math.min(POSITIVE_ATTRIBUTES_COUNT, attributePool.size()) && attempts < maxAttempts) {
			attempts++;
			AttributeType type = attributePool.get(random.nextInt(attributePool.size()));

			// 检查属性是否适用于该槽位
			if (isAttributeApplicable(type, slot) && !selectedTypes.contains(type)) {
				selectedTypes.add(type);
				double baseValue = type.getBaseValue();
				double multiplier = BASE_MULTIPLIER + (level - 1) * PER_LEVEL_BONUS;
				double finalValue = baseValue * multiplier;
				attributes.add(new AttributeEntry(type, finalValue, slot));
			}
		}

		return attributes;
	}

	/**
	 * 生成负面属性
	 */
	private static List<AttributeEntry> generateNegativeAttributes(ToolStack tool, int level, EquipmentSlot slot) {
		List<AttributeEntry> attributes = new ArrayList<>();
		Random random = new Random();

		// 获取负面属性池
		List<AttributeType> negativePool = getAttributePoolForSlot(tool, slot, false);
		if (negativePool.isEmpty()) {
			return attributes;
		}

		// 随机选择一个负面属性
		Set<AttributeType> selectedTypes = new HashSet<>();
		int maxAttempts = negativePool.size() * 2;
		int attempts = 0;

		while (selectedTypes.size() < Math.min(NEGATIVE_ATTRIBUTES_COUNT, negativePool.size()) && attempts < maxAttempts) {
			attempts++;
			AttributeType type = negativePool.get(random.nextInt(negativePool.size()));

			// 检查属性是否适用于该槽位
			if (isAttributeApplicable(type, slot)) {
				selectedTypes.add(type);
				double baseValue = type.getBaseValue();
				double multiplier = NEGATIVE_MULTIPLIER * (1 + (level - 1) * 0.2); // 负面效果随等级增强
				double finalValue = baseValue * multiplier;
				attributes.add(new AttributeEntry(type, finalValue, slot));
			}
		}

		return attributes;
	}

	/**
	 * 获取适用于特定槽位的属性池
	 */
	private static List<AttributeType> getAttributePoolForSlot(ToolStack tool, EquipmentSlot slot, boolean positive) {
		List<AttributeType> attributePool = new ArrayList<>();
		String toolName = tool.getItem().toString().toLowerCase();

		if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
			if (toolName.contains("bow") || toolName.contains("crossbow")) {
				// 远程武器
				if (positive) {
					attributePool.addAll(Arrays.asList(
							AttributeType.DRAW_SPEED,
							AttributeType.ARROW_SPEED,
							AttributeType.ARROW_ACCURACY,
							AttributeType.PROJECTILE_DAMAGE
					));
				} else {
					attributePool.addAll(Arrays.asList(
							AttributeType.HEALTH_REDUCTION_SMALL,
							AttributeType.HEALTH_REDUCTION_MEDIUM,
							AttributeType.ARMOR_REDUCTION_SMALL,
							AttributeType.ARMOR_REDUCTION_MEDIUM
					));
				}
			} else if (toolName.contains("sword") || toolName.contains("axe") || toolName.contains("mace")) {
				// 近战武器
				if (positive) {
					attributePool.addAll(Arrays.asList(
							AttributeType.ATTACK_DAMAGE,
							AttributeType.ATTACK_SPEED,
							AttributeType.CRITICAL_CHANCE,
							AttributeType.CRITICAL_DAMAGE,
							AttributeType.FIRE_ASPECT,
							AttributeType.FROST_ASPECT,
							AttributeType.LIGHTNING_ASPECT
					));
				} else {
					attributePool.addAll(Arrays.asList(
							AttributeType.HEALTH_REDUCTION_SMALL,
							AttributeType.HEALTH_REDUCTION_MEDIUM,
							AttributeType.ARMOR_REDUCTION_SMALL,
							AttributeType.ARMOR_REDUCTION_MEDIUM,
							AttributeType.MOVEMENT_SLOW_SMALL,
							AttributeType.MOVEMENT_SLOW_MEDIUM
					));
				}
			} else if (toolName.contains("pickaxe") || toolName.contains("shovel") || toolName.contains("mattock")) {
				// 工具
				if (positive) {
					attributePool.addAll(Arrays.asList(
							AttributeType.MINING_SPEED,
							AttributeType.DURABILITY,
							AttributeType.HARVEST_LEVEL,
							AttributeType.EFFICIENCY
					));
				} else {
					attributePool.addAll(Arrays.asList(
							AttributeType.DURABILITY_REDUCTION_SMALL,
							AttributeType.DURABILITY_REDUCTION_MEDIUM,
							AttributeType.HARVEST_LEVEL_REDUCTION_SMALL,
							AttributeType.HARVEST_LEVEL_REDUCTION_MEDIUM,
							AttributeType.MINING_SPEED_REDUCTION_SMALL,
							AttributeType.MINING_SPEED_REDUCTION_MEDIUM
					));
				}
			} else {
				// 默认
				if (positive) {
					attributePool.addAll(Arrays.asList(
							AttributeType.ATTACK_DAMAGE,
							AttributeType.ATTACK_SPEED,
							AttributeType.CRITICAL_CHANCE,
							AttributeType.CRITICAL_DAMAGE
					));
				} else {
					attributePool.addAll(Arrays.asList(
							AttributeType.HEALTH_REDUCTION_SMALL,
							AttributeType.HEALTH_REDUCTION_MEDIUM,
							AttributeType.ARMOR_REDUCTION_SMALL,
							AttributeType.ARMOR_REDUCTION_MEDIUM
					));
				}
			}
		} else {
			// 盔甲
			if (positive) {
				attributePool.addAll(Arrays.asList(
						AttributeType.ARMOR,
						AttributeType.MAX_HEALTH,
						AttributeType.ARMOR_TOUGHNESS,
						AttributeType.MOVEMENT_SPEED_SMALL,
						AttributeType.KNOCKBACK_RESISTANCE,
						AttributeType.FEATHER_FALLING,
						AttributeType.PROTECTION
				));
			} else {
				// 负面属性
				attributePool.addAll(Arrays.asList(
						AttributeType.ATTACK_DAMAGE_REDUCTION_SMALL,
						AttributeType.ATTACK_DAMAGE_REDUCTION_MEDIUM,
						AttributeType.ATTACK_SPEED_REDUCTION_SMALL,
						AttributeType.ATTACK_SPEED_REDUCTION_MEDIUM,
						AttributeType.CRITICAL_REDUCTION_SMALL,
						AttributeType.CRITICAL_REDUCTION_MEDIUM,
						AttributeType.CRITICAL_DAMAGE_REDUCTION_SMALL,
						AttributeType.CRITICAL_DAMAGE_REDUCTION_MEDIUM
				));
			}
		}

		return attributePool;
	}

	/**
	 * 检查属性是否适用于该槽位
	 */
	private static boolean isAttributeApplicable(AttributeType type, EquipmentSlot slot) {
		for (EquipmentSlot applicableSlot : type.getApplicableSlots()) {
			if (applicableSlot == slot || slot.getType() == EquipmentSlot.Type.ARMOR && type.getApplicableSlots().contains(slot)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 保存属性到物品NBT
	 */
	private static void saveAttributes(ItemStack stack, List<AttributeEntry> positiveAttributes,
	                                   List<AttributeEntry> negativeAttributes) {
		CompoundTag tag = stack.getOrCreateTag();
		tag.put(POSITIVE_ATTRIBUTES_KEY.toString(), serializeAttributes(positiveAttributes));
		tag.put(NEGATIVE_ATTRIBUTES_KEY.toString(), serializeAttributes(negativeAttributes));
	}

	/**
	 * 序列化属性列表
	 */
	private static CompoundTag serializeAttributes(List<AttributeEntry> attributes) {
		CompoundTag tag = new CompoundTag();
		ListTag list = new ListTag();
		for (AttributeEntry entry : attributes) {
			CompoundTag entryTag = new CompoundTag();
			entryTag.putString("type", entry.type.name());
			entryTag.putDouble("value", entry.value);
			// 存储槽位的小写名称
			entryTag.putString("slot", entry.slot.getName().toLowerCase(Locale.ROOT));
			list.add(entryTag);
		}
		tag.put("attributes", list);
		return tag;
	}

	/**
	 * 反序列化属性列表
	 */
	private static List<AttributeEntry> deserializeAttributes(CompoundTag tag) {
		List<AttributeEntry> attributes = new ArrayList<>();
		if (!tag.contains("attributes", CompoundTag.TAG_LIST)) {
			return attributes;
		}

		ListTag list = tag.getList("attributes", CompoundTag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag entryTag = list.getCompound(i);
			try {
				AttributeType type = AttributeType.valueOf(entryTag.getString("type"));
				double value = entryTag.getDouble("value");
				String slotName = entryTag.getString("slot").toUpperCase(Locale.ROOT);
				EquipmentSlot slot;

				try {
					slot = EquipmentSlot.valueOf(slotName);
				} catch (IllegalArgumentException e) {
					// 尝试其他可能的槽位名称
					if (slotName.contains("HAND")) {
						slot = slotName.contains("MAIN") ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
					} else if (slotName.contains("ARMOR")) {
						slot = EquipmentSlot.CHEST; // 默认为胸甲
					} else {
						// 无法解析槽位，使用主手
						slot = EquipmentSlot.MAINHAND;
					}
				}
				attributes.add(new AttributeEntry(type, value, slot));
			} catch (Exception exception) {
				// 跳过无法解析的属性
				continue;
			}
		}
		return attributes;
	}

	// ========== 事件处理器 ==========

	/**
	 * 处理玩家tick事件
	 * 每10个tick处理一次，减少性能负担
	 */
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;

		Player player = event.player;
		UUID playerId = player.getUUID();

		// 更新玩家计数器
		int counter = tickCounter.getOrDefault(playerId, 0);
		tickCounter.put(playerId, counter + 1);

		// 每10个tick处理一次
		if (counter % 10 != 0) {
			return;
		}

		// 处理主手武器
		ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
		if (SimpleTConUtils.hasModifier(mainHand, NebulaTinker.loadResource("demonization").toString())) {
			applyDemonizationAttributes(player, mainHand, EquipmentSlot.MAINHAND);
			handleDemonizedItem(player, mainHand, true);
		} else {
			// 如果主手没有魔化修饰符，但之前可能有属性，则移除
			if (AttributeApplicator.hasModifierAttributesInSlot(player, EquipmentSlot.MAINHAND, "demonization_positive") ||
					AttributeApplicator.hasModifierAttributesInSlot(player, EquipmentSlot.MAINHAND, "demonization_negative")) {
				removeDemonizationAttributes(player, EquipmentSlot.MAINHAND);
			}
		}

		// 处理副手武器
		ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
		if (SimpleTConUtils.hasModifier(offHand, NebulaTinker.loadResource("demonization").toString())) {
			applyDemonizationAttributes(player, offHand, EquipmentSlot.OFFHAND);
			handleDemonizedItem(player, offHand, false);
		} else {
			if (AttributeApplicator.hasModifierAttributesInSlot(player, EquipmentSlot.OFFHAND, "demonization_positive") ||
					AttributeApplicator.hasModifierAttributesInSlot(player, EquipmentSlot.OFFHAND, "demonization_negative")) {
				removeDemonizationAttributes(player, EquipmentSlot.OFFHAND);
			}
		}

		// 处理盔甲
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() == EquipmentSlot.Type.ARMOR) {
				ItemStack armor = player.getItemBySlot(slot);
				if (SimpleTConUtils.hasModifier(armor, NebulaTinker.loadResource("demonization").toString())) {
					applyDemonizationAttributes(player, armor, slot);
					getOrGenerateAttributes(armor, player);
				} else {
					if (AttributeApplicator.hasModifierAttributesInSlot(player, slot, "demonization_positive") ||
							AttributeApplicator.hasModifierAttributesInSlot(player, slot, "demonization_negative")) {
						removeDemonizationAttributes(player, slot);
					}
				}
			}
		}
	}

	/**
	 * 处理魔化物品的周期性效果
	 */
	private static void handleDemonizedItem(Player player, ItemStack item, boolean isMainHand) {
		AttributePack attributes = getOrGenerateAttributes(item, player);

		long gameTime = player.level().getGameTime();

		// 生成暗红色粒子效果（每5秒）
		if (gameTime % PARTICLE_COOLDOWN == 0 && player.level() instanceof ServerLevel level) {
			// 生成暗红色粒子
			int particleCount = Math.min(5, attributes.positive.size() + attributes.negative.size());
			for (int i = 0; i < particleCount; i++) {
				double offsetX = player.getRandom().nextDouble() - 0.5;
				double offsetY = player.getRandom().nextDouble() * 2.5;
				double offsetZ = player.getRandom().nextDouble() - 0.5;

				// 根据主副手调整粒子位置
				double handOffset = isMainHand ? -0.5 : 0.5;
				level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
						player.getX() + offsetX + handOffset,
						player.getY() + offsetY,
						player.getZ() + offsetZ,
						1, 0, 0.05, 0, 0);
			}

			// 播放恶魔音效
			level.playSound(null, player.blockPosition(),
					SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 0.3f, 0.9f);
		}

		// 魔化副作用：周期性受到伤害和负面效果
		if (!attributes.negative.isEmpty() && gameTime % DAMAGE_COOLDOWN == 0 && !player.level().isClientSide()) {
			float damageAmount = 0.0f;

			// 根据负面属性计算伤害
			for (AttributeEntry negative : attributes.negative) {
				if (negative.value < 0) {
					damageAmount += (float) (-negative.value * 0.02f); // 负面属性的2%作为伤害
				}
			}

			// 受到魔法伤害
			if (damageAmount > 0) {
				player.hurt(player.damageSources().magic(), damageAmount);

				// 显示副作用粒子
				if (player.level() instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
							player.getX(), player.getY() + 1.5, player.getZ(),
							(int) damageAmount, 0, 0, 0, 0);

					// 添加随机负面效果
					if (player.getRandom().nextFloat() < 0.3f) {
						applyRandomNegativeEffect(player);
					}
				}
			}
		}
	}

	/**
	 * 应用随机负面效果
	 */
	private static void applyRandomNegativeEffect(Player player) {
		int effectType = player.getRandom().nextInt(4);
		switch (effectType) {
			case 0:
				player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
				break;
			case 1:
				player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0));
				break;
			case 2:
				player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 0));
				break;
			case 3:
				player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 0));
				break;
		}
	}

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		if (!(source.getEntity() instanceof Player player)) {
			return;
		}

		// 检查主手和副手武器
		ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);

		boolean hasDemonizationMain = SimpleTConUtils.hasModifier(mainHand, NebulaTinker.loadResource("demonization").toString());
		boolean hasDemonizationOff = SimpleTConUtils.hasModifier(offHand, NebulaTinker.loadResource("demonization").toString());

		if (!hasDemonizationMain && !hasDemonizationOff) {
			return;
		}

		// 优先使用主手，如果主手没有则使用副手
		ItemStack weapon = hasDemonizationMain ? mainHand : offHand;
		AttributePack attributes = getOrGenerateAttributes(weapon, player);

		if (attributes.positive.isEmpty()) {
			return;
		}

		// 计算额外伤害（比神化更高）
		float extraDamage = 0.0f;
		boolean hasCriticalChance = false;
		boolean hasCriticalDamage = false;
		double criticalChanceValue = 0;
		double criticalDamageValue = 0;

		for (AttributeEntry attribute : attributes.positive) {
			AttributeType type = attribute.type;
			if (type == AttributeType.ATTACK_DAMAGE) {
				extraDamage += (float) (attribute.value * 1.2f); // 比神化高20%
			} else if (type == AttributeType.CRITICAL_CHANCE) {
				hasCriticalChance = true;
				criticalChanceValue = attribute.value * 1.3f; // 暴击率更高
			} else if (type == AttributeType.CRITICAL_DAMAGE) {
				hasCriticalDamage = true;
				criticalDamageValue = attribute.value * 1.3f; // 暴击伤害更高
			} else if (type.getCategory() == AttributeType.AttributeCategory.ELEMENTAL) {
				applyDemonicEffects(event.getEntity(), attribute, player);
			}
		}

		// 处理暴击
		float finalDamage = event.getAmount() + extraDamage;
		if (hasCriticalChance) {
			// 检查是否跳劈（原版暴击）
			boolean isJumpCritical = player.fallDistance > 0.0F && !player.onGround() && !player.onClimbable() &&
					!player.isInWater() && !player.hasEffect(MobEffects.BLINDNESS) && !player.isPassenger();

			// 计算额外暴击率
			double totalCriticalChance = criticalChanceValue;
			if (isJumpCritical) {
				totalCriticalChance += 1.0; // 跳劈100%暴击
			}

			// 判断是否暴击
			if (player.getRandom().nextDouble() < totalCriticalChance) {
				// 计算暴击伤害倍数
				float criticalMultiplier = 1.5f; // 基础暴击伤害
				if (hasCriticalDamage) {
					criticalMultiplier += (float) criticalDamageValue;
				}

				finalDamage *= criticalMultiplier;

				// 恶魔暴击视觉反馈
				if (player.level() instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
							event.getEntity().getX(),
							event.getEntity().getY() + event.getEntity().getBbHeight() / 2,
							event.getEntity().getZ(),
							15, 0.4, 0.4, 0.4, 0);

					// 播放恶魔暴击音效
					level.playSound(null, player.blockPosition(),
							SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 0.7f, 1.2f);
				}
			}
		}

		event.setAmount(finalDamage);

		// 显示暗红色伤害数字
		if (player.level() instanceof ServerLevel level && finalDamage > event.getAmount()) {
			level.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
					event.getEntity().getX(),
					event.getEntity().getY() + event.getEntity().getBbHeight(),
					event.getEntity().getZ(),
					(int) (finalDamage - event.getAmount()), 0, 0, 0, 0);
		}

		// 魔化副作用：攻击时也会对自己造成轻微伤害
		if (!attributes.negative.isEmpty()) {
			float selfDamage = 0.0f;
			for (AttributeEntry negative : attributes.negative) {
				if (negative.type == AttributeType.HEALTH_REDUCTION_SMALL ||
						negative.type == AttributeType.HEALTH_REDUCTION_MEDIUM) {
					selfDamage += (float) (-negative.value * 0.1f); // 造成10%的负面属性值作为反伤
				}
			}

			if (selfDamage > 0 && !player.level().isClientSide()) {
				player.hurt(player.damageSources().magic(), selfDamage);

				if (player.level() instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
							player.getX(), player.getY() + 1.5, player.getZ(),
							(int) selfDamage, 0, 0, 0, 0);
				}
			}
		}
	}

	/**
	 * 应用恶魔元素效果
	 */
	private static void applyDemonicEffects(LivingEntity target, AttributeEntry attribute, Player player) {
		AttributeType type = attribute.type;
		double value = attribute.value;

		switch (type) {
			case FIRE_ASPECT:
				// 地狱火：伤害和凋零效果
				target.setSecondsOnFire((int) (value / 1.5));
				target.addEffect(new MobEffectInstance(MobEffects.WITHER,
						(int) (value * 15), 0));
				spawnParticles(target, ParticleTypes.SOUL_FIRE_FLAME, 15);
				break;
			case FROST_ASPECT:
				// 深寒：减速和虚弱
				target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
						(int) (value * 40), (int) (value / 1.5)));
				target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,
						(int) (value * 30), 1));
				spawnParticles(target, ParticleTypes.SNOWFLAKE, 12);
				break;
			case LIGHTNING_ASPECT:
				// 恶魔闪电效果：更高概率召唤闪电
				spawnParticles(target, ParticleTypes.ELECTRIC_SPARK, 20);
				// 20%概率召唤闪电
				if (!target.level().isClientSide() && player.getRandom().nextFloat() < 0.2f) {
					// 召唤更强闪电伤害
					target.hurt(target.damageSources().lightningBolt(), (float) (value * 1.5f));
					spawnParticles(target, ParticleTypes.ELECTRIC_SPARK, 40);

					target.level().playSound(null, target.blockPosition(),
							SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 0.8f, 0.8f);
				}
				break;
		}
	}

	/**
	 * 生成粒子效果
	 */
	private static void spawnParticles(LivingEntity entity, ParticleOptions particle, int count) {
		if (!(entity.level() instanceof ServerLevel level)) {
			return;
		}

		for (int i = 0; i < count; i++) {
			double offsetX = entity.getRandom().nextDouble() - 0.5;
			double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
			double offsetZ = entity.getRandom().nextDouble() - 0.5;

			level.sendParticles(particle,
					entity.getX() + offsetX,
					entity.getY() + offsetY,
					entity.getZ() + offsetZ,
					1, 0, 0, 0, 0);
		}
	}

	// ========== 内部类 ==========

	/**
	 * 属性条目类
	 */
	public record AttributeEntry(AttributeType type, double value, EquipmentSlot slot) {

		public Component getDescription() {
			String key = type.getTranslationKey();
			return Component.translatable(key, String.format("+%.1f", value))
					.withStyle(type.getCategory() == AttributeType.AttributeCategory.ELEMENTAL ?
							ChatFormatting.DARK_RED : ChatFormatting.RED);
		}
	}

	/**
	 * 属性包类（包含正面和负面属性）
	 */
	public record AttributePack(List<AttributeEntry> positive, List<AttributeEntry> negative) {
	}
}