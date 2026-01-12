package top.nebula.nebula_tinker.common.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import top.nebula.nebula_tinker.NebulaTinker;

public class ModBlockTags {
	public static TagKey<Block> createTag(String namespace, String name) {
		return BlockTags.create(ResourceLocation.fromNamespaceAndPath(namespace, name));
	}

	public static TagKey<Block> createTag(String name) {
		return BlockTags.create(NebulaTinker.loadResource(name));
	}
}