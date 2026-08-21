package com.bsipe.lotrkingsaddon.common.entities;

import net.minecraft.world.World;

import lotr.common.entity.npc.LOTREntityRangerNorth;

public class LOTRAddonEntityRangerNorth extends LOTREntityRangerNorth {

    public LOTRAddonEntityRangerNorth(World world) {
        super(world);
    }

    @Override
    public boolean isRangerSneaking() {
        return false;
    }

    @Override
    public void setRangerSneaking(boolean flag) {
        this.dataWatcher.updateObject(17, Byte.valueOf((byte) (0)));
    }

}
