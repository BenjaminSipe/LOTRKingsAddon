package com.bsipe.lotrkingsaddon.modules;

import com.bsipe.lotrkingsaddon.Main;
import com.bsipe.lotrkingsaddon.recipes.EnchantedBookRecipe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRMod;
import lotr.common.enchant.LOTREnchantment;
import lotr.common.inventory.LOTRContainerAnvil;
import net.minecraft.enchantment.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.HashMap;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

public class CraftingRecipeModule extends AbstractModule {

    public static boolean ENABLED;
    public static boolean REDSTONE_CRAFTING_ENABLED;
    public static boolean QUARTZ_CRAFTING_ENABLED;
    public static boolean ENDER_CHEST_CRAFTING_ENABLED;
    public static boolean SERVER_ONLY;

    // general config
    public static boolean ENCHANTED_BOOK_CRAFTING_ENABLED;

    // specific enchantments
    public static boolean EFFICIENCY_CRAFTING_ENABLED;
    public static boolean FORTUNE_CRAFTING_ENABLED;
    public static boolean UNBREAKING_CRAFTING_ENABLED;
    public static boolean SHARPNESS_CRAFTING_ENABLED;
    public static boolean FIRE_ASPECT_CRAFTING_ENABLED;
    public static boolean KNOCKBACK_CRAFTING_ENABLED;
    public static boolean LOOTING_CRAFTING_ENABLED;
    public static boolean PROTECTION_CRAFTING_ENABLED;
    public static boolean FIRE_PROT_CRAFTING_ENABLED;
    public static boolean PROJ_PROT_CRAFTING_ENABLED;
    public static boolean FEATHER_FALLING_CRAFTING_ENABLED;
    public static boolean POWER_CRAFTING_ENABLED;
    public static boolean PUNCH_CRAFTING_ENABLED;
    public static boolean BEACON_CRAFTING_ENABLED;
    public static boolean STONE_CHEST_CRAFTING_ENABLED;

    public static boolean REMOVE_REFORGING_COOLDOWN;

    public static final String CONFIG_CATAGORY = "crafting_recipe_module";

    // only used in server_only mode.
    private static HashMap<EntityPlayerMP, int[]> synced = new HashMap<>();

