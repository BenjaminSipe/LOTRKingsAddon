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
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.*;

import static lotr.common.world.spawning.LOTRSpawnerNPCs.getRandomSpawningPointInChunk;
import static lotr.common.world.spawning.LOTRSpawnerNPCs.shuffle;

@Mod(modid = Main.MODID, name= Main.NAME, version = Main.VERSION, acceptableRemoteVersions="*")
public class Main
{

    public static boolean lotr;

    public static final String MODID = "lotr_per_player_mob_cap";
    public static final String VERSION = "1.0";
    public static final String NAME = "LOTR per player mob cap";

    private static final Set<ChunkCoordIntPair> eligibleSpawnChunks = new HashSet<>();
    private static final int CHUNK_RANGE = 7;

    public static int player_index = 0;

    // I Would like this to be set to a specific NUMBER via config.
    public static final int MOBS_PER_PLAYER = 100;

    public static final int LIMIT = 128*128;

    /*
    TODO: update to use expected chunk variable
    TODO: add config file and make per-player cap config based.
    TODO: implement LOTR's interval spawning system.
    TODO: make modrinth ( and curseforge ) page.
    TODO: test in multiplayer setting.
     */

    @EventHandler
    public void preInit(FMLPreInitializationEvent event ) {
        lotr = Loader.isModLoaded("lotr");
        if ( !lotr ) return;
        try {
            ReflectionHelper.setPrivateValue( LOTRDimension.class,LOTRDimension.MIDDLE_EARTH, 0, 14);
        } catch( Exception e ) {
            throw new ReflectionHelper.UnableToAccessFieldException(new String[0], e);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if ( !lotr ) return;
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;

        if ( !world.isRemote
                && event.phase == TickEvent.Phase.END
                && world == DimensionManager.getWorld(LOTRDimension.MIDDLE_EARTH.dimensionID)
                && LOTRMod.canSpawnMobs(world))
        {
            performSpawning(world);
        }
    }

    public void performSpawning( World world ) {

        EntityPlayer player = (EntityPlayer) world.playerEntities.get( player_index++ );

        int mobCount = countNPCs( world, player );

        if ( mobCount >= MOBS_PER_PLAYER ) {
            player = (EntityPlayer) world.playerEntities.get( player_index++ );
            mobCount = countNPCs( world, player );
        }

        if ( mobCount >= MOBS_PER_PLAYER ) return;




        getSpawnableChunks(eligibleSpawnChunks, player);
        attemptToSpawn( world );

    }

    @SuppressWarnings("unchecked")
    private static int countNPCs(World world, EntityPlayer player) {
        int mobCount = 0;

        for(int i = 0; i < world.loadedEntityList.size(); ++i) {
            Entity entity = (Entity)world.loadedEntityList.get(i);
            if (entity instanceof LOTREntityNPC) {
                int spawnCountValue = ((LOTREntityNPC)entity).getSpawnCountValue();

                if ( spawnCountValue > 0 ) {
                    if ( isInRange( player, entity ) ) {
                        mobCount += spawnCountValue;
                    }
                }
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

    public static void attemptToSpawn(World world) {
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

        if ( ! foundValidSpawnLocalation ) return;

        spawnNPCAtCoords( world, chunkPosition, world.getSpawnPoint() );
    }


    public static boolean isValidSpawningLocation(World world, ChunkPosition position ) {
        return world.getBlock(position.chunkPosX, position.chunkPosY, position.chunkPosZ).isNormalCube()
                && world.getBlock(position.chunkPosX, position.chunkPosY, position.chunkPosZ).getMaterial() != Material.air;
    }

    @SuppressWarnings("unchecked")
    public static void spawnNPCAtCoords(World world, ChunkPosition position, ChunkCoordinates spawnPoint) {
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
                            if (world.getClosestPlayer(f,f1,f2, 24.0) == null) {
                                float f3 = f - (float)spawnPoint.posX;
                                float f4 = f1 - (float)spawnPoint.posY;
                                float f5 = f2 - (float)spawnPoint.posZ;
                                float distSq = f3 * f3 + f4 * f4 + f5 * f5;
                                if (distSq >= 576.0F) {
                                    EntityLiving entity;
                                    try {
                                        entity = (EntityLiving)spawnEntry.entityClass.getConstructor(World.class).newInstance(world);
                                    } catch (Exception var42) {
                                        var42.printStackTrace();
                                        return;
                                    }

                                    entity.setLocationAndAngles(f,f1,f2, world.rand.nextFloat() * 360.0F, 0.0F);
                                    if (entity instanceof LOTREntityNPC && isConquestSpawn) {
                                        LOTREntityNPC npc = (LOTREntityNPC)entity;
                                        npc.setConquestSpawning(true);
                                    }

                                    Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(entity, world, f, f1, f2);
                                    if (canSpawn == Event.Result.ALLOW || canSpawn == Event.Result.DEFAULT && entity.getCanSpawnHere()) {
                                        world.spawnEntityInWorld(entity);
//                                        totalMobsSpawned++;
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
//        System.out.println( "Now in getRandomSpawnListEntry()" );
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
//        System.out.println( "Now in canNPCSpawnAtLocation()" );
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

