package top.nebula.nebula_tinker.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
	private static final ForgeConfigSpec.Builder BUILDER;

	static {
		BUILDER = new ForgeConfigSpec.Builder();
	}

	public static final ForgeConfigSpec SPEC = BUILDER.build();
}