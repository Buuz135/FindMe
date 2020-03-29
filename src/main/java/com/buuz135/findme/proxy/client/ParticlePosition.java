package com.buuz135.findme.proxy.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.TexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticlePosition extends TexturedParticle {

    public ParticlePosition(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.10000000149011612D;
        this.motionY *= 0.10000000149011612D;
        this.motionZ *= 0.10000000149011612D;
        this.motionX += motionX;
        this.motionY += motionY;
        this.motionZ += motionZ;
        float color = 1.0F - (float) (Math.random() * 0.30000001192092896D);
        this.particleRed = color;
        this.particleGreen = color;
        this.particleBlue = color;
        this.particleScale *= 1.875F;
        this.maxAge = 20 * 5;
        this.canCollide = false;
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public float getScale(float p_217561_1_) {
        return this.particleScale * MathHelper.clamp(((float) this.age + p_217561_1_) / (float) this.maxAge * 32.0F, 0.0F, 1.0F);
    }

    @Override
    protected float getMinU() {
        return 0;
    }

    @Override
    protected float getMaxU() {
        return 0.1f;
    }

    @Override
    protected float getMinV() {
        return 0;
    }

    @Override
    protected float getMaxV() {
        return 0.1f;
    }

    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        }
    }

    @Override
    public void setAlphaF(float alpha) {
        super.setAlphaF(alpha);
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        RenderSystem.disableDepthTest();
        super.renderParticle(buffer, renderInfo, partialTicks);
    }
}
