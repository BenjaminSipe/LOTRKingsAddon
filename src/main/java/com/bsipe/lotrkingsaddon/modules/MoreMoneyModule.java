package com.bsipe.lotrkingsaddon.modules;

import com.bsipe.lotrkingsaddon.Main;
import com.bsipe.lotrkingsaddon.recipes.CoinPouchRecipe;
import com.bsipe.lotrkingsaddon.renderer.LOTRGuiButtonConvertAllCoin;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import lotr.client.LOTRReflectionClient;
import lotr.client.gui.*;
import lotr.common.LOTRLevelData;
import lotr.common.inventory.LOTRContainerCoinExchange;
import lotr.common.item.LOTRItemCoin;
import lotr.common.network.LOTRPacketHandler;
import lotr.common.network.LOTRPacketRestockPouches;
import lotr.compatibility.LOTRModChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

public class MoreMoneyModule extends AbstractModule {

    public static boolean LARGER_COINS_ENABLED;
    public static boolean BULK_COIN_CONVERSION;
    public static boolean GUI_COIN_CONVERSION;

    private static final String CONFIG_CATAGORY = "more_money_module";
    public MoreMoneyModule(Configuration config, boolean serverOnly ) {
        config.addCustomCategoryComment( CONFIG_CATAGORY, "Adds new coins with higher values, can be used normally and obtained via the currency exchange." );
        config.addCustomCategoryComment( CONFIG_CATAGORY, "This module is required by both client and server." );
        LARGER_COINS_ENABLED = !serverOnly && config.getBoolean( "higher_currencies_enabled", CONFIG_CATAGORY, true, "Adds higher coin denominations past 100" );

        BULK_COIN_CONVERSION = config.getBoolean( "bulk_currency_conversion", CONFIG_CATAGORY, true, "Adds crafting of full large pouches of coins to greater denominations" );
        GUI_COIN_CONVERSION = config.getBoolean( "gui_coin_conversion", CONFIG_CATAGORY, true, "Adds 'convert all' button to coin exchange gui" );
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if ( LARGER_COINS_ENABLED ) {
            LOTRItemCoin.values = new int[] { 1, 10, 100, 1_000, 10_000, 100_000 };
        }

        if ( BULK_COIN_CONVERSION ) {
            RecipeSorter.register(Main.MODID + ":bulkcoinconversion",  CoinPouchRecipe.class,   SHAPELESS, "after:minecraft:shapeless");

            GameRegistry.addRecipe( new CoinPouchRecipe( 0 ) );
            GameRegistry.addRecipe( new CoinPouchRecipe( 1 ) );
            if ( LARGER_COINS_ENABLED ) {
                GameRegistry.addRecipe( new CoinPouchRecipe( 2 ) );
                GameRegistry.addRecipe( new CoinPouchRecipe( 3 ) );
                GameRegistry.addRecipe( new CoinPouchRecipe( 4 ) );

            }
        }
    }

    @Override
    public void init( FMLInitializationEvent event ) {
        if ( GUI_COIN_CONVERSION )
        {
            FMLCommonHandler.instance().bus().register(this);
            MinecraftForge.EVENT_BUS.register(this);
        }
    }


    @SubscribeEvent
    public void postInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if ( ! GUI_COIN_CONVERSION ) return;

        GuiScreen gui = event.gui;
        List buttons = event.buttonList;

//        if (gui instanceof GuiContainer && !(gui instanceof LOTRGuiPouch) && !(gui instanceof LOTRGuiChestWithPouch)) {
        if ( gui instanceof LOTRGuiCoinExchange ) {
            GuiContainer guiContainer = (GuiContainer)gui;
            EntityPlayer thePlayer = guiContainer.mc.thePlayer;
            InventoryPlayer playerInv = thePlayer.inventory;
            boolean containsPlayer = false;
            Slot topRightPlayerSlot = null;
            Slot topLeftPlayerSlot = null;
            Container container = guiContainer.inventorySlots;
            Iterator var10 = container.inventorySlots.iterator();

            while(var10.hasNext()) {
                Object obj = var10.next();
                Slot slot = (Slot)obj;
                boolean acceptableSlotIndex = slot.getSlotIndex() < playerInv.mainInventory.length;
                if (gui instanceof GuiContainerCreative) {
                    acceptableSlotIndex = slot.getSlotIndex() >= 9;
                }

                if (slot.inventory == playerInv && acceptableSlotIndex) {
                    containsPlayer = true;
                    boolean isTopRight = false;
                    if (topRightPlayerSlot == null) {
                        isTopRight = true;
                    } else if (slot.yDisplayPosition < topRightPlayerSlot.yDisplayPosition) {
                        isTopRight = true;
                    } else if (slot.yDisplayPosition == topRightPlayerSlot.yDisplayPosition && slot.xDisplayPosition > topRightPlayerSlot.xDisplayPosition) {
                        isTopRight = true;
                    }

                    if (isTopRight) {
                        topRightPlayerSlot = slot;
                    }

                    boolean isTopLeft = false;
                    if (topLeftPlayerSlot == null) {
                        isTopLeft = true;
                    } else if (slot.yDisplayPosition < topLeftPlayerSlot.yDisplayPosition) {
                        isTopLeft = true;
                    } else if (slot.yDisplayPosition == topLeftPlayerSlot.yDisplayPosition && slot.xDisplayPosition < topLeftPlayerSlot.xDisplayPosition) {
                        isTopLeft = true;
                    }

                    if (isTopLeft) {
                        topLeftPlayerSlot = slot;
                    }
                }
            }

            if (containsPlayer) {
                int guiLeft = LOTRReflectionClient.getGuiLeft(guiContainer);
                int guiTop = LOTRReflectionClient.getGuiTop(guiContainer);
                int guiXSize = LOTRReflectionClient.getGuiXSize(guiContainer);
                int buttonX = topRightPlayerSlot.xDisplayPosition -9;
                int buttonY = topRightPlayerSlot.yDisplayPosition - 18;
//                if (pouchRestock_leftPositionGUIs.contains(gui.getClass())) {
//                    buttonX = topLeftPlayerSlot.xDisplayPosition - 1;
//                    buttonY = topLeftPlayerSlot.yDisplayPosition - 14;
//                } else if (pouchRestock_sidePositionGUIs.contains(gui.getClass())) {
//                    buttonX = topRightPlayerSlot.xDisplayPosition + 21;
//                    buttonY = topRightPlayerSlot.yDisplayPosition - 1;
//                }

                if (LOTRModChecker.hasNEI() && guiContainer instanceof InventoryEffectRenderer && LOTRReflectionClient.hasGuiPotionEffects((InventoryEffectRenderer)guiContainer)) {
                    buttonX -= 60;
                }

                buttons.add(new LOTRGuiButtonConvertAllCoin(guiContainer, 2000, guiLeft + buttonX , guiTop + buttonY));
            }
        }
    }

    @SubscribeEvent
    public void postActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {

        if ( ! GUI_COIN_CONVERSION ) return;

        Minecraft mc = Minecraft.getMinecraft();
        GuiScreen gui = event.gui;
        List buttons = event.buttonList;
        GuiButton button = event.button;

        if (button instanceof LOTRGuiButtonRestockPouch && button.enabled) {
            LOTRPacketRestockPouches packet = new LOTRPacketRestockPouches();
            LOTRPacketHandler.networkWrapper.sendToServer(packet);
        }

    }

}
