package com.bsipe.lotrkingsaddon.modules;

import com.bsipe.lotrkingsaddon.items.LOTRItemThrowingDagger;
import com.bsipe.lotrkingsaddon.material.AddonMaterial;
import com.bsipe.lotrkingsaddon.renderer.LOTRKingsAddonItemRendererManager;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import lotr.common.item.LOTRItemSword;
import lotr.common.item.LOTRItemThrowingAxe;
import lotr.common.item.LOTRMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;

public class LoreWeaponsModule extends AbstractModule {

    public static boolean ENABLED;
    public static String CATEGORY_NAME = "weapons_module";

    public LoreWeaponsModule( Configuration config, boolean serverOnly ) {
        config.addCustomCategoryComment( CATEGORY_NAME, "Planned Module will add custom weapons and armor specific to each faction." );

        ENABLED = !serverOnly && config.getBoolean( "weapons_module_enabled", CATEGORY_NAME, false, "Currently disabled by default, will control if custom weapons/armor are included " );
        try {

        } catch (Exception e ) {
            System.out.println( "AN EXCEPTION OCCURRED" );
            System.out.println( e );
        }
    }


    public static Item rohanLoreSword;
    public static Item gondorLoreDagger;

    public void preInit( FMLPreInitializationEvent event ) {

        if ( ! ENABLED ) return;

        rohanLoreSword = (new LOTRItemSword(AddonMaterial.LEGENDARY.toToolMaterial())).setUnlocalizedName("lotr:rohanLoreSword" ).setTextureName("lotr:rohanLoreSword");
        gondorLoreDagger = (new LOTRItemThrowingDagger(AddonMaterial.LEGENDARY.toToolMaterial())).setUnlocalizedName("lotr:gondorLoreDagger" ).setTextureName("lotr:gondorLoreDagger");

        registerItem( rohanLoreSword );
        registerItem( gondorLoreDagger );

        // this might be all I need.
        LOTRKingsAddonItemRendererManager.load();

    }

    public void init( FMLInitializationEvent event ) {
        AddonMaterial.setCraftingItems();
    }

    private void registerItem(Item item) {
        String prefixUnlocal = "item:lotr.";
        GameRegistry.registerItem(item, "item." + item.getUnlocalizedName().substring(prefixUnlocal.length()));
    }
}
