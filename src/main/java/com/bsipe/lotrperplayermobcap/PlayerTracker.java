package com.bsipe.lotrperplayermobcap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;

public class PlayerTracker {
    int x,y,z;
    int mobCount;
    final int limit;
    boolean counted;
    EntityPlayer player;

    public PlayerTracker(EntityPlayer player, int limit) {
        this.player = player;
        this.x = player.chunkCoordX;
        this.y = player.chunkCoordY;
        this.z = player.chunkCoordZ;
        this.limit = limit * limit;
    }

    public int getMobCount() {
        return mobCount;
    }

    public void setCounted( boolean counted ) {
        this.counted = counted;
    }

    public boolean isCounted() { return counted; }

    public void increaseMobCap(int amount) {
        if ( ! counted ) return;
        mobCount += amount;
        counted = false;
    }

    public boolean getDistance(int a, int b, int c ) {
        int d1=x-a,d2=y-b,d3=z-c;
        return limit > d1*d1 + d2*d2 + d3*d3;
    }

    public boolean checkMobInCap( int a, int b, int c ) {

        return counted = getDistance( a, b, c );
    }

}
