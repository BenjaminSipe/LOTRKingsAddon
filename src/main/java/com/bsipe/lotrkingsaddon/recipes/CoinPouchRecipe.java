package com.bsipe.lotrkingsaddon.recipes;

import lotr.common.LOTRMod;
import lotr.common.inventory.LOTRInventoryPouch;
import lotr.common.item.LOTRItemCoin;
import lotr.common.item.LOTRItemPouch;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class CoinPouchRecipe implements IRecipe {

    public ItemStack result;
    private int coinIndex;

    public CoinPouchRecipe( int coinIndex ) {
        this.coinIndex = coinIndex;
        result = new ItemStack( LOTRMod.pouch );
        result.setItemDamage( 2 );
        LOTRInventoryPouch pouch = new LOTRInventoryPouch( result );

        pouch.setInventorySlotContents( 0, new ItemStack( LOTRMod.silverCoin, 64, coinIndex + 1 ) );
        pouch.setInventorySlotContents( 1, new ItemStack( LOTRMod.silverCoin, 64, coinIndex + 1 ) );
        pouch.setInventorySlotContents( 2, new ItemStack( LOTRMod.silverCoin, 44, coinIndex + 1 ) );
        pouch.setInventorySlotContents( 3, new ItemStack( LOTRMod.silverCoin,  8, coinIndex ) );
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        ItemStack pouch = null;
        for ( int i = 0; i < inventory.getSizeInventory() ; i ++ ) {
            ItemStack temp = inventory.getStackInSlot( i );
            if ( temp == null ) continue;
            if ( ! ( temp.getItem() instanceof LOTRItemPouch ) ) return false;

            if ( pouch != null ) return false;

            // subtype = large
            if ( temp.getItemDamage() != 0 ) return false;

            pouch = temp;

        }
        return pouch != null && pouchIsFullOfCoins( pouch );
    }

    public boolean pouchIsFullOfCoins( ItemStack pouch ) {
        LOTRInventoryPouch inventory = new LOTRInventoryPouch( pouch );

        for ( int i = 0 ; i < inventory.getSizeInventory() ; i ++ ) {
            ItemStack stack = inventory.getStackInSlot( i );
            if ( stack == null ) return false;
            if ( ! ( stack.getItem() instanceof LOTRItemCoin ) ) return false;
            if ( stack.stackSize != 64 ) return false;
            if ( stack.getItemDamage() != coinIndex ) return false;
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack temp = ItemStack.copyItemStack( result );
        int i = 0;
        ItemStack pouch;
        do {
            pouch = inventory.getStackInSlot( i++ );
        } while ( pouch == null );

        if ( LOTRItemPouch.isPouchDyed( pouch ) ) {
            LOTRItemPouch.setPouchColor( temp, LOTRItemPouch.getPouchColor( pouch ) );
        }
        temp.setStackDisplayName( pouch.getDisplayName() );

        return temp;
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.copyItemStack( result );
    }
}
