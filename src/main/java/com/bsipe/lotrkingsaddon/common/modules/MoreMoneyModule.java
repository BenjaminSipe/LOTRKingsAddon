package com.bsipe.lotrkingsaddon.common.modules;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

import net.minecraftforge.oredict.RecipeSorter;

import com.bsipe.lotrkingsaddon.Config;
import com.bsipe.lotrkingsaddon.MyMod;
import com.bsipe.lotrkingsaddon.common.network.LOTRKingsPacketHandler;
import com.bsipe.lotrkingsaddon.common.recipes.CoinPouchRecipe;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import lotr.common.item.LOTRItemCoin;

public class MoreMoneyModule extends AbstractModule {

    public static Config.MoreMoneyModuleConfig config;

    private static LOTRKingsPacketHandler packetHandler;

    public MoreMoneyModule() {
        config = Config.getMoreMoneyModuleConfig();
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (config.higherCurrencies()) {
            LOTRItemCoin.values = new int[] { 1, 10, 100, 1_000, 10_000, 100_000 };
        }

        if (config.bulkCoinConversion()) {
            RecipeSorter.register(
                MyMod.MODID + ":bulkcoinconversion",
                CoinPouchRecipe.class,
                SHAPELESS,
                "after:minecraft:shapeless");

            GameRegistry.addRecipe(new CoinPouchRecipe(0));
            GameRegistry.addRecipe(new CoinPouchRecipe(1));
            if (config.higherCurrencies()) {
                GameRegistry.addRecipe(new CoinPouchRecipe(2));
                GameRegistry.addRecipe(new CoinPouchRecipe(3));
                GameRegistry.addRecipe(new CoinPouchRecipe(4));

            }
        }
        if (config.guiCoinConversion()) {
            packetHandler = new LOTRKingsPacketHandler();
        }
    }
}
