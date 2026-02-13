//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.enovak.lotrmoremobs.entity.animal;

import java.util.List;
import lotr.common.LOTRMod;
import lotr.common.LOTRReflection;
import lotr.common.entity.ai.LOTREntityAIAttackOnCollide;
import lotr.common.entity.animal.LOTREntityHorse;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class LOTREntityMumakil extends LOTREntityHorse {
    public LOTREntityMumakil(World world) {
        super(world);
//        this.setSize(1.7F, 1.9F);
        this.setSize(8F, 15F);
    }

    protected boolean isMountHostile() {
        return true;
    }

    protected EntityAIBase createMountAttackAI() {
        return new LOTREntityAIAttackOnCollide(this, (double)1.0F, true);
    }

    public int getHorseType() {
        return 0;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue((double)4.0F);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT( nbt );

    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn) {}

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    protected void collideWithEntity(Entity p_82167_1_) {
        if ( p_82167_1_ instanceof IProjectile ) {
            super.collideWithEntity( p_82167_1_ );
        }
    }

    @Override
    protected void collideWithNearbyEntities() {}

    protected void onLOTRHorseSpawn() {
        double maxHealth = this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
        maxHealth *= (double)1.5F;
        maxHealth = Math.max(maxHealth, (double)40.0F);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(maxHealth);
        double speed = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
        speed *= 1.2;
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(speed);
        double jumpStrength = this.getEntityAttribute(LOTRReflection.getHorseJumpStrength()).getAttributeValue();
        jumpStrength *= (double)0.5F;
        this.getEntityAttribute(LOTRReflection.getHorseJumpStrength()).setBaseValue(jumpStrength);

    }

    protected double clampChildHealth(double health) {
        return MathHelper.clamp_double(health, (double)20.0F, (double)50.0F);
    }

    protected double clampChildJump(double jump) {
        return MathHelper.clamp_double(jump, 0.2, 0.8);
    }

    protected double clampChildSpeed(double speed) {
        return MathHelper.clamp_double(speed, 0.12, 0.42);
    }

    public boolean isBreedingItem(ItemStack itemstack) {
        return itemstack != null && itemstack.getItem() == Items.wheat;
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote) {
            if (this.riddenByEntity instanceof EntityLivingBase) {
                EntityLivingBase rhinoRider = (EntityLivingBase)this.riddenByEntity;
                float momentum = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                if (momentum > 0.2F) {
                    this.setSprinting(true);
                } else {
                    this.setSprinting(false);
                }

                if (momentum >= 0.32F) {
                    float strength = momentum * 15.0F;
                    Vec3 position = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
                    Vec3 look = this.getLookVec();
                    float sightWidth = 1.0F;
                    double range = (double)0.5F;
                    List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.contract((double)1.0F, (double)1.0F, (double)1.0F).addCoord(look.xCoord * range, look.yCoord * range, look.zCoord * range).expand((double)sightWidth, (double)sightWidth, (double)sightWidth));
                    boolean hitAnyEntities = false;

                    for(int i = 0; i < list.size(); ++i) {
                        Entity obj = (Entity)list.get(i);
                        if (obj instanceof EntityLivingBase) {
                            EntityLivingBase entity = (EntityLivingBase)obj;
                            if (entity != rhinoRider && (!(rhinoRider instanceof EntityPlayer) || LOTRMod.canPlayerAttackEntity((EntityPlayer)rhinoRider, entity, false)) && (!(rhinoRider instanceof EntityCreature) || LOTRMod.canNPCAttackEntity((EntityCreature)rhinoRider, entity, false))) {
                                boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), strength);
                                if (flag) {
                                    float knockback = strength * 0.05F;
                                    entity.addVelocity((double)(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * knockback), (double)knockback, (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * knockback));
                                    hitAnyEntities = true;
                                    if (entity instanceof EntityLiving) {
                                        EntityLiving entityliving = (EntityLiving)entity;
                                        if (entityliving.getAttackTarget() == this) {
                                            entityliving.getNavigator().clearPathEntity();
                                            entityliving.setAttackTarget(rhinoRider);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (hitAnyEntities) {
                        this.worldObj.playSoundAtEntity(this, "lotr:troll.ologHai_hammer", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                    }
                }
            } else if (this.getAttackTarget() != null) {
                float momentum = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                if (momentum > 0.2F) {
                    this.setSprinting(true);
                } else {
                    this.setSprinting(false);
                }
            } else {
                this.setSprinting(false);
            }
        }

    }

    protected void dropFewItems(boolean flag, int i) {
        int j = this.rand.nextInt(2) + this.rand.nextInt(1 + i);

        for(int k = 0; k < j; ++k) {
            this.dropItem(LOTRMod.rhinoHorn, 1);
        }

        int meat = this.rand.nextInt(3) + this.rand.nextInt(1 + i);

        for(int l = 0; l < meat; ++l) {
            if (this.isBurning()) {
                this.dropItem(LOTRMod.rhinoCooked, 1);
            } else {
                this.dropItem(LOTRMod.rhinoRaw, 1);
            }
        }

    }

    protected String getLivingSound() {
        super.getLivingSound();
        return "lotr:rhino.say";
    }

    protected String getHurtSound() {
        super.getHurtSound();
        return "lotr:rhino.hurt";
    }

    protected String getDeathSound() {
        super.getDeathSound();
        return "lotr:rhino.death";
    }

    protected String getAngrySoundName() {
        super.getAngrySoundName();
        return "lotr:rhino.say";
    }

    @Override
    public double getMountedYOffset() {
        double d = (double)this.height - .2f;
        if (this.riddenByEntity != null) {
            d += (double)this.riddenByEntity.yOffset - this.riddenByEntity.getYOffset();
        }

        return d;
    }
}
