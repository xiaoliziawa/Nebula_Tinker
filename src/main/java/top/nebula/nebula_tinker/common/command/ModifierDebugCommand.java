package top.nebula.nebula_tinker.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.common.modifier.*;
import top.nebula.nebula_tinker.utils.SimpleTConUtils;

import java.util.Locale;

public class ModifierDebugCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("nebula_debug")
                .requires(source -> source.hasPermission(2)) // 需要管理员权限
                .then(Commands.literal("attributes")
                        .executes(ModifierDebugCommand::debugAttributes))
                .then(Commands.literal("demonization")
                        .executes(ModifierDebugCommand::debugDemonization))
                .then(Commands.literal("divinization")
                        .executes(ModifierDebugCommand::debugDivinization))
                .then(Commands.literal("check_hands")
                        .executes(ModifierDebugCommand::checkHands))
        );
    }

    private static int debugAttributes(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayer();

        if (player == null) {
            source.sendFailure(Component.literal("只有玩家可以使用此命令"));
            return 0;
        }

        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);

        // 检查是否有魔化或神化修饰器
        boolean hasDemonizationMain = SimpleTConUtils.hasModifier(mainHand, NebulaTinker.loadResource("demonization").toString());
        boolean hasDemonizationOff = SimpleTConUtils.hasModifier(offHand, NebulaTinker.loadResource("demonization").toString());
        boolean hasDivinizationMain = SimpleTConUtils.hasModifier(mainHand, NebulaTinker.loadResource("divinization").toString());
        boolean hasDivinizationOff = SimpleTConUtils.hasModifier(offHand, NebulaTinker.loadResource("divinization").toString());

        if (!hasDemonizationMain && !hasDemonizationOff && !hasDivinizationMain && !hasDivinizationOff) {
            source.sendFailure(Component.literal("主手或副手物品没有神化或魔化修饰器"));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("=== 物品属性调试 ===").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
        source.sendSuccess(() -> Component.literal("主手物品: ").append(mainHand.getDisplayName()).withStyle(ChatFormatting.YELLOW), false);
        source.sendSuccess(() -> Component.literal("副手物品: ").append(offHand.getDisplayName()).withStyle(ChatFormatting.YELLOW), false);

        if (hasDemonizationMain) {
            source.sendSuccess(() -> Component.literal("主手物品有魔化修饰器").withStyle(ChatFormatting.DARK_RED), false);
            debugDemonizationAttribute(source, mainHand, player, "主手");
        }

        if (hasDemonizationOff) {
            source.sendSuccess(() -> Component.literal("副手物品有魔化修饰器").withStyle(ChatFormatting.DARK_RED), false);
            debugDemonizationAttribute(source, offHand, player, "副手");
        }

        if (hasDivinizationMain) {
            source.sendSuccess(() -> Component.literal("主手物品有神化修饰器").withStyle(ChatFormatting.YELLOW), false);
            debugDivinizationAttribute(source, mainHand, player, "主手");
        }

        if (hasDivinizationOff) {
            source.sendSuccess(() -> Component.literal("副手物品有神化修饰器").withStyle(ChatFormatting.YELLOW), false);
            debugDivinizationAttribute(source, offHand, player, "副手");
        }

        return 1;
    }

    private static void debugDemonizationAttribute(CommandSourceStack source, ItemStack stack, Player player, String hand) {
        DemonizationModifier.AttributePack attributes = DemonizationModifier.getOrGenerateAttributes(stack, player);

        source.sendSuccess(() -> Component.literal(hand + "魔化属性:").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);

        if (attributes.positive.isEmpty()) {
            source.sendSuccess(() -> Component.literal("  没有正面属性").withStyle(ChatFormatting.GRAY), false);
        } else {
            source.sendSuccess(() -> Component.literal("  正面属性:").withStyle(ChatFormatting.GREEN), false);
            for (DemonizationModifier.AttributeEntry entry : attributes.positive) {
                String info = String.format(Locale.ROOT, "    %s: %.2f (槽位: %s)",
                        entry.type.name(), entry.value, entry.slot.getName());
                source.sendSuccess(() -> Component.literal(info).withStyle(ChatFormatting.GRAY), false);
            }
        }

        if (attributes.negative.isEmpty()) {
            source.sendSuccess(() -> Component.literal("  没有负面属性").withStyle(ChatFormatting.GRAY), false);
        } else {
            source.sendSuccess(() -> Component.literal("  负面属性:").withStyle(ChatFormatting.RED), false);
            for (DemonizationModifier.AttributeEntry entry : attributes.negative) {
                String info = String.format(Locale.ROOT, "    %s: %.2f (槽位: %s)",
                        entry.type.name(), entry.value, entry.slot.getName());
                source.sendSuccess(() -> Component.literal(info).withStyle(ChatFormatting.DARK_GRAY), false);
            }
        }
    }

    private static void debugDivinizationAttribute(CommandSourceStack source, ItemStack stack, Player player, String hand) {
        java.util.List<DivinizationModifier.AttributeEntry> attributes = DivinizationModifier.getOrGenerateAttributes(stack, player);

        source.sendSuccess(() -> Component.literal(hand + "神化属性:").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD), false);

        if (attributes.isEmpty()) {
            source.sendSuccess(() -> Component.literal("  没有属性").withStyle(ChatFormatting.GRAY), false);
        } else {
            for (DivinizationModifier.AttributeEntry entry : attributes) {
                String info = String.format(Locale.ROOT, "  %s: %.2f (槽位: %s)",
                        entry.type.name(), entry.value, entry.slot.getName());
                source.sendSuccess(() -> Component.literal(info).withStyle(ChatFormatting.YELLOW), false);
            }
        }
    }

    private static int debugDemonization(CommandContext<CommandSourceStack> context) {
        return debugSingleModifier(context, "demonization");
    }

    private static int debugDivinization(CommandContext<CommandSourceStack> context) {
        return debugSingleModifier(context, "divinization");
    }

    private static int debugSingleModifier(CommandContext<CommandSourceStack> context, String modifierName) {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayer();

        if (player == null) {
            source.sendFailure(Component.literal("只有玩家可以使用此命令"));
            return 0;
        }

        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);

        boolean hasModifierMain = SimpleTConUtils.hasModifier(mainHand, NebulaTinker.loadResource(modifierName).toString());
        boolean hasModifierOff = SimpleTConUtils.hasModifier(offHand, NebulaTinker.loadResource(modifierName).toString());

        if (!hasModifierMain && !hasModifierOff) {
            source.sendFailure(Component.literal("主手或副手物品没有" +
                    (modifierName.equals("demonization") ? "魔化" : "神化") + "修饰器"));
            return 0;
        }

        if (modifierName.equals("demonization")) {
            if (hasModifierMain) {
                debugDemonizationAttribute(source, mainHand, player, "主手");
            }
            if (hasModifierOff) {
                debugDemonizationAttribute(source, offHand, player, "副手");
            }
        } else {
            if (hasModifierMain) {
                debugDivinizationAttribute(source, mainHand, player, "主手");
            }
            if (hasModifierOff) {
                debugDivinizationAttribute(source, offHand, player, "副手");
            }
        }

        return 1;
    }

    private static int checkHands(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayer();

        if (player == null) {
            source.sendFailure(Component.literal("只有玩家可以使用此命令"));
            return 0;
        }

        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);

        source.sendSuccess(() -> Component.literal("=== 手部物品检测 ===").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
        source.sendSuccess(() -> Component.literal("主手: ").append(mainHand.getDisplayName())
                .append(" (").append(Component.literal(mainHand.isEmpty() ? "空" : "有物品").withStyle(ChatFormatting.YELLOW))
                .append(")").withStyle(ChatFormatting.WHITE), false);
        source.sendSuccess(() -> Component.literal("副手: ").append(offHand.getDisplayName())
                .append(" (").append(Component.literal(offHand.isEmpty() ? "空" : "有物品").withStyle(ChatFormatting.YELLOW))
                .append(")").withStyle(ChatFormatting.WHITE), false);

        // 检查修饰器
        checkModifierInHand(source, mainHand, "主手", "demonization", "魔化");
        checkModifierInHand(source, mainHand, "主手", "divinization", "神化");
        checkModifierInHand(source, offHand, "副手", "demonization", "魔化");
        checkModifierInHand(source, offHand, "副手", "divinization", "神化");

        return 1;
    }

    private static void checkModifierInHand(CommandSourceStack source, ItemStack stack, String hand, String modifierId, String modifierName) {
        if (SimpleTConUtils.hasModifier(stack, NebulaTinker.loadResource(modifierId).toString())) {
            int level = SimpleTConUtils.getModifierLevel(stack, NebulaTinker.loadResource(modifierId).toString());
            source.sendSuccess(() -> Component.literal(hand + "有" + modifierName + "修饰器，等级: " + level)
                    .withStyle(modifierId.equals("demonization") ? ChatFormatting.DARK_RED : ChatFormatting.YELLOW), false);
        }
    }
}