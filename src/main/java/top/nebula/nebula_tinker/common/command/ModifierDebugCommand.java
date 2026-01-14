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
			source.sendFailure(Component.translatable("command.nebula_tinker.player_only"));
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
			source.sendFailure(Component.translatable("command.nebula_tinker.no_modifier"));
			return 0;
		}

		source.sendSuccess(() -> {
			return Component.translatable("command.nebula_tinker.debug.title")
					.withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
		}, false);

		source.sendSuccess(() -> {
			return Component.translatable("command.nebula_tinker.debug.main_hand", mainHand.getDisplayName().getString())
					.withStyle(ChatFormatting.YELLOW);
		}, false);

		source.sendSuccess(() -> {
			return Component.translatable("command.nebula_tinker.debug.off_hand", offHand.getDisplayName().getString())
					.withStyle(ChatFormatting.YELLOW);
		}, false);

		if (hasDemonizationMain) {
			source.sendSuccess(() -> {
				return Component.translatable("command.nebula_tinker.debug.main_has_demonization")
						.withStyle(ChatFormatting.DARK_RED);
			}, false);
			debugDemonizationAttribute(source, mainHand, player, Component.translatable("command.nebula_tinker.hand.main").getString());
		}

		if (hasDemonizationOff) {
			source.sendSuccess(() -> {
				return Component.translatable("command.nebula_tinker.debug.off_has_demonization")
						.withStyle(ChatFormatting.DARK_RED);
			}, false);
			debugDemonizationAttribute(source, offHand, player, Component.translatable("command.nebula_tinker.hand.off").getString());
		}

		if (hasDivinizationMain) {
			source.sendSuccess(() -> {
				return Component.translatable("command.nebula_tinker.debug.main_has_divinization")
						.withStyle(ChatFormatting.YELLOW);
			}, false);
			debugDivinizationAttribute(source, mainHand, player, Component.translatable("command.nebula_tinker.hand.main").getString());
		}

		if (hasDivinizationOff) {
			source.sendSuccess(() -> {
				return Component.translatable("command.nebula_tinker.debug.off_has_divinization")
						.withStyle(ChatFormatting.YELLOW);
			}, false);
			debugDivinizationAttribute(source, offHand, player, Component.translatable("command.nebula_tinker.hand.off").getString());
		}

		return 1;
	}

	private static void debugDemonizationAttribute(CommandSourceStack source, ItemStack stack, Player player, String hand) {
		Demonization.AttributePack attributes = Demonization.getOrGenerateAttributes(stack, player);

		source.sendSuccess(() -> {
			return Component.translatable("command.nebula_tinker.debug.demonization_attr", hand)
					.withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
		}, false);

		if (attributes.positive().isEmpty()) {
			source.sendSuccess(() -> {
				return Component.translatable("command.nebula_tinker.debug.no_positive")
						.withStyle(ChatFormatting.GRAY);
			}, false);
		} else {
			source.sendSuccess(() -> {
				return Component.translatable("command.nebula_tinker.debug.positive")
						.withStyle(ChatFormatting.GREEN);
			}, false);

			for (Demonization.AttributeEntry entry : attributes.positive()) {
				String info = String.format(
						Locale.ROOT,
						"    %s: %.2f (%s: %s)",
						entry.type().name(),
						entry.value(),
						Component.translatable("command.nebula_tinker.debug.slot").getString(),
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
				return Component.translatable("command.nebula_tinker.debug.no_negative")
						.withStyle(ChatFormatting.GRAY);
			}, false);
		} else {
			source.sendSuccess(() -> {
				return Component.translatable("command.nebula_tinker.debug.negative")
						.withStyle(ChatFormatting.RED);
			}, false);

			for (Demonization.AttributeEntry entry : attributes.negative()) {
				String info = String.format(
						Locale.ROOT,
						"    %s: %.2f (%s: %s)",
						entry.type().name(),
						entry.value(),
						Component.translatable("command.nebula_tinker.debug.slot").getString(),
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
			return Component.translatable("command.nebula_tinker.debug.divinization_attr", hand)
					.withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD);
		}, false);

		if (attributes.isEmpty()) {
			source.sendSuccess(() -> {
				return Component.translatable("command.nebula_tinker.debug.no_attributes")
						.withStyle(ChatFormatting.GRAY);
			}, false);
		} else {
			for (Divinization.AttributeEntry entry : attributes) {
				String info = String.format(
						Locale.ROOT,
						"  %s: %.2f (%s: %s)",
						entry.type().name(),
						entry.value(),
						Component.translatable("command.nebula_tinker.debug.slot").getString(),
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
			source.sendFailure(Component.translatable("command.nebula_tinker.player_only"));
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
			source.sendFailure(Component.translatable(
					modifierName.equals("demonization") ? "command.nebula_tinker.no_demonization" : "command.nebula_tinker.no_divinization"
			));
			return 0;
		}

		String mainHandStr = Component.translatable("command.nebula_tinker.hand.main").getString();
		String offHandStr = Component.translatable("command.nebula_tinker.hand.off").getString();

		if (modifierName.equals("demonization")) {
			if (hasModifierMain) {
				debugDemonizationAttribute(source, mainHand, player, mainHandStr);
			}
			if (hasModifierOff) {
				debugDemonizationAttribute(source, offHand, player, offHandStr);
			}
		} else {
			if (hasModifierMain) {
				debugDivinizationAttribute(source, mainHand, player, mainHandStr);
			}
			if (hasModifierOff) {
				debugDivinizationAttribute(source, offHand, player, offHandStr);
			}
		}

		return 1;
	}

	private static int checkHands(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		Player player = source.getPlayer();

		if (player == null) {
			source.sendFailure(Component.translatable("command.nebula_tinker.player_only"));
			return 0;
		}

		ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);

		source.sendSuccess(() -> {
			return Component.translatable("command.nebula_tinker.check_hands.title")
					.withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
		}, false);

		String emptyStr = Component.translatable("command.nebula_tinker.check_hands.empty").getString();
		String hasItemStr = Component.translatable("command.nebula_tinker.check_hands.has_item").getString();

		source.sendSuccess(() -> {
			return Component.translatable("command.nebula_tinker.check_hands.main",
					mainHand.getDisplayName().getString(),
					mainHand.isEmpty() ? emptyStr : hasItemStr);
		}, false);

		source.sendSuccess(() -> {
			return Component.translatable("command.nebula_tinker.check_hands.off",
					offHand.getDisplayName().getString(),
					offHand.isEmpty() ? emptyStr : hasItemStr);
		}, false);

		String mainHandStr = Component.translatable("command.nebula_tinker.hand.main").getString();
		String offHandStr = Component.translatable("command.nebula_tinker.hand.off").getString();
		String demonizationStr = Component.translatable("command.nebula_tinker.modifier.demonization").getString();
		String divinizationStr = Component.translatable("command.nebula_tinker.modifier.divinization").getString();

		checkModifierInHand(source, mainHand, mainHandStr, "demonization", demonizationStr);
		checkModifierInHand(source, mainHand, mainHandStr, "divinization", divinizationStr);
		checkModifierInHand(source, offHand, offHandStr, "demonization", demonizationStr);
		checkModifierInHand(source, offHand, offHandStr, "divinization", divinizationStr);

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
			int level = SimpleTConUtils.getModifierLevel(stack, NebulaTinker.loadResource(modifierId));

			source.sendSuccess(() -> {
				return Component.translatable("command.nebula_tinker.check_hands.has_modifier", hand, modifierName, level)
						.withStyle(modifierId.equals("demonization") ? ChatFormatting.DARK_RED : ChatFormatting.YELLOW);
			}, false);
		}
	}
}