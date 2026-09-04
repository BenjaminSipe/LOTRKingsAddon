package com.bsipe.lotrkingsaddon.common.recipes.anvil;

import lotr.common.LOTRMod;
import lotr.common.enchant.LOTREnchantment;
import lotr.common.enchant.LOTREnchantmentHelper;
import lotr.common.item.LOTRItemChisel;
import lotr.common.item.LOTRMaterial;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

import java.util.Iterator;
import java.util.Map;

public class AnvilInputHandler implements AnvilRecipe {
    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public boolean exclusive() {
        return false;
    }

    // this one doesn't do any important recipe crap. It will just check if there is anything wrong.
    @Override
    public AnvilRecipeResult apply(IInventory inventory, AnvilRecipeResult result) {
        boolean isTrader = inventory.getSizeInventory() == 2;
        ItemStack input = inventory.getStackInSlot( 0 );

        // fail if input is null, or if there's a repair item and it is wrong.
        if ( input == null || ( !isTrader && inventory.getStackInSlot( 2 ) != null
            && ! isRepairMaterial( input, inventory.getStackInSlot( 2 ) ) ) ) return result.fail();

        // this will only be half the fee. . . as combine recipes will have to modify it, but that's ok.
        return result.setFee( getBaseAnvilCost( input ) );
    }

    @Override
    public int priority() {
        return -1000;
    }

    public static boolean isRepairMaterial(ItemStack inputItem, ItemStack materialItem) {
        if (inputItem.getItem().getIsRepairable(inputItem, materialItem)) {
            return true;
        } else {
            Item item = inputItem.getItem();
            if (item == Items.bow && LOTRMod.rohanBow.getIsRepairable(inputItem, materialItem)) {
                return true;
            } else if (item instanceof ItemFishingRod && materialItem.getItem() == Items.string) {
                return true;
            } else if (item instanceof ItemShears && materialItem.getItem() == Items.iron_ingot) {
                return true;
            } else if (item instanceof LOTRItemChisel && materialItem.getItem() == Items.iron_ingot) {
                return true;
            } else if (item instanceof ItemEnchantedBook && materialItem.getItem() == Items.paper) {
                return true;
            } else {
                Item.ToolMaterial material = null;
                if (item instanceof ItemTool) {
                    material = Item.ToolMaterial.valueOf(((ItemTool)item).getToolMaterialName());
                } else if (item instanceof ItemSword) {
                    material = Item.ToolMaterial.valueOf(((ItemSword)item).getToolMaterialName());
                }

                if (material != Item.ToolMaterial.WOOD && material != LOTRMaterial.MOREDAIN_WOOD.toToolMaterial()) {
                    if (material == LOTRMaterial.MALLORN.toToolMaterial()) {
                        return materialItem.getItem() == Item.getItemFromBlock(LOTRMod.planks) && materialItem.getItemDamage() == 1;
                    } else if (material != LOTRMaterial.MALLORN_MACE.toToolMaterial()) {
                        if (item instanceof ItemArmor) {
                            ItemArmor armor = (ItemArmor)item;
                            ItemArmor.ArmorMaterial armorMaterial = armor.getArmorMaterial();
                            if (armorMaterial == LOTRMaterial.BONE.toArmorMaterial()) {
                                return LOTRMod.isOreNameEqual(materialItem, "bone");
                            }
                        }

                        return false;
                    } else {
                        return materialItem.getItem() == Item.getItemFromBlock(LOTRMod.wood) && materialItem.getItemDamage() == 1;
                    }
                } else {
                    return LOTRMod.isOreNameEqual(materialItem, "plankWood");
                }
            }
        }
    }

    public static int getBaseAnvilCost( ItemStack stack ) {
        int baseCost = LOTREnchantmentHelper.getAnvilCost( stack );

        // vanilla enchants
        Map<Integer, Integer> vanillaEnchants = EnchantmentHelper.getEnchantments( stack );
        Iterator<Integer> i = vanillaEnchants.keySet().iterator();

        int numberOfEnchants = 0;

        while( i.hasNext() ) {
            Integer enchantIndex = i.next();
            Enchantment enchantment = Enchantment.enchantmentsList[ enchantIndex ];
            numberOfEnchants ++;
            int enchantmentLevel = vanillaEnchants.get( enchantIndex );
            int costPerLevel = switch ( enchantment.getWeight() ) {
              case ( 1 )  -> 8;
              case ( 2 )  -> 4;
              case ( 5 )  -> 2;
              case ( 10 ) -> 1;
              default -> 0;
            };

            baseCost += numberOfEnchants + enchantmentLevel * costPerLevel;
        }


        // lotr modifiers:
        baseCost += LOTREnchantmentHelper.getEnchantList( stack )
            .stream().filter(LOTREnchantment::isBeneficial )
            .mapToInt( mod -> (int) Math.max( 1, mod.getValueModifier() ) ).sum();

        return baseCost;
    }
}
