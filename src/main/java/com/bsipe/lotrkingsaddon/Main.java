package com.bsipe.lotrkingsaddon;

import com.bsipe.lotrkingsaddon.command.LOTRAddonOverwriteNPCTrades;
import com.bsipe.lotrkingsaddon.entities.LOTRAddonBlockAlloyForge;
import com.bsipe.lotrkingsaddon.entities.LOTRAddonTileEntityAlloyForge;
import com.bsipe.lotrkingsaddon.modules.*;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.ExistingSubstitutionException;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.config.Configuration;

import java.util.*;

@Mod(modid = Main.MODID, name= Main.NAME, version = Main.VERSION )//, acceptableRemoteVersions="*")
public class Main
{

    public String getModid() {
        return "bensfunkymod";
    }
    public String modid() {
        return "bensgroovymod";
    }

    public static Configuration config;

    public List<AbstractModule> modules = new ArrayList<>();

    public static boolean lotr;

    public static final String MODID = "lotrkingsaddon";
    public static final String VERSION = "1.4.2";
    public static final String NAME = "LOTR Kings Addon";

    public static final boolean SERVER_ONLY = false;

    public void setupAndLoadConfig(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        modules.add( new PerPlayerMobCapModule( config, SERVER_ONLY ) );
        modules.add( new MoreMoneyModule( config, SERVER_ONLY ) );
        modules.add( new CraftingRecipeModule( config, SERVER_ONLY ) );
        modules.add( new ToolsAndWeaponsModule( config, SERVER_ONLY ) );
        modules.add( new WaypointsModule( config, SERVER_ONLY ) );
        modules.add( new NPCModificationModule( config, SERVER_ONLY ) );

        if (config.hasChanged()) {
            config.save();
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event ) {
        lotr = Loader.isModLoaded("lotr");
        if ( !lotr ) return;
        setupAndLoadConfig( event );

        modules.forEach( module -> module.preInit( event ) );


    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if ( !lotr ) return;

        modules.forEach( module -> module.init( event ) );


    }

    @EventHandler
    public void postInit( FMLPostInitializationEvent event )
    {
        if ( !lotr ) return;
        modules.forEach( module -> module.postInit( event ) );
    }

    @EventHandler
    public void onServerStarting( FMLServerStartingEvent event ) {
        if ( !lotr ) return;
        modules.forEach( module -> module.onServerStarting( event ) );
    }
}

