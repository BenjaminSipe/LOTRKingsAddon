package com.bsipe.lotrkingsaddon.common.recipes.anvil;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class AnvilRepairRecipe implements AnvilRecipe {

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public boolean exclusive() {
        return false;
    }

    @Override
    public AnvilRecipeResult apply(IInventory inventory, AnvilRecipeResult result) {
        boolean previousSuccess = result.getResult() != null;

        // the result stack will either be the exising one, or a copy of the stack in slot 1.
        ItemStack resultStack = previousSuccess ? result.getResult() : inventory.getStackInSlot( 0 ).copy();

        // if the result can't be damaged, leave it be.
        if ( ! resultStack.isItemDamaged() ) return result; // leave it as is.

        // if aren't already successful, and there's something in the second slot, fail.
        if ( ! previousSuccess && ( inventory.getStackInSlot( 1 ) != null )  ) return result.fail();

        // get full cost to repair.
        int costToFullyRepair = getCostToFullyRepair( resultStack );
        // get current anvil cost
        int currentCosts = result.cost + result.fee;

        // available material = material in stack 2 - current costs.
        int availableMaterial = (inventory.getStackInSlot( 2 ) == null ? 0 : inventory.getStackInSlot( 2 ).stackSize ) - currentCosts;

        // use all the available materail unless it's an NPC inventory, or the cost to repair is less than the material you have.
        int materialToUse = inventory.getSizeInventory() == 2 || availableMaterial > costToFullyRepair ? costToFullyRepair : availableMaterial;

        // if it's a negative number, then we don't have enough material to repair, so pass it along.
        if ( materialToUse < 0 && previousSuccess ) return result;

        // if there's enough to cover the fee, but not the repair, then assume they are checking how much would repair...
        if ( materialToUse <= 0 && ! previousSuccess ) materialToUse = 1;


        resultStack.setItemDamage( resultStack.getItemDamage() - getDurabilityRepaired( resultStack, materialToUse ) );

        return result.setResult( resultStack ).addCost( materialToUse );
    }

    @Override
    // basic repair needs to go last, so that it can use the "remaining" ingots for durability repair.
    public int priority() {
        return 90;
    }

    private static int getCostToFullyRepair( ItemStack stack ) {
        // math explanation: each ingredient repairs 1/4th a tools durability.
        // so damage / ( .25 * totalDurability ) re-ordered -> damage * 4 / totalDurability.
        return (int) Math.ceil( ( (double) stack.getItemDamage() * 4 ) / stack.getMaxDamage() );
    }

    private static int getDurabilityRepaired( ItemStack stack, double materialCount ) {
        return (int) Math.round( materialCount * stack.getMaxDamage() / 4 );
    }
}
