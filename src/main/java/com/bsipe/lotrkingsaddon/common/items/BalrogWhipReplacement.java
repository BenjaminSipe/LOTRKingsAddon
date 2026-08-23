package com.bsipe.lotrkingsaddon.common.items;

import lotr.common.item.LOTRItemBalrogWhip;
import net.minecraft.item.ItemStack;

public class BalrogWhipReplacement extends LOTRItemBalrogWhip {

    private int maxDurationCooldown = 20;

    public BalrogWhipReplacement( int maxDurationCooldown ) {
        this.maxDurationCooldown = maxDurationCooldown;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemstack) {
        return maxDurationCooldown;
    }

}
