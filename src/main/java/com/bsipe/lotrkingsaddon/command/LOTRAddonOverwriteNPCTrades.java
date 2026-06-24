package com.bsipe.lotrkingsaddon.command;

import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRMod;
import lotr.common.entity.npc.*;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.*;

public class LOTRAddonOverwriteNPCTrades extends CommandBase {

    private final static LOTRTradeEntry[] EMPTY_TRADES = new LOTRTradeEntry[] {};

    public final static Map<String, LOTRTradeEntry> SELL_TRADE_ENTRIES = new HashMap<>();
    public final static Map<String, LOTRTradeEntry> BUY_TRADE_ENTRIES = new HashMap<>();

    static {
        SELL_TRADE_ENTRIES.put( "WHEAT", new LOTRTradeEntry( new ItemStack( Items.wheat, 64 ), 20 ) );
        SELL_TRADE_ENTRIES.put( "WHEAT", new LOTRTradeEntry( new ItemStack( LOTRMod.pipeweedLeaf, 64 ), 20 ) );
        SELL_TRADE_ENTRIES.put( "WHEAT", new LOTRTradeEntry( new ItemStack( LOTRMod.flax, 64 ), 20 ) );
//        SELL_TRADE_ENTRIES.put( "MELON", new LOTRTradeEntry( new ItemStack( Items.melon, 64 ), 16 ) );
        SELL_TRADE_ENTRIES.put( "CARROT", new LOTRTradeEntry( new ItemStack( Items.carrot, 64 ), 12 ) );
        SELL_TRADE_ENTRIES.put( "POTATO", new LOTRTradeEntry( new ItemStack( Items.potato, 64 ), 12 ) );
        SELL_TRADE_ENTRIES.put( "LETTUCE", new LOTRTradeEntry( new ItemStack(LOTRMod.lettuce, 64 ), 12 ) );
        SELL_TRADE_ENTRIES.put( "CORN", new LOTRTradeEntry( new ItemStack( LOTRMod.corn, 64 ), 4 ) );
        SELL_TRADE_ENTRIES.put( "LEEK", new LOTRTradeEntry( new ItemStack( LOTRMod.leek, 64 ), 12 ) );
        SELL_TRADE_ENTRIES.put( "TURNIP", new LOTRTradeEntry( new ItemStack( LOTRMod.turnip, 64 ), 12 ) );
        SELL_TRADE_ENTRIES.put( "RED_GRAPE", new LOTRTradeEntry( new ItemStack( LOTRMod.grapeRed, 64 ), 30 ) );
        SELL_TRADE_ENTRIES.put( "WHITE_GRAPE", new LOTRTradeEntry( new ItemStack( LOTRMod.grapeWhite, 64 ), 30 ) );

        SELL_TRADE_ENTRIES.put( "BLUEBERRY", new LOTRTradeEntry( new ItemStack( LOTRMod.blueberry, 64 ), 16 ) );
        SELL_TRADE_ENTRIES.put( "BLACKBERRY", new LOTRTradeEntry( new ItemStack( LOTRMod.blackberry, 64 ), 16 ) );
        SELL_TRADE_ENTRIES.put( "RASPBERRY", new LOTRTradeEntry( new ItemStack( LOTRMod.raspberry, 64 ), 16 ) );
        SELL_TRADE_ENTRIES.put( "CRANBERRY", new LOTRTradeEntry( new ItemStack( LOTRMod.cranberry, 64 ), 16 ) );
        SELL_TRADE_ENTRIES.put( "ELDARBERRY", new LOTRTradeEntry( new ItemStack( LOTRMod.elderberry, 64 ), 16 ) );
    }

    @Override
    public String getCommandName() {
        return "trader";
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender player) {
        return "Just run the dang command.";
    }