    public CraftingRecipeModule( Configuration config, boolean serverOnly ) {
        SERVER_ONLY = serverOnly;

        ENABLED = config.getBoolean( "custom_crafting_recipes_enabled", CONFIG_CATAGORY, true, "Controls whether Custom Crafting recipes are added." );
        REDSTONE_CRAFTING_ENABLED = config.getBoolean( "redstone_crafting_enabled", CONFIG_CATAGORY, true, "Adds redstone dust crafting recipe" );
        QUARTZ_CRAFTING_ENABLED = config.getBoolean( "quartz_crafting_enabled", CONFIG_CATAGORY, true, "Adds quartz crystal crafting recipe" );
        ENDER_CHEST_CRAFTING_ENABLED = config.getBoolean( "ender_chest_crafting_enabled", CONFIG_CATAGORY, true, "Adds LOTR Friendly recipe for ender chests" );
        BEACON_CRAFTING_ENABLED = config.getBoolean( "beacon_crafting_enabled", CONFIG_CATAGORY, true, "Adds LOTR Friendly recipe for beacons" );
        STONE_CHEST_CRAFTING_ENABLED = config.getBoolean( "stone_chest_crafting_enabled", CONFIG_CATAGORY, true, "Adds recipe for stone chests." );

        ENCHANTED_BOOK_CRAFTING_ENABLED = config.getBoolean( "enchanted_book_crafting_enabled", CONFIG_CATAGORY, true, "Gates all enchanted book crafting behind a single config." );
        EFFICIENCY_CRAFTING_ENABLED = config.getBoolean( "efficiency_crafting_enabled", CONFIG_CATAGORY, true, "Allows Efficiency 5 books to be crafted with scrolls" );
        FORTUNE_CRAFTING_ENABLED = config.getBoolean( "fortune_crafting_enabled", CONFIG_CATAGORY, true, "Allows Fortune 3 books to be crafted with scrolls" );
        UNBREAKING_CRAFTING_ENABLED = config.getBoolean( "unbreaking_crafting_enabled", CONFIG_CATAGORY, true, "Allows Unbreaking 3 books to be crafted with scrolls" );
        SHARPNESS_CRAFTING_ENABLED = config.getBoolean( "sharpness_crafting_enabled", CONFIG_CATAGORY, false, "Allows Sharpness 5 books to be crafted with scrolls" );
        FIRE_ASPECT_CRAFTING_ENABLED = config.getBoolean( "fire_aspect_crafting_enabled", CONFIG_CATAGORY, false, "Allows Fire Aspect 2 books to be crafted with scrolls" );
        KNOCKBACK_CRAFTING_ENABLED = config.getBoolean( "knockback_crafting_enabled", CONFIG_CATAGORY, false, "Allows Knockback 2 to be crafted with scrolls" );
        LOOTING_CRAFTING_ENABLED = config.getBoolean( "looting_crafting_enabled", CONFIG_CATAGORY, false, "Allows Looting 3 books to be crafted with scrolls" );
        PROTECTION_CRAFTING_ENABLED = config.getBoolean( "protection_crafting_enabled", CONFIG_CATAGORY, true, "Allows Protection 4 books to be crafted with scrolls" );
        FIRE_PROT_CRAFTING_ENABLED = config.getBoolean( "fire_prot_crafting_enabled", CONFIG_CATAGORY, true, "Allows Fire Protection 4 books to be crafted with scrolls" );
        PROJ_PROT_CRAFTING_ENABLED = config.getBoolean( "proj_prot_crafting_enabled", CONFIG_CATAGORY, true, "Allows Projectile Protection 4 books to be crafted with scrolls" );
        FEATHER_FALLING_CRAFTING_ENABLED = config.getBoolean( "feather_falling_crafting_enabled", CONFIG_CATAGORY, true, "Allows Feather Falling 4 books to be crafted with scrolls" );
        POWER_CRAFTING_ENABLED = config.getBoolean( "power_crafting_enabled", CONFIG_CATAGORY, false, "Allows Power 5 books to be crafted with scrolls" );
        PUNCH_CRAFTING_ENABLED = config.getBoolean( "punch_crafting_enabled", CONFIG_CATAGORY, false, "Allows Punch 2 books to be crafted with scrolls" );

        REMOVE_REFORGING_COOLDOWN = config.getBoolean( "remove_reforge_cooldown", CONFIG_CATAGORY, true, "Remove the reforging cooldown (careful not to spam :)" );

    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        if ( ! ENABLED ) return;

        if ( REDSTONE_CRAFTING_ENABLED ) GameRegistry.addShapelessRecipe( new ItemStack( Items.redstone, 2 ), LOTRMod.bronze, Items.glowstone_dust );
        if ( QUARTZ_CRAFTING_ENABLED ) GameRegistry.addShapedRecipe( new ItemStack( Items.quartz, 4 ), new Object[] { " x ", "xvx", " x ", 'x', Blocks.sand, 'v', LOTRMod.salt });
        if (ENDER_CHEST_CRAFTING_ENABLED ) GameRegistry.addShapedRecipe( new ItemStack( Blocks.ender_chest ), new Object[] { "ooo", "omo", "ooo", 'o', Blocks.obsidian, 'm', LOTRMod.mithril } );
        if (BEACON_CRAFTING_ENABLED ) GameRegistry.addShapedRecipe( new ItemStack( Items.nether_star ), new Object[] { "cmf", "mpm","fmc",'c', LOTRMod.chilling, 'm', LOTRMod.oreMithril, 'f', LOTRMod.balrogFire, 'p', LOTRMod.pearl } );
        if ( STONE_CHEST_CRAFTING_ENABLED ) GameRegistry.addShapedRecipe( new ItemStack( LOTRMod.chestStone ), new Object[] { "sss", "scs", "sss", 's', LOTRMod.scorchedStone, 'c', Blocks.chest } );

        if ( ENCHANTED_BOOK_CRAFTING_ENABLED ) addEnchantedBookCraftingRecipes();

        if ( SERVER_ONLY || REMOVE_REFORGING_COOLDOWN) {
            FMLCommonHandler.instance().bus().register(this);
        }

    }

