package com.bsipe.lotrkingsaddon;

import com.bsipe.lotrkingsaddon.modules.CraftingModule;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import lotr.common.LOTRMod;
import lotr.common.enchant.LOTREnchantment;
import lotr.common.item.LOTRItemModifierTemplate;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import java.util.HashMap;
import java.util.Map;

@Mod(modid = Main.MODID, name= Main.NAME, version = Main.VERSION, acceptableRemoteVersions="*")
public class Main
{
    public static Configuration config;
    public static boolean lotr;

    CraftingModule craftingModule;


    public static Item DUMMY_ITEM = Items.wheat_seeds;
    public static Item REPLACEMENT = Items.enchanted_book;

    public static final String MODID = "lotradapter";
    public static final String VERSION = "1.0";
    public static final String NAME = "LOTR adapter";

    public void setupAndLoadConfig(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());

        boolean craftingEnabled = config.getBoolean( "crafting_module_enabled", "crafting_tweaks", true, "Enables custom crafting recipes" );
        boolean craftableQuartz = config.getBoolean( "lotr_friendly_nether_quartz", "crafting_tweaks", true, "Enables crafting of nether quartz" );
        boolean craftableRedstone = config.getBoolean( "lotr_friendly_redstone", "crafting_tweaks", true, "Enables crafting of redstone dust" );

        craftingModule = new CraftingModule( new CraftingModule.Config( craftingEnabled, craftableQuartz, craftableRedstone ) );

//        config.addCustomCategoryComment( "mobs_per_player", "These numbers were determined via testing to match current game behavior." );
//        ME_MOBS_PER_PLAYER = config.getInt("middle_earth", "mobs_per_player", 114, 0, 2000, "Number of mob 'points' per player in the middle earth dimension" );
//        UTUMNO_MOBS_PER_PLAYER = config.getInt("utumno", "mobs_per_player", 573, 0, 2000, "Number of mob 'points' per player in the utumno dimension" );