    @Override
    public void processCommand(ICommandSender player, String[] args) {

        String mode = null;
        if ( args.length < 0 || args.length > 2 ) {
            throw new WrongUsageException( "Requires 1 or 2 arguments. . . idiot." );
        }

        String command = args[0];

        if ( "ADD".equalsIgnoreCase( command ) ) mode = "ADD";
        if ( "TEST".equalsIgnoreCase( command ) ) mode = "TEST";
        if ( "CLEAR".equalsIgnoreCase( command ) ) mode = "CLEAR";
        LOTRTradeEntry entry = null;
        if ( "ADD".equals( mode ) && args.length != 2 ) {
            throw new WrongUsageException("Must specify TRADE_ENTRY. or use test to confirm which trader is to be changed.");
        }
        if ( args.length == 2 ) {
            entry = SELL_TRADE_ENTRIES.getOrDefault( args[1].toUpperCase() , null );
            if ( entry == null ) {
                throw new WrongUsageException( "Invalid ENTRY, must be a valid sale type" );
            }
        }

        if ( mode == null ) {
            throw new WrongUsageException( "Invalid mode, select from clear, add, and test" );
        }

        LOTREntityNPC trader = (LOTREntityNPC) findNearestNPCTrader( player );

        if ( trader == null ) {
            // say that something failed.
            func_152373_a( player, this, "No nearby trader found.", new Object[0] );
            return;
        }
        switch ( mode ) {

            case "CLEAR":
                clearNpcTrades( trader );
                lockNpcTrades( trader );
                break;
            case "ADD":
                ArrayList<LOTRTradeEntry> sellEntries = getNpcTrades( trader, true );
                ArrayList<LOTRTradeEntry> buyEntries = getNpcTrades( trader, false );
                sellEntries.add( entry );
                setTraderNpcInfo( trader, sellEntries, true );
                setTraderNpcInfo( trader, buyEntries, false );
                break;
            case "TEST":
                func_152373_a( player, this, "Nearest Trader: " + trader.getNPCName(), new Object[0] );
                break;
        }
    }

    private void setTraderNpcInfo( LOTREntityNPC entity, List<LOTRTradeEntry> trades, boolean sellTrades )
    {
        LOTRTraderNPCInfo traderInfo = entity.traderNPCInfo;
        ReflectionHelper.setPrivateValue( LOTRTraderNPCInfo.class, traderInfo, copyArray( trades, traderInfo ), sellTrades ? "sellTrades" : "buyTrades" );
    }

    private void lockNpcTrades( LOTREntityNPC entity )
    {
        LOTRTraderNPCInfo tradeInfo = entity.traderNPCInfo;
        ReflectionHelper.setPrivateValue(LOTRTraderNPCInfo.class, tradeInfo, false, "shouldRefresh" );
        ReflectionHelper.setPrivateValue(LOTRTraderNPCInfo.class, tradeInfo, false, "shouldLockTrades" );
    }
    private void clearNpcTrades( LOTREntityNPC entity )
    {
        LOTRTraderNPCInfo tradeInfo = entity.traderNPCInfo;
        ReflectionHelper.setPrivateValue( LOTRTraderNPCInfo.class, tradeInfo, EMPTY_TRADES, "buyTrades" );
        ReflectionHelper.setPrivateValue( LOTRTraderNPCInfo.class, tradeInfo, EMPTY_TRADES, "sellTrades" );
    }

    private ArrayList<LOTRTradeEntry> getNpcTrades( LOTREntityNPC entityNPC, boolean getSellTrades ) {
        return new ArrayList( Arrays.asList( ReflectionHelper.getPrivateValue( LOTRTraderNPCInfo.class, entityNPC.traderNPCInfo, getSellTrades ? "sellTrades" : "buyTrades" ) ) );
    }

    private Entity findNearestNPCTrader( ICommandSender player ) {
        World world = player.getEntityWorld();
        double d = 5;
        double x = player.getPlayerCoordinates().posX;
        double y = player.getPlayerCoordinates().posY;
        double z = player.getPlayerCoordinates().posZ;

        AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox( x - d, y - d, z - d, x + d, y + d, z + d );
        List listOfEntities = world.getEntitiesWithinAABB(LOTRTradeable.class, boundingBox );
        Entity closest = null;
        double closestEntityDistance = -1;
        for ( Entity entity : ( (List<Entity>) listOfEntities ) ) {
            double xd = x- entity.serverPosX;
            double yd = y- entity.serverPosY;
            double zd = z- entity.serverPosZ;
            double distance = xd*xd + yd*yd + zd*zd;
            if ( closest == null ) {
                closest = entity;
                closestEntityDistance = distance;
            } else if ( distance < closestEntityDistance ) {
                closest = entity;
                closestEntityDistance = distance;
            }
        }

        return closest;
    }

    private LOTRTradeEntry copy( LOTRTradeEntry entry, LOTRTraderNPCInfo info ) {
        LOTRTradeEntry e = new LOTRTradeEntry( entry.createTradeItem(), entry.getCost() );
        e.setOwningTrader( info );
        return e;
    }

    private LOTRTradeEntry[] copyArray( List<LOTRTradeEntry> entries, LOTRTraderNPCInfo info ) {
        return (LOTRTradeEntry[]) entries.stream().map( entry -> copy( entry, info ) ).toArray();
    }
}
