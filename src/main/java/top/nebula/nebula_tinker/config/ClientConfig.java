package top.nebula.nebula_tinker.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
	private static final ForgeConfigSpec.Builder BUILDER;

	static {
		BUILDER = new ForgeConfigSpec.Builder();

		BUILDER.comment("Here is the client config file for Nebula Tinker")
				.push("general");
	}

	public static final ForgeConfigSpec SPEC = BUILDER.build();
}