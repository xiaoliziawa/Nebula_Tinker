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
import top.nebula.nebula_tinker.utils.SimpleTConUtils;

import java.util.*;

@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DivinizationModifier extends Modifier {
    private static final ResourceLocation ATTRIBUTES_KEY = new ResourceLocation(NebulaTinker.MODID, "divinization_attributes");
    private static final int MAX_LEVEL = 9;
    private static final double BASE_MULTIPLIER = 1.0;
    private static final double PER_LEVEL_BONUS = 0.1;
    private static final int ATTRIBUTES_COUNT = 3;

    // 神化效果生成标识
    private static final String GENERATED_KEY = "divinization_generated";

    /**
     * 获取或生成神化属性
     */
    private static List<AttributeEntry> getOrGenerateAttributes(ItemStack stack, Player player) {
        if (stack.isEmpty()) {
            return Collections.emptyList();
        }

        CompoundTag tag = stack.getOrCreateTag();

        // 检查是否已生成属性
        if (tag.contains(GENERATED_KEY) && tag.contains(ATTRIBUTES_KEY.toString())) {
            return deserializeAttributes(tag.getCompound(ATTRIBUTES_KEY.toString()));
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
        EquipmentSlot slot = EquipmentSlot.MAINHAND; // 默认主手
        String itemName = stack.getItem().toString().toLowerCase();

        // 简单判断装备类型
        if (itemName.contains("helmet") || itemName.contains("head")) {
            slot = EquipmentSlot.HEAD;
        } else if (itemName.contains("chestplate") || itemName.contains("chest")) {
            slot = EquipmentSlot.CHEST;
        } else if (itemName.contains("leggings") || itemName.contains("leg")) {
            slot = EquipmentSlot.LEGS;
        } else if (itemName.contains("boots") || itemName.contains("feet")) {
            slot = EquipmentSlot.FEET;
        }

        List<AttributeEntry> attributes = generateAttributes(tool, level, slot);
        saveAttributes(stack, attributes);
        tag.putBoolean(GENERATED_KEY, true);

        // 显示生成信息
        if (player != null) {
            MutableComponent message = Component.translatable("message.nebula_tinker.divinization.generate")
                    .withStyle(ChatFormatting.GOLD);
            player.displayClientMessage(message, true);

            // 播放音效
            player.level().playSound(null, player.blockPosition(),
                    SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5f, 1.0f);
        }

        return attributes;
    }

    /**
     * 生成随机神化属性
     */
    private static List<AttributeEntry> generateAttributes(ToolStack tool, int level, EquipmentSlot slot) {
        List<AttributeEntry> attributes = new ArrayList<>();
        Random random = new Random();

        // 获取可用的属性池
        List<AttributeType> attributePool = new ArrayList<>();

        // 根据装备槽位获取属性池
        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
            // 可能是武器或工具
            String toolName = tool.getItem().toString().toLowerCase();

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
                        AttributeType.CRITICAL_CHANCE
                ));
            }
        } else {
            // 盔甲
            attributePool.addAll(Arrays.asList(
                    AttributeType.ARMOR,
                    AttributeType.MAX_HEALTH,
                    AttributeType.ARMOR_TOUGHNESS,
                    AttributeType.MOVEMENT_SPEED,
                    AttributeType.KNOCKBACK_RESISTANCE
            ));
        }

        if (attributePool.isEmpty()) {
            return attributes;
        }

        // 随机选择ATTRIBUTES_COUNT个不同的正面属性
        Set<AttributeType> selectedTypes = new HashSet<>();
        while (selectedTypes.size() < Math.min(ATTRIBUTES_COUNT, attributePool.size())) {
            AttributeType type = attributePool.get(random.nextInt(attributePool.size()));

            // 检查属性是否适用于该槽位
            boolean isApplicable = false;
            for (EquipmentSlot applicableSlot : type.getApplicableSlots()) {
                if (applicableSlot == slot ||
                        (slot == EquipmentSlot.MAINHAND && applicableSlot == EquipmentSlot.MAINHAND) ||
                        (slot.getType() == EquipmentSlot.Type.ARMOR && type.getApplicableSlots().contains(slot))) {
                    isApplicable = true;
                    break;
                }
            }

            if (isApplicable && !selectedTypes.contains(type)) {
                selectedTypes.add(type);

                // 计算加成值：基础值 * (1 + 等级加成)
                double baseValue = type.getBaseValue();
                double multiplier = BASE_MULTIPLIER + (level - 1) * PER_LEVEL_BONUS;
                double finalValue = baseValue * multiplier;

                attributes.add(new AttributeEntry(type, finalValue, slot));
            }
        }

        return attributes;
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
            entryTag.putString("slot", entry.slot.getName()); // getName() 返回小写
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

        if (tag.contains("attributes", CompoundTag.TAG_LIST)) {
            ListTag list = tag.getList("attributes", CompoundTag.TAG_COMPOUND);

            for (int i = 0; i < list.size(); i++) {
                CompoundTag entryTag = list.getCompound(i);
                AttributeType type = AttributeType.valueOf(entryTag.getString("type"));
                double value = entryTag.getDouble("value");
                String slotName = entryTag.getString("slot");
                EquipmentSlot slot;

                try {
                    // 先尝试用 valueOf 解析大写形式（向后兼容）
                    slot = EquipmentSlot.valueOf(slotName);
                } catch (IllegalArgumentException e) {
                    // 如果失败，尝试用小写形式解析
                    slot = EquipmentSlot.byName(slotName.toLowerCase(Locale.ROOT));
                }

                attributes.add(new AttributeEntry(type, value, slot));
            }
        }

        return attributes;
    }

    /**
     * 应用元素伤害效果
     */
    private static void applyElementalEffects(LivingEntity target, AttributeEntry attribute, Player player) {
        AttributeType type = attribute.type;
        double value = attribute.value;

        switch (type) {
            case FIRE_ASPECT:
                target.setSecondsOnFire((int) (value / 2.0));
                spawnParticles(target, ParticleTypes.FLAME, 10);
                break;
            case FROST_ASPECT:
                // 冰冻效果：降低移动速度
                target.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,
                        (int) (value * 20),
                        (int) (value / 2.0)
                ));
                spawnParticles(target, ParticleTypes.SNOWFLAKE, 8);
                break;
            case LIGHTNING_ASPECT:
                // 召唤闪电链效果（视觉）
                spawnParticles(target, ParticleTypes.ELECTRIC_SPARK, 12);

                // 25%概率播放闪电音效
                if (!target.level().isClientSide() && player.getRandom().nextFloat() < 0.25f) {
                    target.level().playSound(null, target.blockPosition(),
                            SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 0.5f, 1.0f);
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

            level.sendParticles(
                    particle,
                    entity.getX() + offsetX,
                    entity.getY() + offsetY,
                    entity.getZ() + offsetZ,
                    1,
                    0, 0, 0,
                    0
            );
        }
    }

    // ========== 事件处理器 ==========

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;

        // 检查主手武器
        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (SimpleTConUtils.hasModifier(mainHand, NebulaTinker.loadResource("divinization").toString())) {
            // 确保属性已生成
            getOrGenerateAttributes(mainHand, player);

            // 每5秒播放一次神圣粒子效果
            if (player.level().getGameTime() % 100 == 0 && player.level() instanceof ServerLevel level) {
                // 生成金色粒子
                for (int i = 0; i < 3; i++) {
                    double offsetX = player.getRandom().nextDouble() - 0.5;
                    double offsetY = player.getRandom().nextDouble() * 2.0;
                    double offsetZ = player.getRandom().nextDouble() - 0.5;

                    level.sendParticles(
                            ParticleTypes.ENCHANT,
                            player.getX() + offsetX,
                            player.getY() + offsetY,
                            player.getZ() + offsetZ,
                            1,
                            0, 0.1, 0,
                            0
                    );
                }
            }
        }

        // 检查装备的盔甲
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack armor = player.getItemBySlot(slot);
                if (SimpleTConUtils.hasModifier(armor, NebulaTinker.loadResource("divinization").toString())) {
                    getOrGenerateAttributes(armor, player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();

        if (!(source.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack weapon = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!SimpleTConUtils.hasModifier(weapon, NebulaTinker.loadResource("divinization").toString())) {
            return;
        }

        List<AttributeEntry> attributes = getOrGenerateAttributes(weapon, player);
        if (attributes.isEmpty()) {
            return;
        }

        // 计算额外伤害
        float extraDamage = 0.0f;
        for (AttributeEntry attribute : attributes) {
            AttributeType type = attribute.type;

            if (type == AttributeType.ATTACK_DAMAGE) {
                extraDamage += attribute.value;
            } else if (type.getCategory() == AttributeType.AttributeCategory.ELEMENTAL) {
                extraDamage += attribute.value * 0.5f;
                applyElementalEffects(event.getEntity(), attribute, player);
            }
        }

        if (extraDamage > 0) {
            event.setAmount(event.getAmount() + extraDamage);

            // 显示伤害数字（视觉反馈）
            if (player.level() instanceof ServerLevel level) {
                level.sendParticles(
                        ParticleTypes.DAMAGE_INDICATOR,
                        event.getEntity().getX(),
                        event.getEntity().getY() + event.getEntity().getBbHeight(),
                        event.getEntity().getZ(),
                        (int) extraDamage,
                        0, 0, 0,
                        0
                );

                // 播放神化攻击音效
                level.playSound(null, player.blockPosition(),
                        SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.3f, 1.5f);
            }
        }
    }

    // ========== 内部类 ==========

    /**
     * 属性条目类
     */
    private static class AttributeEntry {
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