package com.bsipe.lotrperplayermobcap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRConfig;
import lotr.common.LOTRDimension;
import lotr.common.LOTRMod;
import lotr.common.LOTRSpawnDamping;
import lotr.common.entity.npc.LOTREntityNPC;
import lotr.common.world.LOTRWorldChunkManager;
import lotr.common.world.LOTRWorldProvider;
import lotr.common.world.biome.LOTRBiome;
import lotr.common.world.biome.variant.LOTRBiomeVariant;
import lotr.common.world.spawning.LOTRBiomeSpawnList;
import lotr.common.world.spawning.LOTREventSpawner;
import lotr.common.world.spawning.LOTRSpawnEntry;
import lotr.common.world.spawning.LOTRSpawnerNPCs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.*;
import java.util.stream.Collectors;

import static lotr.common.world.spawning.LOTRSpawnerNPCs.getRandomSpawningPointInChunk;
import static lotr.common.world.spawning.LOTRSpawnerNPCs.shuffle;

@Mod(modid = Main.MODID, name= Main.NAME, version = Main.VERSION, acceptableRemoteVersions="*")
public class Main
{

    public static boolean lotr;

    public static final String MODID = "lotr_per_player_mob_cap";
    public static final String VERSION = "1.0";
    public static final String NAME = "LOTR per player mob cap";

