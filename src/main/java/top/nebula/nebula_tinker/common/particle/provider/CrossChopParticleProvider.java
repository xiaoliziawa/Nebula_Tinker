package top.nebula.nebula_tinker.common.particle.provider;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import top.nebula.nebula_tinker.common.particle.CrossChopParticle;

@OnlyIn(Dist.CLIENT)
public class CrossChopParticleProvider implements ParticleProvider<SimpleParticleType> {
	private final SpriteSet sprites;

	public CrossChopParticleProvider(SpriteSet sprites) {
		this.sprites = sprites;
	}

	@Override
	public Particle createParticle(
			@NotNull SimpleParticleType type,
			@NotNull ClientLevel level,
			double x,
			double y,
			double z,
			double rot, // 用 xSpeed 传旋转角
			double unusedY,
			double unusedZ
	) {
		return new CrossChopParticle(
				level,
				x,
				y,
				z,
				(float) rot,
				this.sprites
		);
	}
}