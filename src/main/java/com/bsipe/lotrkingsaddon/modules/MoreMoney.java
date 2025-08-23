package com.bsipe.lotrkingsaddon.modules;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lotr.common.item.LOTRItemCoin;
import net.minecraftforge.common.config.Configuration;

public class MoreMoney extends AbstractModule {

    public static boolean ENABLED;

    private static final String CONFIG_CATAGORY = "more_money_module";

    public MoreMoney( Configuration config ) {
        config.addCustomCategoryComment( CONFIG_CATAGORY, "Adds new coins with higher values, can be used normally and obtained via the currency exchange." );
        config.getBoolean( "higher_currencies_enabled", CONFIG_CATAGORY, true, "Adds higher coin denominations past 100" );
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        LOTRItemCoin.values = new int[] { 1, 10, 100, 1_000, 10_000, 100_000 };
    }
}