    private static Set<ChunkCoordIntPair> eligibleSpawnChunks = new HashSet<>();
    private static final int chunkRange = 7;
    public static final int expectedChunks = 196;
    public static int totalMobsSpawned = 0;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event ) {
        // item/block init registry.
        lotr = Loader.isModLoaded("lotr");
        System.out.println( "IS THE LOTR MOD LOADED: " + lotr );

        try {
            ReflectionHelper.setPrivateValue( LOTRDimension.class,LOTRDimension.MIDDLE_EARTH, 0, 14);
        } catch( Exception e ) {
            throw new ReflectionHelper.UnableToAccessFieldException(new String[0], e);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        if ( lotr ) {
            // deleting most crap
            FMLCommonHandler.instance().bus().register(this);
        }


    }

//    public void postInit( FMLPostInitializationEvent event ) {
//        // not really using this maybe. . .
//    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        // if we are serverside,
        // and at the end of the tick phase,
        // and in the middle earth dimension
        // and config allows mob spawning . . .
        if ( !world.isRemote
                && event.phase == TickEvent.Phase.END
                && world == DimensionManager.getWorld(LOTRDimension.MIDDLE_EARTH.dimensionID)
                && LOTRMod.canSpawnMobs(world)) {

                performSpawning(world);
        }
    }


    // so there's several steps involved here.. .

    // primarily

    public void performSpawning( World world ) {
        System.out.println( "Now in performSpawning()" );
        // hardcoded for now.
        final int mobsPerPlayer = 100;

        // this gives me the basic info I need.
        int[] mobCounts = countNPCs( world );
        if ( mobCounts == null ) return;

        List<EntityPlayer> playersBelowMobCap = new ArrayList<>();

        for ( int i = 0; i < mobCounts.length; i ++ ) {
            if ( mobCounts[i] < mobsPerPlayer * mobCounts.length ) {
                playersBelowMobCap.add( (EntityPlayer) world.playerEntities.get( i ) );
            }
        }

        if ( playersBelowMobCap.size() == 0 ) return;

        getSpawnableChunks(world, eligibleSpawnChunks, playersBelowMobCap);
        attemptToSpawn( world );


        // so the just of this is . . .

        //Q1 : do I spawn anything. . .

        // try to spawn something.



    }


    private static int[] countNPCs(World world) {
        System.out.println( "Now in countNPCs()" );
        if ( world.playerEntities.size() == 0 ) return null;


        final int limit = 128;

        List<PlayerTracker> players = (List<PlayerTracker>) world.playerEntities.stream().map(player -> new PlayerTracker((EntityPlayer)player, limit )).collect(Collectors.toList());

        for(int i = 0; i < world.loadedEntityList.size(); ++i) {
            Entity entity = (Entity)world.loadedEntityList.get(i);
            if (entity instanceof LOTREntityNPC) {
                int spawnCountValue = ((LOTREntityNPC)entity).getSpawnCountValue();
                int nearbyPlayers = 0;

                // this is where I check locality.
                for ( PlayerTracker p : players ) {
                    nearbyPlayers += p.checkMobInCap( entity.chunkCoordX, entity.chunkCoordY, entity.chunkCoordZ ) ? 1 : 0;
                }
                for ( PlayerTracker p : players ) {
                    p.increaseMobCap( nearbyPlayers  * spawnCountValue );
                }
            }
        }
        // at the end of this, I have my info.

        return players.stream().mapToInt(PlayerTracker::getMobCount ).toArray();
    }


    public static void getSpawnableChunks(World world, Set<ChunkCoordIntPair> set, List<EntityPlayer> players) {
        System.out.println( "Now in getSpawnableChunks()" );
        set.clear();

        for(int l = 0; l < players.size(); ++l) {
            EntityPlayer entityplayer = (EntityPlayer)world.playerEntities.get(l);
            int i = MathHelper.floor_double(entityplayer.posX / 16.0);
            int k = MathHelper.floor_double(entityplayer.posZ / 16.0);

            for(int i1 = -chunkRange; i1 <= chunkRange; ++i1) {
                for(int k1 = -chunkRange; k1 <= chunkRange; ++k1) {
                    ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i + i1, k + k1);
                    set.add(chunkcoordintpair);
                }
            }
        }
    }

    public static void attemptToSpawn(World world) {
        System.out.println( "Now in attemptToSpawn()" );
        List<ChunkCoordIntPair> shuffled = shuffle(eligibleSpawnChunks);
        Iterator<ChunkCoordIntPair> iterator = shuffled.iterator();
        boolean foundValidSpawnLocalation = false;
        ChunkPosition chunkPosition = null;
        while ( ! foundValidSpawnLocalation && iterator.hasNext() ) {
            ChunkCoordIntPair chunkCoords = iterator.next();
            chunkPosition = getRandomSpawningPointInChunk(world, chunkCoords);
            if ( chunkPosition == null ) break;
            foundValidSpawnLocalation = isValidSpawningLocation( world, chunkPosition );
        }

        if ( ! foundValidSpawnLocalation || chunkPosition == null ) return;

        spawnNPCAtCoords( world, chunkPosition, world.getSpawnPoint() );
    }


    public static boolean isValidSpawningLocation(World world, ChunkPosition position ) {
        System.out.println( "Now in isValidSpawningLocation()" );
        return world.getBlock(position.chunkPosX, position.chunkPosY, position.chunkPosZ).isNormalCube()
                && world.getBlock(position.chunkPosX, position.chunkPosY, position.chunkPosZ).getMaterial() != Material.air;
    }

    public static void spawnNPCAtCoords(World world, ChunkPosition position, ChunkCoordinates spawnPoint) {
        System.out.println( "Now in spawnNPCAtCoords()" );
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
                            if (world.getClosestPlayer((double)f, (double)f1, (double)f2, 24.0) == null) {
                                float f3 = f - (float)spawnPoint.posX;
                                float f4 = f1 - (float)spawnPoint.posY;
                                float f5 = f2 - (float)spawnPoint.posZ;
                                float distSq = f3 * f3 + f4 * f4 + f5 * f5;
                                if (distSq >= 576.0F) {
                                    EntityLiving entity;
                                    try {
                                        entity = (EntityLiving)spawnEntry.entityClass.getConstructor(World.class).newInstance(world);
                                    } catch (Exception var42) {
                                        Exception e = var42;
                                        e.printStackTrace();
                                        return;
                                    }

                                    entity.setLocationAndAngles((double)f, (double)f1, (double)f2, world.rand.nextFloat() * 360.0F, 0.0F);
                                    if (entity instanceof LOTREntityNPC && isConquestSpawn) {
                                        LOTREntityNPC npc = (LOTREntityNPC)entity;
                                        npc.setConquestSpawning(true);
                                    }

                                    Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(entity, world, f, f1, f2);
                                    if (canSpawn == Event.Result.ALLOW || canSpawn == Event.Result.DEFAULT && entity.getCanSpawnHere()) {
                                        world.spawnEntityInWorld(entity);
                                        totalMobsSpawned++;
                                        if (entity instanceof LOTREntityNPC) {
                                            LOTREntityNPC npc = (LOTREntityNPC)entity;
                                            npc.isNPCPersistent = false;
                                            npc.setShouldTraderRespawn(false);
                                            npc.setConquestSpawning(false);
                                        }

                                        if (!ForgeEventFactory.doSpecialSpawn(entity, world, f, f1, f2)) {
                                            entityData = entity.onSpawnWithEgg(entityData);
                                        }

                                        // COMMENTED, will be needed for intervals and cycles. but not for this.
//                                        totalSpawnCount += entity instanceof LOTREntityNPC ? ((LOTREntityNPC)entity).getSpawnCountValue() : 1;
//                                        if (c > 0 && totalSpawnCount > maxSpawnCount) {
//                                            return;
//                                        }

                                        ++spawned;
                                        if (spawned >= spawnCount) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static LOTRSpawnEntry.Instance getRandomSpawnListEntry(World world, int i, int j, int k) {
        System.out.println( "Now in getRandomSpawnListEntry()" );
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
        System.out.println( "Now in canNPCSpawnAtLocation()" );
        if (!World.doesBlockHaveSolidTopSurface(world, i, j - 1, k)) {
            return false;
        } else {
            Block block = world.getBlock(i, j - 1, k);
            world.getBlockMetadata(i, j - 1, k);
            boolean spawnBlock = block.canCreatureSpawn(EnumCreatureType.monster, world, i, j - 1, k);
            return spawnBlock && block != Blocks.bedrock && !world.getBlock(i, j, k).isNormalCube() && !world.getBlock(i, j, k).getMaterial().isLiquid() && !world.getBlock(i, j + 1, k).isNormalCube();
        }
    }
}

