package com.bsipe.lotrkingsaddon.common.modules;

import static lotr.common.entity.LOTREntities.registerEntity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import lotr.common.item.LOTRItemAxe;
import lotr.common.item.LOTRItemHoe;
import lotr.common.item.LOTRItemPickaxe;
import lotr.common.item.LOTRItemShovel;
import lotr.common.item.LOTRItemSword;
import lotr.common.item.LOTRMaterial;
import net.minecraft.block.Block;
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

public class ToolsAndWeaponsModule extends AbstractModule {

    public static boolean SERVER_ONLY;
    public Config.ToolsAndWeaponsModuleConfig config;

    public ToolsAndWeaponsModule(boolean SERVER_ONLY) {
        ToolsAndWeaponsModule.SERVER_ONLY = SERVER_ONLY;
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

    public static Block addonAlloyForge;

    public void preInit(FMLPreInitializationEvent event) {

        if (config.loreWeapons()) {
            registerEntity(LOTRAddonEntityThrowingDagger.class, "ThrowingDagger", 2036, 64, Integer.MAX_VALUE, false);

            LEGENDARY = setMaterial("LEGENDARY", false, 10000, 4.0f, 8.0f, 4, 9.0f);
            rohanLoreSword = (new LOTRItemSword(LEGENDARY)).setCreativeTab(LOTRCreativeTabs.tabStory)
                .setUnlocalizedName("lotr:rohanLoreSword")
                .setTextureName("lotr:rohanLoreSword");
            gondorLoreDagger = (new LOTRAddonThrowingDagger(LEGENDARY)).setCreativeTab(LOTRCreativeTabs.tabStory)
                .setUnlocalizedName("lotr:gondorLoreDagger")
                .setTextureName("lotr:gondorLoreDagger");

            registerItem(rohanLoreSword);
            registerItem(gondorLoreDagger);

        }
        if (config.steelToolset()) {
            addonAlloyForge = (new LOTRAddonBlockAlloyForge()).setBlockName("lotr:alloyForge")
                .setBlockTextureName("lotr:alloyForge");

            STEEL = setMaterial("STEEL", true, 500, 2.0f, 0.6f, 2, 6.0f);
            steelIngot = new Item().setCreativeTab(LOTRCreativeTabs.tabMaterials)
                .setUnlocalizedName("lotr:steelIngot")
                .setTextureName("lotrkingsaddon:steelIngot");
            steelSword = new LOTRItemSword(STEEL).setCreativeTab(LOTRCreativeTabs.tabCombat)
                .setUnlocalizedName("lotr:steelSword")
                .setTextureName("lotrkingsaddon:steelSword");
            steelPickaxe = new LOTRItemPickaxe(STEEL).setCreativeTab(LOTRCreativeTabs.tabTools)
                .setUnlocalizedName("lotr:steelPickaxe")
                .setTextureName("lotrkingsaddon:steelPickaxe");
            steelShovel = new LOTRItemShovel(STEEL).setCreativeTab(LOTRCreativeTabs.tabTools)
                .setUnlocalizedName("lotr:steelShovel")
                .setTextureName("lotrkingsaddon:steelShovel");
            steelAxe = new LOTRItemAxe(STEEL).setCreativeTab(LOTRCreativeTabs.tabTools)
                .setUnlocalizedName("lotr:steelAxe")
                .setTextureName("lotrkingsaddon:steelAxe");
            steelHoe = new LOTRItemHoe(STEEL).setCreativeTab(LOTRCreativeTabs.tabTools)
                .setUnlocalizedName("lotr:steelHoe")
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
        if (config.loreWeapons()) {
            this.setCraftingItem(LEGENDARY, Items.iron_ingot);
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

            this.setCraftingItem(STEEL, steelIngot);
            GameRegistry.addShapedRecipe(
                new ItemStack(steelPickaxe),
                new Object[] { "sss", " t ", " t ", 's', steelIngot, 't', Items.stick });
            GameRegistry.addShapedRecipe(
                new ItemStack(steelAxe),
                new Object[] { "ss", "st", " t", 's', steelIngot, 't', Items.stick });
            GameRegistry.addShapedRecipe(
                new ItemStack(steelHoe),
                new Object[] { "ss", " t", " t", 's', steelIngot, 't', Items.stick });
            GameRegistry.addShapedRecipe(
                new ItemStack(steelShovel),
                new Object[] { "s", "t", "t", 's', steelIngot, 't', Items.stick });
            GameRegistry.addShapedRecipe(
                new ItemStack(steelSword),
                new Object[] { "s", "s", "t", 's', steelIngot, 't', Items.stick });

        }
        if (config.balanceRareWeapons()) {
            balanceRareWeapons();
        }

        if (config.balanceFactionGear()) {

        }
    }

    private void registerItem(Item item) {
        String prefixUnlocal = "item:lotr.";
        GameRegistry.registerItem(
            item,
            "item." + item.getUnlocalizedName()
                .substring(prefixUnlocal.length()));
    }

    private static void setCraftingItem(LOTRMaterial material, Item repairMaterial) {
        try {
            setProperty(LOTRMaterial.class, material, "setCraftingItem", Item.class, repairMaterial);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static LOTRMaterial setMaterial(String name, boolean damageable, int uses, float damage, float protection,
        int harvestLevel, float speed) {
        try {

            Class lotrMaterial = LOTRMaterial.class;
            Constructor<LOTRMaterial> c = lotrMaterial.getDeclaredConstructor(String.class);
            c.setAccessible(true);

            LOTRMaterial material = (c.newInstance(name));

            if (damageable == false) {
                runCommand(lotrMaterial, material, "setUndamageable");
            }
            setProperty(lotrMaterial, material, "setUses", int.class, uses);
            setProperty(lotrMaterial, material, "setDamage", float.class, damage);
            setProperty(lotrMaterial, material, "setProtection", float.class, protection);
            setProperty(lotrMaterial, material, "setHarvestLevel", int.class, harvestLevel);
            setProperty(lotrMaterial, material, "setSpeed", float.class, speed);
            return material;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
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

    }

    private static void setProperty(Class c, Object o, String methodName, Class parameter, Object... args)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = c.getDeclaredMethod(methodName, parameter);
        m.setAccessible(true);
        m.invoke(o, args);
    }

    private static void runCommand(Class c, Object o, String methodName)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = c.getDeclaredMethod(methodName, (Class[]) null);
        m.setAccessible(true);
        m.invoke(o);
    }
}
