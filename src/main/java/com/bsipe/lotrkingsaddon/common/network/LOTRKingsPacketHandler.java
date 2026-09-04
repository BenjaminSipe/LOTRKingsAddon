package com.bsipe.lotrkingsaddon.common.network;

import com.bsipe.lotrkingsaddon.common.network.packets.LOTRAddonOpenGuiPacket;
import com.bsipe.lotrkingsaddon.common.network.packets.LOTRKingsConvertAllCoinPacket;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class LOTRKingsPacketHandler {

    public static final SimpleNetworkWrapper networkWrapper;

    public LOTRKingsPacketHandler() {
        networkWrapper.registerMessage(
            LOTRKingsConvertAllCoinPacket.Handler.class,
            LOTRKingsConvertAllCoinPacket.class,
            1,
            Side.SERVER);
        networkWrapper.registerMessage(
            LOTRAddonOpenGuiPacket.Handler.class,
            LOTRAddonOpenGuiPacket.class,
            2,
            Side.CLIENT);

    }

    static {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("lotrkings_");
    }
}
