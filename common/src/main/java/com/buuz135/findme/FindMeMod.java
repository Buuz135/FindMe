package com.buuz135.findme;

import com.buuz135.findme.client.ClientTickHandler;
import com.buuz135.findme.network.PositionRequestMessage;
import com.buuz135.findme.network.PositionResponseMessage;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class FindMeMod {

    public static final String MOD_ID = "findme";

    public static NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation(MOD_ID, "default"));

    public static boolean DO_TRACKING = true;
    public static String TRACKING_COLOR = "#cf9d15";
    public static int RADIUS_RANGE = 12;
    public static boolean IGNORE_COMPARING_DAMAGE = true;
    public static int TRACKING_TIME = 30 * 20;

    public static List<BiPredicate<BlockEntity, ItemStack>> BLOCK_CHECKERS = new ArrayList<>();

    public static long lastTooltipTime = 0;
    public static ItemStack lastRenderedStack = ItemStack.EMPTY;

    public static KeyMapping KEY = new KeyMapping("key.findme.search", InputConstants.getKey("key.keyboard.y").getValue(), "key.findme.category");

    public static void init() {
        CHANNEL.register(PositionRequestMessage.class,
                PositionRequestMessage::toBytes,
                friendlyByteBuf -> new PositionRequestMessage().fromBytes(friendlyByteBuf),
                PositionRequestMessage::handle
                );
        CHANNEL.register(PositionResponseMessage.class,
                PositionResponseMessage::toBytes,
                friendlyByteBuf -> new PositionResponseMessage().fromBytes(friendlyByteBuf),
                PositionResponseMessage::handle
        );
        BLOCK_CHECKERS.add((blockEntity, itemStack) -> {
            if (blockEntity instanceof Container inventory) {
                if (inventory.isEmpty()) return false;
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    if (!inventory.getItem(i).isEmpty() && PositionRequestMessage.compareItems(itemStack, inventory.getItem(i))) {
                        return true;
                    }
                }
            }
            return false;
        });
        KeyMappingRegistry.register(KEY);
        ClientTickEvent.CLIENT_PRE.register(instance -> ClientTickHandler.clientTick());
        ClientTooltipEvent.ITEM.register((stack, lines, flag) -> {
            if (!stack.isEmpty() && Minecraft.getInstance().level != null){
                lastRenderedStack = stack;
                lastTooltipTime = Minecraft.getInstance().level.getGameTime();
            }
        });
        ClientRawInputEvent.KEY_PRESSED.register((client, keyCode, scanCode, action, modifiers) -> {
            if (keyCode == KEY.getDefaultKey().getValue() && !lastRenderedStack.isEmpty() && client.level.getGameTime() - lastTooltipTime < 3){
                CHANNEL.sendToServer(new PositionRequestMessage(lastRenderedStack));
            }
            return EventResult.pass();
        });
    }

}
