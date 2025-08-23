package com.bsipe.lotrperplayermobcap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRConfig;
import lotr.common.LOTRDimension;
import lotr.common.LOTRMod;
import lotr.common.LOTRSpawnDamping;
import lotr.common.enchant.LOTREnchantment;
import lotr.common.entity.npc.LOTREntityNPC;
import lotr.common.item.LOTRItemModifierTemplate;
import lotr.common.world.LOTRWorldChunkManager;
import lotr.common.world.LOTRWorldProvider;
import lotr.common.world.biome.LOTRBiome;
import lotr.common.world.biome.variant.LOTRBiomeVariant;
import lotr.common.world.spawning.LOTRBiomeSpawnList;
import lotr.common.world.spawning.LOTRSpawnEntry;
import lotr.common.world.spawning.LOTRSpawnerNPCs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
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
import scala.swing.TextComponent;
import tv.twitch.chat.Chat;

import java.util.*;

import static lotr.common.LOTRSpawnDamping.TYPE_NPC;
import static lotr.common.LOTRSpawnDamping.getSpawnCap;
import static lotr.common.world.spawning.LOTRSpawnerNPCs.getRandomSpawningPointInChunk;
import static lotr.common.world.spawning.LOTRSpawnerNPCs.shuffle;

@Mod(modid = Main.MODID, name= Main.NAME, version = Main.VERSION, acceptableRemoteVersions="*")
public class Main
{

    public static Configuration config;

    public static boolean lotr;

    public static final String MODID = "lotr_per_player_mob_cap";
    public static final String VERSION = "1.0";
    public static final String NAME = "LOTR per player mob cap";

    private static final Set<ChunkCoordIntPair> eligibleSpawnChunks = new HashSet<>();
    private static final int CHUNK_RANGE = 7;

    public static int player_index = 0;
    public static int tickCounter = 0;

    // I Would like this to be set to a specific NUMBER via config.
    public static int ME_MOBS_PER_PLAYER;
    public static int UTUMNO_MOBS_PER_PLAYER;

    public static final int LIMIT = 128*128;

    /*
    TODO: make modrinth ( and curseforge ) page.
    TODO: test in multiplayer setting.
     */

