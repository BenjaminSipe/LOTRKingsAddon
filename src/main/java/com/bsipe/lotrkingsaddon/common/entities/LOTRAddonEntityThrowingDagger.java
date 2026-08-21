package com.bsipe.lotrkingsaddon.common.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.bsipe.lotrkingsaddon.common.items.LOTRAddonThrowingDagger;

import lotr.common.entity.projectile.LOTREntityProjectileBase;

public class LOTRAddonEntityThrowingDagger extends LOTREntityProjectileBase {

    private int daggerRotation;

    public LOTRAddonEntityThrowingDagger(World world, EntityLivingBase entityliving, ItemStack item, float charge) {
        super(world, entityliving, item, charge);
    }

    public LOTRAddonEntityThrowingDagger(World world, ItemStack item, double d, double d1, double d2) {
        super(world, item, d, d1, d2);
    }

    public LOTRAddonEntityThrowingDagger(World world) {
        super(world);
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

    private boolean isThrowingAxe() {
        Item item = this.getProjectileItem()
            .getItem();
        return item instanceof LOTRAddonThrowingDagger;
    }

    public void onUpdate() {
        super.onUpdate();
        if (!this.inGround) {
            ++this.daggerRotation;
            if (this.daggerRotation > 9) {
                this.daggerRotation = 0;
            }

            this.rotationPitch = (float) this.daggerRotation / 9.0F * 360.0F;
        }

        if (!this.isThrowingAxe()) {
            this.setDead();
        }

    }

    public float getBaseImpactDamage(Entity entity, ItemStack itemstack) {
        if (!this.isThrowingAxe()) {
            return 0.0F;
        } else {
            float speed = MathHelper
                .sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            float damage = ((LOTRAddonThrowingDagger) itemstack.getItem())
                .getRangedDamageMultiplier(itemstack, this.shootingEntity, entity);
            return speed * damage;
        }
    }
}
