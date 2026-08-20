package com.bsipe.lotrkingsaddon.common.modules;

import com.bsipe.lotrkingsaddon.Config;
import com.bsipe.lotrkingsaddon.MyMod;
import com.bsipe.lotrkingsaddon.common.recipes.EnchantedBookRecipe;
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
import lotr.common.item.LOTRItemModifierTemplate;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.HashMap;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

public class CraftingRecipeModule extends AbstractModule {

    private static HashMap<EntityPlayerMP, int[]> synced = new HashMap<>();
    private static boolean serverOnly;
    private Config.CraftingRecipeModuleConfig config;
    public CraftingRecipeModule( boolean serverOnly ) {
        CraftingRecipeModule.serverOnly = serverOnly;
        config = Config.getCraftingRecipeModuleConfig();
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        if ( config.redstone() ) GameRegistry.addShapelessRecipe( new ItemStack( Items.redstone, 2 ), LOTRMod.bronze, Items.glowstone_dust );
        if ( config.quartz() ) GameRegistry.addShapedRecipe( new ItemStack( Items.quartz, 4 ), new Object[] { " x ", "xvx", " x ", 'x', Blocks.sand, 'v', LOTRMod.salt });
        if ( config.enderChest() ) GameRegistry.addShapedRecipe( new ItemStack( Blocks.ender_chest ), new Object[] { "ooo", "omo", "ooo", 'o', Blocks.obsidian, 'm', LOTRMod.mithril } );
        if ( config.beacon() ) GameRegistry.addShapedRecipe( new ItemStack( Items.nether_star ), new Object[] { "cmf", "mpm","fmc",'c', LOTRMod.chilling, 'm', LOTRMod.oreMithril, 'f', LOTRMod.balrogFire, 'p', LOTRMod.pearl } );
        if ( config.stoneChest() ) GameRegistry.addShapedRecipe( new ItemStack( LOTRMod.chestStone ), new Object[] { "sss", "scs", "sss", 's', LOTRMod.scorchedStone, 'c', Blocks.chest } );

        if ( config.enchantedBooks() ) addEnchantedBookCraftingRecipes();

        if ( config.handyHardyScrolls() ) {
            ItemStack hardy = new ItemStack( LOTRMod.modTemplate );
            LOTRItemModifierTemplate.setModifier( hardy, LOTREnchantment.durable1 );
            GameRegistry.addRecipe( new EnchantedBookRecipe( hardy, null, 2, ItemBlock.getItemFromBlock( Blocks.cobblestone ) ) );
            ItemStack handy = new ItemStack( LOTRMod.modTemplate );
            LOTRItemModifierTemplate.setModifier( handy, LOTREnchantment.toolSpeed1 );
            GameRegistry.addRecipe( new EnchantedBookRecipe( handy, null, 2, Items.feather ) );
        }

        if ( serverOnly || config.removeReforgeCooldown() ) {
            FMLCommonHandler.instance().bus().register(this);
        }

    }

