package com.bsipe.lotrkingsaddon.modules;

import cpw.mods.fml.common.registry.GameRegistry;
import lotr.common.LOTRMod;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CraftingModule {

    public static Config config;

    public CraftingModule(Config config) {
        this.config = config;
    };

    public static void load() {
        if ( ! config.MODULE_ENABLED ) return;

        if ( config.CRAFTABLE_QUARTZ ) {
            GameRegistry.addShapedRecipe( new ItemStack( Items.quartz, 4 ), new Object[] { " x ", "xvx", " x ", 'x', Blocks.sand, 'v', LOTRMod.salt });
        }

        if ( config.CRAFTABLE_REDSTONE ) {
            GameRegistry.addShapelessRecipe( new ItemStack( Items.redstone, 2 ), LOTRMod.bronze, Items.glowstone_dust );
        }
    }

    public static class Config {
        private final boolean MODULE_ENABLED;
        private final boolean CRAFTABLE_QUARTZ;
        private final boolean CRAFTABLE_REDSTONE;

        public Config( boolean moduleEnabled, boolean craftableQuartz, boolean craftableRedstone ) {
            this.MODULE_ENABLED = moduleEnabled;
            this.CRAFTABLE_QUARTZ = craftableQuartz;
            this.CRAFTABLE_REDSTONE = craftableRedstone;
        }
    }
}