        if (config.hasChanged()) {
            config.save();
        }
    }



    @EventHandler
    public void preInit(FMLPreInitializationEvent event ) {
        // item/block init registry.
        lotr = Loader.isModLoaded("lotr");
        System.out.println( "IS THE LOTR MOD LOADED: " + lotr );
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        if ( lotr ) {
            GameRegistry.addRecipe( new IRecipe() {

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
//            GameRegistry.addShapelessRecipe( new ItemStack( DUMMY_ITEM ), LOTRMod.modTemplate, LOTRMod.modTemplate, LOTRMod.modTemplate );
//            GameRegistry.addShapelessRecipe( new ItemStack( DUMMY_ITEM  ), LOTRMod.modTemplate, LOTRMod.modTemplate, LOTRMod.modTemplate, LOTRMod.modTemplate );
//            GameRegistry.addShapelessRecipe( new ItemStack( DUMMY_ITEM  ), LOTRMod.modTemplate, LOTRMod.modTemplate, LOTRMod.modTemplate, LOTRMod.modTemplate, LOTRMod.modTemplate );

            GameRegistry.addShapelessRecipe( new ItemStack( Items.redstone, 2 ), LOTRMod.bronze, Items.glowstone_dust );
            GameRegistry.addShapedRecipe( new ItemStack( Items.quartz, 4 ), new Object[] { " x ", "xvx", " x ", 'x', Blocks.sand, 'v', LOTRMod.salt });
            FMLCommonHandler.instance().bus().register(this);
        }


    }

    public void postInit( FMLPostInitializationEvent event ) {
        // not really using this maybe. . .
    }

    private static HashMap<EntityPlayerMP, int[]> synced = new HashMap<EntityPlayerMP, int[]>();

    public static void tick(EntityPlayerMP player) {
        // used to disallow faction specific crafting tables.
        if (player.openContainer.getClass().equals( ContainerWorkbench.class )) {

            final ContainerWorkbench crafting = (ContainerWorkbench) player.openContainer;
            final ItemStack result = CraftingManager.getInstance().findMatchingRecipe(crafting.craftMatrix, player.worldObj);

            if ( result == null ) {
                synced.remove( player );
                return;
            }

            if (synced.containsKey(player)) {
                final int[] info = synced.get(player);

                if (info[0] == player.currentWindowId && info[1] == Item.getIdFromItem(result.getItem()) && ! result.getItem().equals( DUMMY_ITEM ) ) {
                    return;
                }

            }
            Enchantment enchantment = null;
            if ( result.getItem().equals( DUMMY_ITEM ) ) {
                enchantment = getEnchantment( crafting.craftMatrix );

            }
            int enchantmentID = enchantment == null ? -1 : enchantment.effectId;
            if ( synced.containsKey( player ) && synced.get( player )[2] == enchantmentID ) return;


            if (! result.getItem().equals( DUMMY_ITEM ) ) {
                player.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(crafting.windowId, 0, result));
                synced.put(player, new int[] {player.currentWindowId, Item.getIdFromItem(result.getItem()), -1 });
                return;
            }

            ItemStack stack = null;
            if ( enchantment != null ) {
                stack = new ItemStack( REPLACEMENT );
                Items.enchanted_book.addEnchantment( stack, new EnchantmentData( enchantment, enchantment.getMaxLevel() ) );
            }
            player.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(crafting.windowId, 0, stack));

            synced.put(player, new int[] {player.currentWindowId, Item.getIdFromItem(result.getItem()), enchantmentID });
        }
    }

    @SubscribeEvent
    public void logOut(PlayerEvent.PlayerLoggedOutEvent event) {
        synced.remove(event.player);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (lotr && event.player instanceof EntityPlayerMP && event.player.openContainer != null ) {
            tick( (EntityPlayerMP) event.player );
        }
    }

    private static final Map<LOTREnchantment, Enchantment> enchantmentMap = new HashMap<LOTREnchantment, Enchantment>();
    static {
        enchantmentMap.put( LOTREnchantment.durable3, Enchantment.unbreaking);
        enchantmentMap.put( LOTREnchantment.toolSpeed4, Enchantment.efficiency);
        enchantmentMap.put( LOTREnchantment.looting3, Enchantment.fortune);
        enchantmentMap.put( LOTREnchantment.protect2, Enchantment.protection);
        enchantmentMap.put( LOTREnchantment.protectRanged3, Enchantment.projectileProtection);
        enchantmentMap.put( LOTREnchantment.protectFall3, Enchantment.featherFalling);
        enchantmentMap.put( LOTREnchantment.protectFire3, Enchantment.fireProtection);
    }

    @SubscribeEvent
    public void onItemCraftedEvent(PlayerEvent.ItemCraftedEvent event ) {
        if ( ! ( event.player instanceof EntityPlayerMP ) ) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        int[] sync = synced.get( player );

        if ( sync == null || sync[0] != player.currentWindowId || ! event.crafting.getItem().equals( DUMMY_ITEM ) ) return;

        if ( sync[2] == -1 ) {
            for ( int i = 0 ; i < event.craftMatrix.getSizeInventory() ; i ++ ) {
                ItemStack stack = event.craftMatrix.getStackInSlot( i );
                if ( stack != null ) {
                    player.entityDropItem( stack, 2 );
                }
            }
        } else {
            ItemStack book = new ItemStack( REPLACEMENT );
            Enchantment e = Enchantment.enchantmentsList[sync[2]];
            Items.enchanted_book.addEnchantment( book, new EnchantmentData( e, e.getMaxLevel() ) );
            if ( ! player.inventory.addItemStackToInventory( book ) ) {
                player.entityDropItem( book, 2 );
            }
            player.inventory.markDirty();
        }
    }

    public static Enchantment getEnchantment( InventoryCrafting inventory ) {
        LOTREnchantment enchantment = null; int count = 0;
        for ( int i = 0 ; i < 9 ; i ++ ) {
            ItemStack s = inventory.getStackInSlot( i );
            if ( s == null ) continue;
            LOTREnchantment scroll = LOTRItemModifierTemplate.getModifier( s );
            Enchantment enchant = enchantmentMap.get( scroll );
            if ( enchant == null || ! ( enchantment == null || enchantment.getDisplayName().equals( scroll.getDisplayName() ) ) ) return null;
            enchantment = scroll;
            count++;
        }
        return enchantment == null || count != enchantmentMap.get( enchantment ).getMaxLevel() ? null : enchantmentMap.get( enchantment );
    }
}

