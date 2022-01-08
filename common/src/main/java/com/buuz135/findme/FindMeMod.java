package com.buuz135.findme;

import com.buuz135.findme.network.PositionRequestMessage;
import com.buuz135.findme.network.PositionResponseMessage;
import com.buuz135.findme.particle.CustomParticleType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;


public class FindMeMod {

    public static final String MOD_ID = "findme";

    public static NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation(MOD_ID, "default"));

    public static FindMeConfig CONFIG = new FindMeConfig();

    public static List<BiPredicate<BlockEntity, ItemStack>> BLOCK_CHECKERS = new ArrayList<>();
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(FindMeMod.MOD_ID, Registry.PARTICLE_TYPE_REGISTRY);
    public static RegistrySupplier<ParticleType<?>> FINDME = PARTICLES.register("particle", () -> new CustomParticleType(false));

    public static void init() {
        PARTICLES.register();
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
        File file = new File(Platform.getConfigFolder() + File.separator + MOD_ID + ".json");
        if (!file.exists()) {
            createConfig(file);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileReader reader = new FileReader(file);
            CONFIG = gson.fromJson(reader, FindMeConfig.class);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            createConfig(file);
        }
    }

    private static void createConfig(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter fileWriter = new FileWriter(file);
            gson.toJson(CONFIG, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
