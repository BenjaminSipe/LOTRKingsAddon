package com.bsipe.lotrkingsaddon.common.items;

import java.util.Iterator;

import net.minecraft.block.BlockDispenser;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.bsipe.lotrkingsaddon.common.entities.LOTRAddonEntityThrowingDagger;

import lotr.common.enchant.LOTREnchantment;
import lotr.common.enchant.LOTREnchantmentHelper;
import lotr.common.item.LOTRItemDagger;
import lotr.common.item.LOTRMaterial;
import lotr.common.recipe.LOTRRecipes;

public class LOTRAddonThrowingDagger extends LOTRItemDagger {

    private Item.ToolMaterial axeMaterial;

    public LOTRAddonThrowingDagger(LOTRMaterial material) {
        this(material.toToolMaterial());
    }

    public LOTRAddonThrowingDagger(ToolMaterial material) {
        super(material);
        this.axeMaterial = material;
        BlockDispenser.dispenseBehaviorRegistry.putObject(this, new LOTRAddonDispenseThrowingDagger());

    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        LOTRAddonEntityThrowingDagger axe = new LOTRAddonEntityThrowingDagger(
            world,
            entityplayer,
            itemstack.copy(),
            2.0F);
        axe.setIsCritical(true);
        int fireAspect = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemstack)
            + LOTREnchantmentHelper.calcFireAspect(itemstack);
        if (fireAspect > 0) {
            axe.setFire(100);
        }

        Iterator var6 = LOTREnchantment.allEnchantments.iterator();

        while (var6.hasNext()) {
            LOTREnchantment ench = (LOTREnchantment) var6.next();
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

    @Override
    public boolean hitEntity(ItemStack itemstack, EntityLivingBase hitEntity, EntityLivingBase user) {
        boolean result = super.hitEntity(itemstack, hitEntity, user);
        itemstack.setItemDamage(0);
        return result;
    }

    public boolean getIsRepairable(ItemStack itemstack, ItemStack repairItem) {
        return LOTRRecipes.checkItemEquals(this.axeMaterial.getRepairItemStack(), repairItem) ? true
            : super.getIsRepairable(itemstack, repairItem);
    }

    public float getRangedDamageMultiplier(ItemStack itemstack, Entity shooter, Entity hit) {
        float damage = this.axeMaterial.getDamageVsEntity() + 4.0F;
        if (shooter instanceof EntityLivingBase && hit instanceof EntityLivingBase) {
            damage += EnchantmentHelper
                .getEnchantmentModifierLiving((EntityLivingBase) shooter, (EntityLivingBase) hit);
        } else {
            damage += EnchantmentHelper.func_152377_a(itemstack, EnumCreatureAttribute.UNDEFINED);
        }

        return damage * 0.5F;
    }
}
