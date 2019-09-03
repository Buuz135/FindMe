package com.buuz135.findme;

import com.buuz135.findme.proxy.ClientProxy;
import com.buuz135.findme.proxy.CommonProxy;
import com.buuz135.findme.proxy.FindMeConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import static com.buuz135.findme.FindMe.MOD_ID;

@Mod(MOD_ID)
public class FindMe {

    public static final String MOD_ID = "findme";

    public static SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "network"), () -> {
        return "1.0";
    }, (s) -> {
        return true;
    }, (s) -> {
        return true;
    });

    public static CommonProxy proxy;

    public FindMe() {
        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        proxy.init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FindMeConfig.BUILDER.build());
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

}
