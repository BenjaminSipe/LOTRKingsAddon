package com.bsipe.lotrkingsaddon.entities;

import lotr.common.block.LOTRBlockAlloyForge;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class LOTRAddonBlockAlloyForge extends LOTRBlockAlloyForge {

    @Override
    public TileEntity createNewTileEntity(World world, int i ) {
        return new LOTRAddonTileEntityAlloyForge();
    }

}
