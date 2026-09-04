package com.bsipe.lotrkingsaddon.client.render.gui;

import com.bsipe.lotrkingsaddon.common.inventory.LOTRAddonContainerAnvil;
import lotr.common.LOTRAchievement;
import lotr.common.LOTRLevelData;
import lotr.common.inventory.LOTRContainerAnvil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class LOTRAddonSlotAnvilOutput extends Slot {
    private LOTRAddonContainerAnvil theAnvil;

    public LOTRAddonSlotAnvilOutput(LOTRAddonContainerAnvil container, IInventory inv, int id, int i, int j) {
        super(inv, id, i, j);
        this.theAnvil = container;
    }

    public boolean isItemValid(ItemStack itemstack) {
        return false;
    }

    public boolean canTakeStack(EntityPlayer entityplayer) {
        if (this.getHasStack()) {
            return this.theAnvil.materialCost > 0 ? this.theAnvil.hasMaterialOrCoinAmount(this.theAnvil.materialCost) : true;
        } else {
            return false;
        }
    }

    public void onPickupFromSlot(EntityPlayer entityplayer, ItemStack itemstack) {
        int materials = this.theAnvil.materialCost;
        boolean wasSmithCombine = this.theAnvil.isSmithScrollCombine;
        this.theAnvil.invInput.setInventorySlotContents(0, (ItemStack)null);
        ItemStack combinerItem = this.theAnvil.invInput.getStackInSlot(1);
        if (combinerItem != null) {
            --combinerItem.stackSize;
            if (combinerItem.stackSize <= 0) {
                this.theAnvil.invInput.setInventorySlotContents(1, (ItemStack)null);
            } else {
                this.theAnvil.invInput.setInventorySlotContents(1, combinerItem);
            }
        }

        if (materials > 0) {
            this.theAnvil.takeMaterialOrCoinAmount(materials);
        }

        if (!entityplayer.worldObj.isRemote && wasSmithCombine) {
            LOTRLevelData.getData(entityplayer).addAchievement(LOTRAchievement.combineSmithScrolls);
        }

        this.theAnvil.materialCost = 0;
        this.theAnvil.isSmithScrollCombine = false;
        this.theAnvil.playAnvilSound();
    }
}
