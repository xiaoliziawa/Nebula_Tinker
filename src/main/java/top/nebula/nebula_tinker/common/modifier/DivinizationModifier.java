package top.nebula.nebula_tinker.common.modifier;

import net.minecraft.ChatFormatting;
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
public class DivinizationModifier extends Modifier {
    private static final ResourceLocation ATTRIBUTES_KEY = new ResourceLocation(NebulaTinker.MODID, "divinization_attributes");
    private static final int MAX_LEVEL = 9;
    private static final double BASE_MULTIPLIER = 1.0;
    private static final double PER_LEVEL_BONUS = 0.1;
    private static final int ATTRIBUTES_COUNT = 3;

    // 缓存修饰符物品的属性，避免重复计算
    private static final Map<UUID, Map<ItemStack, List<AttributeEntry>>> attributeCache = new ConcurrentHashMap<>();
    // 计数器，减少tick处理频率
    private static final Map<UUID, Integer> tickCounter = new ConcurrentHashMap<>();
    // 粒子效果冷却时间
    private static final int PARTICLE_COOLDOWN = 100; // 5秒（20 ticks/秒 * 5 = 100 ticks）

    // 神化效果生成标识
    private static final String GENERATED_KEY = "divinization_generated";

    /**
     * 获取或生成神化属性
     */
    public static List<AttributeEntry> getOrGenerateAttributes(ItemStack stack, Player player) {
        if (stack.isEmpty() || player == null) {
            return Collections.emptyList();
        }

        // 尝试从缓存获取
        UUID playerId = player.getUUID();
        Map<ItemStack, List<AttributeEntry>> playerCache = attributeCache.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
        if (playerCache.containsKey(stack)) {
            return playerCache.get(stack);
        }

        CompoundTag tag = stack.getOrCreateTag();

        // 检查是否已生成属性
        if (tag.contains(GENERATED_KEY) && tag.contains(ATTRIBUTES_KEY.toString())) {
            List<AttributeEntry> attributes = deserializeAttributes(tag.getCompound(ATTRIBUTES_KEY.toString()));
            playerCache.put(stack, attributes);
            return attributes;
        }

        // 生成新属性
        ToolStack tool = ToolStack.from(stack);
        if (tool == null || tool.isBroken()) {
            return Collections.emptyList();
        }

        int level = SimpleTConUtils.getModifierLevel(stack, NebulaTinker.loadResource("divinization").toString());
        if (level <= 0) {
            return Collections.emptyList();
        }

        // 确定装备槽位
        EquipmentSlot slot = determineEquipmentSlot(stack, player);
        List<AttributeEntry> attributes = generateAttributes(tool, level, slot);

        if (!attributes.isEmpty()) {
            saveAttributes(stack, attributes);
            tag.putBoolean(GENERATED_KEY, true);

            // 缓存结果
            playerCache.put(stack, attributes);

            // 显示生成信息
            MutableComponent message = Component.translatable("message.nebula_tinker.divinization.generate").withStyle(ChatFormatting.GOLD);
            player.displayClientMessage(message, true);

            // 播放音效
            if (!player.level().isClientSide()) {
                player.level().playSound(null, player.blockPosition(),
                        SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5f, 1.0f);
            }
        }

        return attributes;
    }

    /**
     * 应用神化属性到玩家
     */
    private static void applyDivinizationAttributes(Player player, ItemStack stack, EquipmentSlot slot) {
        List<AttributeEntry> attributes = getOrGenerateAttributes(stack, player);

        if (attributes != null && !attributes.isEmpty()) {
            // 转换AttributeEntry列表为DemonizationModifier.AttributeEntry列表
            List<DemonizationModifier.AttributeEntry> convertedAttributes = new ArrayList<>();
            for (AttributeEntry entry : attributes) {
                convertedAttributes.add(new DemonizationModifier.AttributeEntry(
                        entry.type, entry.value, entry.slot
                ));
            }

            AttributeApplicator.applyAttributes(player, convertedAttributes, stack, "divinization");
        }
    }

