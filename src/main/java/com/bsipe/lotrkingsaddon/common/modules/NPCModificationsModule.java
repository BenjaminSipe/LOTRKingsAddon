package com.bsipe.lotrkingsaddon.common.modules;

import com.bsipe.lotrkingsaddon.Config;
import com.bsipe.lotrkingsaddon.common.commands.LOTRAddonOverwriteNPCTrades;
import com.bsipe.lotrkingsaddon.common.entities.LOTRAddonEntityRangerNorth;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import lotr.common.entity.LOTREntities;

public class NPCModificationsModule extends AbstractModule {

    public static boolean SERVER_ONLY;

    Config.NPCModificationsModuleConfig config;

    public NPCModificationsModule(boolean serverOnly) {
        SERVER_ONLY = serverOnly;
        config = Config.getNpcModificationsModuleConfig();
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (config.removeRangerHiding()) {
            LOTREntities.registerCreature(LOTRAddonEntityRangerNorth.class, "RangerNorth", 47, 1, 1);
        }
    }

    public void onServerStarting(FMLServerStartingEvent event) {
        if (config.addArmorerCommand()) {
            event.registerServerCommand(new LOTRAddonOverwriteNPCTrades());
        }

    }
}
