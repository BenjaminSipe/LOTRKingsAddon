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

    public ItemStack result = new ItemStack( LOTRMod.pouch, 1, 2 );
    private int coinIndex;

    public CoinPouchRecipe( int coinIndex ) {
        this.coinIndex = coinIndex;

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
            if ( temp.getItemDamage() != 2 ) return false;

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
        int i = 0;
        ItemStack pouch;
        do {
            pouch = inventory.getStackInSlot( i++ );
        } while ( pouch == null );

        result = pouch.copy();

        LOTRInventoryPouch pouchInv = new LOTRInventoryPouch( result );

        pouchInv.openInventory();

        pouchInv.setInventorySlotContents( 0, new ItemStack( LOTRMod.silverCoin, 64, coinIndex + 1 ) );
        pouchInv.setInventorySlotContents( 1, new ItemStack( LOTRMod.silverCoin, 64, coinIndex + 1 ) );
        pouchInv.setInventorySlotContents( 2, new ItemStack( LOTRMod.silverCoin, 44, coinIndex + 1 ) );
        pouchInv.setInventorySlotContents( 3, new ItemStack( LOTRMod.silverCoin,  8, coinIndex ) );
        for ( int j = 4 ; j < 27 ; j ++ )  pouchInv.setInventorySlotContents( j, null );

        if ( pouch.hasDisplayName() ) result.setStackDisplayName( pouch.getDisplayName() );
        if ( LOTRItemPouch.isPouchDyed( pouch ) ) LOTRItemPouch.setPouchColor( result, LOTRItemPouch.getPouchColor( pouch ) );


        return result;
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
