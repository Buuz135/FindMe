package com.buuz135.findme.proxy.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.TexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

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
        return 1;
    }

    @Override
    protected float getMinV() {
        return 0;
    }

    @Override
    protected float getMaxV() {
        return 1;
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
    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        Minecraft.getInstance().textureManager.bindTexture(new ResourceLocation("textures/particle/glitter_7.png"));
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.draw();

        GlStateManager.disableDepthTest();
        GlStateManager.color4f(1f, 1f, 1f, 0.5f);
        buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        tessellator.draw();

        GlStateManager.enableDepthTest();

        buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }

}
