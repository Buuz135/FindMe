package com.buuz135.findme.proxy;

import com.buuz135.findme.FindMe;
import com.buuz135.findme.jei.JEIPlugin;
import com.buuz135.findme.network.PositionRequestMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {

    public static KeyBinding KEY = new KeyBinding("key.findme.search", Keyboard.KEY_Y, "key.findme.category");

    @Override
    public void preinit(FMLPreInitializationEvent event) {
        super.preinit(event);
        ClientRegistry.registerKeyBinding(KEY);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void keyPress(GuiScreenEvent.KeyboardInputEvent.Post event) {
        if (Keyboard.getEventKeyState() && KEY.isActiveAndMatches(Keyboard.getEventKey()) && Minecraft.getMinecraft().currentScreen != null) {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if (screen instanceof GuiContainer) {
                Object o = JEIPlugin.runtime.getIngredientListOverlay().getIngredientUnderMouse();
                if (o != null) {
                    if (o instanceof ItemStack) {
                        FindMe.NETWORK.sendToServer(new PositionRequestMessage((ItemStack) o));
                    }
                } else {
                    Slot slot = ((GuiContainer) screen).getSlotUnderMouse();
                    if (slot != null) {
                        ItemStack stack = slot.getStack();
                        if (!stack.isEmpty()) {
                            FindMe.NETWORK.sendToServer(new PositionRequestMessage(stack));
                        }
                    }
                }
            }
        }
    }
}
