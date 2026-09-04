package com.bsipe.lotrkingsaddon.common.inventory;

import com.bsipe.lotrkingsaddon.client.render.gui.LOTRAddonSlotAnvilOutput;
import com.bsipe.lotrkingsaddon.common.recipes.anvil.AnvilRecipeHandler;
import com.bsipe.lotrkingsaddon.common.recipes.anvil.AnvilRecipeResult;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lotr.common.LOTRAchievement;
import lotr.common.LOTRConfig;
import lotr.common.LOTRLevelData;
import lotr.common.LOTRMod;
import lotr.common.enchant.LOTREnchantment;
import lotr.common.enchant.LOTREnchantmentCombining;
import lotr.common.enchant.LOTREnchantmentHelper;
import lotr.common.entity.npc.LOTREntityDwarf;
import lotr.common.entity.npc.LOTREntityNPC;
import lotr.common.entity.npc.LOTREntityScrapTrader;
import lotr.common.entity.npc.LOTRTradeEntries;
import lotr.common.entity.npc.LOTRTradeEntry;
import lotr.common.entity.npc.LOTRTradeable;
import lotr.common.inventory.OddmentCollectorNameMischief;
import lotr.common.item.AnvilNameColorProvider;
import lotr.common.item.LOTRItemBlowgun;
import lotr.common.item.LOTRItemChisel;
import lotr.common.item.LOTRItemCoin;
import lotr.common.item.LOTRItemCrossbow;
import lotr.common.item.LOTRItemEnchantment;
import lotr.common.item.LOTRItemModifierTemplate;
import lotr.common.item.LOTRItemOwnership;
import lotr.common.item.LOTRItemThrowingAxe;
import lotr.common.item.LOTRMaterial;
import lotr.common.recipe.LOTRRecipePoisonWeapon;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LOTRAddonContainerAnvil extends Container {
    public IInventory invOutput;
    public IInventory invInput;
    public final EntityPlayer thePlayer;
    public final World theWorld;
    public final boolean isTrader;
    private int xCoord;
    private int yCoord;
    private int zCoord;
    public LOTREntityNPC theNPC;
    public LOTRTradeable theTrader;
    public int materialCost;
    public int reforgeCost;
    public int engraveOwnerCost;
    private String repairedItemName;
    private long lastReforgeTime;
    public static final int maxReforgeTime = 40;
    public int clientReforgeTime;
    private boolean doneMischief;
    public boolean isSmithScrollCombine;

    private LOTRAddonContainerAnvil(EntityPlayer entityplayer, boolean trader) {
        this.lastReforgeTime = -1L;
        this.thePlayer = entityplayer;
        this.theWorld = entityplayer.worldObj;
        this.isTrader = trader;
        this.invOutput = new InventoryCraftResult();
        this.invInput = new InventoryBasic("Repair", true, this.isTrader ? 2 : 3) {
            public void markDirty() {
                super.markDirty();
                LOTRAddonContainerAnvil.this.onCraftMatrixChanged(this);
            }
        };
        this.addSlotToContainer(new Slot(this.invInput, 0, 27, 58));
        this.addSlotToContainer(new Slot(this.invInput, 1, 76, 47));
        if (!this.isTrader) {
            this.addSlotToContainer(new Slot(this.invInput, 2, 76, 70));
        }

        this.addSlotToContainer(new LOTRAddonSlotAnvilOutput(this, this.invOutput, 0, 134, 58));

        int i1;
        for(i1 = 0; i1 < 3; ++i1) {
            for(int i2 = 0; i2 < 9; ++i2) {
                this.addSlotToContainer(new Slot(entityplayer.inventory, i2 + i1 * 9 + 9, 8 + i2 * 18, 116 + i1 * 18));
            }
        }

        for(i1 = 0; i1 < 9; ++i1) {
            this.addSlotToContainer(new Slot(entityplayer.inventory, i1, 8 + i1 * 18, 174));
        }

    }

    public LOTRAddonContainerAnvil(EntityPlayer entityplayer, int i, int j, int k) {
        this(entityplayer, false);
        this.xCoord = i;
        this.yCoord = j;
        this.zCoord = k;
    }

    public LOTRAddonContainerAnvil(EntityPlayer entityplayer, LOTREntityNPC npc) {
        this(entityplayer, true);
        this.theNPC = npc;
        this.theTrader = (LOTRTradeable)npc;
    }

    public void onCraftMatrixChanged(IInventory inv) {
        super.onCraftMatrixChanged(inv);
        if (inv == this.invInput) {
            this.updateRepairOutput();
        }

    }

    private boolean tryNewMethod() {
        ItemStack inputItem = this.invInput.getStackInSlot(0);
        this.materialCost = 0;
        this.reforgeCost = 0;
        this.engraveOwnerCost = 0;
        this.isSmithScrollCombine = false;

//        int baseAnvilCost = 0;
//        int repairCost = 0;
//        int combineCost = 0;
//        int renameCost = 0;
        // check 1: input null -> do nothing.
        if (inputItem == null ||
            ( this.invInput.getSizeInventory() > 2 && this.invInput.getStackInSlot( 2 ) != null && ! this.isRepairMaterial( inputItem, this.invInput.getStackInSlot( 2 ) ) ) ) {
            this.invOutput.setInventorySlotContents(0, (ItemStack)null);
            this.materialCost = 0;
            return true;
        } else {
            AnvilRecipeResult result = AnvilRecipeHandler.apply( invInput );
            if ( result.failed() || result.getResult() == null ) {
                this.invOutput.setInventorySlotContents(0, null);
                this.materialCost = 0;
            } else {
                this.invOutput.setInventorySlotContents( 0, result.getResult() );
                this.materialCost = Math.round( ( result.getCost() + result.getFee() ) * ( this.isTrader ? this.getTraderMaterialPrice( inputItem ) : 1 ) );
            }
            this.reforgeCost = 2;


            this.detectAndSendChanges();
            return true;
        }
    }

    private void updateRepairOutput() {

        if ( tryNewMethod() ) return;

        ItemStack inputItem = this.invInput.getStackInSlot(0);
        this.materialCost = 0;
        this.reforgeCost = 0;
        this.engraveOwnerCost = 0;
        this.isSmithScrollCombine = false;
        int baseAnvilCost = 0;
        int repairCost = 0;
        int combineCost = 0;
        int renameCost = 0;
        // check 1: input null -> do nothing.
        if (inputItem == null) {
            this.invOutput.setInventorySlotContents(0, (ItemStack)null);
            this.materialCost = 0;

        // otherwise
        } else {

            ItemStack inputCopy = inputItem.copy();
            ItemStack combinerItem = this.invInput.getStackInSlot(1);
            ItemStack materialItem = this.isTrader ? null : this.invInput.getStackInSlot(2);
            Map inputEnchants = EnchantmentHelper.getEnchantments(inputCopy);
            boolean enchantingWithBook = false;
            List<LOTREnchantment> inputModifiers = LOTREnchantmentHelper.getEnchantList(inputCopy);
            baseAnvilCost = LOTREnchantmentHelper.getAnvilCost(inputItem) + (combinerItem == null ? 0 : LOTREnchantmentHelper.getAnvilCost(combinerItem));
            this.materialCost = 0;
            String previousDisplayName = inputCopy.getDisplayName();
            String defaultItemName = inputCopy.getItem().getItemStackDisplayName(inputCopy);
            String nameToApply = this.repairedItemName;
            String formattedNameToApply = nameToApply;
            List<EnumChatFormatting> colorsToApply = new ArrayList();
            colorsToApply.addAll(getAppliedFormattingCodes(inputCopy.getDisplayName()));
            boolean alteringNameColor = false;
            int nextAnvilCost;
            int oneItemRepair;
            // if rename:
            if (costsToRename(inputItem) && combinerItem != null) {
                // colors?
                if (combinerItem.getItem() instanceof AnvilNameColorProvider) {
                    AnvilNameColorProvider nameColorProvider = (AnvilNameColorProvider)combinerItem.getItem();
                    EnumChatFormatting newColor = nameColorProvider.getAnvilNameColor();
                    boolean isDifferentColor = !colorsToApply.contains(newColor);
                    if (isDifferentColor) {
                        EnumChatFormatting[] var21 = EnumChatFormatting.values();
                        nextAnvilCost = var21.length;

                        for(oneItemRepair = 0; oneItemRepair < nextAnvilCost; ++oneItemRepair) {
                            EnumChatFormatting ecf = var21[oneItemRepair];
                            if (ecf.isColor()) {
                                while(colorsToApply.contains(ecf)) {
                                    colorsToApply.remove(ecf);
                                }
                            }
                        }

                        colorsToApply.add(newColor);
                        alteringNameColor = true;
                    }
                } else if (combinerItem.getItem() == Items.flint && !colorsToApply.isEmpty()) {
                    colorsToApply.clear();
                    alteringNameColor = true;
                }

                if (alteringNameColor) {
                    ++renameCost;
                }
            }

            if (!colorsToApply.isEmpty()) {
                if (StringUtils.isBlank(formattedNameToApply)) {
                    formattedNameToApply = defaultItemName;
                }

                formattedNameToApply = applyFormattingCodes(formattedNameToApply, colorsToApply);
            }

            boolean nameChange = false;
            if (formattedNameToApply != null && !formattedNameToApply.equals(previousDisplayName)) {
                if (!StringUtils.isBlank(formattedNameToApply) && !formattedNameToApply.equals(defaultItemName)) {
                    inputCopy.setStackDisplayName(formattedNameToApply);
                    if (!stripFormattingCodes(previousDisplayName).equals(stripFormattingCodes(formattedNameToApply))) {
                        nameChange = true;
                    }
                } else if (inputCopy.hasDisplayName()) {
                    inputCopy.func_135074_t();
                    if (!stripFormattingCodes(previousDisplayName).equals(stripFormattingCodes(formattedNameToApply))) {
                        nameChange = true;
                    }
                }
            }

            boolean combining;
            if (nameChange) {
                combining = costsToRename(inputItem);
                if (combining) {
                    ++renameCost;
                }
            }

            if (this.isTrader) {
                LOTREnchantmentCombining.CombineRecipe scrollCombine = LOTREnchantmentCombining.getCombinationResult(inputItem, combinerItem);
                if (scrollCombine != null) {
                    this.invOutput.setInventorySlotContents(0, scrollCombine.createOutputItem());
                    this.materialCost = scrollCombine.cost;
                    this.reforgeCost = 0;
                    this.engraveOwnerCost = 0;
                    this.isSmithScrollCombine = true;
                    return;
                }
            }

            combining = false;
            int newDamage;
            int inputEnchLevel;
            byte stringFactor;
            int usedMaterials;
            if (combinerItem != null) {
                enchantingWithBook = combinerItem.getItem() == Items.enchanted_book && Items.enchanted_book.func_92110_g(combinerItem).tagCount() > 0;
                if (enchantingWithBook && !LOTRConfig.enchantingVanilla) {
                    this.invOutput.setInventorySlotContents(0, (ItemStack)null);
                    this.materialCost = 0;
                    return;
                }

                LOTREnchantment combinerItemEnchant = null;
                if (combinerItem.getItem() instanceof LOTRItemEnchantment) {
                    combinerItemEnchant = ((LOTRItemEnchantment)combinerItem.getItem()).theEnchant;
                } else if (combinerItem.getItem() instanceof LOTRItemModifierTemplate) {
                    combinerItemEnchant = LOTRItemModifierTemplate.getModifier(combinerItem);
                }

                if (!enchantingWithBook && combinerItemEnchant == null) {
                    if (inputCopy.isItemStackDamageable() && inputCopy.getItem() == combinerItem.getItem()) {
                        int inputUseLeft = inputItem.getMaxDamage() - inputItem.getItemDamageForDisplay();
                        nextAnvilCost = combinerItem.getMaxDamage() - combinerItem.getItemDamageForDisplay();
                        oneItemRepair = nextAnvilCost + inputCopy.getMaxDamage() * 12 / 100;
                        usedMaterials = inputUseLeft + oneItemRepair;
                        newDamage = inputCopy.getMaxDamage() - usedMaterials;
                        newDamage = Math.max(newDamage, 0);
                        if (newDamage < inputCopy.getItemDamage()) {
                            inputCopy.setItemDamage(newDamage);
                            int restoredUses1 = inputCopy.getMaxDamage() - inputUseLeft;
                            inputEnchLevel = inputCopy.getMaxDamage() - nextAnvilCost;
                            combineCost += Math.max(0, Math.min(restoredUses1, inputEnchLevel) / 100);
                        }

                        combining = true;
                    } else if (!alteringNameColor) {
                        this.invOutput.setInventorySlotContents(0, (ItemStack)null);
                        this.materialCost = 0;
                        return;
                    }
                }

                Map outputEnchants = new HashMap(inputEnchants);
                int combinerEnchLevel;
                if (LOTRConfig.enchantingVanilla) {
                    Map combinerEnchants = EnchantmentHelper.getEnchantments(combinerItem);
                    Iterator var50 = combinerEnchants.keySet().iterator();

                    label421:
                    while(var50.hasNext()) {
                        Object obj = var50.next();
                        newDamage = (Integer)obj;
                        Enchantment combinerEnch = Enchantment.enchantmentsList[newDamage];
                        inputEnchLevel = 0;
                        if (outputEnchants.containsKey(newDamage)) {
                            inputEnchLevel = (Integer)outputEnchants.get(newDamage);
                        }

                        combinerEnchLevel = (Integer)combinerEnchants.get(newDamage);
                        int combinedEnchLevel;
                        if (inputEnchLevel == combinerEnchLevel) {
                            ++combinerEnchLevel;
                            combinedEnchLevel = combinerEnchLevel;
                        } else {
                            combinedEnchLevel = Math.max(combinerEnchLevel, inputEnchLevel);
                        }

                        combinerEnchLevel = combinedEnchLevel;
                        int levelsAdded = combinerEnchLevel - inputEnchLevel;
                        boolean canApply = combinerEnch.canApply(inputItem);
                        if (this.thePlayer.capabilities.isCreativeMode || inputItem.getItem() == Items.enchanted_book) {
                            canApply = true;
                        }

                        Iterator var32 = outputEnchants.keySet().iterator();

                        while(true) {
                            int inputEnchID;
                            Enchantment inputEnch;
                            do {
                                do {
                                    if (!var32.hasNext()) {
                                        if (canApply) {
                                            combinerEnchLevel = Math.min(combinerEnchLevel, combinerEnch.getMaxLevel());
                                            outputEnchants.put(newDamage, combinerEnchLevel);
                                            int costPerLevel = 0;
                                            int enchWeight = combinerEnch.getWeight();
                                            if (enchWeight == 1) {
                                                costPerLevel = 8;
                                            } else if (enchWeight == 2) {
                                                costPerLevel = 4;
                                            } else if (enchWeight == 5) {
                                                costPerLevel = 2;
                                            } else if (enchWeight == 10) {
                                                costPerLevel = 1;
                                            }

                                            combineCost += costPerLevel * levelsAdded;
                                        }
                                        continue label421;
                                    }

                                    Object objIn = var32.next();
                                    inputEnchID = (Integer)objIn;
                                    inputEnch = Enchantment.enchantmentsList[inputEnchID];
                                } while(inputEnchID == newDamage);
                            } while(combinerEnch.canApplyTogether(inputEnch) && inputEnch.canApplyTogether(combinerEnch));

                            canApply = false;
                            combineCost += levelsAdded;
                        }
                    }
                } else {
                    outputEnchants.clear();
                }

                EnchantmentHelper.setEnchantments(outputEnchants, inputCopy);
                stringFactor = 3;
                List<LOTREnchantment> outputMods = new ArrayList();
                outputMods.addAll(inputModifiers);
                List<LOTREnchantment> combinerMods = LOTREnchantmentHelper.getEnchantList(combinerItem);
                if (combinerItemEnchant != null) {
                    combinerMods.add(combinerItemEnchant);
                    if (combinerItemEnchant == LOTREnchantment.fire) {
                        Item item = inputCopy.getItem();
                        if (LOTRRecipePoisonWeapon.poisonedToInput.containsKey(item)) {
                            Item unpoisoned = (Item)LOTRRecipePoisonWeapon.poisonedToInput.get(item);
                            inputCopy.func_150996_a(unpoisoned);
                        }
                    }
                }

                Iterator var57 = combinerMods.iterator();

                while(true) {
                    if (!var57.hasNext()) {
                        LOTREnchantmentHelper.setEnchantList(inputCopy, outputMods);
                        break;
                    }

                    LOTREnchantment combinerMod = (LOTREnchantment)var57.next();
                    boolean canApply = combinerMod.canApply(inputItem, false);
                    if (canApply) {
                        Iterator var66 = outputMods.iterator();

                        label381:
                        while(true) {
                            LOTREnchantment mod;
                            do {
                                if (!var66.hasNext()) {
                                    break label381;
                                }

                                mod = (LOTREnchantment)var66.next();
                            } while(mod.isCompatibleWith(combinerMod) && combinerMod.isCompatibleWith(mod));

                            canApply = false;
                        }
                    }

                    combinerEnchLevel = 0;
                    Iterator var68 = outputMods.iterator();

                    while(var68.hasNext()) {
                        LOTREnchantment mod = (LOTREnchantment)var68.next();
                        if (!mod.bypassAnvilLimit()) {
                            ++combinerEnchLevel;
                        }
                    }

                    if (!combinerMod.bypassAnvilLimit() && combinerEnchLevel >= stringFactor) {
                        canApply = false;
                    }

                    if (canApply) {
                        outputMods.add(combinerMod);
                        if (combinerMod.isBeneficial()) {
                            combineCost += Math.max(1, (int)combinerMod.getValueModifier());
                        }
                    }
                }
            }

            if (combineCost > 0) {
                combining = true;
            }

            int numEnchants = 0;

            Iterator var44;
            byte costPerLevel;
            for(var44 = inputEnchants.keySet().iterator(); var44.hasNext(); baseAnvilCost += numEnchants + newDamage * costPerLevel) {
                Object obj = var44.next();
                oneItemRepair = (Integer)obj;
                Enchantment ench = Enchantment.enchantmentsList[oneItemRepair];
                newDamage = (Integer)inputEnchants.get(oneItemRepair);
                ++numEnchants;
                costPerLevel = 0;
                inputEnchLevel = ench.getWeight();
                if (inputEnchLevel == 1) {
                    costPerLevel = 8;
                } else if (inputEnchLevel == 2) {
                    costPerLevel = 4;
                } else if (inputEnchLevel == 5) {
                    costPerLevel = 2;
                } else if (inputEnchLevel == 10) {
                    costPerLevel = 1;
                }
            }

            if (enchantingWithBook && !inputCopy.getItem().isBookEnchantable(inputCopy, combinerItem)) {
                inputCopy = null;
            }

            var44 = inputModifiers.iterator();

            while(var44.hasNext()) {
                LOTREnchantment mod = (LOTREnchantment)var44.next();
                if (mod.isBeneficial()) {
                    baseAnvilCost += Math.max(1, (int)mod.getValueModifier());
                }
            }

            boolean repairing;
            if (inputCopy.isItemStackDamageable()) {
                repairing = false;
                nextAnvilCost = 0;
                if (this.isTrader) {
                    repairing = this.getTraderMaterialPrice(inputItem) > 0.0F;
                    nextAnvilCost = Integer.MAX_VALUE;
                } else {
                    repairing = materialItem != null && this.isRepairMaterial(inputItem, materialItem);
                    if (materialItem != null) {
                        nextAnvilCost = materialItem.stackSize - combineCost - renameCost;
                    }
                }

                oneItemRepair = Math.min(inputCopy.getItemDamageForDisplay(), inputCopy.getMaxDamage() / 4);
                if (repairing && nextAnvilCost > 0 && oneItemRepair > 0) {
                    nextAnvilCost -= baseAnvilCost;
                    if (nextAnvilCost <= 0) {
                        if (!nameChange && !combining) {
                            repairCost = 1;
                            usedMaterials = inputCopy.getItemDamageForDisplay() - oneItemRepair;
                            inputCopy.setItemDamage(usedMaterials);
                        }
                    } else {
                        for(usedMaterials = 0; oneItemRepair > 0 && usedMaterials < nextAnvilCost; ++usedMaterials) {
                            newDamage = inputCopy.getItemDamageForDisplay() - oneItemRepair;
                            inputCopy.setItemDamage(newDamage);
                            oneItemRepair = Math.min(inputCopy.getItemDamageForDisplay(), inputCopy.getMaxDamage() / 4);
                        }

                        repairCost += usedMaterials;
                    }
                }
            }

            repairing = repairCost > 0;
            if (!combining && !repairing) {
                this.materialCost = 0;
            } else {
                this.materialCost = baseAnvilCost;
                this.materialCost += combineCost + repairCost;
            }

            this.materialCost += renameCost;
            if (inputCopy != null) {
                nextAnvilCost = LOTREnchantmentHelper.getAnvilCost(inputItem);
                if (combinerItem != null) {
                    oneItemRepair = LOTREnchantmentHelper.getAnvilCost(combinerItem);
                    nextAnvilCost = Math.max(nextAnvilCost, oneItemRepair);
                }

                if (combining) {
                    nextAnvilCost += 2;
                } else if (repairing) {
                    ++nextAnvilCost;
                }

                nextAnvilCost = Math.max(nextAnvilCost, 0);
                if (nextAnvilCost > 0) {
                    LOTREnchantmentHelper.setAnvilCost(inputCopy, nextAnvilCost);
                }
            }

            if (LOTREnchantmentHelper.isReforgeable(inputItem)) {
                this.reforgeCost = 2;
                if (inputItem.getItem() instanceof ItemArmor) {
                    this.reforgeCost = 3;
                }

                if (inputItem.isItemStackDamageable()) {
                    ItemStack reforgeCopy = inputItem.copy();
                    oneItemRepair = Math.min(reforgeCopy.getItemDamageForDisplay(), reforgeCopy.getMaxDamage() / 4);
                    if (oneItemRepair > 0) {
                        for(usedMaterials = 0; oneItemRepair > 0; ++usedMaterials) {
                            newDamage = reforgeCopy.getItemDamageForDisplay() - oneItemRepair;
                            reforgeCopy.setItemDamage(newDamage);
                            oneItemRepair = Math.min(reforgeCopy.getItemDamageForDisplay(), reforgeCopy.getMaxDamage() / 4);
                        }

                        this.reforgeCost += usedMaterials;
                    }
                }

                this.engraveOwnerCost = 2;
            } else {
                this.reforgeCost = 0;
                this.engraveOwnerCost = 0;
            }

            if (this.isRepairMaterial(inputItem, new ItemStack(Items.string))) {
                stringFactor = 3;
                this.materialCost *= stringFactor;
                this.reforgeCost *= stringFactor;
                this.engraveOwnerCost *= stringFactor;
            }

            if (this.isTrader) {
                boolean isCommonRenameOnly = nameChange && this.materialCost == 0;
                float materialPrice = this.getTraderMaterialPrice(inputItem);
                if (materialPrice > 0.0F) {
                    this.materialCost = Math.round((float)this.materialCost * materialPrice);
                    this.materialCost = Math.max(this.materialCost, 1);
                    this.reforgeCost = Math.round((float)this.reforgeCost * materialPrice);
                    this.reforgeCost = Math.max(this.reforgeCost, 1);
                    this.engraveOwnerCost = Math.round((float)this.engraveOwnerCost * materialPrice);
                    this.engraveOwnerCost = Math.max(this.engraveOwnerCost, 1);
                    if (this.theTrader instanceof LOTREntityScrapTrader) {
                        this.materialCost = MathHelper.ceiling_float_int((float)this.materialCost * 0.5F);
                        this.materialCost = Math.max(this.materialCost, 1);
                        this.reforgeCost = MathHelper.ceiling_float_int((float)this.reforgeCost * 0.5F);
                        this.reforgeCost = Math.max(this.reforgeCost, 1);
                        this.engraveOwnerCost = MathHelper.ceiling_float_int((float)this.engraveOwnerCost * 0.5F);
                        this.engraveOwnerCost = Math.max(this.engraveOwnerCost, 1);
                    }
                } else if (!isCommonRenameOnly) {
                    this.invOutput.setInventorySlotContents(0, (ItemStack)null);
                    this.materialCost = 0;
                    this.reforgeCost = 0;
                    this.engraveOwnerCost = 0;
                    return;
                }
            }

            if (!combining && !repairing && !nameChange && !alteringNameColor) {
                this.invOutput.setInventorySlotContents(0, (ItemStack)null);
                this.materialCost = 0;
            } else {
                this.invOutput.setInventorySlotContents(0, inputCopy);
            }

            this.detectAndSendChanges();
        }

    }

    private static boolean costsToRename(ItemStack itemstack) {
        Item item = itemstack.getItem();
        if (!(item instanceof ItemSword) && !(item instanceof ItemTool)) {
            if (item instanceof ItemArmor && ((ItemArmor)item).damageReduceAmount > 0) {
                return true;
            } else {
                return item instanceof ItemBow || item instanceof LOTRItemCrossbow || item instanceof LOTRItemThrowingAxe || item instanceof LOTRItemBlowgun;
            }
        } else {
            return true;
        }
    }

    private static List<EnumChatFormatting> getAppliedFormattingCodes(String name) {
        List<EnumChatFormatting> colors = new ArrayList();
        EnumChatFormatting[] var2 = EnumChatFormatting.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            EnumChatFormatting color = var2[var4];
            String formatCode = color.toString();
            if (name.startsWith(formatCode)) {
                colors.add(color);
            }
        }

        return colors;
    }

    public static String stripFormattingCodes(String name) {
        EnumChatFormatting[] var1 = EnumChatFormatting.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EnumChatFormatting color = var1[var3];
            String formatCode = color.toString();
            if (name.startsWith(formatCode)) {
                name = name.substring(formatCode.length());
            }
        }

        return name;
    }

    private static String applyFormattingCodes(String name, List<EnumChatFormatting> colors) {
        EnumChatFormatting color;
        for(Iterator var2 = colors.iterator(); var2.hasNext(); name = color + name) {
            color = (EnumChatFormatting)var2.next();
        }

        return name;
    }

    public List<EnumChatFormatting> getActiveItemNameFormatting() {
        ItemStack inputItem = this.invInput.getStackInSlot(0);
        ItemStack resultItem = this.invOutput.getStackInSlot(0);
        if (resultItem != null) {
            return getAppliedFormattingCodes(resultItem.getDisplayName());
        } else {
            return (List)(inputItem != null ? getAppliedFormattingCodes(inputItem.getDisplayName()) : new ArrayList());
        }
    }

    public boolean isRepairMaterial(ItemStack inputItem, ItemStack materialItem) {
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

    private float getTraderMaterialPrice(ItemStack inputItem) {
        float materialPrice = 0.0F;
        LOTRTradeEntry[] sellTrades = this.theNPC.traderNPCInfo.getSellTrades();
        int var6;
        if (sellTrades != null) {
            LOTRTradeEntry[] var4 = sellTrades;
            int var5 = sellTrades.length;

            for(var6 = 0; var6 < var5; ++var6) {
                LOTRTradeEntry trade = var4[var6];
                ItemStack tradeItem = trade.createTradeItem();
                if (this.isRepairMaterial(inputItem, tradeItem)) {
                    materialPrice = (float)trade.getCost() / (float)trade.createTradeItem().stackSize;
                    break;
                }
            }
        }

        if (materialPrice <= 0.0F) {
            LOTRTradeEntries sellPool = this.theTrader.getSellPool();
            LOTRTradeEntry[] var11 = sellPool.tradeEntries;
            var6 = var11.length;

            for(int var12 = 0; var12 < var6; ++var12) {
                LOTRTradeEntry trade = var11[var12];
                ItemStack tradeItem = trade.createTradeItem();
                if (this.isRepairMaterial(inputItem, tradeItem)) {
                    materialPrice = (float)trade.getCost() / (float)trade.createTradeItem().stackSize;
                    break;
                }
            }
        }

        if (materialPrice <= 0.0F && (this.isRepairMaterial(inputItem, new ItemStack(LOTRMod.mithril)) || this.isRepairMaterial(inputItem, new ItemStack(LOTRMod.mithrilMail))) && this.theTrader instanceof LOTREntityDwarf) {
            materialPrice = 200.0F;
        }

        return materialPrice;
    }

    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for(int i = 0; i < this.crafters.size(); ++i) {
            ICrafting crafting = (ICrafting)this.crafters.get(i);
            crafting.sendProgressBarUpdate(this, 0, this.materialCost);
            crafting.sendProgressBarUpdate(this, 1, this.reforgeCost);
            crafting.sendProgressBarUpdate(this, 3, this.engraveOwnerCost);
        }

    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int i, int j) {
        if (i == 0) {
            this.materialCost = j;
        }

        if (i == 1) {
            this.reforgeCost = j;
        }

        if (i == 2) {
            this.clientReforgeTime = 40;
        }

        if (i == 3) {
            this.engraveOwnerCost = j;
        }

    }

    public boolean hasMaterialOrCoinAmount(int cost) {
        if (this.isTrader) {
            return LOTRItemCoin.getInventoryValue(this.thePlayer, false) >= cost;
        } else {
            ItemStack inputItem = this.invInput.getStackInSlot(0);
            ItemStack materialItem = this.invInput.getStackInSlot(2);
            if (materialItem == null) {
                return false;
            } else {
                return this.isRepairMaterial(inputItem, materialItem) && materialItem.stackSize >= cost;
            }
        }
    }

    public void takeMaterialOrCoinAmount(int cost) {
        if (this.isTrader) {
            if (!this.theWorld.isRemote) {
                LOTRItemCoin.takeCoins(cost, this.thePlayer);
                this.detectAndSendChanges();
                this.theNPC.playTradeSound();
            }
        } else {
            ItemStack materialItem = this.invInput.getStackInSlot(2);
            if (materialItem != null) {
                materialItem.stackSize -= cost;
                if (materialItem.stackSize <= 0) {
                    this.invInput.setInventorySlotContents(2, (ItemStack)null);
                } else {
                    this.invInput.setInventorySlotContents(2, materialItem);
                }
            }
        }

    }

    public void onContainerClosed(EntityPlayer entityplayer) {
        super.onContainerClosed(entityplayer);
        if (!this.theWorld.isRemote) {
            for(int i = 0; i < this.invInput.getSizeInventory(); ++i) {
                ItemStack itemstack = this.invInput.getStackInSlotOnClosing(i);
                if (itemstack != null) {
                    entityplayer.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }

            if (this.doneMischief && this.isTrader && this.theNPC instanceof LOTREntityScrapTrader) {
                this.theNPC.sendSpeechBank(entityplayer, ((LOTREntityScrapTrader)this.theNPC).getSmithSpeechBank());
            }
        }

    }

    public boolean canInteractWith(EntityPlayer entityplayer) {
        if (this.isTrader) {
            return this.theNPC != null && (double)entityplayer.getDistanceToEntity(this.theNPC) <= 12.0 && this.theNPC.isEntityAlive() && this.theNPC.getAttackTarget() == null && this.theTrader.canTradeWith(entityplayer);
        } else {
            return this.theWorld.getBlock(this.xCoord, this.yCoord, this.zCoord) == Blocks.anvil && entityplayer.getDistanceSq((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5) <= 64.0;
        }
    }

    public ItemStack slotClick(int slotNo, int j, int k, EntityPlayer entityplayer) {
        ItemStack resultItem = this.invOutput.getStackInSlot(0);
        resultItem = ItemStack.copyItemStack(resultItem);
        boolean changed = false;
        ItemStack slotClickResult;
        if (resultItem != null && slotNo == this.getSlotFromInventory(this.invOutput, 0).slotNumber && !this.theWorld.isRemote && this.isTrader && this.theNPC instanceof LOTREntityScrapTrader) {
            slotClickResult = resultItem.copy();
            changed = this.applyMischief(slotClickResult);
            if (changed) {
                this.invOutput.setInventorySlotContents(0, slotClickResult);
            }
        }

        slotClickResult = super.slotClick(slotNo, j, k, entityplayer);
        if (changed) {
            this.doneMischief = true;
            if (this.invOutput.getStackInSlot(0) != null) {
                this.invOutput.setInventorySlotContents(0, resultItem.copy());
            }
        }

        return slotClickResult;
    }

    public ItemStack transferStackInSlot(EntityPlayer entityplayer, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(i);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            int inputSize = this.invInput.getSizeInventory();
            if (i == inputSize) {
                if (!this.mergeItemStack(itemstack1, inputSize + 1, inputSize + 37, true)) {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (i >= inputSize + 1) {
                if (i >= inputSize + 1 && i < inputSize + 37 && !this.mergeItemStack(itemstack1, 0, inputSize, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, inputSize + 1, inputSize + 37, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack)null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(entityplayer, itemstack1);
        }

        return itemstack;
    }

    public void updateItemName(String name) {
        List<EnumChatFormatting> colors = getAppliedFormattingCodes(name);
        name = stripFormattingCodes(name);
        name = ChatAllowedCharacters.filerAllowedCharacters(name);
        this.repairedItemName = name;
        ItemStack itemstack = this.invOutput.getStackInSlot(0);
        if (itemstack != null) {
            if (StringUtils.isBlank(this.repairedItemName)) {
                itemstack.func_135074_t();
            } else {
                itemstack.setStackDisplayName(this.repairedItemName);
            }

            if (!colors.isEmpty()) {
                itemstack.setStackDisplayName(applyFormattingCodes(itemstack.getDisplayName(), colors));
            }
        }

        this.updateRepairOutput();
    }

    public void reforgeItem() {
        long curTime = System.currentTimeMillis();
        if (this.lastReforgeTime < 0L || curTime - this.lastReforgeTime >= 2000L) {
            ItemStack inputItem = this.invInput.getStackInSlot(0);
            if (inputItem != null && this.reforgeCost > 0 && this.hasMaterialOrCoinAmount(this.reforgeCost)) {
                int cost = this.reforgeCost;
                if (inputItem.isItemStackDamageable()) {
                    inputItem.setItemDamage(0);
                }

                LOTREnchantmentHelper.applyRandomEnchantments(inputItem, this.theWorld.rand, true, true);
                LOTREnchantmentHelper.setAnvilCost(inputItem, 0);
                if (this.isTrader && this.theNPC instanceof LOTREntityScrapTrader) {
                    boolean changed = this.applyMischief(inputItem);
                    if (changed) {
                        this.doneMischief = true;
                    }
                }

                this.invInput.setInventorySlotContents(0, inputItem);
                this.takeMaterialOrCoinAmount(cost);
                this.playAnvilSound();
                this.lastReforgeTime = curTime;
                ((EntityPlayerMP)this.thePlayer).sendProgressBarUpdate(this, 2, 0);
                if (!this.isTrader) {
                    LOTRLevelData.getData(this.thePlayer).addAchievement(LOTRAchievement.reforge);
                }
            }
        }

    }

    public boolean canEngraveNewOwner(ItemStack itemstack, EntityPlayer entityplayer) {
        String currentOwner = LOTRItemOwnership.getCurrentOwner(itemstack);
        if (currentOwner == null) {
            return true;
        } else {
            return !currentOwner.equals(entityplayer.getCommandSenderName());
        }
    }

    public void engraveOwnership() {
        ItemStack inputItem = this.invInput.getStackInSlot(0);
        if (inputItem != null && this.engraveOwnerCost > 0 && this.hasMaterialOrCoinAmount(this.engraveOwnerCost)) {
            int cost = this.engraveOwnerCost;
            LOTRItemOwnership.setCurrentOwner(inputItem, this.thePlayer.getCommandSenderName());
            if (this.isTrader && this.theNPC instanceof LOTREntityScrapTrader) {
                boolean changed = this.applyMischief(inputItem);
                if (changed) {
                    this.doneMischief = true;
                }
            }

            this.invInput.setInventorySlotContents(0, inputItem);
            this.takeMaterialOrCoinAmount(cost);
            this.playAnvilSound();
            LOTRLevelData.getData(this.thePlayer).addAchievement(LOTRAchievement.engraveOwnership);
        }

    }

    private boolean applyMischief(ItemStack itemstack) {
        boolean changed = false;
        Random rand = this.theWorld.rand;
        if (rand.nextFloat() < 0.8F) {
            String name = itemstack.getDisplayName();
            name = OddmentCollectorNameMischief.garbleName(name, rand);
            if (name.equals(itemstack.getItem().getItemStackDisplayName(itemstack))) {
                itemstack.func_135074_t();
            } else {
                itemstack.setStackDisplayName(name);
            }

            changed = true;
        }

        if (rand.nextFloat() < 0.2F) {
            LOTREnchantmentHelper.applyRandomEnchantments(itemstack, rand, false, true);
            changed = true;
        }

        return changed;
    }

    public void playAnvilSound() {
        if (!this.theWorld.isRemote) {
            int i;
            int j;
            int k;
            if (this.isTrader) {
                i = MathHelper.floor_double(this.theNPC.posX);
                j = MathHelper.floor_double(this.theNPC.posY);
                k = MathHelper.floor_double(this.theNPC.posZ);
            } else {
                i = this.xCoord;
                j = this.yCoord;
                k = this.zCoord;
            }

            this.theWorld.playAuxSFX(1021, i, j, k, 0);
        }

    }
}