    /**
     * 移除神化属性
     */
    private static void removeDivinizationAttributes(Player player, EquipmentSlot slot) {
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
     * 生成随机神化属性
     */
    private static List<AttributeEntry> generateAttributes(ToolStack tool, int level, EquipmentSlot slot) {
        List<AttributeEntry> attributes = new ArrayList<>();
        Random random = new Random();

        // 获取可用的属性池
        List<AttributeType> attributePool = getAttributePoolForSlot(tool, slot);
        if (attributePool.isEmpty()) {
            return attributes;
        }

        // 随机选择ATTRIBUTES_COUNT个不同的正面属性
        Set<AttributeType> selectedTypes = new HashSet<>();
        int maxAttempts = attributePool.size() * 2; // 防止死循环的最大尝试次数
        int attempts = 0;

        while (selectedTypes.size() < Math.min(ATTRIBUTES_COUNT, attributePool.size()) && attempts < maxAttempts) {
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
     * 获取适用于特定槽位的属性池
     */
    private static List<AttributeType> getAttributePoolForSlot(ToolStack tool, EquipmentSlot slot) {
        List<AttributeType> attributePool = new ArrayList<>();
        String toolName = tool.getItem().toString().toLowerCase();

        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
            // 根据工具/武器类型选择不同的属性
            if (toolName.contains("bow") || toolName.contains("crossbow")) {
                // 远程武器
                attributePool.addAll(Arrays.asList(
                        AttributeType.DRAW_SPEED,
                        AttributeType.ARROW_SPEED,
                        AttributeType.ARROW_ACCURACY,
                        AttributeType.PROJECTILE_DAMAGE
                ));
            } else if (toolName.contains("sword") || toolName.contains("axe") || toolName.contains("mace")) {
                // 近战武器
                attributePool.addAll(Arrays.asList(
                        AttributeType.ATTACK_DAMAGE,
                        AttributeType.ATTACK_SPEED,
                        AttributeType.CRITICAL_CHANCE,
                        AttributeType.CRITICAL_DAMAGE,
                        AttributeType.FIRE_ASPECT,
                        AttributeType.FROST_ASPECT,
                        AttributeType.LIGHTNING_ASPECT
                ));
            } else if (toolName.contains("pickaxe") || toolName.contains("shovel") || toolName.contains("mattock")) {
                // 工具
                attributePool.addAll(Arrays.asList(
                        AttributeType.MINING_SPEED,
                        AttributeType.DURABILITY,
                        AttributeType.HARVEST_LEVEL,
                        AttributeType.EFFICIENCY
                ));
            } else {
                // 默认添加战斗属性
                attributePool.addAll(Arrays.asList(
                        AttributeType.ATTACK_DAMAGE,
                        AttributeType.ATTACK_SPEED,
                        AttributeType.CRITICAL_CHANCE,
                        AttributeType.CRITICAL_DAMAGE
                ));
            }
        } else {
            // 盔甲
            attributePool.addAll(Arrays.asList(
                    AttributeType.ARMOR,
                    AttributeType.MAX_HEALTH,
                    AttributeType.ARMOR_TOUGHNESS,
                    AttributeType.MOVEMENT_SPEED_SMALL,
                    AttributeType.KNOCKBACK_RESISTANCE,
                    AttributeType.FEATHER_FALLING,
                    AttributeType.PROTECTION
            ));
        }

        return attributePool;
    }

    /**
     * 检查属性是否适用于该槽位
     */
    private static boolean isAttributeApplicable(AttributeType type, EquipmentSlot slot) {
        for (EquipmentSlot applicableSlot : type.getApplicableSlots()) {
            if (applicableSlot == slot ||
                    (slot == EquipmentSlot.MAINHAND && applicableSlot == EquipmentSlot.MAINHAND) ||
                    (slot == EquipmentSlot.OFFHAND && applicableSlot == EquipmentSlot.OFFHAND) ||
                    (slot.getType() == EquipmentSlot.Type.ARMOR && type.getApplicableSlots().contains(slot))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 保存属性到物品NBT
     */
    private static void saveAttributes(ItemStack stack, List<AttributeEntry> attributes) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.put(ATTRIBUTES_KEY.toString(), serializeAttributes(attributes));
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
            // 存储槽位的小写名称，避免解析问题
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
            } catch (Exception e) {
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
        if (SimpleTConUtils.hasModifier(mainHand, NebulaTinker.loadResource("divinization").toString())) {
            applyDivinizationAttributes(player, mainHand, EquipmentSlot.MAINHAND);
            handleDivinizedItem(player, mainHand, true);
        } else {
            // 如果主手没有神化修饰符，但之前可能有属性，则移除
            if (AttributeApplicator.hasModifierAttributesInSlot(player, EquipmentSlot.MAINHAND, "divinization")) {
                removeDivinizationAttributes(player, EquipmentSlot.MAINHAND);
            }
        }

        // 处理副手武器
        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
        if (SimpleTConUtils.hasModifier(offHand, NebulaTinker.loadResource("divinization").toString())) {
            applyDivinizationAttributes(player, offHand, EquipmentSlot.OFFHAND);
            handleDivinizedItem(player, offHand, false);
        } else {
            if (AttributeApplicator.hasModifierAttributesInSlot(player, EquipmentSlot.OFFHAND, "divinization")) {
                removeDivinizationAttributes(player, EquipmentSlot.OFFHAND);
            }
        }

        // 处理盔甲
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack armor = player.getItemBySlot(slot);
                if (SimpleTConUtils.hasModifier(armor, NebulaTinker.loadResource("divinization").toString())) {
                    applyDivinizationAttributes(player, armor, slot);
                    getOrGenerateAttributes(armor, player);
                } else {
                    if (AttributeApplicator.hasModifierAttributesInSlot(player, slot, "divinization")) {
                        removeDivinizationAttributes(player, slot);
                    }
                }
            }
        }
    }

    /**
     * 处理神化物品的周期性效果
     */
    private static void handleDivinizedItem(Player player, ItemStack item, boolean isMainHand) {
        // 确保属性已生成
        List<AttributeEntry> attributes = getOrGenerateAttributes(item, player);

        if (attributes.isEmpty()) {
            return;
        }

        // 生成粒子效果（每5秒一次）
        long gameTime = player.level().getGameTime();
        if (gameTime % PARTICLE_COOLDOWN == 0 && player.level() instanceof ServerLevel level) {
            // 生成金色粒子
            int particleCount = Math.min(3, attributes.size());
            for (int i = 0; i < particleCount; i++) {
                double offsetX = player.getRandom().nextDouble() - 0.5;
                double offsetY = player.getRandom().nextDouble() * 2.0;
                double offsetZ = player.getRandom().nextDouble() - 0.5;

                // 根据主副手调整粒子位置
                double handOffset = isMainHand ? -0.5 : 0.5;
                level.sendParticles(ParticleTypes.ENCHANT,
                        player.getX() + offsetX + handOffset,
                        player.getY() + offsetY,
                        player.getZ() + offsetZ,
                        1, 0, 0.1, 0, 0);
            }
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

        boolean hasDivinizationMain = SimpleTConUtils.hasModifier(mainHand, NebulaTinker.loadResource("divinization").toString());
        boolean hasDivinizationOff = SimpleTConUtils.hasModifier(offHand, NebulaTinker.loadResource("divinization").toString());

        if (!hasDivinizationMain && !hasDivinizationOff) {
            return;
        }

        // 优先使用主手，如果主手没有则使用副手
        ItemStack weapon = hasDivinizationMain ? mainHand : offHand;
        List<AttributeEntry> attributes = getOrGenerateAttributes(weapon, player);

        if (attributes.isEmpty()) {
            return;
        }

        // 计算额外伤害
        float extraDamage = 0.0f;
        boolean hasCriticalChance = false;
        boolean hasCriticalDamage = false;
        double criticalChanceValue = 0;
        double criticalDamageValue = 0;

        for (AttributeEntry attribute : attributes) {
            AttributeType type = attribute.type;
            if (type == AttributeType.ATTACK_DAMAGE) {
                extraDamage += (float) attribute.value;
            } else if (type == AttributeType.CRITICAL_CHANCE) {
                hasCriticalChance = true;
                criticalChanceValue = attribute.value;
            } else if (type == AttributeType.CRITICAL_DAMAGE) {
                hasCriticalDamage = true;
                criticalDamageValue = attribute.value;
            } else if (type.getCategory() == AttributeType.AttributeCategory.ELEMENTAL) {
                applyElementalEffects(event.getEntity(), attribute, player);
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

                // 暴击视觉反馈
                if (player.level() instanceof ServerLevel level) {
                    level.sendParticles(ParticleTypes.CRIT,
                            event.getEntity().getX(),
                            event.getEntity().getY() + event.getEntity().getBbHeight() / 2,
                            event.getEntity().getZ(),
                            10, 0.3, 0.3, 0.3, 0);

                    // 播放暴击音效
                    level.playSound(null, player.blockPosition(),
                            SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0f, 1.0f);
                }
            }
        }

        event.setAmount(finalDamage);

        // 显示伤害数字（视觉反馈）
        if (player.level() instanceof ServerLevel level && finalDamage > event.getAmount()) {
            level.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                    event.getEntity().getX(),
                    event.getEntity().getY() + event.getEntity().getBbHeight(),
                    event.getEntity().getZ(),
                    (int) (finalDamage - event.getAmount()), 0, 0, 0, 0);

            // 播放神化攻击音效
            level.playSound(null, player.blockPosition(),
                    SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.3f, 1.5f);
        }
    }

    /**
     * 应用元素效果
     */
    private static void applyElementalEffects(LivingEntity target, AttributeEntry attribute, Player attacker) {
        AttributeType type = attribute.type;
        double value = attribute.value;

        switch (type) {
            case FIRE_ASPECT:
                // 火焰效果
                target.setSecondsOnFire((int) (value / 2));
                spawnParticles(target, ParticleTypes.FLAME, 10);
                break;
            case FROST_ASPECT:
                // 寒冷效果：减速和挖掘疲劳
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                        (int) (value * 20), (int) (value / 2)));
                target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,
                        (int) (value * 10), 0));
                spawnParticles(target, ParticleTypes.SNOWFLAKE, 12);
                break;
            case LIGHTNING_ASPECT:
                // 闪电效果：有概率召唤闪电
                spawnParticles(target, ParticleTypes.ELECTRIC_SPARK, 15);
                // 10%概率召唤闪电
                if (!target.level().isClientSide() && attacker.getRandom().nextFloat() < 0.1f) {
                    // 召唤小型闪电伤害
                    target.hurt(target.damageSources().lightningBolt(), (float) value);
                    spawnParticles(target, ParticleTypes.ELECTRIC_SPARK, 30);

                    target.level().playSound(null, target.blockPosition(),
                            SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 0.5f, 0.8f);
                }
                break;
        }
    }

    /**
     * 生成粒子效果
     */
    private static void spawnParticles(LivingEntity entity, net.minecraft.core.particles.ParticleOptions particle, int count) {
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
    public static class AttributeEntry {
        public final AttributeType type;
        public final double value;
        public final EquipmentSlot slot;

        public AttributeEntry(AttributeType type, double value, EquipmentSlot slot) {
            this.type = type;
            this.value = value;
            this.slot = slot;
        }

        public Component getDescription() {
            String key = type.getTranslationKey();
            return Component.translatable(key, String.format("+%.1f", value));
        }
    }
}