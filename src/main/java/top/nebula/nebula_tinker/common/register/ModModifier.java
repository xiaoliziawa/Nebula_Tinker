package top.nebula.nebula_tinker.common.register;

import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.common.modifier.Acupoint;
import top.nebula.nebula_tinker.common.modifier.CausalTruncation;
import top.nebula.nebula_tinker.common.modifier.Frenzy;

public class ModModifier {
	public static final ModifierDeferredRegister MODIFIERS;
	public static final StaticModifier<Acupoint> ACUPOINT;
	public static final StaticModifier<Frenzy> FRENZY;
	public static final StaticModifier<CausalTruncation> CAUSAL_TRUNCATION;

	static {
		MODIFIERS = ModifierDeferredRegister.create(NebulaTinker.MODID);

		ACUPOINT = MODIFIERS.register("acupoint", Acupoint::new);
		FRENZY = MODIFIERS.register("frenzy", Frenzy::new);
		CAUSAL_TRUNCATION = MODIFIERS.register("causal_truncation", CausalTruncation::new);
	}
}