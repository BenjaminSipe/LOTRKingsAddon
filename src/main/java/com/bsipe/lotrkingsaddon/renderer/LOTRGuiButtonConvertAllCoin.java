package com.bsipe.lotrkingsaddon.renderer;

import lotr.client.LOTRReflectionClient;
import lotr.client.gui.LOTRGuiCoinExchange;
import lotr.common.LOTRMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class LOTRGuiButtonConvertAllCoin extends GuiButton {
    private static final ResourceLocation texture = new ResourceLocation("lotrkingsaddon:gui/npccoinconversion.png");
    private final GuiContainer parentGUI;

    public LOTRGuiButtonConvertAllCoin( GuiContainer parent, int i, int j, int k ) {
        super( i, j, k, 14, 14, "" );
        parentGUI = parent;
    }

    public void drawButton(Minecraft mc, int i, int j) {
        this.checkCoinConvertEnabled(mc);
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(texture);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = i >= this.xPosition && j >= this.yPosition && i < this.xPosition + this.width && j < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
//            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 128 + k * 10, this.width, this.height);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 28 - k * 14 , this.width, this.height);
            this.mouseDragged(mc, i, j);
        }

    }

    private void checkCoinConvertEnabled(Minecraft mc) {
        InventoryPlayer inv = mc.thePlayer.inventory;
        this.enabled = this.visible = inv.hasItem(LOTRMod.silverCoin);

    }

}
