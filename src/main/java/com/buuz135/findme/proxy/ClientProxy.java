package com.buuz135.findme.proxy;

import com.buuz135.findme.FindMe;
import com.buuz135.findme.jei.Plugin;
import com.buuz135.findme.network.PositionRequestMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FindMe.MOD_ID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    public static KeyBinding KEY = new KeyBinding("key.findme.search", InputMappings.getInputByName("key.keyboard.y").getKeyCode(), "key.findme.category");
    private static ItemStack stack = ItemStack.EMPTY;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onTooltip(RenderTooltipEvent.Pre event) {
        stack = event.getStack();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRender(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            stack = ItemStack.EMPTY;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void keyPress(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        if (KEY.getKey().getKeyCode() == event.getKeyCode() && Minecraft.getInstance().currentScreen != null) {
            Screen screen = Minecraft.getInstance().currentScreen;
            if (!stack.isEmpty()) {
                FindMe.NETWORK.sendToServer(new PositionRequestMessage(stack));
            }
            if (screen instanceof ContainerScreen) {
                Object o = Plugin.runtime.getIngredientListOverlay().getIngredientUnderMouse();
                if (o != null) {
                    if (o instanceof ItemStack) {
                        FindMe.NETWORK.sendToServer(new PositionRequestMessage((ItemStack) o));
                    }
                } else {
                    Slot slot = ((ContainerScreen) screen).getSlotUnderMouse();
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

    @Override
    public void init() {
        super.init();
        ClientRegistry.registerKeyBinding(KEY);
    }

}
