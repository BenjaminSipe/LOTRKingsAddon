package com.bsipe.lotrkingsaddon.common.modules;

import static lotr.common.entity.LOTREntities.registerEntity;
import static net.minecraft.block.Block.soundTypeAnvil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.bsipe.lotrkingsaddon.MyMod;
import com.bsipe.lotrkingsaddon.common.blocks.LOTRAddonBlockAnvil;
import com.bsipe.lotrkingsaddon.common.items.BalrogWhipReplacement;
import com.bsipe.lotrkingsaddon.common.items.LOTRAddonItemAnvilBlock;
import com.bsipe.lotrkingsaddon.common.network.LOTRKingsPacketHandler;
import com.bsipe.lotrkingsaddon.common.network.packets.LOTRAddonOpenGuiPacket;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import lotr.common.LOTRConfig;
import lotr.common.item.LOTRItemAxe;
import lotr.common.item.LOTRItemBalrogWhip;
import lotr.common.item.LOTRItemHoe;
import lotr.common.item.LOTRItemPickaxe;
import lotr.common.item.LOTRItemShovel;
import lotr.common.item.LOTRItemSword;
import lotr.common.item.LOTRMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

import com.bsipe.lotrkingsaddon.Config;
import com.bsipe.lotrkingsaddon.common.blocks.LOTRAddonBlockAlloyForge;
import com.bsipe.lotrkingsaddon.common.entities.LOTRAddonEntityThrowingDagger;
import com.bsipe.lotrkingsaddon.common.entities.LOTRAddonTileEntityAlloyForge;
import com.bsipe.lotrkingsaddon.common.items.LOTRAddonThrowingDagger;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.ExistingSubstitutionException;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRCreativeTabs;
import lotr.common.LOTRMod;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class ToolsAndWeaponsModule extends AbstractModule {

    public Config.ToolsAndWeaponsModuleConfig config;

    public ToolsAndWeaponsModule() {
        this.config = Config.getToolsAndWeaponsModuleConfig();
    }

    public static LOTRMaterial LEGENDARY, STEEL;

    public static Item rohanLoreSword;
    public static Item gondorLoreDagger;
    public static Item steelIngot;
    public static Item steelSword;
    public static Item steelAxe;
    public static Item steelShovel;
    public static Item steelPickaxe;
    public static Item steelHoe;

    // replacement block/items
    public static Block addonAlloyForge;
    public static Block addonAnvilReplacement;
    public static Item addonItemAnvilBlockReplacement;
    public static Item balrogWhipReplacement;


    public void preInit(FMLPreInitializationEvent event) {

        if (config.loreWeapons()) {
            registerEntity(LOTRAddonEntityThrowingDagger.class, "ThrowingDagger", 2036, 64, Integer.MAX_VALUE, false);

            LEGENDARY = setMaterial("LEGENDARY", false, 10000, 4.0f, 8.0f, 4, 9.0f);
            rohanLoreSword = (new LOTRItemSword(LEGENDARY)).setCreativeTab(LOTRCreativeTabs.tabStory)
                .setUnlocalizedName("lotrkingsaddon:rohanLoreSword")
                .setTextureName("lotrkingsaddon:rohanLoreSword");
            gondorLoreDagger = (new LOTRAddonThrowingDagger(LEGENDARY)).setCreativeTab(LOTRCreativeTabs.tabStory)
                .setUnlocalizedName("lotrkingsaddon:gondorLoreDagger")
                .setTextureName("lotrkingsaddon:gondorLoreDagger");

            registerItem(rohanLoreSword);
            registerItem(gondorLoreDagger);
            if ( config.craftLegendaryGear() ) {
                addonAnvilReplacement = ( new LOTRAddonBlockAnvil().setHardness(5.0F).setStepSound(soundTypeAnvil).setResistance(2000.0F).setBlockName("minecraft:anvil"));
                addonItemAnvilBlockReplacement = ( new LOTRAddonItemAnvilBlock( addonAnvilReplacement ).setUnlocalizedName("anvil") );
            }
        }

        if ( config.balanceRareWeapons() ) {
            balrogWhipReplacement = (new BalrogWhipReplacement( config.balrogWhipCooldown() )).setUnlocalizedName("lotrkingsaddon:balrogWhip").setTextureName( "lotrkingsaddon:balrogWhip");
        }

        if (config.steelToolset()) {
            addonAlloyForge = (new LOTRAddonBlockAlloyForge()).setBlockName("lotr:alloyForge")
                .setBlockTextureName("lotr:alloyForge");

            STEEL = setMaterial("STEEL", true, 500, 2.0f, 0.6f, 2, 6.0f);
            steelIngot = new Item().setCreativeTab(LOTRCreativeTabs.tabMaterials)
                .setUnlocalizedName("lotrkingsaddon:steelIngot")
                .setTextureName("lotrkingsaddon:steelIngot");
            steelSword = new LOTRItemSword(STEEL).setCreativeTab(LOTRCreativeTabs.tabCombat)
                .setUnlocalizedName("lotrkingsaddon:steelSword")
                .setTextureName("lotrkingsaddon:steelSword");
            steelPickaxe = new LOTRItemPickaxe(STEEL).setCreativeTab(LOTRCreativeTabs.tabTools)
                .setUnlocalizedName("lotrkingsaddon:steelPickaxe")
                .setTextureName("lotrkingsaddon:steelPickaxe");
            steelShovel = new LOTRItemShovel(STEEL).setCreativeTab(LOTRCreativeTabs.tabTools)
                .setUnlocalizedName("lotrkingsaddon:steelShovel")
                .setTextureName("lotrkingsaddon:steelShovel");
            steelAxe = new LOTRItemAxe(STEEL).setCreativeTab(LOTRCreativeTabs.tabTools)
                .setUnlocalizedName("lotrkingsaddon:steelAxe")
                .setTextureName("lotrkingsaddon:steelAxe");
            steelHoe = new LOTRItemHoe(STEEL).setCreativeTab(LOTRCreativeTabs.tabTools)
                .setUnlocalizedName("lotrkingsaddon:steelHoe")
                .setTextureName("lotrkingsaddon:steelHoe");
            registerItem(steelIngot);
            registerItem(steelSword);
            registerItem(steelPickaxe);
            registerItem(steelShovel);
            registerItem(steelAxe);
            registerItem(steelHoe);

            LOTRAddonTileEntityAlloyForge.addRecipe(Items.iron_ingot, Items.coal, new ItemStack(steelIngot));
        }



    }

    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
        if (config.loreWeapons()) {
            setCraftingItem(LEGENDARY, Items.iron_ingot);
            if ( config.craftLegendaryGear() ) {
                try {
                    GameRegistry.addSubstitutionAlias( "minecraft:anvil", GameRegistry.Type.BLOCK, addonAnvilReplacement );
                    GameRegistry.addSubstitutionAlias(
                        "minecraft:anvil",
                        GameRegistry.Type.ITEM,
                        addonItemAnvilBlockReplacement);
                } catch (ExistingSubstitutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (config.steelToolset()) {
            try {
                GameRegistry.addSubstitutionAlias("lotr:tile.alloyForge", GameRegistry.Type.BLOCK, addonAlloyForge);
                GameRegistry.addSubstitutionAlias(
                    "lotr:tile.alloyForge",
                    GameRegistry.Type.ITEM,
                    new ItemBlock(addonAlloyForge));
            } catch (ExistingSubstitutionException e) {
                throw new RuntimeException(e);
            }
            GameRegistry.registerTileEntity(LOTRAddonTileEntityAlloyForge.class, "LOTRAddonAlloyForge");

            setCraftingItem(STEEL, steelIngot);
            GameRegistry.addShapedRecipe(
                new ItemStack(steelPickaxe),
                "sss", " t ", " t ", 's', steelIngot, 't', Items.stick);
            GameRegistry.addShapedRecipe(
                new ItemStack(steelAxe),
                "ss", "st", " t", 's', steelIngot, 't', Items.stick);
            GameRegistry.addShapedRecipe(
                new ItemStack(steelHoe),
                "ss", " t", " t", 's', steelIngot, 't', Items.stick);
            GameRegistry.addShapedRecipe(
                new ItemStack(steelShovel),
                "s", "t", "t", 's', steelIngot, 't', Items.stick);
            GameRegistry.addShapedRecipe(
                new ItemStack(steelSword),
                "s", "s", "t", 's', steelIngot, 't', Items.stick);

        }
        if (config.balanceRareWeapons()) {
            balanceRareWeapons();
            try {
                GameRegistry.addSubstitutionAlias("lotr:item.balrogWhip", GameRegistry.Type.ITEM, balrogWhipReplacement);
            } catch (ExistingSubstitutionException e) {
                throw new RuntimeException(e);
            }

        }

        if (config.balanceFactionGear()) {

        }
    }

    private void registerItem(Item item) {
        String prefixUnlocal = "item:lotrkingsaddon.";
        GameRegistry.registerItem(
            item,
            "item." + item.getUnlocalizedName()
                .substring(prefixUnlocal.length()));
    }

    private static void setCraftingItem(LOTRMaterial material, Item repairMaterial) {
        try {
            setProperty(material, "setCraftingItem", Item.class, repairMaterial);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static LOTRMaterial setMaterial(String name, boolean damageable, int uses, float damage, float protection,
        int harvestLevel, float speed) {
        try {

            Constructor<LOTRMaterial> c = LOTRMaterial.class.getDeclaredConstructor(String.class);
            c.setAccessible(true);

            LOTRMaterial material = (c.newInstance(name));

            if (!damageable) {
                Method m = LOTRMaterial.class.getDeclaredMethod("setUndamageable", (Class<?>[]) null);
                m.setAccessible(true);
                m.invoke(material);
            }
            setProperty(material, "setUses", int.class, uses);
            setProperty(material, "setDamage", float.class, damage);
            setProperty(material, "setProtection", float.class, protection);
            setProperty(material, "setHarvestLevel", int.class, harvestLevel);
            setProperty(material, "setSpeed", float.class, speed);
            return material;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private void balanceRareWeapons() {
        // items to adjust:
        /*
         * Mithril,
         * Mallorn Mace
         * Gondolin
         * Story Swrods
         * Utumno
         * list of each item in turn:
         * swordMithril,
         * spearMithril,
         * daggerMithril,
         * battleaxeMithril,
         * hammerMithril,
         * halberdMithril,
         * mattockMithril,
         * maceMallornCharred,
         * swordGondolin,
         * swordUtumno,
         * daggerUtumno,
         * daggerUtumnoPoisonedd,
         * spearUtumno,
         * battleaxeUtumno,
         * hammerUtumno,
         * anduril,
         * ringil,
         * sting,
         * glamdring,
         */

        List<Item> simpleNerfItems = Arrays.asList(
            LOTRMod.swordMithril,
            LOTRMod.spearMithril,
            LOTRMod.daggerMithril,
            LOTRMod.daggerMithrilPoisoned,
            LOTRMod.battleaxeMithril,
            LOTRMod.halberdMithril,
            LOTRMod.swordGondolin,
            LOTRMod.anduril,
            LOTRMod.ringil,
            LOTRMod.glamdring);
        List<Item> simpleBuffItems = Arrays.asList(
            LOTRMod.swordUtumno,
            LOTRMod.daggerUtumno,
            LOTRMod.daggerUtumnoPoisoned,
            LOTRMod.spearUtumno,
            LOTRMod.battleaxeUtumno,
            LOTRMod.hammerUtumno);

        for (Item item : simpleNerfItems) {
            ((LOTRItemSword) item).addWeaponDamage(-1.0f);
        }
        for (Item item : simpleBuffItems) {
            ((LOTRItemSword) item).addWeaponDamage(0.5f);
        }

        ReflectionHelper.setPrivateValue(ItemTool.class, (ItemTool) LOTRMod.shovelMithril, 5.0f, "damageVsEntity");
        ReflectionHelper.setPrivateValue(ItemTool.class, (ItemTool) LOTRMod.pickaxeMithril, 6.0f, "damageVsEntity");
        ReflectionHelper.setPrivateValue(ItemTool.class, (ItemTool) LOTRMod.mattockMithril, 6.0f, "damageVsEntity");
        ReflectionHelper.setPrivateValue(ItemTool.class, (ItemTool) LOTRMod.utumnoPickaxe, 6.0f, "damageVsEntity");
        ReflectionHelper.setPrivateValue(ItemTool.class, (ItemTool) LOTRMod.axeMithril, 7.0f, "damageVsEntity");

        ((LOTRItemSword) LOTRMod.maceMallornCharred).addWeaponDamage(-0.5f);
        // I need to nerf the mace.

    }

    private static void setProperty(Object o, String methodName, Class<?> parameter, Object... args)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = LOTRMaterial.class.getDeclaredMethod(methodName, parameter);
        m.setAccessible(true);
        m.invoke(o, args);
    }
}
