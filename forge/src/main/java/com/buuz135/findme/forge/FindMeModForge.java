package com.buuz135.findme.forge;

import com.buuz135.findme.FindMeMod;
import com.buuz135.findme.FindMeModClient;
import com.buuz135.findme.network.PositionRequestMessage;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;


@Mod(FindMeMod.MOD_ID)
public class FindMeModForge {

    public FindMeModForge() {
        // Submit our event bus to let architectury register our content on the right time
        //EventBusesHooks.registerModEventBus(FindMeMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        //FMLJavaModLoadingContext.get().getModEventBus().register(this);
        FindMeMod.init();
        FindMeMod.BLOCK_CHECKERS.add((blockEntity, itemStack) -> {
            var handler = blockEntity.getLevel().getCapability(Capabilities.Item.BLOCK, blockEntity.getBlockPos(), null);
            if (handler == null) {
                return false;
            }
            for (int i = 0; i < handler.size(); i++) {
                ItemResource resource = handler.getResource(i);
                if (!resource.isEmpty() && PositionRequestMessage.compareItems(itemStack, resource.toStack())) {
                    return true;
                }
            }
            return false;
        });
        FindMeMod.BLOCK_EXTRACTORS.add((entity, stack, amount, player) -> {
            if (!canBlockBeInteracted(entity.getLevel(), entity.getBlockPos(), player)) {
                return 0;
            }
            var handler = entity.getLevel().getCapability(Capabilities.Item.BLOCK, entity.getBlockPos(), null);
            if (handler == null) {
                return 0;
            }
            var extractedAmount = 0;
            for (int i = 0; i < handler.size(); i++) {
                ItemResource resource = handler.getResource(i);
                if (!resource.isEmpty() && PositionRequestMessage.compareItems(stack, resource.toStack())) {
                    try (Transaction transaction = Transaction.openRoot()) {
                        int extracted = handler.extract(i, resource, amount - extractedAmount, transaction);
                        if (extracted > 0) {
                            transaction.commit();
                            player.getInventory().placeItemBackInInventory(resource.toStack(extracted));
                            extractedAmount += extracted;
                        }
                    }
                }
                if (extractedAmount >= amount) {
                    break;
                }
            }
            return extractedAmount;
        });
        if (Platform.getEnvironment() == Env.CLIENT) {
            new FindMeModClient();
        }
    }

    public static boolean canBlockBeInteracted(Level world, BlockPos pos, Player player) {
        var event = new PlayerInteractEvent.RightClickBlock(player, InteractionHand.MAIN_HAND, pos, new BlockHitResult(new Vec3(0, 0, 0), Direction.UP, pos, false));
        NeoForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }

}
