package com.jemnetworks.strongholdconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jemnetworks.strongholdconfig.util.TypedField;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;

public class StrongholdPositionGenerator {
    private static Throwable loadException = null;

    private static TypedField<List<ChunkCoordIntPair>> STRONGHOLDS_FIELD;
    private static TypedField<WorldChunkManager> POPULATION_SOURCE_FIELD;

    static {
        try {
            STRONGHOLDS_FIELD = TypedField.getDeclaredField(ChunkGenerator.class, "f");
            STRONGHOLDS_FIELD.getWrapped().setAccessible(true);
            POPULATION_SOURCE_FIELD = TypedField.getDeclaredField(ChunkGenerator.class, "b");
            POPULATION_SOURCE_FIELD.getWrapped().setAccessible(true);
        } catch (Throwable e) {
            loadException = e;
        }
    }

    public static void generateStrongholdPositions(ChunkGenerator gen) throws ReflectiveOperationException {
        List<ChunkCoordIntPair> strongholds = STRONGHOLDS_FIELD.get(gen);

        // Strongholds already generated!
        if (!strongholds.isEmpty())
            return;

        // Cancel if no strongholds in this world
        StrongholdConfigWrapper strongholdConfig = new StrongholdConfigWrapper(gen.getSettings().b());
        if (strongholdConfig == null || strongholdConfig.getCount() == 0)
            return;

        // Find a list of biomes whitelisted for strongholds
        List<BiomeBase> allowedBiomes = new ArrayList<>(); // NOTE: an ArrayList is faster than a HashSet for some reason
        for (BiomeBase biome : POPULATION_SOURCE_FIELD.get(gen).b()) {
            if (biome.e().a(StructureGenerator.k))
                allowedBiomes.add(biome);
        }

        // Load config
        int distance = strongholdConfig.getDistance(); // Distance between each ring
        int count = strongholdConfig.getCount(); // The maximum number of strongholds in the world
        int spread = strongholdConfig.getSpread(); // How many strongholds in the first stronghold ring

        // Initialize RNG
        Random random = new Random();
        random.setSeed(gen.e);

        // Generate the strongholds
        double offsetAngle = random.nextDouble() * Math.PI * 2.0D; // Starting angle
        int strongholdsInRing = 0;
        int activeRing = 0;
        for (int strongholdNumber = 0; strongholdNumber < count; strongholdNumber++) {
            // First generation of chunk coordinates
            double distanceFromWorldOrigin = (4 * distance + distance * activeRing * 6) // Minimum distance
                                           + (random.nextDouble() - 0.5D) * distance * 2.5D; // Distance in ring
            int chunkX = (int)Math.round(Math.cos(offsetAngle) * distanceFromWorldOrigin);
            int chunkY = (int)Math.round(Math.sin(offsetAngle) * distanceFromWorldOrigin);

            // Move the stronghold if it's not in a whitelisted biome
            BlockPosition strongholdPos = locateBiome(POPULATION_SOURCE_FIELD.get(gen), SectionPosition.a(chunkX, 8), SectionPosition.a(chunkY, 8), allowedBiomes, random);
            if (strongholdPos != null) {
                chunkX = SectionPosition.a(strongholdPos.getX());
                chunkY = SectionPosition.a(strongholdPos.getZ());
            }

            // Track the stronghold
            strongholds.add(new ChunkCoordIntPair(chunkX, chunkY));

            // Calculate the next stronghold position
            offsetAngle += 6.283185307179586D / spread;

            // Update generation settings if moving to the next ring
            if (++strongholdsInRing == spread) {
                activeRing++;
                strongholdsInRing = 0;
                spread += 2 * spread / (activeRing + 1);
                spread = Math.min(spread, count - strongholdNumber);
                offsetAngle += random.nextDouble() * Math.PI * 2.0D;
            }
        }
    }

    // This code is still obfuscated, sorry!
    private static BlockPosition locateBiome(WorldChunkManager parent, int x, int z, List<BiomeBase> allowedBiomes, Random random) {
        int biomeX = QuartPos.a(x);
        int biomeZ = QuartPos.a(z);
        BlockPosition result = null;
        int foundCount = 0;
        for (int offsetZ = -28; offsetZ <= 28; offsetZ++) {
            for (int offsetX = -28; offsetX <= 28; offsetX++) {
                int testX = biomeX + offsetX;
                int testZ = biomeZ + offsetZ;
                if (allowedBiomes.contains(parent.getBiome(testX, 0, testZ))) {
                    if (result == null || random.nextInt(foundCount + 1) == 0) {
                        result = new BlockPosition(QuartPos.b(testX), 0, QuartPos.b(testZ));
                    }
                    foundCount++;
                }
            }
        }
        return result;
    }

    public static boolean ensureLoadedCorrectly() {
        return loadException == null;
    }

    public static void failIfUnloaded() throws Exception {
        if (!ensureLoadedCorrectly()) {
            throw new RuntimeException("Unable to load StrongholdPositionGenerator", loadException);
        }
    }
}
