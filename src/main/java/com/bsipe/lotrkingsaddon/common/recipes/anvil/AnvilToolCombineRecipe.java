package com.bsipe.lotrkingsaddon.common.recipes.anvil;

import lotr.common.enchant.LOTREnchantment;
import lotr.common.enchant.LOTREnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import scala.Int;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class AnvilToolCombineRecipe implements AnvilRecipe {
    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public boolean exclusive() {
        return true;
    }

    // priority no longer really matters, as we are just figuring out if this pattern holds.
    @Override
    public AnvilRecipeResult apply(IInventory inventory, AnvilRecipeResult result) {
        ItemStack item = inventory.getStackInSlot( 0 ).copy();
        // items must match for this to work.
        if ( ! item.isItemStackDamageable()
            || inventory.getStackInSlot( 1) == null
            || ! item.isItemEqual( inventory.getStackInSlot( 1 ) ) ) return result;


        // so I basically need to pick the larger of the anvil costs. . .
        ItemStack secondary = inventory.getStackInSlot( 1 );

        // vanilla enchants,
        int cost = 0;
        cost += combineVanillaEnchants( item, secondary );
        cost += combineLotrEnchants( item, secondary );

        int fee = Math.max( LOTREnchantmentHelper.getAnvilCost( item ), LOTREnchantmentHelper.getAnvilCost( secondary ) );

        return result.setResult( item ).setFee( fee ).addCost( cost );
        // this is the end list as far as I know.



        // get the higher anvil cost.
//        int anvilCost = Math.max( LOTREnchantmentHelper.getAnvilCost( item ), LOTREnchantmentHelper.getAnvilCost( secondary ) );
//
//        // the thing I don't feel like I understand is really the pricing of it.
//
//        // to that end, lets start with the actual combining of the items.
//        int combineCost = LOTREnchantmentHelper.getAnvilCost( inventory.getStackInSlot( 1 ) );
//

        // there's basically 3 cases:
        // 1.

//        return null;


    }

    @Override
    public int priority() {
        return 0;
    }

    // returns cost.
    private static int combineVanillaEnchants( ItemStack input, ItemStack second ) {
        Map<Integer, Integer> itemEnchants = EnchantmentHelper.getEnchantments( input );
        Map<Integer, Integer> secondaryEnchants = EnchantmentHelper.getEnchantments( second );
        int vanillaCombineCost = 0;
        for ( Map.Entry<Integer, Integer> entry : secondaryEnchants.entrySet() ) {
            if ( itemEnchants.containsKey( entry.getKey() ) ) {
                int level = itemEnchants.get( entry.getKey() );
                if ( level < entry.getValue() ) {
                    itemEnchants.put( entry.getKey(), entry.getValue() );
                    vanillaCombineCost += getVanillaEnchantCost( entry.getKey(), entry.getValue() - level );
                } else if ( level == entry.getValue() ) {
                    itemEnchants.put( entry.getKey(), Math.min( level + 1, Enchantment.enchantmentsList[entry.getKey()].getMaxLevel() ) );
                    vanillaCombineCost += getVanillaEnchantCost( entry.getKey(), 1 );
                }
            } else if ( ! itemEnchants.entrySet().stream().filter( itemEntry -> ! Enchantment.enchantmentsList[ itemEntry.getKey() ].canApplyTogether( Enchantment.enchantmentsList[entry.getKey()] ) ).findAny().isPresent() ) {
                itemEnchants.put( entry.getKey(), entry.getValue() );
                vanillaCombineCost += getVanillaEnchantCost( entry.getKey(), entry.getValue() );
            }
        }
        EnchantmentHelper.setEnchantments( itemEnchants, input );
        return vanillaCombineCost;
    }

    private static int combineLotrEnchants( ItemStack input, ItemStack second ) {
        List<LOTREnchantment> inputEnchantments = LOTREnchantmentHelper.getEnchantList( input );
        List<LOTREnchantment> secondEnchantments = LOTREnchantmentHelper.getEnchantList( second );

        int modifierCost = 0;

        for ( LOTREnchantment secondEnchant : secondEnchantments ) {
            // canApply just checks if it can go on the tool...
            if ( ! secondEnchant.canApply( input, false ) ) continue;
            if ( inputEnchantments.stream().filter( e -> ! e.isCompatibleWith( secondEnchant ) || ! secondEnchant.isCompatibleWith( e ) ).findAny().isPresent() ) continue;
            if ( ! secondEnchant.bypassAnvilLimit() && inputEnchantments.stream().filter( e -> !e.bypassAnvilLimit() ).count() >= 3 ) continue;

            inputEnchantments.add( secondEnchant );
            modifierCost += Math.max( 1, secondEnchant.getValueModifier() );
        }
        return modifierCost;
    }


    private static int getVanillaEnchantCost( int enchantment, int level ) {
        return level * switch ( Enchantment.enchantmentsList[enchantment].getWeight() ) {
            case ( 1 )  -> 8;
            case ( 2 )  -> 4;
            case ( 5 )  -> 2;
            case ( 10 ) -> 1;
            default -> 0;
        };
    }
}
