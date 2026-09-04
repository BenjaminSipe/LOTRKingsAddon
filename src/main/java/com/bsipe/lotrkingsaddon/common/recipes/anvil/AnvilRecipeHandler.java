package com.bsipe.lotrkingsaddon.common.recipes.anvil;

import net.minecraft.inventory.IInventory;

import java.util.ArrayList;
import java.util.List;

public class AnvilRecipeHandler {

    public AnvilRecipeHandler() {}

    public static List<AnvilRecipe> RECIPES = new ArrayList<>();


    static {
        addRecipe( new AnvilRepairRecipe() );
        addRecipe( new AnvilToolCombineRecipe() );
        addRecipe( new AnvilInputHandler() );
    }

    // recipes ordered by priority, then by inverse order added.
    public static void addRecipe( AnvilRecipe recipe ) {
        if ( ! recipe.enabled() ) return;
        int indexToInsert = 0;
        while (indexToInsert < RECIPES.size() && RECIPES.get( indexToInsert ).priority() < recipe.priority() ) {
            indexToInsert ++;
        }
        if ( indexToInsert == RECIPES.size() ) {
            RECIPES.add( recipe );
        } else {
            RECIPES.add( indexToInsert, recipe );
        }
    }

    public static AnvilRecipeResult apply( IInventory inventory ) {
        AnvilRecipeResult result = new AnvilRecipeResult();
        for ( AnvilRecipe recipe : RECIPES ) {
            // don't apply recipes if they are exclusive and there's already a result.
            if ( ! result.failed() && ( ! recipe.exclusive() || result.getResult() == null ) )
                result = recipe.apply( inventory, result );
        }

        return result;
    }

}