    public void setupAndLoadConfig(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.addCustomCategoryComment( "mobs_per_player", "These numbers were determined via testing to match current game behavior." );
        ME_MOBS_PER_PLAYER = config.getInt("middle_earth", "mobs_per_player", 114, 0, 2000, "Number of mob 'points' per player in the middle earth dimension" );
        UTUMNO_MOBS_PER_PLAYER = config.getInt("utumno", "mobs_per_player", 573, 0, 2000, "Number of mob 'points' per player in the utumno dimension" );


        if (config.hasChanged()) {
            config.save();
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event ) {
        lotr = Loader.isModLoaded("lotr");
        if ( !lotr ) return;
        try {
            ReflectionHelper.setPrivateValue( LOTRDimension.class,LOTRDimension.MIDDLE_EARTH, 0, 14);
            ReflectionHelper.setPrivateValue( LOTRDimension.class,LOTRDimension.UTUMNO, 0, 14);
        } catch( Exception e ) {
            throw new ReflectionHelper.UnableToAccessFieldException(new String[0], e);
        }

        setupAndLoadConfig( event );
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if ( !lotr ) return;

        addCraftingRecipe();

        FMLCommonHandler.instance().bus().register(this);


    }


    public static void addCraftingRecipe() {
        GameRegistry.addRecipe(new IRecipe() {

            @Override
            public boolean matches(InventoryCrafting inventory, World world) {
                int numberOfEnduringScrolls = 0;
                for ( int i = 0; i < inventory.getSizeInventory() ; i ++ ) {
                    if ( inventory.getStackInSlot( i ) != null && inventory.getStackInSlot( i ).getItem().equals( LOTRMod.modTemplate ) ) {
                        if ( LOTRItemModifierTemplate.getModifier( inventory.getStackInSlot( i ) ).equals( LOTREnchantment.durable3 ) ) {
                            numberOfEnduringScrolls ++;
                        } else {
                            return false;
                        }
                    }
                }

                return numberOfEnduringScrolls == 3;
            }

            @Override
            public ItemStack getCraftingResult(InventoryCrafting inventory) {
                ItemStack stack = new ItemStack( Items.enchanted_book );
                Items.enchanted_book.addEnchantment( stack, new EnchantmentData( Enchantment.unbreaking, 3 ) );

                return stack;
            }

            @Override
            public int getRecipeSize() {
                return 3;
            }

            @Override
            public ItemStack getRecipeOutput() {
                ItemStack stack = new ItemStack( Items.enchanted_book );
                Items.enchanted_book.addEnchantment( stack, new EnchantmentData( Enchantment.unbreaking, 3 ) );
                return stack;
            }
        } );
    }


    @EventHandler
    public void postInit( FMLPostInitializationEvent event )
    {
        // make LOTR classic mob spawning appear only once / hour, and always fails.
        LOTRConfig.mobSpawnInterval = 72000;
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;

        if ( world == null || world.isRemote ) return;

        if ( event.phase == TickEvent.Phase.END
                && ( world == DimensionManager.getWorld(LOTRDimension.MIDDLE_EARTH.dimensionID) || world == DimensionManager.getWorld(LOTRDimension.UTUMNO.dimensionID) )
                && LOTRMod.canSpawnMobs(world))
        {
            if ( world == DimensionManager.getWorld( LOTRDimension.MIDDLE_EARTH.dimensionID ) ) {
                performSpawning( world, ME_MOBS_PER_PLAYER );
            }
            else if ( world == DimensionManager.getWorld( LOTRDimension.UTUMNO.dimensionID) ) {
                performSpawning( world, UTUMNO_MOBS_PER_PLAYER );
            }
        }
    }



    public void performSpawning( World world, int mobCap ) {
        if ( world.playerEntities.isEmpty() ) return;

        EntityPlayer player = (EntityPlayer) world.playerEntities.get( player_index ++ );

        player_index = player_index % world.playerEntities.size();

        int count = countNPCs( world, player );
        if ( count >= mobCap ) return;


        getSpawnableChunks(eligibleSpawnChunks, player);
        boolean success = attemptToSpawn( world );
        // if there's more than one player, try twice just to give each player
        if ( ! success && world.playerEntities.size() > 1 ) {
            attemptToSpawn( world );
        }

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

    public static boolean attemptToSpawn(World world) {
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

        if ( ! foundValidSpawnLocalation ) return false;

        return spawnNPCAtCoords( world, chunkPosition, world.getSpawnPoint() );
    }


    public static boolean isValidSpawningLocation(World world, ChunkPosition position ) {
        return world.getBlock(position.chunkPosX, position.chunkPosY, position.chunkPosZ).isNormalCube()
                && world.getBlock(position.chunkPosX, position.chunkPosY, position.chunkPosZ).getMaterial() != Material.air;
    }

    @SuppressWarnings("unchecked")
    public static boolean spawnNPCAtCoords(World world, ChunkPosition position, ChunkCoordinates spawnPoint) {
        int groups = 3;

        boolean success = false;

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
                                        return false;
                                    }

                                    entity.setLocationAndAngles(f,f1,f2, world.rand.nextFloat() * 360.0F, 0.0F);
                                    if (entity instanceof LOTREntityNPC && isConquestSpawn) {
                                        LOTREntityNPC npc = (LOTREntityNPC)entity;
                                        npc.setConquestSpawning(true);
                                    }

                                    Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(entity, world, f, f1, f2);
                                    if (canSpawn == Event.Result.ALLOW || canSpawn == Event.Result.DEFAULT && entity.getCanSpawnHere()) {
                                        world.spawnEntityInWorld(entity);
                                        success = true;
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
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return success;
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
}

