package com.bsipe.lotrkingsaddon;

import com.bsipe.lotrkingsaddon.modules.PerPlayerMobCap;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRConfig;
import lotr.common.LOTRDimension;
import lotr.common.LOTRMod;
import lotr.common.enchant.LOTREnchantment;
import lotr.common.entity.npc.LOTREntityNPC;
import lotr.common.item.LOTRItemModifierTemplate;
import lotr.common.world.LOTRWorldChunkManager;
import lotr.common.world.LOTRWorldProvider;
import lotr.common.world.biome.LOTRBiome;
import lotr.common.world.biome.variant.LOTRBiomeVariant;
import lotr.common.world.spawning.LOTRBiomeSpawnList;
import lotr.common.world.spawning.LOTRSpawnEntry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.*;

import static lotr.common.LOTRSpawnDamping.getSpawnCap;
import static lotr.common.world.spawning.LOTRSpawnerNPCs.getRandomSpawningPointInChunk;
import static lotr.common.world.spawning.LOTRSpawnerNPCs.shuffle;

@Mod(modid = Main.MODID, name= Main.NAME, version = Main.VERSION, acceptableRemoteVersions="*")
public class Main
{

    public static Configuration config;

    public PerPlayerMobCap perPlayerMobCapModule;

    public static boolean lotr;

    public static final String MODID = "lotrkingsaddon";
    public static final String VERSION = "1.0";
    public static final String NAME = "LOTR Kings Addon";

    public void setupAndLoadConfig(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());

        perPlayerMobCapModule = new PerPlayerMobCap( config );

        if (config.hasChanged()) {
            config.save();
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event ) {
        lotr = Loader.isModLoaded("lotr");
        if ( !lotr ) return;
        setupAndLoadConfig( event );

        perPlayerMobCapModule.preInit( event );
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if ( !lotr ) return;

        addCraftingRecipe();

        perPlayerMobCapModule.init( event );

    }

    @EventHandler
    public void postInit( FMLPostInitializationEvent event )
    {
        perPlayerMobCapModule.postInit( event );
    }

    public static void addCraftingRecipe() {
        GameRegistry.addRecipe(new IRecipe() {

            @Override
            public boolean matches(InventoryCrafting inventory, World world) {
                int numberOfEnduringScrolls = 0;
                for ( int i = 0; i < inventory.getSizeInventory() ; i ++ ) {
                    if ( inventory.getStackInSlot( i ) != null && inventory.getStackInSlot( i ).getItem().equals( LOTRMod.modTemplate ) ) {
                        if ( LOTRItemModifierTemplate.getModifier( inventory.getStackInSlot( i ) ).equals( LOTREnchantment.durable3 ) ) {
                            numberOfEnduringScrolls ++;
                        } else {
                            return false;
                        }
                    }
                }

                return numberOfEnduringScrolls == 3;
            }

            @Override
            public ItemStack getCraftingResult(InventoryCrafting inventory) {
                ItemStack stack = new ItemStack( Items.enchanted_book );
                Items.enchanted_book.addEnchantment( stack, new EnchantmentData( Enchantment.unbreaking, 3 ) );

                return stack;
            }

            @Override
            public int getRecipeSize() {
                return 3;
            }

            @Override
            public ItemStack getRecipeOutput() {
                ItemStack stack = new ItemStack( Items.enchanted_book );
                Items.enchanted_book.addEnchantment( stack, new EnchantmentData( Enchantment.unbreaking, 3 ) );
                return stack;
            }
        } );
    }

}

