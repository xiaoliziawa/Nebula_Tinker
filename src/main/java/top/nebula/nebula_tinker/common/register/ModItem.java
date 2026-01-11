package top.nebula.nebula_tinker.common.register;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.common.item.DemonizationStoneItem;
import top.nebula.nebula_tinker.common.item.DivinizationStoneItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItem {
	public static final DeferredRegister<Item> ITEMS =
			DeferredRegister.create(ForgeRegistries.ITEMS, NebulaTinker.MODID);

	public static final List<Supplier<Item>> CREATIVE_TAB_ITEMS = new ArrayList<>();

	public static final Supplier<Item> DEMONIZATION_STONE;
	public static final Supplier<Item> DIVINIZATION_STONE;

	static {
		DEMONIZATION_STONE = registerItem("demonization_stone", () -> {
			return new DemonizationStoneItem(new Item.Properties());
		});
		DIVINIZATION_STONE = registerItem("divinization_stone", () -> {
			return new DivinizationStoneItem(new Item.Properties());
		});
	}

	private static Supplier<Item> registerItem(String id, Supplier<Item> supplier) {
		Supplier<Item> item = ITEMS.register(id, supplier);
		CREATIVE_TAB_ITEMS.add(item);
		return item;
	}
}