    public void addEnchantedBookCraftingRecipes() {
        RecipeSorter.register(Main.MODID + ":enchantedbookcrafting",  EnchantedBookRecipe.class,   SHAPELESS, "after:minecraft:shapeless");

        if ( EFFICIENCY_CRAFTING_ENABLED ) GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.efficiency, 5, LOTREnchantment.toolSpeed4, 5 ) );
        if ( FORTUNE_CRAFTING_ENABLED ) GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.fortune, 3, LOTREnchantment.looting3, 3 ) );
        if ( UNBREAKING_CRAFTING_ENABLED ) GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.unbreaking, 3, LOTREnchantment.durable3, 3 ) );
        if ( SHARPNESS_CRAFTING_ENABLED ) GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.sharpness, 5, LOTREnchantment.strong4, 5 ) );
        if ( KNOCKBACK_CRAFTING_ENABLED ) GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.knockback, 2, LOTREnchantment.knockback2, 2 ) );
        if ( LOOTING_CRAFTING_ENABLED && ! FORTUNE_CRAFTING_ENABLED ) GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.looting, 3, LOTREnchantment.looting3, 3 ) );
        if ( PROTECTION_CRAFTING_ENABLED ) GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.protection, 4, LOTREnchantment.protect2, 4 ) );
        if ( FIRE_PROT_CRAFTING_ENABLED ) GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.fireProtection, 4, LOTREnchantment.protectFire3, 4 ) );
        if ( PROJ_PROT_CRAFTING_ENABLED ) GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.projectileProtection, 4, LOTREnchantment.protectRanged3, 4 ) );
        if ( FEATHER_FALLING_CRAFTING_ENABLED ) GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.featherFalling, 4, LOTREnchantment.protectFall3, 4 ) );
        if ( POWER_CRAFTING_ENABLED ) GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.power, 5, LOTREnchantment.rangedStrong3, 5 ) );
        if ( PUNCH_CRAFTING_ENABLED ) GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.punch, 2, LOTREnchantment.rangedKnockback2, 2 ) );

        // fire aspect uses "flame of udun" rather than a scroll.
        if ( FIRE_ASPECT_CRAFTING_ENABLED ) {
            ItemStack stack = new ItemStack( Items.enchanted_book );
            Items.enchanted_book.addEnchantment( stack, new EnchantmentData( Enchantment.fireAspect, 2 ) );
            GameRegistry.addShapelessRecipe( stack, LOTRMod.balrogFire, LOTRMod.balrogFire ); // these are flame of udun, just called balrog fire in the code.
        }
    }

    public static void tick(EntityPlayerMP player) {
        // used to disallow faction specific crafting tables.
        if (REMOVE_REFORGING_COOLDOWN && player.openContainer.getClass().equals( LOTRContainerAnvil.class )) {
            // hard reset the re-forging cooldown to -1.
            ReflectionHelper.setPrivateValue( LOTRContainerAnvil.class,(LOTRContainerAnvil)player.openContainer, -1L, 14);
        }

        if ( SERVER_ONLY && player.openContainer.getClass().equals( ContainerWorkbench.class ) ) {
            final ContainerWorkbench crafting = (ContainerWorkbench) player.openContainer;
            final ItemStack result = CraftingManager.getInstance().findMatchingRecipe(crafting.craftMatrix, player.worldObj);

            if ( result == null ) {
                synced.remove( player );
                return;
            }


            if (synced.containsKey(player)) {
                final int[] info = synced.get(player);
                if (info[0] == player.currentWindowId && info[1] == Item.getIdFromItem( result.getItem() ) && ! Items.enchanted_book.equals( result.getItem() ) ) {
                    return;
                }
            }

            player.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(crafting.windowId, 0, result));
            synced.put(player, new int[] {player.currentWindowId, Item.getIdFromItem(result.getItem()) });
        }
    }

    @SubscribeEvent
    public void logOut(PlayerEvent.PlayerLoggedOutEvent event) {
        // shouldn't run anyway, but just in case.
        if ( ! ENABLED || !SERVER_ONLY ) return;
        synced.remove(event.player);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // shouldn't run anyway, but just in case.
        if ( ! ENABLED || ! ( SERVER_ONLY || REMOVE_REFORGING_COOLDOWN ) ) return;

        if (event.player instanceof EntityPlayerMP && event.player.openContainer != null ) {
            tick( (EntityPlayerMP) event.player );
        }
    }
}
