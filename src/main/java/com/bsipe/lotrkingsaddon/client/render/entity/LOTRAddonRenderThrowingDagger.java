package com.bsipe.lotrkingsaddon.client.render.entity;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.bsipe.lotrkingsaddon.common.entities.LOTRAddonEntityThrowingDagger;

import cpw.mods.fml.common.FMLLog;

public class LOTRAddonRenderThrowingDagger extends Render {

    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.locationItemsTexture;
    }

    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
        LOTRAddonEntityThrowingDagger axe = (LOTRAddonEntityThrowingDagger) entity;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d, (float) d1, (float) d2);
        if (!axe.inGround) {
            GL11.glTranslatef(0.0F, 0.5F, 0.0F);
        }

        GL11.glRotatef(axe.prevRotationYaw + (axe.rotationYaw - axe.prevRotationYaw) * f1 - 90.0F, 0.0F, 1.0F, 0.0F);
        if (!axe.inGround) {
            GL11.glRotatef(axe.rotationPitch + (axe.inGround ? 0.0F : 45.0F * f1), 0.0F, 0.0F, -1.0F);
        } else {
            GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(0.0F, 0.75F, 0.0F);
        }

        GL11.glEnable(32826);
        float f2 = (float) axe.shake - f1;
        if (f2 > 0.0F) {
            float f3 = -MathHelper.sin(f2 * 3.0F) * f2;
            GL11.glRotatef(f3, 0.0F, 0.0F, 1.0F);
        }

        GL11.glRotatef(-135.0F, 0.0F, 0.0F, 1.0F);
        ItemStack axeItem = axe.getProjectileItem();
        IIcon icon = axeItem.getIconIndex();
        if (icon == null) {
            FMLLog.severe("Error rendering throwing axe: no icon for " + axeItem.toString(), new Object[0]);
            GL11.glPopMatrix();
        } else {
            this.bindEntityTexture(entity);
            Tessellator tessellator = Tessellator.instance;
            ItemRenderer.renderItemIn2D(
                tessellator,
                icon.getMaxU(),
                icon.getMinV(),
                icon.getMinU(),
                icon.getMaxV(),
                icon.getIconWidth(),
                icon.getIconHeight(),
                0.0625F);
            GL11.glDisable(32826);
            GL11.glPopMatrix();
        }
    }
}
