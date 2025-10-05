package com.bsipe.lotrkingsaddon.renderer;

import com.bsipe.lotrkingsaddon.modules.LoreWeaponsModule;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lotr.client.LOTRItemRendererManager;
import lotr.client.render.item.*;
import lotr.client.render.tileentity.LOTRRenderAnimalJar;
import lotr.common.LOTRMod;
import lotr.common.item.LOTRItemAnimalJar;
import lotr.common.item.LOTRItemBow;
import lotr.common.item.LOTRItemCrossbow;
import lotr.common.item.LOTRItemSword;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LOTRKingsAddonItemRendererManager implements IResourceManagerReloadListener {
    private static LOTRKingsAddonItemRendererManager INSTANCE;
    private static List<LOTRRenderLargeItem> largeItemRenderers = new ArrayList<>();

    public LOTRKingsAddonItemRendererManager() {}

    public static void load() {
        Minecraft mc = Minecraft.getMinecraft();
        IResourceManager resMgr = mc.getResourceManager();
        INSTANCE = new LOTRKingsAddonItemRendererManager();
        INSTANCE.onResourceManagerReload(resMgr);
        ((IReloadableResourceManager)resMgr).registerReloadListener(INSTANCE);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    public void onResourceManagerReload(IResourceManager resourceManager) {
        largeItemRenderers.clear();

        Item sword = LoreWeaponsModule.rohanLoreSword;
        MinecraftForgeClient.registerItemRenderer(sword, (IItemRenderer)null);
        LOTRRenderLargeItem largeItemRenderer = LOTRRenderLargeItem.getRendererIfLarge(sword);
        boolean isLarge = largeItemRenderer != null;
        if ( sword instanceof LOTRItemBow) {
            MinecraftForgeClient.registerItemRenderer(sword, new LOTRRenderBow(largeItemRenderer));
        } else if (sword instanceof LOTRItemSword && ((LOTRItemSword)sword).isElvenBlade()) {
            double d = 24.0;
            if (sword == LOTRMod.sting) {
                d = 40.0;
            }

            MinecraftForgeClient.registerItemRenderer(sword, new LOTRRenderElvenBlade(d, largeItemRenderer));
        } else if (isLarge) {
            MinecraftForgeClient.registerItemRenderer(sword, largeItemRenderer);
        }


        if (largeItemRenderer != null) {
            largeItemRenderers.add(largeItemRenderer);
        }
    }

    @SubscribeEvent
    public void preTextureStitch(TextureStitchEvent.Pre event) {
        TextureMap map = event.map;
        if (map.getTextureType() == 1) {
            Iterator var3 = largeItemRenderers.iterator();

            while(var3.hasNext()) {
                LOTRRenderLargeItem largeRenderer = (LOTRRenderLargeItem)var3.next();
                largeRenderer.registerIcons(map);
            }
        }

    }
}
