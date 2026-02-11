package com.bsipe.lotrkingsaddon.modules;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.fac.LOTRFaction;
import lotr.common.world.map.LOTRRoads;
import lotr.common.world.map.LOTRWaypoint;
import lotr.common.world.map.LOTRWorldGenIsengardWalls;
import lotr.common.world.map.LOTRWorldGenRammasEchor;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;

import java.util.List;

public class NewDefaultWaypointsModule extends AbstractModule {
    public static final Class[][] ENUM_HELPER_CLASS_MAP = {{LOTRWaypoint.class, LOTRWaypoint.Region.class, LOTRFaction.class, double.class, double.class}};

    public static boolean SERVER_ONLY;
    public static boolean ENABLED;
    public static boolean MOVE_HELMS_DEEP;
    public static boolean MOVE_ISENGARD;
    public static boolean ADD_KINGS_CUSTOM_WAYPOINTS;
    public static boolean MOVE_DOL_AMROTH;

    private static final String CONFIG_CATAGORY = "more_default_waypoints";

    public NewDefaultWaypointsModule(Configuration config, boolean serverOnly ) {
        SERVER_ONLY = serverOnly;
        ENABLED = !serverOnly && config.getBoolean( "enabled", CONFIG_CATAGORY, true, "Adds new default waypoints to the map." );
        MOVE_HELMS_DEEP = !serverOnly && config.getBoolean( "move_helms_deep", CONFIG_CATAGORY, true, "Move helms deep waypoint to adjusted location." );
        MOVE_ISENGARD = !serverOnly && config.getBoolean( "move_isengard", CONFIG_CATAGORY, true, "Move Isengard to adjusted location." );
        ADD_KINGS_CUSTOM_WAYPOINTS = !serverOnly && config.getBoolean( "add_kings_custom_waypoints", CONFIG_CATAGORY, true, "Add Kings Custom Waypoints." );
        MOVE_DOL_AMROTH = !serverOnly && config.getBoolean( "move_dol_amroth", CONFIG_CATAGORY, false, "Move Dol amroth." );

    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {

        if ( ADD_KINGS_CUSTOM_WAYPOINTS ) {
            addWaypoint( "UTUMNO_ENTRANCE", LOTRWaypoint.Region.FORODWAITH, LOTRFaction.UNALIGNED,1139, 394);
            addWaypoint( "DAL_MINTHRUIL", LOTRWaypoint.Region.WOODLAND_REALM, LOTRFaction.WOOD_ELF,1391, 638);
            addWaypoint( "ENDUILS_GATE", LOTRWaypoint.Region.WOODLAND_REALM, LOTRFaction.WOOD_ELF,1378, 624);
            addWaypoint( "SOUTH_ERIADOR", LOTRWaypoint.Region.ERIADOR, LOTRFaction.UNALIGNED,779, 824);
            addWaypoint( "MUNBILEK", LOTRWaypoint.Region.RHUN, LOTRFaction.RHUDEL,1830, 970);
            addWaypoint( "SHAGRATS_TOMB", LOTRWaypoint.Region.ROHAN, LOTRFaction.ISENGARD,1143, 1051);
            addWaypoint( "SAPPERS_GROVE", LOTRWaypoint.Region.ROHAN, LOTRFaction.ISENGARD,1167, 1044);
            addWaypoint( "DOLENUI", LOTRWaypoint.Region.LONE_LANDS, LOTRFaction.RANGER_NORTH,1094, 761);
        }

        if ( MOVE_DOL_AMROTH ) {
            // still tweaking this.
            ReflectionHelper.setPrivateValue( LOTRWaypoint.class,LOTRWaypoint.DOL_AMROTH, 1156, 276);
            ReflectionHelper.setPrivateValue( LOTRWaypoint.class,LOTRWaypoint.DOL_AMROTH, LOTRWaypoint.mapToWorldX( 1156), 278);


        }

    }

    public void addWaypoint( String name, LOTRWaypoint.Region region, LOTRFaction faction, double x, double z ) {
        EnumHelper.addEnum(ENUM_HELPER_CLASS_MAP, LOTRWaypoint.class, name, region, faction, x, z);
    }

    public void postInit( FMLPostInitializationEvent event ) {

        // moving waypoints in post-init happens after road initialization...
        if ( MOVE_ISENGARD ) {
            ReflectionHelper.setPrivateValue( LOTRWaypoint.class,LOTRWaypoint.ISENGARD, 1058, 277);
            ReflectionHelper.setPrivateValue( LOTRWaypoint.class,LOTRWaypoint.ISENGARD, LOTRWaypoint.mapToWorldZ(1058 ), 279);
            // The Isengard wall is based on the isengard waypoint, so shifting the WP requires counter-shifting the wall position slightly.
            ReflectionHelper.setPrivateValue( LOTRWorldGenIsengardWalls.class, LOTRWorldGenIsengardWalls.INSTANCE, LOTRWaypoint.mapToWorldZ( 1058 ), 2 );
        }

        if ( MOVE_HELMS_DEEP ) {
            ReflectionHelper.setPrivateValue( LOTRWaypoint.class,LOTRWaypoint.HELMS_DEEP, 1133, 276);
            ReflectionHelper.setPrivateValue( LOTRWaypoint.class,LOTRWaypoint.HELMS_DEEP, 1117, 277);
            ReflectionHelper.setPrivateValue( LOTRWaypoint.class,LOTRWaypoint.HELMS_DEEP, 41498, 278);
            ReflectionHelper.setPrivateValue( LOTRWaypoint.class,LOTRWaypoint.HELMS_DEEP, 49600, 279);
        }
    }
}
