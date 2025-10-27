package com.bsipe.lotrkingsaddon.modules;

import com.bsipe.lotrkingsaddon.Main;
import com.bsipe.lotrkingsaddon.recipes.CoinPouchRecipe;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import lotr.common.item.LOTRItemCoin;
import net.minecraft.item.crafting.RecipeBookCloning;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.RecipeSorter;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

public class MoreMoneyModule extends AbstractModule {

    public static boolean LARGER_COINS_ENABLED;
    public static boolean BULK_COIN_CONVERSION;

    private static final String CONFIG_CATAGORY = "more_money_module";

    public MoreMoneyModule(Configuration config ) {
        config.addCustomCategoryComment( CONFIG_CATAGORY, "Adds new coins with higher values, can be used normally and obtained via the currency exchange." );
        config.addCustomCategoryComment( CONFIG_CATAGORY, "This module is required by both client and server." );
        LARGER_COINS_ENABLED = config.getBoolean( "higher_currencies_enabled", CONFIG_CATAGORY, true, "Adds higher coin denominations past 100" );
        BULK_COIN_CONVERSION = config.getBoolean( "bulk_currency_conversion", CONFIG_CATAGORY, true, "Adds crafting of full large pouches of coins to greater denominations" );
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if ( LARGER_COINS_ENABLED ) {
            LOTRItemCoin.values = new int[] { 1, 10, 100, 1_000, 10_000, 100_000 };
        }

        if ( BULK_COIN_CONVERSION ) {
            RecipeSorter.register(Main.MODID + ":bulkcoinconversion",  CoinPouchRecipe.class,   SHAPELESS, "after:minecraft:shapeless");

            GameRegistry.addRecipe( new CoinPouchRecipe( 0 ) );
            GameRegistry.addRecipe( new CoinPouchRecipe( 1 ) );
            if ( LARGER_COINS_ENABLED ) {
                GameRegistry.addRecipe( new CoinPouchRecipe( 2 ) );
                GameRegistry.addRecipe( new CoinPouchRecipe( 3 ) );
                GameRegistry.addRecipe( new CoinPouchRecipe( 4 ) );

            }
        }



    }
}
