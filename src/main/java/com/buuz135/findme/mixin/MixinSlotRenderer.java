package com.buuz135.findme.mixin;

import com.buuz135.findme.proxy.FindMeConfig;
import com.buuz135.findme.tracking.TrackingList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(ContainerScreen.class)
public class MixinSlotRenderer {

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/gui/screen/inventory/ContainerScreen;moveItems(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/inventory/container/Slot;)V", cancellable = true)
    private void renderSlot(MatrixStack matrixStack, Slot slot, CallbackInfo info) {
        if (FindMeConfig.CLIENT.doTracking() && slot.getHasStack()) {
            if (TrackingList.beingTracked(slot.getStack())) {
                Color c = FindMeConfig.CLIENT.getColor();
                RenderSystem.disableDepthTest();
                AbstractGui.fill(matrixStack, slot.xPos, slot.yPos, slot.xPos + 16, slot.yPos + 16, c.getRGB());
                RenderSystem.enableDepthTest();
            }
        }
    }
}