    public void addEnchantedBookCraftingRecipes() {
        RecipeSorter.register(MyMod.MODID + ":enchantedbookcrafting",  EnchantedBookRecipe.class, SHAPELESS, "after:minecraft:shapeless");

        if ( config.unbreaking() ) {
            GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.unbreaking, 3, LOTREnchantment.durable3, 3 ) );
            if ( config.lowTierEnchantedBooks() ) {
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.unbreaking, 2, LOTREnchantment.durable2, 3 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.unbreaking, 1, LOTREnchantment.durable1, 3 ) );
            }
        }
        if ( config.sharpness() ) {
            GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.sharpness, 5, LOTREnchantment.strong4, 5 ) );
            if ( config.lowTierEnchantedBooks() ) {
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.sharpness, 4, LOTREnchantment.strong3, 5 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.sharpness, 3, LOTREnchantment.strong2, 5 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.sharpness, 2, LOTREnchantment.strong1, 5 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.sharpness, 1, LOTREnchantment.strong1, 3 ) );
            }
        }
        if ( config.knockback() ) {
            GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.knockback, 2, LOTREnchantment.knockback2, 2 ) );
            if (config.lowTierEnchantedBooks() ) {
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.knockback, 1, LOTREnchantment.knockback1, 2 ) );
            }
        }
        if ( config.looting() && ! config.fortune() ) {
            GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.looting, 3, LOTREnchantment.looting3, 3 ) );
            if ( config.lowTierEnchantedBooks() ) {
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.looting, 2, LOTREnchantment.looting2, 3 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.looting, 1, LOTREnchantment.looting1, 3 ) );
            }
        }

        if ( config.protection() ) {
            GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.protection, 4, LOTREnchantment.protect2, 4 ) );
            if ( config.lowTierEnchantedBooks() ) {
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.protection, 3, LOTREnchantment.protect1, 4 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.protection, 2, LOTREnchantment.protect1, 2 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.protection, 1, LOTREnchantment.protect1, 1 ) );
            }
        }
        if ( config.fireProtection() ) {
            GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.fireProtection, 4, LOTREnchantment.protectFire3, 4 ) );
            if ( config.lowTierEnchantedBooks() ) {
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.fireProtection, 3, LOTREnchantment.protectFire2, 4 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.fireProtection, 2, LOTREnchantment.protectFire1, 4 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.fireProtection, 1, LOTREnchantment.protectFire1, 2 ) );
            }
        }
        if ( config.projectileProtection() ) {
            GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.projectileProtection, 4, LOTREnchantment.protectRanged3, 4 ) );
            if ( config.lowTierEnchantedBooks() ) {
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.projectileProtection, 3, LOTREnchantment.protectRanged2, 4 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.projectileProtection, 2, LOTREnchantment.protectRanged1, 4 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.projectileProtection, 1, LOTREnchantment.protectRanged1, 2 ) );
            }
        }

        if ( config.featherFalling() ) {
            GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.featherFalling, 4, LOTREnchantment.protectFall3, 4 ) );
            if ( config.lowTierEnchantedBooks() ) {
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.featherFalling, 3, LOTREnchantment.protectFall2, 4 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.featherFalling, 2, LOTREnchantment.protectFall1, 4 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.featherFalling, 1, LOTREnchantment.protectFall1, 2 ) );
            }
        }
        if ( config.power() ) {
            GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.power, 5, LOTREnchantment.rangedStrong3, 5 ) );
            if ( config.lowTierEnchantedBooks() ) {
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.power, 4, LOTREnchantment.rangedStrong2, 5 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.power, 3, LOTREnchantment.rangedStrong1, 5 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.power, 2, LOTREnchantment.rangedStrong1, 3 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.power, 1, LOTREnchantment.rangedStrong1, 2 ) );
            }
        }
        if ( config.punch() ) {
            GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.punch, 2, LOTREnchantment.rangedKnockback2, 2 ) );
            if ( config.lowTierEnchantedBooks() ) {
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.punch, 1, LOTREnchantment.rangedKnockback1, 2 ) );
            }
        }

        if ( config.efficiency() ) {
            GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.efficiency, 5, LOTREnchantment.toolSpeed4, 5 ) );
            if ( config.lowTierEnchantedBooks() ) {
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.efficiency, 4, LOTREnchantment.toolSpeed3, 5 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.efficiency, 3, LOTREnchantment.toolSpeed2, 5 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.efficiency, 2, LOTREnchantment.toolSpeed1, 5 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.efficiency, 1, LOTREnchantment.toolSpeed1, 3 ) );
            }
        }

        if ( config.fortune() ) {
            GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.fortune, 3, LOTREnchantment.looting3, 3 ) );
            if ( config.lowTierEnchantedBooks() ) {
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.fortune, 2, LOTREnchantment.looting2, 3 ) );
                GameRegistry.addRecipe( new EnchantedBookRecipe( Enchantment.fortune, 1, LOTREnchantment.looting1, 3 ) );
            }
        }

        // fire aspect uses "flame of udun" rather than a scroll.
        if ( config.fireAspect() ) {
            ItemStack stack = new ItemStack( Items.enchanted_book );
            Items.enchanted_book.addEnchantment( stack, new EnchantmentData( Enchantment.fireAspect, 2 ) );
            GameRegistry.addShapelessRecipe( stack, LOTRMod.balrogFire, LOTRMod.balrogFire ); // these are flame of udun, just called balrog fire in the code.
        }
    }

    public void tick(EntityPlayerMP player) {
        // used to disallow faction specific crafting tables.
        if (config.removeReforgeCooldown() && player.openContainer.getClass().equals( LOTRContainerAnvil.class )) {
            // hard reset the re-forging cooldown to -1.
            ReflectionHelper.setPrivateValue( LOTRContainerAnvil.class,(LOTRContainerAnvil)player.openContainer, -1L, 14);
        }

        if ( serverOnly && player.openContainer.getClass().equals( ContainerWorkbench.class ) ) {
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
        if ( !serverOnly ) return;
        synced.remove(event.player);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // shouldn't run anyway, but just in case.
        if ( ! ( serverOnly || config.removeReforgeCooldown() ) ) return;

        if (event.player instanceof EntityPlayerMP && event.player.openContainer != null ) {
            tick( (EntityPlayerMP) event.player );
        }
    }
}
