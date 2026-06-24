package com.bsipe.lotrkingsaddon.recipes;

import lotr.common.LOTRMod;
import lotr.common.enchant.LOTREnchantment;
import lotr.common.item.LOTRItemModifierTemplate;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class EnchantedBookRecipe implements IRecipe {

    private LOTREnchantment lotrModifier;
    private int modifierScrollRequirementCount;


    private ItemStack result;
    private Item additionalItem;
    private boolean hasAdditionalItem;
    private boolean useAnyScroll;

    public EnchantedBookRecipe(Enchantment enchantment, int enchantmentResultLevel, LOTREnchantment lotrModifier, int modifierScrollRequirementCount ) {

        this( new ItemStack( Items.enchanted_book ), lotrModifier, modifierScrollRequirementCount, null );

        Items.enchanted_book.addEnchantment( this.result, new EnchantmentData( enchantment, enchantmentResultLevel ) );

    }


    public EnchantedBookRecipe(ItemStack result, LOTREnchantment lotrModifier, int modifierScrollRequirementCount, Item additionalItem ) {

        this.additionalItem = additionalItem;
        this.hasAdditionalItem = additionalItem != null;

        this.result = result;
        this.lotrModifier = lotrModifier;
        this.useAnyScroll = lotrModifier == null;
        this.modifierScrollRequirementCount = modifierScrollRequirementCount;
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        int matchingScrollCount = 0;
        boolean additionalItemMatched = ! this.hasAdditionalItem;
        for ( int i = 0; i < inventory.getSizeInventory() ; i ++ ) {
            if ( matchingScrollCount > this.modifierScrollRequirementCount ) return false;
            if ( inventory.getStackInSlot( i ) != null ) {
                if ( ! inventory.getStackInSlot( i ).getItem().equals( LOTRMod.modTemplate ) && additionalItemMatched ) {
                    return false;
                } else if ( this.hasAdditionalItem && inventory.getStackInSlot( i ).getItem().equals( this.additionalItem ) ) {
                    additionalItemMatched = true;
                } else if ( inventory.getStackInSlot( i ).getItem().equals( LOTRMod.modTemplate ) ) {
                    if ( useAnyScroll || LOTRItemModifierTemplate.getModifier( inventory.getStackInSlot( i ) ).equals( lotrModifier ) ) {
                        matchingScrollCount ++;
                    } else {
                        return false;
                    }
                }
            }
        }

        return matchingScrollCount == modifierScrollRequirementCount && additionalItemMatched;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        return result;
    }

    @Override
    public int getRecipeSize() {
        return modifierScrollRequirementCount + (hasAdditionalItem ? 1 : 0);
    }

    @Override
    public ItemStack getRecipeOutput() {
        return result;
    }
}
