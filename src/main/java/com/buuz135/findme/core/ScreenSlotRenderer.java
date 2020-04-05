package com.buuz135.findme.core;

import com.buuz135.findme.proxy.FindMeConfig;
import com.buuz135.findme.tracking.TrackingList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ScreenSlotRenderer {
  public static void drawSlot (ContainerScreen<?> container, Slot slot) {
    if (!FindMeConfig.CLIENT.doTracking()) {
      return;
    }

    ItemStack stack = slot.getStack();
    if (stack.isEmpty()) {
      return;
    }

    if (TrackingList.beingTracked(stack)) {
      Color c = FindMeConfig.CLIENT.getColor();
      GlStateManager.disableDepthTest();
      AbstractGui.fill(slot.xPos, slot.yPos, slot.xPos + 16, slot.yPos + 16, c.getRGB());
      GlStateManager.enableDepthTest();
    }
  }
}
