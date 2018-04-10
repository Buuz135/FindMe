package com.buuz135.findme.proxy;

import com.buuz135.findme.network.PositionRequestMessage;
import com.buuz135.findme.network.PositionResponseMessage;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import static com.buuz135.findme.FindMe.NETWORK;

public class CommonProxy {


    public void preinit(FMLPreInitializationEvent event) {
        NETWORK.registerMessage(PositionRequestMessage.Handler.class, PositionRequestMessage.class, 0, Side.SERVER);
        NETWORK.registerMessage(PositionResponseMessage.Handler.class, PositionResponseMessage.class, 1, Side.CLIENT);
    }


    public void init(FMLInitializationEvent event) {

    }


    public void postinit(FMLPostInitializationEvent event) {

    }
}
