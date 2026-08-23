package com.bsipe.lotrkingsaddon.common.modules;

import net.minecraftforge.common.util.EnumHelper;

import com.bsipe.lotrkingsaddon.Config;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.fac.LOTRFaction;
import lotr.common.world.map.LOTRMountains;
import lotr.common.world.map.LOTRWaypoint;
import lotr.common.world.map.LOTRWorldGenIsengardWalls;

public class WaypointsModule extends AbstractModule {

    private static final int IMG_X_param = 276;
    private static final int IMG_Z_param = 277;
    private static final int COORD_X_param = 278;
    private static final int COORD_Z_param = 279;

    public Config.WaypointsModuleConfig config;
    public static final Class[][] ENUM_HELPER_CLASS_MAP = {
        { LOTRWaypoint.class, LOTRWaypoint.Region.class, LOTRFaction.class, double.class, double.class } };

    public WaypointsModule() {
        config = Config.getWaypointsModuleConfig();
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {

        if (config.addKingsCustomWaypoints()) {
            addWaypoint("UTUMNO_ENTRANCE", LOTRWaypoint.Region.FORODWAITH, LOTRFaction.UNALIGNED, 1139, 394);
            addWaypoint("DAL_MINTHRUIL", LOTRWaypoint.Region.WOODLAND_REALM, LOTRFaction.WOOD_ELF, 1391, 638);
            addWaypoint("ENDUILS_GATE", LOTRWaypoint.Region.WOODLAND_REALM, LOTRFaction.WOOD_ELF, 1378, 624);
            addWaypoint("SOUTH_ERIADOR", LOTRWaypoint.Region.ERIADOR, LOTRFaction.UNALIGNED, 779, 824);
            addWaypoint("MUNBILEK", LOTRWaypoint.Region.RHUN, LOTRFaction.RHUDEL, 1830, 970);
            addWaypoint("SHAGRATS_TOMB", LOTRWaypoint.Region.ROHAN, LOTRFaction.ISENGARD, 1143, 1051);
            addWaypoint("SAPPERS_GROVE", LOTRWaypoint.Region.ROHAN, LOTRFaction.ISENGARD, 1167, 1044);
            addWaypoint("DOLENUI", LOTRWaypoint.Region.LONE_LANDS, LOTRFaction.RANGER_NORTH, 1094, 761);
        }

        if (config.removeDolAmrothMountains()) {
            // still tweaking this.
            ReflectionHelper.setPrivateValue(LOTRMountains.class, LOTRMountains.DOL_AMROTH, 0.0F, 41);
            // ReflectionHelper.setPrivateValue( LOTRWaypoint.class,LOTRWaypoint.DOL_AMROTH, 1156, IMG_X_param);
            // ReflectionHelper.setPrivateValue( LOTRWaypoint.class,LOTRWaypoint.DOL_AMROTH, LOTRWaypoint.mapToWorldX(
            // 1156), COORD_X_param);
        }
    }

    public void addWaypoint(String name, LOTRWaypoint.Region region, LOTRFaction faction, double x, double z) {
        EnumHelper.addEnum(ENUM_HELPER_CLASS_MAP, LOTRWaypoint.class, name, region, faction, x, z);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        if (config.makeAllWaypointsFactionSpecific()) {
            // setAllFactionSpecificWPs();
        }
        // moving waypoints in post-init happens after road initialization...
        if (config.moveIsengard()) {
            ReflectionHelper.setPrivateValue(LOTRWaypoint.class, LOTRWaypoint.ISENGARD, 1058, IMG_Z_param);
            ReflectionHelper.setPrivateValue(
                LOTRWaypoint.class,
                LOTRWaypoint.ISENGARD,
                LOTRWaypoint.mapToWorldZ(1058),
                COORD_Z_param);
            // The Isengard wall is based on the isengard waypoint, so shifting the WP requires counter-shifting the
            // wall position slightly.
            ReflectionHelper.setPrivateValue(
                LOTRWorldGenIsengardWalls.class,
                LOTRWorldGenIsengardWalls.INSTANCE,
                LOTRWaypoint.mapToWorldZ(1058),
                2);
        }

        if (config.moveHelmsDeep()) {
            ReflectionHelper.setPrivateValue(LOTRWaypoint.class, LOTRWaypoint.HELMS_DEEP, 1133, IMG_X_param);
            ReflectionHelper.setPrivateValue(LOTRWaypoint.class, LOTRWaypoint.HELMS_DEEP, 1117, IMG_Z_param);
            ReflectionHelper.setPrivateValue(LOTRWaypoint.class, LOTRWaypoint.HELMS_DEEP, 41498, COORD_X_param);
            ReflectionHelper.setPrivateValue(LOTRWaypoint.class, LOTRWaypoint.HELMS_DEEP, 49600, COORD_Z_param);
        }
    }
}
