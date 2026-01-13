package com.bsipe.lotrkingsaddon.modules;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRConfig;
import lotr.common.LOTRDimension;
import lotr.common.LOTRMod;
import lotr.common.entity.npc.LOTREntityNPC;
import lotr.common.world.LOTRWorldChunkManager;
import lotr.common.world.LOTRWorldProvider;
import lotr.common.world.biome.LOTRBiome;
import lotr.common.world.biome.variant.LOTRBiomeVariant;
import lotr.common.world.spawning.LOTRBiomeSpawnList;
import lotr.common.world.spawning.LOTRSpawnEntry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.*;

import static lotr.common.world.spawning.LOTRSpawnerNPCs.getRandomSpawningPointInChunk;
import static lotr.common.world.spawning.LOTRSpawnerNPCs.shuffle;

public class PerPlayerMobCapModule extends AbstractModule {

    public static boolean ENABLED;
    public static boolean ENABLE_LOGGING;
    public static int MIDDLE_EARTH_MOB_CAP;
    public static int UTUMNO_MOB_CAP;
    public static int MAX_SPAWNS_PER_CYCLE;
    public static int MAX_PACK_ATTEMPTS_PER_CYCLE = 5000;
    public static int CHUNK_RANGE;


    public static int previousMobCount;

    public static int player_index = 0;
    private static final Set<ChunkCoordIntPair> eligibleSpawnChunks = new HashSet<>();
    public static final int LIMIT = 128*128;

    public static final String CONFIG_CATAGORY = "mobs_per_player";

    public PerPlayerMobCapModule(Configuration config, boolean serverOnly ) {

        config.addCustomCategoryComment( CONFIG_CATAGORY, "These numbers were determined via testing to match current game behavior." );
        config.addCustomCategoryComment( CONFIG_CATAGORY, "This module is server side only, it is not required by the client to work." );

        ENABLED = config.getBoolean( "per_player_mob_spawning_enabled", CONFIG_CATAGORY, true, "Controls whether mob spawning is switched to a per-player system." );
        ENABLE_LOGGING = config.getBoolean( "mob_spawning_logging", CONFIG_CATAGORY, false, "Adds development logging to check if mobs are spawning properly" );
        MIDDLE_EARTH_MOB_CAP = config.getInt("middle_earth", CONFIG_CATAGORY, 114, 0, 2000, "Number of mob 'points' per player in the middle earth dimension" );
        UTUMNO_MOB_CAP = config.getInt("utumno", CONFIG_CATAGORY, 860, 0, 2000, "Number of mob 'points' per player in the utumno dimension" );
        MAX_SPAWNS_PER_CYCLE = config.getInt( "max_spawns_per_cycle", CONFIG_CATAGORY, 10, 1, 100, "Limit the number of mobs it will attempt to spawn in a given tick." );
        CHUNK_RANGE = config.getInt( "chunk_range", CONFIG_CATAGORY, 6, 5, 7, "Chunk Radius where spawn attempts occur." );
    }

    @Override
    public void preInit(FMLPreInitializationEvent event ) {
        if ( ! ENABLED ) return;
        try {
            ReflectionHelper.setPrivateValue( LOTRDimension.class,LOTRDimension.MIDDLE_EARTH, 0, 14);
            ReflectionHelper.setPrivateValue( LOTRDimension.class,LOTRDimension.UTUMNO, 0, 14);
        } catch( Exception e ) {
            throw new ReflectionHelper.UnableToAccessFieldException(new String[0], e);
        }
    }

    @Override
    public void postInit( FMLPostInitializationEvent event )
    {
        if ( ! ENABLED ) return;
        // make LOTR classic mob spawning appear only once / hour, and always fails.
        LOTRConfig.mobSpawnInterval = 72000;
    }

