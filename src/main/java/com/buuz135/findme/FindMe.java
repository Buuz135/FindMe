package com.buuz135.findme;

import com.buuz135.findme.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(
        modid = FindMe.MOD_ID,
        name = FindMe.MOD_NAME,
        version = FindMe.VERSION
)
public class FindMe {

    public static final String MOD_ID = "findme";
    public static final String MOD_NAME = "FindMe";
    public static final String VERSION = "1.0-SNAPSHOT";

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

    @Mod.Instance(MOD_ID)
    public static FindMe INSTANCE;

    @SidedProxy(serverSide = "com.buuz135.findme.proxy.CommonProxy", clientSide = "com.buuz135.findme.proxy.ClientProxy")
    public static CommonProxy proxy;


    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        proxy.preinit(event);
    }


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        proxy.postinit(event);
    }
}
