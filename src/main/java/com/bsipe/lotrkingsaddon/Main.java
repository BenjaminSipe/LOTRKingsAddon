package com.bsipe.lotrkingsaddon;

import com.bsipe.lotrkingsaddon.modules.*;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import lotr.common.LOTRConfig;
import lotr.common.LOTRMod;
import lotr.common.enchant.LOTREnchantment;
import lotr.common.item.LOTRItemModifierTemplate;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import java.util.*;

import static lotr.common.LOTRSpawnDamping.getSpawnCap;
import static lotr.common.world.spawning.LOTRSpawnerNPCs.getRandomSpawningPointInChunk;

@Mod(modid = Main.MODID, name= Main.NAME, version = Main.VERSION, acceptableRemoteVersions="*")
public class Main
{

    public static Configuration config;

    public List<AbstractModule> modules = new ArrayList<>();

    public static boolean lotr;

    public static final String MODID = "lotrkingsaddon";
    public static final String VERSION = "1.0";
    public static final String NAME = "LOTR Kings Addon";

    public void setupAndLoadConfig(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        modules.add( new PerPlayerMobCapModule( config ) );
        modules.add( new MoreMoneyModule( config ) );
        modules.add( new CraftingRecipeModule( config ) );
        modules.add( new LoreWeaponsModule( config ) );

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
        LOTRConfig.enchantingVanilla = true;
        // leave it as false in config. . . but set it true afterward to allow anvil use.
    }
}

