package com.bsipe.lotrkingsaddon.common.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRMod;
import lotr.common.enchant.LOTREnchantment;
import lotr.common.enchant.LOTREnchantmentHelper;
import lotr.common.entity.npc.LOTREntityNPC;
import lotr.common.entity.npc.LOTRTradeEntry;
import lotr.common.entity.npc.LOTRTradeable;
import lotr.common.entity.npc.LOTRTraderNPCInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LOTRAddonOverwriteNPCTrades extends CommandBase {

    private final static LOTRTradeEntry[] EMPTY_TRADES = new LOTRTradeEntry[] {};

    public final static Map<String, LOTRTradeEntry> SELL_TRADE_ENTRIES = new HashMap<>();
    public final static Map<String, List<LOTRTradeEntry>> BUY_TRADE_ENTRIES = new HashMap<>();

    static {
        setupBuyTrades();
        setupSellTrades();
    }

    private static void setupSellTrades() {
        SELL_TRADE_ENTRIES.put("WHEAT", new LOTRTradeEntry(new ItemStack(Items.wheat, 64), 20));
        SELL_TRADE_ENTRIES.put("PIPEWEED", new LOTRTradeEntry(new ItemStack(LOTRMod.pipeweedLeaf, 64), 20));
        SELL_TRADE_ENTRIES.put("FLAX", new LOTRTradeEntry(new ItemStack(LOTRMod.flax, 64), 20));
        // SELL_TRADE_ENTRIES.put( "MELON", new LOTRTradeEntry( new ItemStack( Items.melon, 64 ), 16 ) );
        SELL_TRADE_ENTRIES.put("CARROT", new LOTRTradeEntry(new ItemStack(Items.carrot, 64), 12));
        SELL_TRADE_ENTRIES.put("POTATO", new LOTRTradeEntry(new ItemStack(Items.potato, 64), 12));
        SELL_TRADE_ENTRIES.put("LETTUCE", new LOTRTradeEntry(new ItemStack(LOTRMod.lettuce, 64), 12));
        SELL_TRADE_ENTRIES.put("CORN", new LOTRTradeEntry(new ItemStack(LOTRMod.corn, 64), 4));
        SELL_TRADE_ENTRIES.put("LEEK", new LOTRTradeEntry(new ItemStack(LOTRMod.leek, 64), 12));
        SELL_TRADE_ENTRIES.put("TURNIP", new LOTRTradeEntry(new ItemStack(LOTRMod.turnip, 64), 12));
        SELL_TRADE_ENTRIES.put("RED_GRAPE", new LOTRTradeEntry(new ItemStack(LOTRMod.grapeRed, 64), 30));
        SELL_TRADE_ENTRIES.put("WHITE_GRAPE", new LOTRTradeEntry(new ItemStack(LOTRMod.grapeWhite, 64), 30));

        SELL_TRADE_ENTRIES.put("BLUEBERRY", new LOTRTradeEntry(new ItemStack(LOTRMod.blueberry, 64), 16));
        SELL_TRADE_ENTRIES.put("BLACKBERRY", new LOTRTradeEntry(new ItemStack(LOTRMod.blackberry, 64), 16));
        SELL_TRADE_ENTRIES.put("RASPBERRY", new LOTRTradeEntry(new ItemStack(LOTRMod.raspberry, 64), 16));
        SELL_TRADE_ENTRIES.put("CRANBERRY", new LOTRTradeEntry(new ItemStack(LOTRMod.cranberry, 64), 16));
        SELL_TRADE_ENTRIES.put("ELDARBERRY", new LOTRTradeEntry(new ItemStack(LOTRMod.elderberry, 64), 16));

    }

    private static void setupBuyTrades() {
        List<LOTRTradeEntry> GondorKingsArmoror = new ArrayList<>();
        GondorKingsArmoror.add(getBuyItem(LOTRMod.helmetGondor, 20, true, EnchantClass.ARMOR_BELE));
        GondorKingsArmoror.add(getBuyItem(LOTRMod.bodyGondor, 32, true, EnchantClass.BODY_BELE));
        GondorKingsArmoror.add(getBuyItem(LOTRMod.legsGondor, 26, true, EnchantClass.ARMOR_BELE));
        GondorKingsArmoror.add(getBuyItem(LOTRMod.bootsGondor, 17, true, EnchantClass.ARMOR_BELE));
        // GondorKingsArmoror.add( getBuyItem( LOTRMod.bodyGondor, 32, true, EnchantClass.BODY_EOL ) );
        GondorKingsArmoror.add(getBuyItem(LOTRMod.bodyGondor, 32, true, EnchantClass.ARMOR_EOL));
        GondorKingsArmoror.add(getBuyItem(LOTRMod.bootsGondor, 17, true, EnchantClass.ARMOR_EOL));
        GondorKingsArmoror.add(getBuyItem(LOTRMod.ironCrossbow, 15, true, EnchantClass.RANGED));
        GondorKingsArmoror.add(getBuyItem(LOTRMod.swordDolAmroth, 18, true, EnchantClass.MELEE));
        GondorKingsArmoror.add(getBuyItem(LOTRMod.battleaxeLossarnach, 18, true, EnchantClass.MELEE));

        BUY_TRADE_ENTRIES.put("GONDOR_KING", GondorKingsArmoror);
    }

    public static LOTRTradeEntry getBuyItem(Item item, int price, boolean calcPrice, EnchantClass enchants) {
        ItemStack stack = new ItemStack(item);
        LOTREnchantmentHelper.setEnchantList(stack, enchants.enchants);
        return new LOTRTradeEntry(stack, Math.round((calcPrice ? calcTradeValueFactor(stack) : 1) * price));
    }

    public static float calcTradeValueFactor(ItemStack itemstack) {
        float value = 1.0F;
        List<LOTREnchantment> enchants = LOTREnchantmentHelper.getEnchantList(itemstack);
        Iterator var3 = enchants.iterator();

        while (var3.hasNext()) {
            LOTREnchantment ench = (LOTREnchantment) var3.next();
            value *= ench.getValueModifier();
            if (ench.isSkilful()) {
                // value *= 1.5F;
            }
        }

        return value;
    }

    private enum EnchantClass {

        BODY_BELE(LOTREnchantment.protect2, LOTREnchantment.protectFire3),
        BODY_EOL(LOTREnchantment.protect2, LOTREnchantment.protectRanged3),
        ARMOR_BELE(LOTREnchantment.protect1, LOTREnchantment.protectFire3),
        ARMOR_EOL(LOTREnchantment.protect1, LOTREnchantment.protectRanged3),
        RANGED(LOTREnchantment.rangedStrong3, LOTREnchantment.rangedKnockback2),
        MELEE(LOTREnchantment.meleeReach1, LOTREnchantment.meleeSpeed1, LOTREnchantment.strong4);

        public List<LOTREnchantment> enchants;

        EnchantClass(LOTREnchantment... enchants) {
            this.enchants = Arrays.asList(enchants);
        }
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
        return "Run with 'TEST' to identify the nearest trader.\n"
            + "Then run with 'CLEAR' to clear and lock that trader's trades.\n"
            + "Run with 'OPTIONS' followed by 'BUY' or 'SELL' to see what options can be used for the final command.\n "
            + "Finally run with 'ADD' to add either an individual item (i.e. wheat, carrot, potato),\n"
            + "Or the armorer key for the sell trades ( only implemented for GONDOR_KING as of v1 ).";
    }

    @Override
    public void processCommand(ICommandSender player, String[] args) {

        String mode = null;
        if (args.length < 0 || args.length > 2) {
            throw new WrongUsageException("Requires 1 or 2 arguments. . . idiot.");
        }

        String command = args[0];

        if ("ADD".equalsIgnoreCase(command)) mode = "ADD";
        if ("TEST".equalsIgnoreCase(command)) mode = "TEST";
        if ("CLEAR".equalsIgnoreCase(command)) mode = "CLEAR";
        if ("OPTIONS".equalsIgnoreCase(command)) mode = "OPTIONS";

        if (mode == null) {
            throw new WrongUsageException("Invalid mode, select from TEST, OPTIONS, CLEAR or ADD");
        }

        LOTRTradeEntry entry = null;
        List<LOTRTradeEntry> entries = null;

        if ("ADD".equals(mode) && args.length != 2) {
            throw new WrongUsageException(
                "Must specify TRADE_ENTRY. or use test to confirm which trader is to be changed.");
        }

        if ("OPTIONS".equals(mode)) {
            if (args.length == 2 && args[1].toUpperCase()
                .equals("BUY")) {
                func_152373_a(
                    player,
                    this,
                    "Buy options are: [ " + String.join(", m", BUY_TRADE_ENTRIES.keySet()) + "]",
                    new Object[0]);

            } else {
                func_152373_a(
                    player,
                    this,
                    "Sell options are: [ " + String.join(", ", SELL_TRADE_ENTRIES.keySet()) + "]",
                    new Object[0]);

            }
            return;
        }

        if (args.length == 2) {
            entry = SELL_TRADE_ENTRIES.getOrDefault(args[1].toUpperCase(), null);
            entries = BUY_TRADE_ENTRIES.getOrDefault(args[1].toUpperCase(), null);
            if (entry == null && entries == null) {
                throw new WrongUsageException("Invalid ENTRY, must be a valid sale or buy type");
            }
        }

        LOTREntityNPC trader = (LOTREntityNPC) findNearestNPCTrader(player);

        if (trader == null) {
            // say that something failed.
            func_152373_a(player, this, "No nearby trader found.", new Object[0]);
            return;
        }
        switch (mode) {

            case "CLEAR":
                clearNpcTrades(trader);
                lockNpcTrades(trader);
                func_152373_a(
                    player,
                    this,
                    "Trader: " + trader.getNPCName() + "'s trades were cleared and locked.",
                    new Object[0]);

                break;
            case "ADD":
                if (entry != null) {
                    ArrayList<LOTRTradeEntry> sellEntries = getNpcTrades(trader, true);
                    sellEntries.add(entry);
                    setTraderNpcInfo(trader, sellEntries, true);
                    func_152373_a(
                        player,
                        this,
                        args[1].toUpperCase() + " add to " + trader.getNPCName() + "'s trades.",
                        new Object[0]);

                } else { // entries != null
                    setTraderNpcInfo(trader, entries, false);
                    func_152373_a(
                        player,
                        this,
                        args[1].toUpperCase() + " trades set for " + trader.getNPCName(),
                        new Object[0]);

                }

                break;
            case "TEST":
                func_152373_a(player, this, "Nearest Trader: " + trader.getNPCName(), new Object[0]);
                break;
        }
    }

    private void setTraderNpcInfo(LOTREntityNPC entity, List<LOTRTradeEntry> trades, boolean sellTrades) {
        LOTRTraderNPCInfo traderInfo = entity.traderNPCInfo;
        ReflectionHelper.setPrivateValue(
            LOTRTraderNPCInfo.class,
            traderInfo,
            copyArray(trades, traderInfo),
            sellTrades ? "sellTrades" : "buyTrades");
    }

    private void lockNpcTrades(LOTREntityNPC entity) {
        LOTRTraderNPCInfo tradeInfo = entity.traderNPCInfo;
        ReflectionHelper.setPrivateValue(LOTRTraderNPCInfo.class, tradeInfo, false, "shouldRefresh");
        ReflectionHelper.setPrivateValue(LOTRTraderNPCInfo.class, tradeInfo, false, "shouldLockTrades");
    }

    private void clearNpcTrades(LOTREntityNPC entity) {
        LOTRTraderNPCInfo tradeInfo = entity.traderNPCInfo;
        ReflectionHelper.setPrivateValue(LOTRTraderNPCInfo.class, tradeInfo, EMPTY_TRADES, "buyTrades");
        ReflectionHelper.setPrivateValue(LOTRTraderNPCInfo.class, tradeInfo, EMPTY_TRADES, "sellTrades");
    }

    private ArrayList<LOTRTradeEntry> getNpcTrades(LOTREntityNPC entityNPC, boolean getSellTrades) {
        return new ArrayList<>(
            Arrays.asList(
                ReflectionHelper.getPrivateValue(
                    LOTRTraderNPCInfo.class,
                    entityNPC.traderNPCInfo,
                    getSellTrades ? "sellTrades" : "buyTrades")));
    }

    private Entity findNearestNPCTrader(ICommandSender player) {
        World world = player.getEntityWorld();
        double d = 5;
        double x = player.getPlayerCoordinates().posX;
        double y = player.getPlayerCoordinates().posY;
        double z = player.getPlayerCoordinates().posZ;

        AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(x - d, y - d, z - d, x + d, y + d, z + d);
        List listOfEntities = world.getEntitiesWithinAABB(LOTRTradeable.class, boundingBox);
        Entity closest = null;
        double closestEntityDistance = -1;
        for (Entity entity : ((List<Entity>) listOfEntities)) {
            double xd = x - entity.serverPosX;
            double yd = y - entity.serverPosY;
            double zd = z - entity.serverPosZ;
            double distance = xd * xd + yd * yd + zd * zd;
            if (closest == null) {
                closest = entity;
                closestEntityDistance = distance;
            } else if (distance < closestEntityDistance) {
                closest = entity;
                closestEntityDistance = distance;
            }
        }

        return closest;
    }

    private LOTRTradeEntry copy(LOTRTradeEntry entry, LOTRTraderNPCInfo info) {
        LOTRTradeEntry e = new LOTRTradeEntry(entry.createTradeItem(), entry.getCost());
        e.setOwningTrader(info);
        return e;
    }

    private LOTRTradeEntry[] copyArray(List<LOTRTradeEntry> entries, LOTRTraderNPCInfo info) {
        return entries.stream()
            .map(entry -> copy(entry, info))
            .toArray(LOTRTradeEntry[]::new);
    }
}
