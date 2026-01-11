package top.nebula.nebula_tinker.common.modifier;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.utils.SimpleTConUtils;

import java.util.List;

@Mod.EventBusSubscriber(modid = NebulaTinker.MODID)
public class ItemTooltipHandler {
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		Player player = event.getEntity();

		if (player == null || stack.isEmpty()) {
			return;
		}

		// 检查是否有魔化或神化修饰符
		boolean hasDemonization = SimpleTConUtils.hasModifier(
				stack,
				NebulaTinker.loadResource("demonization").toString()
		);
		boolean hasDivinization = SimpleTConUtils.hasModifier(
				stack,
				NebulaTinker.loadResource("divinization").toString()
		);

		if (!hasDemonization && !hasDivinization) {
			return;
		}

		// 获取当前工具提示列表
		List<Component> tooltips = event.getToolTip();

		// 在合适的位置插入属性信息
		int insertIndex = findInsertIndex(tooltips);

		// 获取属性工具提示
		List<Component> attributeTooltips = AttributeApplicator.getAttributeTooltips(stack, player);

		// 插入属性信息
		if (!attributeTooltips.isEmpty()) {
			// 添加空行分隔
			if (insertIndex < tooltips.size()) {
				tooltips.add(insertIndex, Component.empty());
				insertIndex++;
			}

			// 添加属性信息
			for (int i = attributeTooltips.size() - 1; i >= 0; i--) {
				tooltips.add(insertIndex, attributeTooltips.get(i));
			}

			// 如果是魔化物品，添加警告提示
			MutableComponent demonizationTranKey = Component.literal("⚠ 魔化物品会带来负面效果")
					.withStyle(ChatFormatting.DARK_RED);

			if (hasDemonization) {
				tooltips.add(insertIndex + attributeTooltips.size(), demonizationTranKey);
			}
		}
	}

	/**
	 * 查找插入位置
	 */
	private static int findInsertIndex(List<Component> tooltips) {
		// 首先尝试在修饰符之后插入
		boolean foundModifiers = false;
		for (int i = 0; i < tooltips.size(); i++) {
			String text = tooltips.get(i).getString();
			if (text.contains("修饰符") || text.contains("Modifier")) {
				foundModifiers = true;
				// 找到修饰符段落后的空行
				for (int j = i + 1; j < tooltips.size(); j++) {
					if (tooltips.get(j).getString().isEmpty()) {
						return j;
					}
				}
				return i + 1;
			}
		}

		// 如果没有找到修饰符，在"按住Shift查看详情"或"按住Ctrl查看详细信息"之后插入
		for (int i = 0; i < tooltips.size(); i++) {
			String text = tooltips.get(i).getString().toLowerCase();
			if (text.contains("shift") || text.contains("ctrl") || text.contains("详细信息")) {
				return i + 1;
			}
		}

		// 如果还没有找到，在倒数第二行插入（最后一行通常是Mod名称）
		return Math.max(0, tooltips.size() - 1);
	}
}