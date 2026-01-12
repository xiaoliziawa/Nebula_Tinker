package top.nebula.nebula_tinker.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import org.jetbrains.annotations.NotNull;

public class CrossChopParticle extends TextureSheetParticle {
	private final SpriteSet sprites;

	public CrossChopParticle(ClientLevel level, double x, double y, double z, float rotation, SpriteSet sprites) {
		super(level, x, y, z, 0, 0, 0);
		this.sprites = sprites;
		this.lifetime = 4;
		this.quadSize = 1.0F;
		this.rCol = 1.0F;
		this.gCol = 1.0F;
		this.bCol = 1.0F;

		this.setSpriteFromAge(sprites);
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime) {
			this.remove();
		} else {
			this.setSpriteFromAge(this.sprites);
		}
	}

	@Override
	public int getLightColor(float tick) {
		return 15728880;
	}

	@Override
	public @NotNull ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_LIT;
	}
}