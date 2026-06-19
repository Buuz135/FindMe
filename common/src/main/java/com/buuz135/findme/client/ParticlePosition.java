package com.buuz135.findme.client;

import com.buuz135.findme.FindMeMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;

import java.awt.*;

public class ParticlePosition extends SingleQuadParticle {

    public ParticlePosition(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, TextureAtlasSprite sprite) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D, sprite);
        this.xd *= 0.10000000149011612D;
        this.yd *= 0.10000000149011612D;
        this.zd *= 0.10000000149011612D;
        this.xd += motionX;
        this.yd += motionY;
        this.zd += motionZ;
        float colorOffset = (float) (Math.random() * 0.30000001192092896D);
        Color c = FindMeMod.CONFIG.CLIENT.getParticleColor();
        this.rCol = ((float)c.getRed()) / 255f - colorOffset;
        this.gCol = ((float)c.getGreen()) / 255f - colorOffset;
        this.bCol = ((float)c.getBlue()) / 255f - colorOffset;
        //this.particleScale *= 1.5F;
        this.lifetime = 20 * 5;
        this.hasPhysics = false;
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    public float getQuadSize(float p_217561_1_) {
        return this.quadSize * Mth.clamp(((float) this.age + p_217561_1_) / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    protected float getU0() {
        return 0;
    }

    @Override
    protected float getU1() {
        return 1f;
    }

    @Override
    protected float getV0() {
        return 0;
    }

    @Override
    protected float getV1() {
        return 1f;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    protected int getLightCoords(float f) {
        return 15728880;
    }
}
