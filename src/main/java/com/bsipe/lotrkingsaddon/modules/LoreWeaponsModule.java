package com.bsipe.lotrkingsaddon.modules;

import com.bsipe.lotrkingsaddon.material.AddonMaterial;
import com.bsipe.lotrkingsaddon.renderer.LOTRKingsAddonItemRendererManager;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import lotr.common.item.LOTRItemSword;
import lotr.common.item.LOTRMaterial;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;

public class LoreWeaponsModule extends AbstractModule {

    public static boolean ENABLED;
    public static String CATEGORY_NAME = "weapons_module";

//    public static LOTRMaterial MITHRIL = (new LOTRMaterial("MITHRIL")).setUses(2400).setDamage(5.0F).setProtection(0.8F).setHarvestLevel(4).setSpeed(9.0F).setEnchantability(8);
//    public static LOTRMaterial LEGENDARY =

    public LoreWeaponsModule( Configuration config ) {
        config.addCustomCategoryComment( CATEGORY_NAME, "Planned Module will add custom weapons and armor specific to each faction." );
//        ENABLED = false;
        ENABLED = config.getBoolean( "weapons_module_enabled", CATEGORY_NAME, false, "Currently disabled by default, will control if custom weapons/armor are included " );
        try {
            LOTRMaterial.allLOTRMaterials.add( ( (LOTRMaterial)( (Object) AddonMaterial.LEGENDARY ) ) );
        } catch (Exception e ) {
            System.out.println( "AN EXCEPTION OCCURRED" );
            System.out.println( e );
        }
    }


    public static Item rohanLoreSword;

    public void preInit( FMLPreInitializationEvent event ) {

        if ( ! ENABLED ) return;

        rohanLoreSword = (new LOTRItemSword(LOTRMaterial.MITHRIL)).setUnlocalizedName("lotr:rohanLoreSword" ).setTextureName("lotr:rohanLoreSword");

        registerItem( rohanLoreSword );

        // this might be all I need.
        LOTRKingsAddonItemRendererManager.load();

    }


    private void registerItem(Item item) {
        String prefixUnlocal = "item:lotr.";
        GameRegistry.registerItem(item, "item." + item.getUnlocalizedName().substring(prefixUnlocal.length()));
    }
}
