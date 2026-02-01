package com.bsipe.lotrkingsaddon.items;

import lotr.common.entity.projectile.LOTREntityThrowingAxe;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class LOTREntityThrowingDagger extends LOTREntityThrowingAxe {

    public LOTREntityThrowingDagger(World world, EntityLivingBase entityliving, ItemStack item, float charge) {
        super(world, entityliving, item, charge);
    }

    @Override
    protected ItemStack createPickupDrop(EntityPlayer entityplayer) {
        ItemStack itemstack = this.getProjectileItem();
        if (itemstack != null) {
            ItemStack itemPickup = itemstack.copy();
            if (itemPickup.isItemStackDamageable()) {
                if (itemPickup.getItemDamage() >= itemPickup.getMaxDamage()) {
                    return null;
                }
            }

            return itemPickup;
        } else {
            return null;
        }
    }
}
