package com.bsipe.lotrkingsaddon;

import static com.bsipe.lotrkingsaddon.Config.CfgEnt.*;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import com.github.bsideup.jabel.Desugar;

public class Config {

    public static String greeting = "Hello World";

    private static PerPlayerMobCapConfig perPlayerMobCapConfig = null;
    private static CraftingRecipeModuleConfig craftingRecipeModuleConfig = null;
    private static MoreMoneyModuleConfig moreMoneyModuleConfig = null;
    private static WaypointsModuleConfig waypointsModuleConfig = null;
    private static NPCModificationsModuleConfig npcModificationsModuleConfig = null;
    private static ToolsAndWeaponsModuleConfig toolsAndWeaponsModuleConfig = null;
    private static ModuleLevelConfig moduleLevelConfig = null;

    public static void synchronizeConfiguration(File configFile) {
        synchronizeConfiguration(configFile, false);
    }

    public static void synchronizeConfiguration(File configFile, boolean check) {
        if (check && moduleLevelConfig != null) return;
        Configuration configuration = new Configuration(configFile);

        greeting = configuration.getString("greeting", Configuration.CATEGORY_GENERAL, greeting, "How shall I greet?");

        setPerPlayerMobCapConfig(configuration);
        setCraftingRecipeModuleConfig(configuration);
        setMoreMoneyModuleConfig(configuration);
        setWaypointsModuleConfig(configuration);
        setNPCModificationsModuleConfig(configuration);
        setToolsAndWeaponsModuleConfig(configuration);
        setModuleLevelConfig();

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static ModuleLevelConfig getModuleLevelConfig() {
        return moduleLevelConfig;
    }

    public static void setModuleLevelConfig() {
        moduleLevelConfig = new ModuleLevelConfig(
            perPlayerMobCapConfig.enabled(),
            craftingRecipeModuleConfig.enabled(),
            moreMoneyModuleConfig.enabled(),
            waypointsModuleConfig.enabled(),
            npcModificationsModuleConfig.enabled(),
            toolsAndWeaponsModuleConfig.enabled());
    }

    public static PerPlayerMobCapConfig getPerPlayerMobCapConfig() {
        return perPlayerMobCapConfig;
    }

    private static void setPerPlayerMobCapConfig(Configuration config) {
        perPlayerMobCapConfig = new PerPlayerMobCapConfig(
            PPMS_ENABLED.getBool(config),
            PPMS_LOGGING_ENABLED.getBool(config),
            PPMS_MIDDLE_EARTH_MOB_CAP.getInt(config),
            PPMS_UTUMNO_MOB_CAP.getInt(config),
            PPMS_MAX_SPAWNS_PER_CYCLE.getInt(config),
            PPMS_CHUNK_RANGE.getInt(config));
    }

    public static CraftingRecipeModuleConfig getCraftingRecipeModuleConfig() {
        return craftingRecipeModuleConfig;
    }

    private static void setCraftingRecipeModuleConfig(Configuration config) {
        craftingRecipeModuleConfig = new CraftingRecipeModuleConfig(
            CRM_ENABLED.getBool(config),
            CRM_REDSTONE_CRAFTING_ENABLED.getBool(config),
            CRM_QUARTZ_CRAFTING_ENABLED.getBool(config),
            CRM_ENDER_CHEST_CRAFTING_ENABLED.getBool(config),
            CRM_BEACON_CRAFTING_ENABLED.getBool(config),
            CRM_STONE_CHEST_CRAFTING_ENABLED.getBool(config),
            CRM_ENCHANTED_BOOK_CRAFTING_ENABLED.getBool(config),
            CRM_ALLOW_LOWER_TIER_ENCHANTMENT_CRAFTING.getBool(config),
            CRM_EFFICIENCY_CRAFTING_ENABLED.getBool(config),
            CRM_FORTUNE_CRAFTING_ENABLED.getBool(config),
            CRM_UNBREAKING_CRAFTING_ENABLED.getBool(config),
            CRM_SHARPNESS_CRAFTING_ENABLED.getBool(config),
            CRM_FIRE_ASPECT_CRAFTING_ENABLED.getBool(config),
            CRM_KNOCKBACK_CRAFTING_ENABLED.getBool(config),
            CRM_LOOTING_CRAFTING_ENABLED.getBool(config),
            CRM_PROTECTION_CRAFTING_ENABLED.getBool(config),
            CRM_FIRE_PROT_CRAFTING_ENABLED.getBool(config),
            CRM_PROJ_PROT_CRAFTING_ENABLED.getBool(config),
            CRM_FEATHER_FALLING_CRAFTING_ENABLED.getBool(config),
            CRM_POWER_CRAFTING_ENABLED.getBool(config),
            CRM_PUNCH_CRAFTING_ENABLED.getBool(config),
            CRM_ADD_HARDY_HANDY_SCROLL_RECIPES.getBool(config),
            CRM_REMOVE_REFORGING_COOLDOWN.getBool(config));
    }

    public static MoreMoneyModuleConfig getMoreMoneyModuleConfig() {
        return moreMoneyModuleConfig;
    }

    private static void setMoreMoneyModuleConfig(Configuration config) {
        moreMoneyModuleConfig = new MoreMoneyModuleConfig(
            MMM_ENABLED.getBool(config),
            MMM_LARGER_COINS_ENABLED.getBool(config),
            MMM_BULK_COIN_CONVERSION.getBool(config),
            MMM_GUI_BULK_COIN_CONVERSION.getBool(config));
    }

    public static WaypointsModuleConfig getWaypointsModuleConfig() {
        return waypointsModuleConfig;
    }

    private static void setWaypointsModuleConfig(Configuration config) {
        waypointsModuleConfig = new WaypointsModuleConfig(
            WM_ENABLED.getBool(config),
            WM_MOVE_HELMS_DEEP.getBool(config),
            WM_MOVE_ISENGARD.getBool(config),
            WM_ADD_KINGS_CUSTOM_WAYPOINTS.getBool(config),
            WM_REMOVE_DOL_AMROTH_MOUNTAIN.getBool(config),
            WM_MAKE_ALL_WAYPOINTS_FACTION_SPECIFIC.getBool(config));
    }

    public static NPCModificationsModuleConfig getNpcModificationsModuleConfig() {
        return npcModificationsModuleConfig;
    }

    private static void setNPCModificationsModuleConfig(Configuration config) {
        npcModificationsModuleConfig = new NPCModificationsModuleConfig(
            NMM_ENABLED.getBool(config),
            NMM_REMOVE_RANGER_HIDING.getBool(config),
            NMM_ADD_ARMORER_COMMAND.getBool(config));
    }

    public static ToolsAndWeaponsModuleConfig getToolsAndWeaponsModuleConfig() {
        return toolsAndWeaponsModuleConfig;
    }

    private static void setToolsAndWeaponsModuleConfig(Configuration config) {
        toolsAndWeaponsModuleConfig = new ToolsAndWeaponsModuleConfig(
            TWM_ENABLED.getBool(config),
            TWM_LORE_WEAPONS.getBool(config),
            TWM_STEEL_TOOLSET.getBool(config),
            TWM_BALANCE_RARE_WEAPONS.getBool(config),
            TWM_BALANCE_FACTION_GEAR.getBool(config));
    }

    enum CfgCat {

        PER_PLAYER_MOB_SPAWNING("mobs_per_player"),
        CRAFTING_RECIPE_MODULE("crafting_recipe_module"),
        MORE_MONEY_MODULE("more_money_module"),
        WAYPOINTS_MODULE("more_default_waypoints"),
        NPC_MODIFICATIONS_MODULE("npc_modifications"),
        TOOLS_AND_WEAPONS_MODULE("weapons_module");

        public String value;

        CfgCat(String s) {
            value = s;
        }
    }

    enum CfgEnt {

        PPMS_ENABLED(CfgCat.PER_PLAYER_MOB_SPAWNING, "per_player_mob_spawning_enabled",
            "Controls whether mob spawning is switched to a per-player system.", true),
        PPMS_LOGGING_ENABLED(CfgCat.PER_PLAYER_MOB_SPAWNING, "mob_spawning_logging",
            "Adds development logging to check if mobs are spawning properly", false),
        PPMS_MIDDLE_EARTH_MOB_CAP(CfgCat.PER_PLAYER_MOB_SPAWNING, "middle_earth",
            "Number of mob 'points' per player in the middle earth dimension", 114, 0, 2000),
        PPMS_UTUMNO_MOB_CAP(CfgCat.PER_PLAYER_MOB_SPAWNING, "utummno",
            "Number of mobs 'points' per player in the utumno dimension", 860, 0, 2000),
        PPMS_MAX_SPAWNS_PER_CYCLE(CfgCat.PER_PLAYER_MOB_SPAWNING, "max_spawns_per_cycle",
            "Number of mob 'points' per player in the middle earth dimension", 10, 1, 100),
        PPMS_CHUNK_RANGE(CfgCat.PER_PLAYER_MOB_SPAWNING, "chunk_range",
            "Number of mobs 'points' per player in the middle earth dimension", 6, 5, 7),
        CRM_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "custom_crafting_recipes_enabled",
            "Controls whether Custom Crafting recipes are added.", true),
        CRM_REDSTONE_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "redstone_crafting_enabled",
            "Adds redstone dust crafting recipe", true),
        CRM_QUARTZ_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "quartz_crafting_enabled",
            "Adds quartz crystal crafting recipe", true),
        CRM_ENDER_CHEST_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "ender_chest_crafting_enabled",
            "Adds LOTR Friendly recipe for ender chests", true),
        CRM_BEACON_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "beacon_crafting_enabled",
            "Adds LOTR Friendly recipe for beacons", true),
        CRM_STONE_CHEST_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "stone_chest_crafting_enabled",
            "Adds recipe for stone chests.", true),
        CRM_ENCHANTED_BOOK_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "enchanted_book_crafting_enabled",
            "Gates all enchanted book crafting behind a single config.", true),
        CRM_ALLOW_LOWER_TIER_ENCHANTMENT_CRAFTING(CfgCat.CRAFTING_RECIPE_MODULE,
            "enchanted_book_lower_tier_crafting_enabled",
            "Allows enabled book recipes for non-max tier enchantments by reducing the number of the required scrolls.",
            true),
        CRM_EFFICIENCY_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "efficiency_crafting_enabled",
            "Allows Efficiency 5 books to be crafted with scrolls", true),
        CRM_FORTUNE_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "fortune_crafting_enabled",
            "Allows Fortune 3 books to be crafted with scrolls", true),
        CRM_UNBREAKING_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "unbreaking_crafting_enabled",
            "Allows Unbreaking 3 books to be crafted with scrolls", true),
        CRM_SHARPNESS_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "sharpness_crafting_enabled",
            "Allows Sharpness 5 books to be crafted with scrolls", true),
        CRM_FIRE_ASPECT_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "fire_aspect_crafting_enabled",
            "Allows Fire Aspect 2 books to be crafted with scrolls", true),
        CRM_KNOCKBACK_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "knockback_crafting_enabled",
            "Allows Knockback 2 to be crafted with scrolls", true),
        CRM_LOOTING_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "looting_crafting_enabled",
            "Allows Looting 3 books to be crafted with scrolls", true),
        CRM_PROTECTION_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "protection_crafting_enabled",
            "Allows Protection 4 books to be crafted with scrolls", true),
        CRM_FIRE_PROT_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "fire_prot_crafting_enabled",
            "Allows Fire Protection 4 books to be crafted with scrolls", true),
        CRM_PROJ_PROT_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "proj_prot_crafting_enabled",
            "Allows Projectile Protection 4 books to be crafted with scrolls", true),
        CRM_FEATHER_FALLING_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "feather_falling_crafting_enabled",
            "Allows Feather Falling 4 books to be crafted with scrolls", true),
        CRM_POWER_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "power_crafting_enabled",
            "Allows Power 5 books to be crafted with scrolls", true),
        CRM_PUNCH_CRAFTING_ENABLED(CfgCat.CRAFTING_RECIPE_MODULE, "punch_crafting_enabled",
            "Allows Punch 2 books to be crafted with scrolls", true),
        CRM_ADD_HARDY_HANDY_SCROLL_RECIPES(CfgCat.CRAFTING_RECIPE_MODULE, "handy_hardy_scroll_recipes",
            "Allows 2 scrolls and a feather or cobblestone to be crafted into a handy or hardy scroll (respectively)",
            true),
        CRM_REMOVE_REFORGING_COOLDOWN(CfgCat.CRAFTING_RECIPE_MODULE, "remove_reforge_cooldown",
            "Remove the reforging cooldown (careful not to spam :)", true),
        MMM_ENABLED(CfgCat.MORE_MONEY_MODULE, "more_money_module_enabled", "Controls all coin module features", true),
        MMM_LARGER_COINS_ENABLED(CfgCat.MORE_MONEY_MODULE, "higher_currencies_enabled",
            "Adds higher coin denomination past 100", true),
        MMM_BULK_COIN_CONVERSION(CfgCat.MORE_MONEY_MODULE, "bulk_coin_conversion",
            "Adds crafting recipe for full pouches of coins.", true),
        MMM_GUI_BULK_COIN_CONVERSION(CfgCat.MORE_MONEY_MODULE, "gui_coin_conversion",
            "Adds button to coin conversion gui to compact all coins in inventory", true),
        WM_ENABLED(CfgCat.WAYPOINTS_MODULE, "enabled", "Adds new default waypoints to the map", false),
        WM_MOVE_HELMS_DEEP(CfgCat.WAYPOINTS_MODULE, "move_helms_deep",
            "Move helms deep to the position where the build is on the LOTR Kings server", false),
        WM_MOVE_ISENGARD(CfgCat.WAYPOINTS_MODULE, "move_isengard",
            "Move Isengard to the center of the ring of isengard.", true),
        WM_ADD_KINGS_CUSTOM_WAYPOINTS(CfgCat.WAYPOINTS_MODULE, "add_kings_custom_waypoints",
            "Add custom waypoints to map.", false),
        WM_REMOVE_DOL_AMROTH_MOUNTAIN(CfgCat.WAYPOINTS_MODULE, "remove_dol_amroth_mountain",
            "remove / mmove dol amroth mountain for dave.", false),
        WM_MAKE_ALL_WAYPOINTS_FACTION_SPECIFIC(CfgCat.WAYPOINTS_MODULE, "make_all_waypoints_faction_specific",
            "Make all waypoints specific to a faction.", false),
        NMM_ENABLED(CfgCat.NPC_MODIFICATIONS_MODULE, "enabled", "Modify some npc behavior and abilities", true),
        NMM_REMOVE_RANGER_HIDING(CfgCat.NPC_MODIFICATIONS_MODULE, "remove_ranger_hiding",
            "Remove the ability of rangers to disappear ( incomplete )", false),
        NMM_ADD_ARMORER_COMMAND(CfgCat.NPC_MODIFICATIONS_MODULE, "add_royal_armorer_command",
            "Add command to overwrite trades with royal armorer trades.", true),
        TWM_ENABLED(CfgCat.TOOLS_AND_WEAPONS_MODULE, "weapons_module_enabled",
            "Enable custom Tools, weapons and tool balancing.", true),
        TWM_LORE_WEAPONS(CfgCat.TOOLS_AND_WEAPONS_MODULE, "lore_weapons_enabled",
            "Add unique lore weapons that can only be obtained via commands.", false),
        TWM_STEEL_TOOLSET(CfgCat.TOOLS_AND_WEAPONS_MODULE, "add_steel_tools",
            "Add steel and steel tools. Steel is a faction neutral iron-coal alloy.", true),
        TWM_BALANCE_RARE_WEAPONS(CfgCat.TOOLS_AND_WEAPONS_MODULE, "balance_rare_weapons",
            "Nerf/buff rare gear in an attempt to balance gameplay.", true),
        TWM_BALANCE_FACTION_GEAR(CfgCat.TOOLS_AND_WEAPONS_MODULE, "balance_faction_gear",
            "Nerf/buff faction gear in an attempt to balance gameplay", true);

        public CfgCat category;
        public String name;
        public String comment;

        public boolean defaultBool;
        public int defaultInt;
        public int defaultIntMin;
        public int defaultIntMax;

        public boolean isIntVal = false;

        CfgEnt() {

        }

        CfgEnt(CfgCat cfgCat, String name, String comment, boolean bool) {
            this.category = cfgCat;
            this.name = name;
            this.comment = comment;
            this.defaultBool = bool;
        }

        CfgEnt(CfgCat cfgCat, String name, String comment, int defaultInt, int defaultIntMin, int defaultIntMax) {
            this.category = cfgCat;
            this.name = name;
            this.comment = comment;
            this.defaultInt = defaultInt;
            this.defaultIntMin = defaultIntMin;
            this.defaultIntMax = defaultIntMax;
            this.isIntVal = true;
        }

        public int getInt(Configuration config) {
            return config.getInt(name, category.value, defaultInt, defaultIntMin, defaultIntMax, comment);
        }

        public boolean getBool(Configuration config) {
            return config.getBoolean(name, category.value, defaultBool, comment);
        }
    }

    @Desugar
    public record PerPlayerMobCapConfig(boolean enabled, boolean enableLogging, int middleEarthMobCap, int utumnoMobCap,
        int maxSpawnsPerCycle, int chunkRange) {}

    @Desugar
    public record CraftingRecipeModuleConfig(boolean enabled, boolean redstone, boolean quartz, boolean enderChest,
        boolean beacon, boolean stoneChest, boolean enchantedBooks, boolean lowTierEnchantedBooks, boolean efficiency,
        boolean fortune, boolean unbreaking, boolean sharpness, boolean fireAspect, boolean knockback, boolean looting,
        boolean protection, boolean fireProtection, boolean projectileProtection, boolean featherFalling, boolean power,
        boolean punch, boolean handyHardyScrolls, boolean removeReforgeCooldown) {}

    @Desugar
    public record MoreMoneyModuleConfig(boolean enabled, boolean higherCurrencies, boolean bulkCoinConversion,
        boolean guiCoinConversion) {}

    @Desugar
    public record WaypointsModuleConfig(boolean enabled, boolean moveHelmsDeep, boolean moveIsengard,
        boolean addKingsCustomWaypoints, boolean removeDolAmrothMountains, boolean makeAllWaypointsFactionSpecific) {}

    @Desugar
    public record NPCModificationsModuleConfig(boolean enabled, boolean removeRangerHiding,
        boolean addArmorerCommand) {}

    @Desugar
    public record ToolsAndWeaponsModuleConfig(boolean enabled, boolean loreWeapons, boolean steelToolset,
        boolean balanceRareWeapons, boolean balanceFactionGear) {}

    @Desugar
    public record ModuleLevelConfig(boolean perPlayerMobCapModule, boolean craftingRecipeModule,
        boolean moreMoneyModule, boolean waypointsModule, boolean npcModificationsModule,
        boolean toolsAndWeaponsModuleConfig) {};
}
