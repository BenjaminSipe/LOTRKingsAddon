package com.bsipe.lotrkingsaddon.entities;

import lotr.common.LOTRMod;
import lotr.common.tileentity.LOTRTileEntityAlloyForge;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LOTRAddonTileEntityAlloyForge extends LOTRTileEntityAlloyForge {

    public static List<AlloyRecipe> alloyRecipes = new ArrayList<>();

    public static void addRecipe( Item item, Item alloy, ItemStack result ) {
        alloyRecipes.add( new AlloyRecipe( item, alloy, result ) );
    }


    public static void addRecipe(Function stack, Function alloy, ItemStack result ) {
        alloyRecipes.add( new AlloyRecipe( stack, alloy, result ) );
    }

    public LOTRAddonTileEntityAlloyForge() {
    }

    @Override
    protected ItemStack getAlloySmeltingResult( ItemStack itemStack, ItemStack alloyStack ) {
        return alloyRecipes.stream().filter( (recipe) -> recipe.checkMatch( itemStack, alloyStack ) ).map( (entry) -> entry.getResult() ).findAny().orElse( super.getAlloySmeltingResult( itemStack, alloyStack ) );

    }
}

class AlloyRecipe {
    Function<ItemStack, Boolean> itemStackCheck;
    Function<ItemStack, Boolean> alloyStackCheck;
    ItemStack result;

    public static Function getPredicateForItem( Item item ) {
        return functionMap.getOrDefault( item, itemStack -> ( (ItemStack) itemStack ).getItem().equals( item ) );
    }

    public static Map<Item, Function> functionMap = new HashMap<>();

    static {
        functionMap.put( Items.iron_ingot, itemStack -> AlloyRecipe.isIron( (ItemStack) itemStack ));
        functionMap.put( LOTRMod.copper, itemStack -> AlloyRecipe.isCopper( (ItemStack) itemStack ));
        functionMap.put( LOTRMod.tin, itemStack -> AlloyRecipe.isTin( (ItemStack) itemStack ));
        functionMap.put( Items.gold_ingot, itemStack -> AlloyRecipe.isGold( (ItemStack) itemStack ));
        functionMap.put( Items.gold_nugget, itemStack -> AlloyRecipe.isGoldNugget( (ItemStack) itemStack ));
        functionMap.put( LOTRMod.silver, itemStack -> AlloyRecipe.isSilver( (ItemStack) itemStack ));
        functionMap.put( LOTRMod.silverNugget, itemStack -> AlloyRecipe.isSilverNugget( (ItemStack) itemStack ));
        functionMap.put( LOTRMod.mithril, itemStack -> AlloyRecipe.isMithril( (ItemStack) itemStack ));
        functionMap.put( LOTRMod.mithrilNugget, itemStack -> AlloyRecipe.isMithrilNugget( (ItemStack) itemStack ));
        functionMap.put( LOTRMod.orcSteel, itemStack -> AlloyRecipe.isOrcSteel( (ItemStack) itemStack ));
        functionMap.put( Items.coal, itemStack -> AlloyRecipe.isCoal( (ItemStack) itemStack ));
        functionMap.put( ItemBlock.getItemFromBlock( Blocks.log ), itemStack -> AlloyRecipe.isWood( (ItemStack) itemStack ));
    }

    public AlloyRecipe( Item item, Item alloy, ItemStack result ) {
        this ( getPredicateForItem( item ), getPredicateForItem( alloy ), result );
    }

    public AlloyRecipe( Function itemStackCheck, Function alloyStackCheck, ItemStack result ) {
        this.itemStackCheck = itemStackCheck;
        this.alloyStackCheck = alloyStackCheck;
        this.result = result;
    }

    public boolean checkStack( ItemStack itemStack ) {
        return this.itemStackCheck.apply( itemStack );
    }

    public boolean checkAlloy( ItemStack alloyStack ) {
        return this.alloyStackCheck == null ? alloyStack == null : this.alloyStackCheck.apply( alloyStack );
    }

    public boolean checkMatch( ItemStack stack, ItemStack alloy ) {
        return checkStack( stack ) && checkAlloy( alloy );
    }

    public ItemStack getResult() { return this.result; }

    public static boolean isCopper(ItemStack itemstack) {
        return LOTRMod.isOreNameEqual(itemstack, "oreCopper") || LOTRMod.isOreNameEqual(itemstack, "ingotCopper");
    }

    public static boolean isTin(ItemStack itemstack) {
        return LOTRMod.isOreNameEqual(itemstack, "oreTin") || LOTRMod.isOreNameEqual(itemstack, "ingotTin");
    }

    public static boolean isIron(ItemStack itemstack) {
        return LOTRMod.isOreNameEqual(itemstack, "oreIron") || LOTRMod.isOreNameEqual(itemstack, "ingotIron");
    }

    public static boolean isGold(ItemStack itemstack) {
        return LOTRMod.isOreNameEqual(itemstack, "oreGold") || LOTRMod.isOreNameEqual(itemstack, "ingotGold");
    }

    public static boolean isGoldNugget(ItemStack itemstack) {
        return LOTRMod.isOreNameEqual(itemstack, "nuggetGold");
    }

    public static boolean isSilver(ItemStack itemstack) {
        return LOTRMod.isOreNameEqual(itemstack, "oreSilver") || LOTRMod.isOreNameEqual(itemstack, "ingotSilver");
    }

    public static boolean isSilverNugget(ItemStack itemstack) {
        return LOTRMod.isOreNameEqual(itemstack, "nuggetSilver");
    }

    public static boolean isMithril(ItemStack itemstack) {
        return itemstack.getItem() == Item.getItemFromBlock(LOTRMod.oreMithril) || itemstack.getItem() == LOTRMod.mithril;
    }

    public static boolean isMithrilNugget(ItemStack itemstack) {
        return itemstack.getItem() == LOTRMod.mithrilNugget;
    }

    public static boolean isOrcSteel(ItemStack itemstack) {
        return itemstack.getItem() == Item.getItemFromBlock(LOTRMod.oreMorgulIron) || itemstack.getItem() == LOTRMod.orcSteel;
    }

    public static boolean isWood(ItemStack itemstack) {
        return LOTRMod.isOreNameEqual(itemstack, "logWood");
    }

    public static boolean isCoal(ItemStack itemstack) {
        return itemstack.getItem() == Items.coal;
    }
}
