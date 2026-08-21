package com.bsipe.lotrkingsaddon.client.modules;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import com.bsipe.lotrkingsaddon.client.render.gui.LOTRGuiButtonConvertAllCoin;
import com.bsipe.lotrkingsaddon.common.modules.MoreMoneyModule;
import com.bsipe.lotrkingsaddon.common.network.LOTRKingsPacketHandler;
import com.bsipe.lotrkingsaddon.common.network.packets.LOTRKingsConvertAllCoinPacket;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lotr.client.LOTRReflectionClient;
import lotr.client.gui.LOTRGuiCoinExchange;
import lotr.common.LOTRMod;
import lotr.compatibility.LOTRModChecker;

public class MoreMoneyClientModule extends MoreMoneyModule {

    public MoreMoneyClientModule(boolean serverOnly) {
        super(serverOnly);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        if (config.guiCoinConversion()) {
            FMLCommonHandler.instance()
                .bus()
                .register(this);
            MinecraftForge.EVENT_BUS.register(this);
        }
        super.init(event);
    }

    // this should be client side... and isn't,
    // that's what is causing this problem.
    @SubscribeEvent
    public void postInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!config.guiCoinConversion()) return;

        GuiScreen gui = event.gui;
        List buttons = event.buttonList;

        // if (gui instanceof GuiContainer && !(gui instanceof LOTRGuiPouch) && !(gui instanceof LOTRGuiChestWithPouch))
        // {
        if (gui instanceof LOTRGuiCoinExchange) {
            GuiContainer guiContainer = (GuiContainer) gui;
            try {
                EntityPlayer thePlayer = guiContainer.mc.thePlayer;
                InventoryPlayer playerInv = thePlayer.inventory;
                boolean containsPlayer = false;
                Slot topRightPlayerSlot = null;
                Slot topLeftPlayerSlot = null;
                Container container = guiContainer.inventorySlots;
                Iterator var10 = container.inventorySlots.iterator();

                while (var10.hasNext()) {
                    Object obj = var10.next();
                    Slot slot = (Slot) obj;
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
                        } else if (slot.yDisplayPosition == topRightPlayerSlot.yDisplayPosition
                            && slot.xDisplayPosition > topRightPlayerSlot.xDisplayPosition) {
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
                        } else if (slot.yDisplayPosition == topLeftPlayerSlot.yDisplayPosition
                            && slot.xDisplayPosition < topLeftPlayerSlot.xDisplayPosition) {
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
                    int buttonX = topRightPlayerSlot.xDisplayPosition + 3;
                    // int buttonX = topRightPlayerSlot.xDisplayPosition -9;
                    int buttonY = topRightPlayerSlot.yDisplayPosition - 18;

                    if (Minecraft.getMinecraft().thePlayer.inventory.hasItem(LOTRMod.pouch)) {
                        buttonX -= 12;
                    }
                    // if (pouchRestock_leftPositionGUIs.contains(gui.getClass())) {
                    // buttonX = topLeftPlayerSlot.xDisplayPosition - 1;
                    // buttonY = topLeftPlayerSlot.yDisplayPosition - 14;
                    // } else if (pouchRestock_sidePositionGUIs.contains(gui.getClass())) {
                    // buttonX = topRightPlayerSlot.xDisplayPosition + 21;
                    // buttonY = topRightPlayerSlot.yDisplayPosition - 1;
                    // }

                    if (LOTRModChecker.hasNEI() && guiContainer instanceof InventoryEffectRenderer
                        && LOTRReflectionClient.hasGuiPotionEffects((InventoryEffectRenderer) guiContainer)) {
                        buttonX -= 60;
                    }

                    buttons
                        .add(new LOTRGuiButtonConvertAllCoin(guiContainer, 2000, guiLeft + buttonX, guiTop + buttonY));
                }
            } catch (Exception e) {
                System.out.println("This failed, lets see what happens");
            }
        }
    }

    @SubscribeEvent
    public void postActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {

        if (!config.bulkCoinConversion()) return;

        Minecraft mc = Minecraft.getMinecraft();
        GuiScreen gui = event.gui;
        List buttons = event.buttonList;
        GuiButton button = event.button;

        if (button instanceof LOTRGuiButtonConvertAllCoin && button.enabled) {
            LOTRKingsConvertAllCoinPacket packet = new LOTRKingsConvertAllCoinPacket();
            LOTRKingsPacketHandler.networkWrapper.sendToServer(packet);
        }

    }
}
