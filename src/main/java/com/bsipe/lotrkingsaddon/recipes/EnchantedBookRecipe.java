package com.bsipe.lotrkingsaddon.recipes;

import lotr.common.LOTRMod;
import lotr.common.enchant.LOTREnchantment;
import lotr.common.item.LOTRItemModifierTemplate;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class EnchantedBookRecipe implements IRecipe {

    private LOTREnchantment lotrModifier;
    private int modifierScrollRequirementCount;

    private ItemStack result;

    public EnchantedBookRecipe( Enchantment enchantment, int enchantmentResultLevel, LOTREnchantment lotrModifier, int modifierScrollRequirementCount ) {
        ItemStack stack = new ItemStack( Items.enchanted_book );

        Items.enchanted_book.addEnchantment( stack, new EnchantmentData( enchantment, enchantmentResultLevel ) );

        result = stack;
        this.lotrModifier = lotrModifier;
        this.modifierScrollRequirementCount = modifierScrollRequirementCount;
    }
    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        int matchingScrollCount = 0;
        for ( int i = 0; i < inventory.getSizeInventory() ; i ++ ) {
            if ( inventory.getStackInSlot( i ) != null && inventory.getStackInSlot( i ).getItem().equals( LOTRMod.modTemplate ) ) {
                if ( LOTRItemModifierTemplate.getModifier( inventory.getStackInSlot( i ) ).equals( lotrModifier ) ) {
                    matchingScrollCount ++;
                } else {
                    return false;
                }
            }
        }

        return matchingScrollCount == modifierScrollRequirementCount;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        return result;
    }

    @Override
    public int getRecipeSize() {
        return modifierScrollRequirementCount;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return result;
    }
}
