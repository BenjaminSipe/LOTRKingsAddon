package com.bsipe.lotrkingsaddon.common.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.bsipe.lotrkingsaddon.common.recipes.AlloyRecipe;

import lotr.common.tileentity.LOTRTileEntityAlloyForge;

public class LOTRAddonTileEntityAlloyForge extends LOTRTileEntityAlloyForge {

    public static List<AlloyRecipe> alloyRecipes = new ArrayList<>();

    public static void addRecipe(Item item, Item alloy, ItemStack result) {
        alloyRecipes.add(new AlloyRecipe(item, alloy, result));
    }

    public static void addRecipe(Function stack, Function alloy, ItemStack result) {
        alloyRecipes.add(new AlloyRecipe(stack, alloy, result));
    }

    public LOTRAddonTileEntityAlloyForge() {}

    @Override
    protected ItemStack getAlloySmeltingResult(ItemStack itemStack, ItemStack alloyStack) {
        return alloyRecipes.stream()
            .filter((recipe) -> recipe.checkMatch(itemStack, alloyStack))
            .map((entry) -> entry.getResult())
            .findAny()
            .orElse(super.getAlloySmeltingResult(itemStack, alloyStack));

    }

}
