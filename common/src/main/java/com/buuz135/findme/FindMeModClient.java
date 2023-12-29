package com.buuz135.findme;

import com.buuz135.findme.client.ClientTickHandler;
import com.buuz135.findme.client.ParticlePosition;
import com.buuz135.findme.network.PositionRequestMessage;
import com.buuz135.findme.network.PullItemRequestMessage;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

import static net.minecraft.client.particle.ParticleEngine.RENDER_ORDER;

public class FindMeModClient {


    public static KeyMapping KEY = new KeyMapping("key.findme.search", InputConstants.getKey("key.keyboard.y").getValue(), "key.findme.category");
    public static KeyMapping PULL_ONE = new KeyMapping("key.findme.pull_one", InputConstants.getKey("key.keyboard.keypad.0").getValue(), "key.findme.category");
    public static KeyMapping PULL_STACK = new KeyMapping("key.findme.pull_stack", InputConstants.getKey("key.keyboard.keypad.1").getValue(), "key.findme.category");



    public static long lastTooltipTime = 0;
    public static ItemStack lastRenderedStack = ItemStack.EMPTY;

    public FindMeModClient() {
        init();
    }

    private static void init() {
        KeyMappingRegistry.register(KEY);
        KeyMappingRegistry.register(PULL_ONE);
        KeyMappingRegistry.register(PULL_STACK);
        ClientTickEvent.CLIENT_PRE.register(instance -> ClientTickHandler.clientTick());
        ClientTooltipEvent.ITEM.register((stack, lines, flag) -> {
            if (!stack.isEmpty() && Minecraft.getInstance().level != null) {
                lastRenderedStack = stack;
                lastTooltipTime = Minecraft.getInstance().level.getGameTime();
            }
        });
        ClientRawInputEvent.KEY_PRESSED.register((client, keyCode, scanCode, action, modifiers) -> {
            if (!lastRenderedStack.isEmpty() && client.level != null && client.level.getGameTime() - lastTooltipTime < 3) {
                if (KEY.matches(keyCode, scanCode))
                    FindMeMod.CHANNEL.sendToServer(new PositionRequestMessage(lastRenderedStack));
                if (PULL_ONE.matches(keyCode, scanCode) && action == 1)
                    FindMeMod.CHANNEL.sendToServer(new PullItemRequestMessage(lastRenderedStack, 1));
                if (PULL_STACK.matches(keyCode, scanCode) && action == 1)
                    FindMeMod.CHANNEL.sendToServer(new PullItemRequestMessage(lastRenderedStack, lastRenderedStack.getMaxStackSize()));
            }
            return EventResult.pass();
        });
        if (!RENDER_ORDER.contains(ParticlePosition.CUSTOM)) {
            RENDER_ORDER = new ArrayList<>(RENDER_ORDER);
            RENDER_ORDER.add(ParticlePosition.CUSTOM);
        }
    }

}
