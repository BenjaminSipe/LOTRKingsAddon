package com.bsipe.lotrkingsaddon.modules;

import com.bsipe.lotrkingsaddon.entities.LOTRAddonBlockAlloyForge;
import com.bsipe.lotrkingsaddon.entities.LOTRAddonTileEntityAlloyForge;
import com.bsipe.lotrkingsaddon.items.LOTRAddonThrowingDagger;
import com.bsipe.lotrkingsaddon.items.LOTRAddonEntityThrowingDagger;
import com.bsipe.lotrkingsaddon.renderer.LOTRKingsAddonItemRendererManager;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.ExistingSubstitutionException;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRCreativeTabs;
import lotr.common.LOTRMod;
import lotr.common.item.*;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.common.config.Configuration;
import scala.Array;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static lotr.common.entity.LOTREntities.registerEntity;

public class ToolsAndWeaponsModule extends AbstractModule {

    public static boolean ENABLED;
    public static String CATEGORY_NAME = "weapons_module";


    public static boolean LORE_WEAPONS_ENABLED;
    public static boolean STEEL_TOOLSET_ENABLED;
    public static boolean BALANCE_RARE_WEAPONS;
    public static boolean BALANCE_FACTION_GEAR;

    public ToolsAndWeaponsModule(Configuration config, boolean serverOnly ) {
        config.addCustomCategoryComment( CATEGORY_NAME, "Planned Module will add custom weapons and armor specific to each faction." );

        ENABLED = !serverOnly && config.getBoolean( "weapons_module_enabled", CATEGORY_NAME, true, "Adjust existing gear and add new gear for balance an fun." );
        LORE_WEAPONS_ENABLED = ENABLED && config.getBoolean( "lore_weapons_enabled", CATEGORY_NAME, false, "Add unique lore weapons that can only be obtained once." );
        STEEL_TOOLSET_ENABLED = ENABLED && config.getBoolean( "add_steel_tools", CATEGORY_NAME, true, "Add steel tools. Steel is a faction neutral iron-coal alloy." );
        BALANCE_RARE_WEAPONS = ENABLED && config.getBoolean( "balance_rare_weapons", CATEGORY_NAME, true, "Nerf/buff rare weapons in an attempt to balance gameplay." );
        BALANCE_FACTION_GEAR = ENABLED && config.getBoolean( "balance_faction_gear", CATEGORY_NAME, true, "Nerf/buff faction gear in an attempt to balance gameplay." );
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


    public void preInit( FMLPreInitializationEvent event ) {

        if ( ! ENABLED ) return;
        if ( LORE_WEAPONS_ENABLED ) {
            registerEntity(LOTRAddonEntityThrowingDagger.class, "ThrowingDagger", 2036, 64, Integer.MAX_VALUE, false);

            LEGENDARY = setMaterial( "LEGENDARY", false, 10000, 4.0f, 8.0f, 4, 9.0f );
            rohanLoreSword = (new LOTRItemSword(LEGENDARY)).setCreativeTab( LOTRCreativeTabs.tabStory ).setUnlocalizedName("lotr:rohanLoreSword" ).setTextureName("lotr:rohanLoreSword");
            gondorLoreDagger = (new LOTRAddonThrowingDagger(LEGENDARY)).setCreativeTab( LOTRCreativeTabs.tabStory ).setUnlocalizedName("lotr:gondorLoreDagger" ).setTextureName("lotr:gondorLoreDagger");

            registerItem( rohanLoreSword );
            registerItem( gondorLoreDagger );
            // this might be all I need.
            LOTRKingsAddonItemRendererManager.load();

        }
        if ( STEEL_TOOLSET_ENABLED ) {
            addonAlloyForge = ( new LOTRAddonBlockAlloyForge()).setBlockName( "lotr:alloyForge" ).setBlockTextureName( "lotr:alloyForge" );
            try {
                GameRegistry.addSubstitutionAlias( "lotr:tile.alloyForge", GameRegistry.Type.BLOCK, addonAlloyForge );
                GameRegistry.addSubstitutionAlias( "lotr:tile.alloyForge", GameRegistry.Type.ITEM, new ItemBlock(addonAlloyForge));
            } catch (ExistingSubstitutionException e) {
                throw new RuntimeException(e);
            }
            STEEL = setMaterial( "STEEL", true, 500, 2.0f, 0.6f, 2, 6.0f );
            steelIngot = new Item().setCreativeTab( LOTRCreativeTabs.tabMaterials ).setUnlocalizedName( "lotr:steelIngot" ).setTextureName( "lotrkingsaddon:steelIngot" );
            steelSword = new LOTRItemSword( STEEL ).setCreativeTab( LOTRCreativeTabs.tabCombat ).setUnlocalizedName( "lotr:steelSword" ).setTextureName( "lotrkingsaddon:steelSword" );
            steelPickaxe = new LOTRItemPickaxe( STEEL ).setCreativeTab( LOTRCreativeTabs.tabTools ).setUnlocalizedName( "lotr:steelPickaxe" ).setTextureName( "lotrkingsaddon:steelPickaxe" );
            steelShovel = new LOTRItemShovel( STEEL ).setCreativeTab( LOTRCreativeTabs.tabTools ).setUnlocalizedName( "lotr:steelShovel" ).setTextureName( "lotrkingsaddon:steelShovel" );
            steelAxe = new LOTRItemAxe( STEEL ).setCreativeTab( LOTRCreativeTabs.tabTools ).setUnlocalizedName( "lotr:steelAxe" ).setTextureName( "lotrkingsaddon:steelAxe" );
            steelHoe = new LOTRItemHoe( STEEL ).setCreativeTab( LOTRCreativeTabs.tabTools ).setUnlocalizedName( "lotr:steelHoe" ).setTextureName( "lotrkingsaddon:steelHoe" );
            registerItem( steelIngot );registerItem( steelSword );registerItem( steelPickaxe );registerItem( steelShovel );registerItem( steelAxe );registerItem( steelHoe );


            LOTRAddonTileEntityAlloyForge.addRecipe( Items.iron_ingot, Items.coal, new ItemStack( steelIngot ) );
        }

        if ( BALANCE_RARE_WEAPONS ) {
            balanceRareWeapons();
        }

        if ( BALANCE_FACTION_GEAR ) {

        }

    }

    public void init( FMLInitializationEvent event ) {
        if ( LORE_WEAPONS_ENABLED ) {
            this.setCraftingItem( LEGENDARY, Items.iron_ingot );
        }
        if ( STEEL_TOOLSET_ENABLED ) {
            GameRegistry.registerTileEntity( LOTRAddonTileEntityAlloyForge.class, "LOTRAddonAlloyForge" );

            this.setCraftingItem( STEEL, steelIngot );
            GameRegistry.addShapedRecipe( new ItemStack( steelPickaxe ), new Object[] { "sss", " t ", " t ", 's', steelIngot, 't', Items.stick } );
            GameRegistry.addShapedRecipe( new ItemStack( steelAxe ), new Object[] { "ss", "st", " t", 's', steelIngot, 't', Items.stick } );
            GameRegistry.addShapedRecipe( new ItemStack( steelHoe ), new Object[] { "ss", " t", " t", 's', steelIngot, 't', Items.stick } );
            GameRegistry.addShapedRecipe( new ItemStack( steelShovel ), new Object[] { "s", "t", "t", 's', steelIngot, 't', Items.stick } );
            GameRegistry.addShapedRecipe( new ItemStack( steelSword ), new Object[] { "s", "s", "t", 's', steelIngot, 't', Items.stick } );

        }
    }

    private void registerItem(Item item) {
        String prefixUnlocal = "item:lotr.";
        GameRegistry.registerItem(item, "item." + item.getUnlocalizedName().substring(prefixUnlocal.length()));
    }

    private static void setCraftingItem( LOTRMaterial material, Item repairMaterial ) {
        try {
            setProperty( LOTRMaterial.class, material, "setCraftingItem", Item.class, repairMaterial );
        } catch ( NoSuchMethodException e ) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static LOTRMaterial setMaterial( String name, boolean damageable, int uses, float damage, float protection, int harvestLevel, float speed ) {
        try {

            Class lotrMaterial = LOTRMaterial.class;
            Constructor<LOTRMaterial> c = lotrMaterial.getDeclaredConstructor( String.class );
            c.setAccessible( true );

            LOTRMaterial material = (c.newInstance( name ));

            if ( damageable == false ) {
                runCommand( lotrMaterial, material, "setUndamageable" );
            }
            setProperty( lotrMaterial, material, "setUses", int.class, uses );
            setProperty( lotrMaterial, material, "setDamage", float.class, damage );
            setProperty( lotrMaterial, material, "setProtection", float.class, protection );
            setProperty( lotrMaterial, material, "setHarvestLevel", int.class, harvestLevel );
            setProperty( lotrMaterial, material, "setSpeed", float.class, speed );
            return material;
        } catch ( NoSuchMethodException e ) {
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
        Mithril,
        Mallorn Mace
        Gondolin
        Story Swrods
        Utumno

        list of each item in turn:
        swordMithril,
        spearMithril,
        daggerMithril,
        battleaxeMithril,
        hammerMithril,
        halberdMithril,
        mattockMithril,
        maceMallornCharred,
        swordGondolin,
        swordUtumno,
        daggerUtumno,
        daggerUtumnoPoisonedd,
        spearUtumno,
        battleaxeUtumno,
        hammerUtumno,
        anduril,
        ringil,
        sting,
        glamdring,
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
                LOTRMod.hammerUtumno
                );

        for ( Item item : simpleNerfItems ) {
            ((LOTRItemSword) item ).addWeaponDamage( -1.0f );
        }
        for ( Item item : simpleBuffItems ) {
            ((LOTRItemSword) item ).addWeaponDamage( 0.5f );
        }

        ReflectionHelper.setPrivateValue( ItemTool.class, (ItemTool) LOTRMod.shovelMithril, 5.0f, "damageVsEntity" );
        ReflectionHelper.setPrivateValue( ItemTool.class, (ItemTool) LOTRMod.pickaxeMithril, 6.0f, "damageVsEntity" );
        ReflectionHelper.setPrivateValue( ItemTool.class, (ItemTool) LOTRMod.mattockMithril, 6.0f, "damageVsEntity" );
        ReflectionHelper.setPrivateValue( ItemTool.class, (ItemTool) LOTRMod.utumnoPickaxe, 6.0f, "damageVsEntity" );
        ReflectionHelper.setPrivateValue( ItemTool.class, (ItemTool) LOTRMod.axeMithril, 7.0f, "damageVsEntity" );

        ((LOTRItemSword)LOTRMod.maceMallornCharred).addWeaponDamage( -0.5f );

    }



    private static void setProperty( Class c, Object o, String methodName, Class parameter, Object... args ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = c.getDeclaredMethod( methodName, parameter );
        m.setAccessible( true );
        m.invoke( o, args );
    }

    private static void runCommand( Class c, Object o, String methodName ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = c.getDeclaredMethod( methodName, null );
        m.setAccessible( true );
        m.invoke( o );
    }

}
