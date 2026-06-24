package com.bsipe.lotrkingsaddon.modules;

import com.bsipe.lotrkingsaddon.command.LOTRAddonOverwriteNPCTrades;
import com.bsipe.lotrkingsaddon.entities.LOTRAddonEntityRangerNorth;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lotr.common.entity.LOTREntities;
import net.minecraftforge.common.config.Configuration;

public class NPCModificationModule extends AbstractModule {

    private static final String CONFIG_CATAGORY = "npc_modifications";

    public static boolean NPC_CHANGES_ENABLED;
    public static boolean REMOVE_RANGER_HIDING;
    public static boolean ADD_ARMORER_COMMAND;
    public NPCModificationModule(Configuration config, boolean serverOnly ) {
        NPC_CHANGES_ENABLED = !serverOnly && config.getBoolean( "enabled", CONFIG_CATAGORY, true, "Modify some NPC behavior and abilities" );
        REMOVE_RANGER_HIDING = false && !serverOnly && config.getBoolean( "remove_ranger_hiding", CONFIG_CATAGORY, false, "Remove rangers ability to hide ( WIP )" );
        ADD_ARMORER_COMMAND = config.getBoolean( "add_royal_armorer_command", CONFIG_CATAGORY, true, "Add command to replace trades of an NPC trader with custom royal armorer trades." );
    }

    @Override
    public void preInit( FMLPreInitializationEvent event ) {
        if ( ! NPC_CHANGES_ENABLED ) return;
        if ( REMOVE_RANGER_HIDING ) {
            LOTREntities.registerCreature(LOTRAddonEntityRangerNorth.class, "RangerNorth", 47, 1, 1);
        }
    }

    public void init( FMLInitializationEvent event ) {
        if ( ! NPC_CHANGES_ENABLED ) return;
    }

    public void postInit(FMLPostInitializationEvent event) {
        if ( ! NPC_CHANGES_ENABLED ) return;
    }

    public void onServerStarting( FMLServerStartingEvent event ) {
        if ( ADD_ARMORER_COMMAND ) {
            event.registerServerCommand( new LOTRAddonOverwriteNPCTrades() );
        }

    }
}
