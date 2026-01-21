package com.bsipe.lotrkingsaddon.items;

import lotr.common.enchant.LOTREnchantment;
import lotr.common.enchant.LOTREnchantmentHelper;
import lotr.common.entity.projectile.LOTREntityThrowingAxe;
import lotr.common.item.LOTRItemDagger;
import lotr.common.item.LOTRItemThrowingAxe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Iterator;

public class LOTRItemThrowingDagger extends LOTRItemThrowingAxe {
    public LOTRItemThrowingDagger(ToolMaterial material) {
        super(material);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        LOTREntityThrowingDagger axe = new LOTREntityThrowingDagger(world, entityplayer, itemstack.copy(), 2.0F);
        axe.setIsCritical(true);
        int fireAspect = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemstack) + LOTREnchantmentHelper.calcFireAspect(itemstack);
        if (fireAspect > 0) {
            axe.setFire(100);
        }

        Iterator var6 = LOTREnchantment.allEnchantments.iterator();

        while(var6.hasNext()) {
            LOTREnchantment ench = (LOTREnchantment)var6.next();
            if (ench.applyToProjectile() && LOTREnchantmentHelper.hasEnchant(itemstack, ench)) {
                LOTREnchantmentHelper.setProjectileEnchantment(axe, ench);
            }
        }

        if (entityplayer.capabilities.isCreativeMode) {
            axe.canBePickedUp = 2;
        }

        world.playSoundAtEntity(entityplayer, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.25F);
        if (!world.isRemote) {
            world.spawnEntityInWorld(axe);
        }

        if (!entityplayer.capabilities.isCreativeMode) {
            --itemstack.stackSize;
        }

        return itemstack;
    }


}
