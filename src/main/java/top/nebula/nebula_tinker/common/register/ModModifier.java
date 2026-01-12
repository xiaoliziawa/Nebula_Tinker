package top.nebula.nebula_tinker.common.register;

import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.common.modifier.*;

/**
 * 由于暂时没空写Json, 因此在这里写一下一些注意事项, 到时候写Json时避免忘记
 * 每个强化的暴击效果不可叠加, 例如施虐者和迅捷之刃不能叠加
 */
public class ModModifier {
	public static final ModifierDeferredRegister MODIFIERS;
	public static final StaticModifier<Acupoint> ACUPOINT;
	public static final StaticModifier<Frenzy> FRENZY;
	public static final StaticModifier<CausalTruncation> CAUSAL_TRUNCATION;
	public static final StaticModifier<Abuser> ABUSER;
	public static final StaticModifier<Divinization> DIVINIZATION;
	public static final StaticModifier<Demonization> DEMONIZATION;
	public static final StaticModifier<SwiftBlade> SWIFT_BLADE;
	public static final StaticModifier<DeathEcho> DEATH_ECHO;
	public static final StaticModifier<CaptureKing> CAPTURE_KING;

	static {
		MODIFIERS = ModifierDeferredRegister.create(NebulaTinker.MODID);

		ACUPOINT = MODIFIERS.register("acupoint", Acupoint::new);
		FRENZY = MODIFIERS.register("frenzy", Frenzy::new);
		CAUSAL_TRUNCATION = MODIFIERS.register("causal_truncation", CausalTruncation::new);
		ABUSER = MODIFIERS.register("abuser", Abuser::new);
		DIVINIZATION = MODIFIERS.register("divinization", Divinization::new);
		DEMONIZATION = MODIFIERS.register("demonization", Demonization::new);
		SWIFT_BLADE = MODIFIERS.register("swift_blade", SwiftBlade::new);
		DEATH_ECHO = MODIFIERS.register("death_echo", DeathEcho::new);
		CAPTURE_KING = MODIFIERS.register("capture_king", CaptureKing::new);
	}
}