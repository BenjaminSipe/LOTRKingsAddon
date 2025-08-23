package com.bsipe.lotrperplayermobcap;

import com.bsipe.lotrperplayermobcap.client.ClientProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import lotr.common.item.LOTRItemCoin;
import lotr.common.item.LOTRItemSword;
import lotr.common.item.LOTRMaterial;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = Main.MODID, name= Main.NAME, version = Main.VERSION, acceptableRemoteVersions="*")
public class Main {

    public static ClientProxy proxy;

    public static Configuration config;

    public static boolean lotr;

    public static final String MODID = "lotr-more-money";
    public static final String VERSION = "1.0";
    public static final String NAME = "LOTR more money addon";


    public static Item rohanLoreSword;



    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        lotr = Loader.isModLoaded("lotr");
        if (!lotr) return;

        LOTRItemCoin.values = new int[] { 1, 10, 100, 1_000, 10_000, 100_000 };

        rohanLoreSword = (new LOTRItemSword(LOTRMaterial.ROHAN)).setUnlocalizedName("lotr:rohanLoreSword");
        rohanLoreSword.setTextureName( "lotr:rohanLoreSword" );
        this.registerItem( rohanLoreSword );

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        if (!lotr) return;
        FMLCommonHandler.instance().bus().register(this);

    }

    // HARD COPIED FROM LOTR BECAUSE LARGE TEXTURES MUST BE IN THE LOTR MOD dir.
    private void registerItem(Item item) {
        String prefixUnlocal = "item:lotr.";
        GameRegistry.registerItem(item, "item." + item.getUnlocalizedName().substring(prefixUnlocal.length()));
    }
}