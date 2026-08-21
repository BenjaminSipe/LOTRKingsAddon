package com.bsipe.lotrkingsaddon.common.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.bsipe.lotrkingsaddon.common.entities.LOTRAddonTileEntityAlloyForge;

import lotr.common.block.LOTRBlockAlloyForge;

public class LOTRAddonBlockAlloyForge extends LOTRBlockAlloyForge {

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new LOTRAddonTileEntityAlloyForge();
    }

}
