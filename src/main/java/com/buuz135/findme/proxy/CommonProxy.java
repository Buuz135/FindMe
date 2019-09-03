package com.buuz135.findme.proxy;

import com.buuz135.findme.network.PositionRequestMessage;
import com.buuz135.findme.network.PositionResponseMessage;

import static com.buuz135.findme.FindMe.NETWORK;

public class CommonProxy {

    public void init() {
        NETWORK.registerMessage(0, PositionRequestMessage.class, PositionRequestMessage::toBytes, packetBuffer -> new PositionRequestMessage().fromBytes(packetBuffer), PositionRequestMessage::handle);
        NETWORK.registerMessage(1, PositionResponseMessage.class, PositionResponseMessage::toBytes, packetBuffer -> new PositionResponseMessage().fromBytes(packetBuffer), PositionResponseMessage::handle);
    }

}
