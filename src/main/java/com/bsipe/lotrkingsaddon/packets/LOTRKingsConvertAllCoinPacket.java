package com.bsipe.lotrkingsaddon.packets;

import com.bsipe.lotrkingsaddon.modules.WaypointsModule;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import lotr.common.item.LOTRItemCoin;
import lotr.common.item.LOTRItemPouch;
import lotr.common.network.LOTRPacketRestockPouches;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LOTRKingsConvertAllCoinPacket implements IMessage {

    public LOTRKingsConvertAllCoinPacket() {

    }

    public void toBytes(ByteBuf data) {
    }

    public void fromBytes(ByteBuf data) {
    }


    public static class Handler implements IMessageHandler<LOTRKingsConvertAllCoinPacket, IMessage> {
        public Handler() {
        }

        public IMessage onMessage(LOTRKingsConvertAllCoinPacket packet, MessageContext context) {
            EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
            if (convertAllCoin(entityplayer)) {
                entityplayer.openContainer.detectAndSendChanges();
                entityplayer.worldObj.playSoundAtEntity(entityplayer, "lotr:event.trade", 0.5F, 1.0F);
            }

            return null;
        }
    }


    public static boolean convertAllCoin( EntityPlayerMP player ) {
        int value = LOTRItemCoin.getInventoryValue( player, false );

        InventoryPlayer inv = player.inventory;
        WaypointsModule.setAllFactionSpecificWPs();
        boolean hasChanged = false;
        for(int i = 0; i < inv.mainInventory.length; ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (itemstack != null) {
                if (itemstack.getItem() instanceof LOTRItemCoin) {
                    inv.setInventorySlotContents(i, (ItemStack)null);
                    hasChanged = true;

                }
            }
        }

        LOTRItemCoin.giveCoins( value, player );


        return hasChanged;
    }
}
