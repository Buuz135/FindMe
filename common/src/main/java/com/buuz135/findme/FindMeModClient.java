package com.buuz135.findme;

import com.buuz135.findme.client.ClientTickHandler;
import com.buuz135.findme.client.ParticlePosition;
import com.buuz135.findme.network.PositionRequestMessage;
import com.buuz135.findme.network.PullItemRequestMessage;
import com.buuz135.findme.tracking.TrackingList;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FindMeModClient {

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(FindMeMod.MOD_ID, "main"));
    public static KeyMapping KEY = new KeyMapping("key.findme.search", InputConstants.getKey("key.keyboard.y").getValue(), CATEGORY);
    public static KeyMapping PULL_ONE = new KeyMapping("key.findme.pull_one", InputConstants.getKey("key.keyboard.keypad.0").getValue(), CATEGORY);
    public static KeyMapping PULL_STACK = new KeyMapping("key.findme.pull_stack", InputConstants.getKey("key.keyboard.keypad.1").getValue(), CATEGORY);



    public static long lastTooltipTime = 0;
    public static ItemStack lastRenderedStack = ItemStack.EMPTY;

    public FindMeModClient() {
        init();
    }

    private static void init() {

        KeyMappingRegistry.register(KEY);
        KeyMappingRegistry.register(PULL_ONE);
        KeyMappingRegistry.register(PULL_STACK);
        ParticleProviderRegistry.register(FindMeMod.FINDME, spriteSet ->
                (particleOptions, clientLevel, x, y, z, velocityX, velocityY, velocityZ, random) ->
                        new ParticlePosition(clientLevel, x, y, z, velocityX, velocityY, velocityZ, spriteSet.get(random)));
        ClientTickEvent.CLIENT_PRE.register(instance -> ClientTickHandler.clientTick());
        ClientTooltipEvent.ITEM.register((stack, lines, tooltipContext, flag) -> {
            if (!stack.isEmpty() && Minecraft.getInstance().level != null) {
                lastRenderedStack = stack.copyWithCount(1);
                lastTooltipTime = Minecraft.getInstance().level.getGameTime();
            }
        });
        ClientRawInputEvent.KEY_PRESSED.register((client, action, keyEvent) -> {
            if (!lastRenderedStack.isEmpty() && client.level != null && client.level.getGameTime() - lastTooltipTime < 3) {
                if (KEY.matches(keyEvent) && action == 1)
                    NetworkManager.sendToServer(new PositionRequestMessage(lastRenderedStack));
                if (PULL_ONE.matches(keyEvent) && action == 1)
                    NetworkManager.sendToServer(new PullItemRequestMessage(lastRenderedStack, 1));
                if (PULL_STACK.matches(keyEvent) && action == 1)
                    NetworkManager.sendToServer(new PullItemRequestMessage(lastRenderedStack, lastRenderedStack.getMaxStackSize()));
            }
            return EventResult.pass();
        });
    }

    public static void handlePositionResponse(List<BlockPos> positions) {
        if (positions.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        minecraft.player.closeContainer();
        minecraft.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        if (FindMeMod.CONFIG.CLIENT.CONTAINER_TRACKING) {
            TrackingList.beginTracking();
            ClientTickHandler.addRunnable(TrackingList::clear, FindMeMod.CONFIG.CLIENT.CONTAINER_TRACK_TIME);
        }

        for (BlockPos position : positions) {
            addParticle(position);
            addParticle(position);
        }
    }

    private static void addParticle(BlockPos position) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        var level = minecraft.level;
        level.addParticle(FindMeMod.FIND_ME_PARTICLE_TYPE,
                position.getX() + 0.75 - level.getRandom().nextDouble() / 2D,
                position.getY() + 0.75 - level.getRandom().nextDouble() / 2D,
                position.getZ() + 0.75 - level.getRandom().nextDouble() / 2D,
                0, 0, 0);
    }

}
