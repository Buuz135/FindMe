package com.buuz135.findme.proxy.client;

import net.minecraft.client.particle.ParticleCloud;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ParticlePosition extends ParticleCloud {

    public ParticlePosition(World worldIn, double posXIn, double posYIn, double posZIn) {
        super(worldIn, posXIn, posYIn, posZIn, 0, 0, 0);
        this.motionX *= 0.25;
        this.motionY *= 0.25;
        this.motionZ *= 0.25;
        this.particleMaxAge = 20 * 5;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.draw();

        GlStateManager.disableDepth();
        GlStateManager.color(1f, 1f, 1f, 0.5f);
        buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        tessellator.draw();

        GlStateManager.enableDepth();

        buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }
}
