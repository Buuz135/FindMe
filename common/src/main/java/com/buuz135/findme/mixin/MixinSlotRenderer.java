package com.buuz135.findme.mixin;

import com.buuz135.findme.FindMeMod;
import com.buuz135.findme.tracking.TrackingList;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(AbstractContainerScreen.class)
public class MixinSlotRenderer {

    @Inject(at = @At("HEAD"), method = "extractSlot")
    private void renderSlot(GuiGraphicsExtractor guiGraphics, Slot slot, int mouseX, int mouseY, CallbackInfo info) {
        if (FindMeMod.CONFIG.CLIENT.CONTAINER_TRACKING && slot.hasItem()) {
            if (TrackingList.beingTracked(slot.getItem())) {
                Color c = FindMeMod.CONFIG.CLIENT.getColor();
                guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, c.getRGB());
            }
        }
    }
}
