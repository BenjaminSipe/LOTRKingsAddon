package com.bsipe.lotrkingsaddon.network;

import com.bsipe.lotrkingsaddon.packets.LOTRKingsConvertAllCoinPacket;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import lotr.common.network.LOTRPacketRestockPouches;

public class LOTRKingsPacketHandler {
    public static final SimpleNetworkWrapper networkWrapper;

    public LOTRKingsPacketHandler() {
        networkWrapper.registerMessage(LOTRKingsConvertAllCoinPacket.Handler.class, LOTRKingsConvertAllCoinPacket.class, 1, Side.SERVER);

    }

    static {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("lotrkings_");
    }
}
