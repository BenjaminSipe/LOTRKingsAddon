package com.bsipe.lotrkingsaddon.modules;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import lotr.common.item.LOTRItemSword;
import lotr.common.item.LOTRMaterial;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;

public class LoreWeaponsModule extends AbstractModule {

    public static boolean ENABLED;
    public static String CATEGORY_NAME = "weapons_module";

    public LoreWeaponsModule( Configuration config ) {
//        config.addCustomCategoryComment( CATEGORY_NAME, "Planned Module will add custom weapons and armor specific to each faction." );
        ENABLED = false;
//        ENABLED = config.getBoolean( "weapons_module_enabled", CATEGORY_NAME, false, "Currently disabled by default, will control if custom weapons/armor are included " );
    }


    public static Item rohanLoreSword;

    public void preInit( FMLPreInitializationEvent event ) {

        if ( ! ENABLED ) return;

        rohanLoreSword = (new LOTRItemSword(LOTRMaterial.MITHRIL)).setUnlocalizedName("lotr:rohanLoreSword" ).setTextureName("lotr:rohanLoreSword");

        registerItem( rohanLoreSword );

    }


    private void registerItem(Item item) {
        String prefixUnlocal = "item:lotr.";
        GameRegistry.registerItem(item, "item." + item.getUnlocalizedName().substring(prefixUnlocal.length()));
    }

}
