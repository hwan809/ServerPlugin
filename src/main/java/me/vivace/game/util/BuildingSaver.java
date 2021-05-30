package me.vivace.game.util;

import me.vivace.game.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class BuildingSaver {

    public static void saveBuilding(Location l1, Location l2, String configname) throws IOException {
        if (!l1.getWorld().equals(l2.getWorld())) return;

        Plugin plugin = Main.getPlugin(Main.class);
        plugin.saveDefaultConfig();

        File configfile = new File(plugin.getDataFolder(), configname + ".yml");
        FileConfiguration dataconfig = YamlConfiguration.loadConfiguration(configfile);
        InputStream defaultStream = plugin.getResource(configname + ".yml");

        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            dataconfig.setDefaults(defaultConfig);
        }

        World w = l1.getWorld();
        int x1 = (int) l1.getX(); int x2 = (int) l2.getX();
        int y1 = (int) l1.getY(); int y2 = (int) l2.getY();
        int z1 = (int) l1.getZ(); int z2 = (int) l2.getZ();

        int minx = Math.min(x1, x2); int maxx = Math.max(x1, x2);
        int miny = Math.min(y1, y2); int maxy = Math.max(y1, y2);
        int minz = Math.min(z1, z2); int maxz = Math.max(z1, z2);

        dataconfig.set("xdif", maxx - minx);
        dataconfig.set("ydif", maxy - miny);
        dataconfig.set("zdif", maxz - minz);

        for (int x = minx; x < maxx + 1; x++) {
            for (int y = miny; y < maxy + 1; y++) {
                for (int z = minz; z < maxz + 1; z++) {
                    Block b = new Location(w, x, y, z).getBlock();

                    String path = (x - minx) + "." + (y - miny) + "." + (z - minz);
                    plugin.getLogger().info(path);
                    dataconfig.set(path + ".type", b.getType().name());
                    dataconfig.set(path + ".data", b.getBlockData().getAsString());
                }
            }
        }

        try {
            dataconfig.save(configfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pasteBuilding(Location l1, String buildingname) {
        Plugin plugin = Main.getPlugin(Main.class);

        File configfile = new File(plugin.getDataFolder(), buildingname + ".yml");
        FileConfiguration dataconfig = YamlConfiguration.loadConfiguration(configfile);

        World w = l1.getWorld();

        int xdif = dataconfig.getInt("xdif");
        int ydif = dataconfig.getInt("ydif");
        int zdif = dataconfig.getInt("zdif");

        int locxdif = (int) l1.getX();
        int locydif = (int) l1.getY();
        int loczdif = (int) l1.getZ();

        for (int x = 0; x < xdif + 1; x++) {
            for (int y = 0; y < ydif + 1; y++) {
                for (int z = 0; z < zdif + 1; z++) {
                    String path = x + "." + y + "." + z;

                    Material type = Material.valueOf(dataconfig.getString(path + ".type"));
                    String cb = dataconfig.getString(path + ".data");
                    Block b = new Location(w, x + locxdif, y + locydif, z + loczdif).getBlock();

                    assert cb != null;
                    b.setType(type);
                    b.setBlockData(Bukkit.getServer().createBlockData(cb));
                }
            }
        }
    }
}
