package com.bsipe.lotrkingsaddon.client.render.item;

import lotr.client.LOTRClientProxy;
import lotr.common.item.LOTRItemLance;
import lotr.common.item.LOTRItemPike;
import lotr.common.item.LOTRItemSpear;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LOTRAddonRenderLargeItem implements IItemRenderer {
    private static Map<String, Float> sizeFolders = new HashMap();
    private final Item theItem;
    private final String folderName;
    private final float largeIconScale;
    private IIcon largeIcon;
    private List<LOTRAddonRenderLargeItem.ExtraLargeIconToken> extraTokens = new ArrayList();

    private static ResourceLocation getLargeTexturePath(Item item, String folder) {
        String prefix = "lotrkingsaddon:";
        String itemName = item.getUnlocalizedName();
        itemName = itemName.substring(itemName.indexOf(prefix) + prefix.length());
        String s = prefix + "textures/items/" + folder + "/" + itemName;
        s = s + ".png";
        return new ResourceLocation(s);
    }

    public static LOTRAddonRenderLargeItem getRendererIfLarge(Item item) {
        Iterator var1 = sizeFolders.keySet().iterator();

        while(var1.hasNext()) {
            String folder = (String)var1.next();
            float iconScale = (Float)sizeFolders.get(folder);

            try {
                ResourceLocation resLoc = getLargeTexturePath(item, folder);
                IResource res = Minecraft.getMinecraft().getResourceManager().getResource(resLoc);
                if (res != null) {
                    return new LOTRAddonRenderLargeItem(item, folder, iconScale);
                }
            } catch (IOException var6) {
            }
        }

        return null;
    }

    public LOTRAddonRenderLargeItem(Item item, String dir, float f) {
        this.theItem = item;
        this.folderName = dir;
        this.largeIconScale = f;
    }

    public LOTRAddonRenderLargeItem.ExtraLargeIconToken extraIcon(String name) {
        LOTRAddonRenderLargeItem.ExtraLargeIconToken token = new LOTRAddonRenderLargeItem.ExtraLargeIconToken(name);
        this.extraTokens.add(token);
        return token;
    }

    public void registerIcons(IIconRegister register) {
        this.largeIcon = this.registerLargeIcon(register, (String)null);

        LOTRAddonRenderLargeItem.ExtraLargeIconToken token;
        for(Iterator var2 = this.extraTokens.iterator(); var2.hasNext(); token.icon = this.registerLargeIcon(register, token.name)) {
            token = (LOTRAddonRenderLargeItem.ExtraLargeIconToken)var2.next();
        }

    }

    private IIcon registerLargeIcon(IIconRegister register, String extra) {
        String prefix = "lotrkingsaddon:";
        String itemName = this.theItem.getUnlocalizedName();
        itemName = itemName.substring(itemName.indexOf(prefix) + prefix.length());
        String path = prefix + this.folderName + "/" + itemName;
        if (!StringUtils.isNullOrEmpty(extra)) {
            path = path + "_" + extra;
        }

        return register.registerIcon(path);
    }

    private void doTransformations() {
        GL11.glTranslatef(-(this.largeIconScale - 1.0F) / 2.0F, -(this.largeIconScale - 1.0F) / 2.0F, 0.0F);
        GL11.glScalef(this.largeIconScale, this.largeIconScale, 1.0F);
    }

    public void renderLargeItem() {
        this.renderLargeItem(this.largeIcon);
    }

    public void renderLargeItem(LOTRAddonRenderLargeItem.ExtraLargeIconToken token) {
        this.renderLargeItem(token.icon);
    }

    private void renderLargeItem(IIcon icon) {
        this.doTransformations();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationItemsTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tess = Tessellator.instance;
        ItemRenderer.renderItemIn2D(tess, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
    }

    public boolean handleRenderType(ItemStack itemstack, IItemRenderer.ItemRenderType type) {
        return type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
    }

    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack itemstack, IItemRenderer.ItemRendererHelper helper) {
        return false;
    }

    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack itemstack, Object... data) {
        GL11.glPushMatrix();
        Entity holder = (Entity)data[1];
        boolean isFirstPerson = holder == Minecraft.getMinecraft().thePlayer && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0;
        Item item = itemstack.getItem();
        if (item instanceof LOTRItemSpear && holder instanceof EntityPlayer && ((EntityPlayer)holder).getItemInUse() == itemstack) {
            GL11.glRotatef(260.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-1.0F, 0.0F, 0.0F);
        }

        EntityLivingBase entityliving;
        if (item instanceof LOTRItemPike && holder instanceof EntityLivingBase) {
            entityliving = (EntityLivingBase)holder;
            if (entityliving.getHeldItem() == itemstack && entityliving.swingProgress <= 0.0F) {
                if (entityliving.isSneaking()) {
                    if (isFirstPerson) {
                        GL11.glRotatef(270.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-1.0F, 0.0F, 0.0F);
                    } else {
                        GL11.glTranslatef(0.0F, -0.1F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                    }
                } else if (!isFirstPerson) {
                    GL11.glTranslatef(0.0F, -0.3F, 0.0F);
                    GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                }
            }
        }

        if (item instanceof LOTRItemLance && holder instanceof EntityLivingBase) {
            entityliving = (EntityLivingBase)holder;
            if (entityliving.getHeldItem() == itemstack) {
                if (isFirstPerson) {
                    GL11.glRotatef(260.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(-1.0F, 0.0F, 0.0F);
                } else {
                    GL11.glTranslatef(0.7F, 0.0F, 0.0F);
                    GL11.glRotatef(-30.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(-1.0F, 0.0F, 0.0F);
                }
            }
        }

        this.renderLargeItem();
        if (itemstack != null && itemstack.hasEffect(0)) {
            LOTRClientProxy.renderEnchantmentEffect();
        }

        GL11.glPopMatrix();
    }

    static {
        sizeFolders.put("large", 2.0F);
        sizeFolders.put("large2", 3.0F);
    }

    public static class ExtraLargeIconToken {
        public String name;
        public IIcon icon;

        public ExtraLargeIconToken(String s) {
            this.name = s;
        }
    }
}
