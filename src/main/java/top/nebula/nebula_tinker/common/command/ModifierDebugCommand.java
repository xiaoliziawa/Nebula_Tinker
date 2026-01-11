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

import java.util.List;
import java.util.Locale;

public class ModifierDebugCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("nebula_tinker")
				.requires((source) -> {
					return source.hasPermission(2);
				})
				.then(Commands.literal("debug")
						.then(Commands.literal("attributes")
								.executes(ModifierDebugCommand::debugAttributes))
						.then(Commands.literal("demonization")
								.executes(ModifierDebugCommand::debugDemonization))
						.then(Commands.literal("divinization")
								.executes(ModifierDebugCommand::debugDivinization))
						.then(Commands.literal("check_hands")
								.executes(ModifierDebugCommand::checkHands)))
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

		boolean hasDemonizationMain = SimpleTConUtils.hasModifier(
				mainHand,
				NebulaTinker.loadResource("demonization").toString()
		);
		boolean hasDemonizationOff = SimpleTConUtils.hasModifier(
				offHand,
				NebulaTinker.loadResource("demonization").toString()
		);
		boolean hasDivinizationMain = SimpleTConUtils.hasModifier(
				mainHand, NebulaTinker.loadResource(
						"divinization").toString()
		);
		boolean hasDivinizationOff = SimpleTConUtils.hasModifier(
				offHand,
				NebulaTinker.loadResource("divinization").toString()
		);

		if (!hasDemonizationMain && !hasDemonizationOff && !hasDivinizationMain && !hasDivinizationOff) {
			source.sendFailure(Component.literal("主手或副手物品没有神化或魔化修饰器"));
			return 0;
		}

		source.sendSuccess(() -> {
			return Component.literal("=== 物品属性调试 ===")
					.withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
		}, false);

		source.sendSuccess(() -> {
			return Component.literal(String.format("主手物品: %s", mainHand.getDisplayName().getString()))
					.withStyle(ChatFormatting.YELLOW);
		}, false);

		source.sendSuccess(() -> {
			return Component.literal(String.format("副手物品: %s", offHand.getDisplayName().getString()))
					.withStyle(ChatFormatting.YELLOW);
		}, false);

		if (hasDemonizationMain) {
			source.sendSuccess(() -> {
				return Component.literal("主手物品有魔化修饰器")
						.withStyle(ChatFormatting.DARK_RED);
			}, false);
			debugDemonizationAttribute(source, mainHand, player, "主手");
		}

		if (hasDemonizationOff) {
			source.sendSuccess(() -> {
				return Component.literal("副手物品有魔化修饰器")
						.withStyle(ChatFormatting.DARK_RED);
			}, false);
			debugDemonizationAttribute(source, offHand, player, "副手");
		}

		if (hasDivinizationMain) {
			source.sendSuccess(() -> {
				return Component.literal("主手物品有神化修饰器")
						.withStyle(ChatFormatting.YELLOW);
			}, false);
			debugDivinizationAttribute(source, mainHand, player, "主手");
		}

		if (hasDivinizationOff) {
			source.sendSuccess(() -> {
				return Component.literal("副手物品有神化修饰器")
						.withStyle(ChatFormatting.YELLOW);
			}, false);
			debugDivinizationAttribute(source, offHand, player, "副手");
		}

		return 1;
	}

	private static void debugDemonizationAttribute(CommandSourceStack source, ItemStack stack, Player player, String hand) {
		Demonization.AttributePack attributes = Demonization.getOrGenerateAttributes(stack, player);

		source.sendSuccess(() -> {
			return Component.literal(String.format("%s魔化属性:", hand))
					.withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
		}, false);

		if (attributes.positive().isEmpty()) {
			source.sendSuccess(() -> {
				return Component.literal("  没有正面属性")
						.withStyle(ChatFormatting.GRAY);
			}, false);
		} else {
			source.sendSuccess(() -> {
				return Component.literal("  正面属性:")
						.withStyle(ChatFormatting.GREEN);
			}, false);

			for (Demonization.AttributeEntry entry : attributes.positive()) {
				String info = String.format(
						Locale.ROOT,
						"    %s: %.2f (槽位: %s)",
						entry.type().name(),
						entry.value(),
						entry.slot().getName()
				);
				source.sendSuccess(() -> {
					return Component.literal(info)
							.withStyle(ChatFormatting.GRAY);
				}, false);
			}
		}

		if (attributes.negative().isEmpty()) {
			source.sendSuccess(() -> {
				return Component.literal("  没有负面属性")
						.withStyle(ChatFormatting.GRAY);
			}, false);
		} else {
			source.sendSuccess(() -> {
				return Component.literal("  负面属性:")
						.withStyle(ChatFormatting.RED);
			}, false);

			for (Demonization.AttributeEntry entry : attributes.negative()) {
				String info = String.format(
						Locale.ROOT,
						"    %s: %.2f (槽位: %s)",
						entry.type().name(),
						entry.value(),
						entry.slot().getName()
				);
				source.sendSuccess(() -> {
					return Component.literal(info)
							.withStyle(ChatFormatting.DARK_GRAY);
				}, false);
			}
		}
	}

	private static void debugDivinizationAttribute(CommandSourceStack source, ItemStack stack, Player player, String hand) {
		List<Divinization.AttributeEntry> attributes = Divinization.getOrGenerateAttributes(stack, player);

		source.sendSuccess(() -> {
			return Component.literal(String.format("%s神化属性:", hand))
					.withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD);
		}, false);

		if (attributes.isEmpty()) {
			source.sendSuccess(() -> {
				return Component.literal("  没有属性")
						.withStyle(ChatFormatting.GRAY);
			}, false);
		} else {
			for (Divinization.AttributeEntry entry : attributes) {
				String info = String.format(
						Locale.ROOT,
						"  %s: %.2f (槽位: %s)",
						entry.type().name(),
						entry.value(),
						entry.slot().getName()
				);
				source.sendSuccess(() -> {
					return Component.literal(info)
							.withStyle(ChatFormatting.YELLOW);
				}, false);
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

		boolean hasModifierMain = SimpleTConUtils.hasModifier(
				mainHand,
				NebulaTinker.loadResource(modifierName).toString()
		);
		boolean hasModifierOff = SimpleTConUtils.hasModifier(
				offHand,
				NebulaTinker.loadResource(modifierName).toString()
		);

		if (!hasModifierMain && !hasModifierOff) {
			source.sendFailure(Component.literal(
					String.format("主手或副手物品没有%s修饰器", modifierName.equals("demonization") ? "魔化" : "神化")
			));
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

		source.sendSuccess(() -> {
			return Component.literal("=== 手部物品检测 ===")
					.withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
		}, false);

		source.sendSuccess(() -> {
			return Component.literal(String.format("主手: %s (%s)", mainHand.getDisplayName().getString(), mainHand.isEmpty() ? "空" : "有物品"));
		}, false);

		source.sendSuccess(() -> {
			return Component.literal(String.format("副手: %s (%s)", offHand.getDisplayName().getString(), offHand.isEmpty() ? "空" : "有物品"));
		}, false);

		checkModifierInHand(source, mainHand, "主手", "demonization", "魔化");
		checkModifierInHand(source, mainHand, "主手", "divinization", "神化");
		checkModifierInHand(source, offHand, "副手", "demonization", "魔化");
		checkModifierInHand(source, offHand, "副手", "divinization", "神化");

		return 1;
	}

	private static void checkModifierInHand(
			CommandSourceStack source,
			ItemStack stack,
			String hand,
			String modifierId,
			String modifierName
	) {
		if (SimpleTConUtils.hasModifier(stack, NebulaTinker.loadResource(modifierId).toString())) {
			int level = SimpleTConUtils.getModifierLevel(
					stack, NebulaTinker.loadResource(modifierId).toString());

			source.sendSuccess(() -> {
				return Component.literal(String.format("%s有%s修饰器，等级: %d", hand, modifierName, level))
						.withStyle(modifierId.equals("demonization") ? ChatFormatting.DARK_RED : ChatFormatting.YELLOW);
			}, false);
		}
	}
}