    @Override
    public void init( FMLInitializationEvent event ) {
        if ( ! ENABLED ) return;

        // This enables the "SubscribeEvent" annotation.
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if ( ! ENABLED ) return;
        World world = event.world;

        if ( world == null || world.isRemote ) return;

        if ( event.phase == TickEvent.Phase.END
                && ( world == DimensionManager.getWorld(LOTRDimension.MIDDLE_EARTH.dimensionID) || world == DimensionManager.getWorld(LOTRDimension.UTUMNO.dimensionID) )
                && LOTRMod.canSpawnMobs(world))
        {
            if ( world == DimensionManager.getWorld( LOTRDimension.MIDDLE_EARTH.dimensionID ) ) {
                performSpawning( world, MIDDLE_EARTH_MOB_CAP );
            }
            else if ( world == DimensionManager.getWorld( LOTRDimension.UTUMNO.dimensionID) ) {
                performSpawning( world, UTUMNO_MOB_CAP );
            }
        }
    }

    public static EntityPlayer getPlayer( World world, int index ) {
        if ( world.playerEntities.size() > index ) {
            return (EntityPlayer) world.playerEntities.get( index );
        } else if ( world.playerEntities.isEmpty() )  {
            return null;
        } else {
            ( (EntityPlayer) world.playerEntities.get( 0 )).addChatMessage( new ChatComponentText( "Tried to get " + index + " out of " + world.playerEntities.size() ) );
            return (EntityPlayer) world.playerEntities.get( 0 );
        }
    }

    public void performSpawning( World world, int mobCap ) {
        if ( world.playerEntities.isEmpty() ) return;

        player_index = world.playerEntities.size() == 1 ? 0 : ( player_index + 1 ) % world.playerEntities.size();

        EntityPlayer player = getPlayer( world, player_index );

        int count = countNPCs( world, player );
        if ( count >= mobCap ) return;

        int mobsNeeded = mobCap - count;

        getSpawnableChunks(eligibleSpawnChunks, player);
        attemptToSpawn( world, mobsNeeded, player ); // retunrs int
    }

    @SuppressWarnings("unchecked")
    private static int countNPCs(World world, EntityPlayer player) {
        int mobCount = 0;
        HashMap<String, Integer> classMap = new HashMap<>();

        for(int i = 0; i < world.loadedEntityList.size(); ++i) {
            Entity entity = (Entity)world.loadedEntityList.get( i );
            if (entity instanceof LOTREntityNPC) {
                int spawnCountValue = ((LOTREntityNPC)entity).getSpawnCountValue();

                if ( spawnCountValue > 0 ) {
                    classMap.put( ((LOTREntityNPC) entity).getEntityClassName(), classMap.getOrDefault( ((LOTREntityNPC) entity).getEntityClassName(), 0 ) + 1 );
                    if ( isInRange( player, entity ) ) {
                        mobCount += spawnCountValue;
                    }
                }
            }
        }

        if ( ENABLE_LOGGING ) {

            if ( mobCount != previousMobCount ) {
                previousMobCount = mobCount;
                boolean isMiddleEarth = world == DimensionManager.getWorld( LOTRDimension.MIDDLE_EARTH.dimensionID );
                LOG( world, "Counted " + mobCount + "/" + UTUMNO_MOB_CAP + " mobs for " + player.getDisplayName() + " in " + (isMiddleEarth ? "Middle Earth" : "Utumno" ) );
            }
        }



        return mobCount;
    }

    public static boolean isInRange(EntityPlayer p, Entity e ) {
        int d1=p.chunkCoordX - e.chunkCoordX,d2=p.chunkCoordZ - e.chunkCoordZ;
        return LIMIT > d1*d1 + d2*d2;
    }


