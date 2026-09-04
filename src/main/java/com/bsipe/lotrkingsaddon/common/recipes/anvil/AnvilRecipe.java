package com.bsipe.lotrkingsaddon.common.recipes.anvil;

import net.minecraft.inventory.IInventory;

public interface AnvilRecipe {

    boolean enabled();

    AnvilRecipeResult apply( IInventory inventory, AnvilRecipeResult result );

    boolean exclusive();

    int priority();
}
