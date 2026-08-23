package com.bsipe.lotrkingsaddon.client.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.bsipe.lotrkingsaddon.client.render.item.LOTRAddonRenderLargeItem;
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

import com.bsipe.lotrkingsaddon.client.render.entity.LOTRAddonRenderThrowingDagger;
import com.bsipe.lotrkingsaddon.common.entities.LOTRAddonEntityThrowingDagger;
import com.bsipe.lotrkingsaddon.common.modules.ToolsAndWeaponsModule;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lotr.client.render.item.LOTRRenderBow;
import lotr.client.render.item.LOTRRenderElvenBlade;
import lotr.client.render.item.LOTRRenderLargeItem;
import lotr.common.LOTRMod;
import lotr.common.item.LOTRItemBow;
import lotr.common.item.LOTRItemSword;

public class LOTRKingsAddonItemRendererManager implements IResourceManagerReloadListener {

    private static LOTRKingsAddonItemRendererManager INSTANCE;
    private static List<LOTRAddonRenderLargeItem> largeItemRenderers = new ArrayList<>();

    private static List<Item> ITEMS = new ArrayList<>();

    public static void addItemToRenderer( Item item ) {
        ITEMS.add( item );
    }

    public LOTRKingsAddonItemRendererManager() {}

    public static void load() {
        Minecraft mc = Minecraft.getMinecraft();
        IResourceManager resMgr = mc.getResourceManager();
        INSTANCE = new LOTRKingsAddonItemRendererManager();
        INSTANCE.onResourceManagerReload(resMgr);
        ((IReloadableResourceManager) resMgr).registerReloadListener(INSTANCE);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        RenderingRegistry
            .registerEntityRenderingHandler(LOTRAddonEntityThrowingDagger.class, new LOTRAddonRenderThrowingDagger());

    }

    public void onResourceManagerReload(IResourceManager resourceManager) {
        largeItemRenderers.clear();
        for (Item item : ITEMS) {
            MinecraftForgeClient.registerItemRenderer(item, (IItemRenderer) null);
            LOTRAddonRenderLargeItem largeItemRenderer = LOTRAddonRenderLargeItem.getRendererIfLarge(item);
            boolean isLarge = largeItemRenderer != null;
//            if (item instanceof LOTRItemBow) {
//                MinecraftForgeClient.registerItemRenderer(item, new LOTRRenderBow(largeItemRenderer));
//            } else if (item instanceof LOTRItemSword && ((LOTRItemSword) item).isElvenBlade()) {
//                double d = 24.0;
//                if (item == LOTRMod.sting) {
//                    d = 40.0;
//                }
//
//                MinecraftForgeClient.registerItemRenderer(item, new LOTRRenderElvenBlade(d, largeItemRenderer));
//            } else if (isLarge) {
            MinecraftForgeClient.registerItemRenderer(item, largeItemRenderer);
//            }

            if (largeItemRenderer != null) {
                largeItemRenderers.add(largeItemRenderer);
            }
        }
    }

    @SubscribeEvent
    public void preTextureStitch(TextureStitchEvent.Pre event) {
        TextureMap map = event.map;
        if (map.getTextureType() == 1) {
            Iterator var3 = largeItemRenderers.iterator();

            while (var3.hasNext()) {
                LOTRAddonRenderLargeItem largeRenderer = (LOTRAddonRenderLargeItem) var3.next();
                largeRenderer.registerIcons(map);
            }
        }
    }
}
