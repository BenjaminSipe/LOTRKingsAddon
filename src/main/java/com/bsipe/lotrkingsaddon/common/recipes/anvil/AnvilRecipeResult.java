package com.bsipe.lotrkingsaddon.common.recipes.anvil;

import net.minecraft.item.ItemStack;

public class AnvilRecipeResult {
    ItemStack result = null;
    int cost = 0;
    int fee = 0;

    boolean fail = false;

    public AnvilRecipeResult() {}

    public AnvilRecipeResult( ItemStack result, int cost ) {
        this.result = result;
        this.cost = cost;
    }

    public ItemStack getResult() { return result; }

    public int getCost() { return cost; }

    public int getFee() { return fee; }

    public boolean success() {
        return ! fail && result != null;
    }

    public boolean failed() {
        return fail;
    }

    public AnvilRecipeResult fail() {
        this.fail = true;
        this.result = null;
        this.cost = 0;

        return this;
    }

    public AnvilRecipeResult addCost(int cost) {
        this.cost += cost;
        return this;
    }

    public AnvilRecipeResult setFee( int fee ) {
        this.fee = Math.max( this.fee, fee );
        return this;
    }

    public AnvilRecipeResult setResult( ItemStack result ) {
        this.result = result;
        return this;
    }
}
