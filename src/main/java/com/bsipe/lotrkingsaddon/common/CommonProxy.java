package com.bsipe.lotrkingsaddon.common;

import java.util.ArrayList;
import java.util.List;

import com.bsipe.lotrkingsaddon.Config;
import com.bsipe.lotrkingsaddon.common.modules.*;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public static final boolean SERVER_ONLY = false;
    public List<AbstractModule> modules = new ArrayList<>();

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile(), true );

        // if there are already modules on the list, then we don't need to add serverClientModules here.
        boolean includeServerClientModules = modules.size() == 0;


        Config.ModuleLevelConfig configuredModules = Config.getModuleLevelConfig();
        if (configuredModules.perPlayerMobCapModule()) modules.add(new PerPlayerMobCapModule(SERVER_ONLY));
        if (configuredModules.craftingRecipeModule()) modules.add(new CraftingRecipeModule(SERVER_ONLY));
        if ( configuredModules.waypointsModule() && ! SERVER_ONLY ) modules.add(new WaypointsModule(SERVER_ONLY));
        if ( configuredModules.npcModificationsModule() ) modules.add(new NPCModificationsModule(SERVER_ONLY));
        // SERVER_CLIENT_MODULES
        if ( configuredModules.moreMoneyModule() && includeServerClientModules) modules.add( new MoreMoneyModule( SERVER_ONLY ));

        modules.forEach(module -> module.preInit(event));
        // MyMod.LOG.info("I am MyMod at version " + Tags.VERSION);
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        modules.forEach(module -> module.init(event));
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        modules.forEach(module -> module.postInit(event));
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        modules.forEach(module -> module.onServerStarting(event));
    }
}
