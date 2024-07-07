package com.buuz135.findme.forge;

import com.buuz135.findme.FindMeMod;
import com.buuz135.findme.FindMeModClient;
import com.buuz135.findme.network.PositionRequestMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.Optional;


@Mod(FindMeMod.MOD_ID)
public class FindMeModForge {

    public FindMeModForge() {
        // Submit our event bus to let architectury register our content on the right time
        //EventBusesHooks.registerModEventBus(FindMeMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        //FMLJavaModLoadingContext.get().getModEventBus().register(this);
        FindMeMod.init();
        FindMeMod.BLOCK_CHECKERS.add((blockEntity, itemStack) -> Optional.ofNullable(blockEntity.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null)).map(handler -> {
            for (int i = 0; i < handler.getSlots(); i++) {
                if (!handler.getStackInSlot(i).isEmpty() && PositionRequestMessage.compareItems(itemStack, handler.getStackInSlot(i))) {
                    return true;
                }
            }
            return false;
        }).orElse(false));
        FindMeMod.BLOCK_EXTRACTORS.add((entity, stack, amount, player) -> {
            if (!canBlockBeInteracted(entity.getLevel(), entity.getBlockPos(), player)) {
                return 0;
            }
            return Optional.ofNullable(entity.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, entity.getBlockPos(), null)).map(handler -> {
                var extractedAmount = 0;
                for (int i = 0; i < handler.getSlots(); i++) {
                    if (!handler.getStackInSlot(i).isEmpty() && PositionRequestMessage.compareItems(stack, handler.getStackInSlot(i))) {
                        var extracted = handler.extractItem(i, amount - extractedAmount, false);
                        ItemHandlerHelper.giveItemToPlayer(player, extracted);
                        extractedAmount += extracted.getCount();
                    }
                    if (extractedAmount >= amount) {
                        break;
                    }
                }
                return extractedAmount;
            }).orElse(0);
        });
        if (FMLEnvironment.dist.isClient()) {
            new FindMeModClient();
        }
    }

    public static boolean canBlockBeInteracted(Level world, BlockPos pos, Player player) {
        var event = new PlayerInteractEvent.RightClickBlock(player, InteractionHand.MAIN_HAND, pos, new BlockHitResult(new Vec3(0, 0, 0), Direction.UP, pos, false));
        NeoForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }

}
