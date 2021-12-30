package com.buuz135.findme.mixin;

import com.buuz135.findme.FindMeMod;
import com.buuz135.findme.tracking.TrackingList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(AbstractContainerScreen.class)
public class MixinSlotRenderer {

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlot(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/inventory/Slot;)V", cancellable = true)
    private void renderSlot(PoseStack matrixStack, Slot slot, CallbackInfo info) {
        if (FindMeMod.DO_TRACKING && slot.hasItem()) {
            if (TrackingList.beingTracked(slot.getItem())) {
                Color c = Color.decode(FindMeMod.TRACKING_COLOR);
                RenderSystem.disableDepthTest();
                GuiComponent.fill(matrixStack, slot.x, slot.y, slot.x + 16, slot.y + 16, c.getRGB());
                RenderSystem.enableDepthTest();
            }
        }
    }
}
