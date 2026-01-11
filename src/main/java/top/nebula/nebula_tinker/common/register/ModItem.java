package top.nebula.nebula_tinker.common.register;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.common.item.DemonizationStoneItem;
import top.nebula.nebula_tinker.common.item.DivinizationStoneItem;

import java.util.function.Supplier;

public class ModItem {
	public static final DeferredRegister<Item> ITEMS;
	public static final Supplier<Item> DEMONIZATION_STONE;
	public static final Supplier<Item> DIVINIZATION_STONE;

	static {
		ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NebulaTinker.MODID);

		DEMONIZATION_STONE = ITEMS.register("demonization_stone", () -> {
			return new DemonizationStoneItem(new Item.Properties());
		});
		DIVINIZATION_STONE = ITEMS.register("divinization_stone", () -> {
			return new DivinizationStoneItem(new Item.Properties());
		});
	}
}