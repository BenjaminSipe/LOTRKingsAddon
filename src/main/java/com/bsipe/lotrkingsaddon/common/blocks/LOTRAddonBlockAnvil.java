package com.bsipe.lotrkingsaddon.common.blocks;

import lotr.common.LOTRMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class LOTRAddonBlockAnvil extends BlockAnvil {

    public AxisAlignedBB getCollisionBoundingBoxFromPool(Block block, World world, int i, int j, int k) {
        block.setBlockBoundsBasedOnState(world, i, j, k);
        return AxisAlignedBB.getBoundingBox((double)i + block.getBlockBoundsMinX(), (double)j + block.getBlockBoundsMinY(), (double)k + block.getBlockBoundsMinZ(), (double)i + block.getBlockBoundsMaxX(), (double)j + block.getBlockBoundsMaxY(), (double)k + block.getBlockBoundsMaxZ());
    }

    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {



        if (!worldIn.isRemote) {
            player.openGui(LOTRMod.instance, 52, worldIn, x, y, z);
        }
        return true;

    }
}