    public static void getSpawnableChunks(Set<ChunkCoordIntPair> set, EntityPlayer player) {
        set.clear();

        int i = MathHelper.floor_double(player.posX / 16.0);
        int k = MathHelper.floor_double(player.posZ / 16.0);

        for(int i1 = -CHUNK_RANGE; i1 <= CHUNK_RANGE; ++i1) {
            for(int k1 = -CHUNK_RANGE; k1 <= CHUNK_RANGE; ++k1) {
                ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i + i1, k + k1);
                set.add(chunkcoordintpair);
            }
        }
    }

    public static int attemptToSpawn(World world, int mobsNeeded, EntityPlayer player ) {
        Iterator<ChunkCoordIntPair> iterator = null;
        int mobsSpawned = 0;
        int timesSpawnPackAttempted = 0;
        // something not working here.
        while ( mobsSpawned < Math.min( mobsNeeded, MAX_SPAWNS_PER_CYCLE ) && timesSpawnPackAttempted < MAX_PACK_ATTEMPTS_PER_CYCLE ) {
            if ( iterator == null || ! iterator.hasNext() ) {
                iterator = shuffle( eligibleSpawnChunks ).iterator();
                LOG( world, "Shuffle Chunks" );
            }

            ChunkPosition chunkPosition = getRandomSpawningPointInChunk(world, iterator.next());

            if ( chunkPosition == null || ! isValidSpawningLocation( world, chunkPosition ) ) continue;
            timesSpawnPackAttempted ++;
            // reset attempts.
            int spawns = spawnNPCAtCoords( world, chunkPosition, world.getSpawnPoint(), player );
            if ( spawns > 0 ) {
                LOG( world, "Times spawn pack attempted before success: " + timesSpawnPackAttempted );
                timesSpawnPackAttempted = 0;

            }
            mobsSpawned += spawns;

        }
        if ( timesSpawnPackAttempted >= MAX_PACK_ATTEMPTS_PER_CYCLE ) {
            LOG( world, "Spawn Pack attempts reached" );

        }

        return mobsSpawned;
    }


    public static boolean isValidSpawningLocation(World world, ChunkPosition position ) {
        return world.getBlock(position.chunkPosX, position.chunkPosY, position.chunkPosZ).isNormalCube()
                && world.getBlock(position.chunkPosX, position.chunkPosY, position.chunkPosZ).getMaterial() != Material.air;
    }

    @SuppressWarnings("unchecked")
    public static int spawnNPCAtCoords(World world, ChunkPosition position, ChunkCoordinates spawnPoint, EntityPlayer player ) {
        int mobsSpawned = 0;

        int groups = 3;

        for(int l = 0; l < groups; ++l) {
            int i1 = position.chunkPosX;
            int j1 = position.chunkPosY;
            int k1 = position.chunkPosZ;
            int range = 5;
            int yRange = 0;
            int rangeP1 = range + 1;
            int yRangeP1 = yRange + 1;
            LOTRSpawnEntry.Instance spawnEntryInstance = getRandomSpawnListEntry(world, i1, j1, k1);
            if (spawnEntryInstance != null) {
                LOTRSpawnEntry spawnEntry = spawnEntryInstance.spawnEntry;
                boolean isConquestSpawn = spawnEntryInstance.isConquestSpawn;
                int spawnCount = MathHelper.getRandomIntegerInRange(world.rand, spawnEntry.minGroupCount, spawnEntry.maxGroupCount);
                int chance = spawnEntryInstance.spawnChance;
                if (chance == 0 || world.rand.nextInt(chance) == 0) {
                    IEntityLivingData entityData = null;
                    int spawned = 0;
                    int attempts = spawnCount * 8;

                    for(int a = 0; a < attempts; ++a) {
                        i1 += world.rand.nextInt(rangeP1) - world.rand.nextInt(rangeP1);
                        j1 += world.rand.nextInt(yRangeP1) - world.rand.nextInt(yRangeP1);
                        k1 += world.rand.nextInt(rangeP1) - world.rand.nextInt(rangeP1);
                        if (world.blockExists(i1, j1, k1) && canNPCSpawnAtLocation(world, i1, j1, k1)) {
                            float f = (float)i1 + 0.5F;
                            float f1 = (float)j1;
                            float f2 = (float)k1 + 0.5F;
                            float p = (float) player.posX - f;
                            float p1 = (float) player.posY - f1;
                            float p2 = (float) player.posZ - f2;

                            if ( p*p + p1*p1 + p2*p2 >= 576.0F ) {
                                float f3 = f - (float)spawnPoint.posX;
                                float f4 = f1 - (float)spawnPoint.posY;
                                float f5 = f2 - (float)spawnPoint.posZ;
                                // check if pack attempt is close to original attempt.
                                float distSq = f3 * f3 + f4 * f4 + f5 * f5;
                                if (distSq >= 576.0F) {
                                    EntityLiving entity;
                                    try {
                                        entity = (EntityLiving)spawnEntry.entityClass.getConstructor(World.class).newInstance(world);
                                    } catch (Exception var42) {
                                        var42.printStackTrace();
                                        return mobsSpawned; // if we fail, track how many we got.
                                    }

                                    entity.setLocationAndAngles(f,f1,f2, world.rand.nextFloat() * 360.0F, 0.0F);
                                    if (entity instanceof LOTREntityNPC && isConquestSpawn) {
                                        LOTREntityNPC npc = (LOTREntityNPC)entity;
                                        npc.setConquestSpawning(true);
                                    }

                                    Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(entity, world, f, f1, f2);

                                    if (canSpawn == Event.Result.ALLOW || canSpawn == Event.Result.DEFAULT && entity.getCanSpawnHere()) {
                                        world.spawnEntityInWorld(entity);
                                        mobsSpawned += ((LOTREntityNPC)entity).getSpawnCountValue();

                                        LOG( world, "Spawned " + entity.getClass().getSimpleName() + " at coords(" + f + "," + f1 + "," + f2 + ")");
                                        if (entity instanceof LOTREntityNPC) {
                                            LOTREntityNPC npc = (LOTREntityNPC)entity;
                                            npc.isNPCPersistent = false;
                                            npc.setShouldTraderRespawn(false);
                                            npc.setConquestSpawning(false);
                                        }

                                        if (!ForgeEventFactory.doSpecialSpawn(entity, world, f, f1, f2)) {
                                            entityData = entity.onSpawnWithEgg(entityData);
                                        }

                                        ++spawned;
                                        if (spawned >= spawnCount) {
                                            break;
                                        }
                                    } else {
                                            LOG( world,"canSpawnEntity Failed" );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return mobsSpawned;
    }

    private static LOTRSpawnEntry.Instance getRandomSpawnListEntry(World world, int i, int j, int k) {
        LOTRBiomeSpawnList spawnlist = null;
        BiomeGenBase biome = world.getBiomeGenForCoords(i, k);
        if (biome instanceof LOTRBiome && world.provider instanceof LOTRWorldProvider) {
            LOTRBiome lotrbiome = (LOTRBiome)biome;
            LOTRWorldChunkManager worldChunkMgr = (LOTRWorldChunkManager)world.provider.worldChunkMgr;
            LOTRBiomeVariant variant = worldChunkMgr.getBiomeVariantAt(i, k);
            spawnlist = lotrbiome.getNPCSpawnList(world, world.rand, i, j, k, variant);
        }

        return spawnlist != null ? spawnlist.getRandomSpawnEntry(world.rand, world, i, j, k) : null;
    }

    private static boolean canNPCSpawnAtLocation(World world, int i, int j, int k) {
        if (!World.doesBlockHaveSolidTopSurface(world, i, j - 1, k)) {
            return false;
        } else {
            Block block = world.getBlock(i, j - 1, k);
            world.getBlockMetadata(i, j - 1, k);
            boolean spawnBlock = block.canCreatureSpawn(EnumCreatureType.monster, world, i, j - 1, k);
            return spawnBlock && block != Blocks.bedrock && !world.getBlock(i, j, k).isNormalCube() && !world.getBlock(i, j, k).getMaterial().isLiquid() && !world.getBlock(i, j + 1, k).isNormalCube();
        }
    }

    public static void LOG( World world, String message ) {
        LOG( world, message, false );
    }

    public static void LOG( World world, String message, boolean override ) {
        if ( ENABLE_LOGGING || override ) {
            ( (EntityPlayer) world.playerEntities.get( 0 )).addChatMessage( new ChatComponentText( message ) );
        }
    }

}
