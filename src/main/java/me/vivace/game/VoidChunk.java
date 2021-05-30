package me.vivace.game;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.generator.ChunkGenerator;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class VoidChunk extends ChunkGenerator {
    @Override
    @Nonnull
    public ChunkData generateChunkData(@Nonnull World world, @Nonnull Random random, int x, int z, @Nonnull BiomeGrid biome) {
        return createChunkData(world);
    }

    public static World generateNullWorld(String worldname) {
        WorldCreator c = new WorldCreator(worldname);

        c.generator(new VoidChunk());
        World w = c.createWorld();

        assert w != null;
        w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        w.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        w.setGameRule(GameRule.DO_MOB_SPAWNING, false);

        return w;
    }

    public static void deleteWorld(World w) throws IOException {

        Bukkit.getServer().unloadWorld(w.getName(), false);
        File destDir = new File("." + File.separator + w.getName());
        FileUtils.deleteDirectory(destDir);
    }
}
