package me.vivace.game.util;

import me.vivace.game.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test {
    public static List<java.awt.Color> colors = new ArrayList<java.awt.Color>();
    public static final int[][] diagnal = {{0}, {1, 9}, {2, 10, 18}, {3, 11, 19, 27}, {4, 12, 20, 28, 36}, {5, 13, 21, 29, 37, 45},
            {6, 14, 22, 30, 38, 46}, {7, 15, 23, 31, 39, 47}, {8, 16, 24, 32, 40, 48}, {17, 25, 33, 41, 49},
            {26, 34, 42, 50}, {35, 43, 51}, {44, 52}, {53}};

    public static final Material[] rainbow = {Material.RED_CONCRETE, Material.ORANGE_CONCRETE, Material.YELLOW_CONCRETE, Material.LIME_CONCRETE, Material.LIGHT_BLUE_CONCRETE, Material.BLUE_CONCRETE, Material.PURPLE_CONCRETE};
    public static void sendVector(Player p) {
        new BukkitRunnable() {

            @Override
            public void run() {
                p.sendMessage(
                        p.getLocation().getDirection().toString()
                );
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
    }

    public void gamBak(Location l1, Material m, int tick) {
        new BukkitRunnable() {

            @Override
            public void run() {
                Block b = l1.getBlock();

                boolean ison = b.getType() == m;
                if (ison) {
                    b.setType(Material.AIR);
                } else {
                    b.setType(m);
                }
            }
        }.runTaskTimer(Main.getPlugin(Main.class), tick, tick);
    }

    public void allBlocks(Location l1) {
        final int[] i = {0};

        new BukkitRunnable() {

            @Override
            public void run() {

                if (i[0] >= Material.values().length) return;
                Material m = Material.values()[i[0]];
                while (!Material.values()[i[0]].isBlock()) {
                    i[0]++;
                    m = Material.values()[i[0]];
                }

                Block b = l1.getBlock();
                b.setType(m);
                i[0]++;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
    }

    public static void terrorPlayer(Player p) {
        final int[] s = {0};

        new BukkitRunnable() {

            @Override
            public void run() {
                p.playSound(p.getLocation(), Sound.values()[s[0]], 5f, 5f);
                if (s[0] >= Sound.values().length - 1) {
                    s[0] = 0;
                } else {
                    s[0]++;
                }
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
    }

    public static void particlePlayer(Player p) {
        final int[] s = {0};

        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    p.spawnParticle(Particle.values()[s[0]], p.getLocation(), 100);
                } catch (Exception e) {}
                if (s[0] >= Sound.values().length - 1) {
                    s[0] = 0;
                } else {
                    s[0]++;
                }
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
    }

    public static void summonMob() {
        World w = Bukkit.getWorld("world");

        new BukkitRunnable() {

            @Override
            public void run() {
                Random r = new Random();

                for (int x = -118; x >= -127; x--) {
                    Location spawnloc = new Location(w, x, 6, 54);
                    int type = r.nextInt(EntityType.values().length);
                    EntityType entitytype = EntityType.values()[type];

                    while (entitytype == EntityType.PLAYER) {
                        type = r.nextInt(EntityType.values().length);
                        entitytype = EntityType.values()[type];
                    }

                    w.spawnEntity(spawnloc, entitytype);
                }
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 60, 60);
    }

    public void allArmor() {
        final int[] i = {0};

        new BukkitRunnable() {

            @Override
            public void run() {
                if (i[0] >= colors.size()) {
                    i[0] = 0;
                }

                for (Player p : Bukkit.getOnlinePlayers()) {
                    java.awt.Color c = colors.get(i[0]);
                    int r = c.getRed();
                    int g = c.getGreen();
                    int b = c.getBlue();
                    org.bukkit.Color bukkitcolor = org.bukkit.Color.fromRGB(r, g, b);

                    p.getInventory().setHelmet(getColorArmor(Material.LEATHER_HELMET, bukkitcolor));
                    p.getInventory().setChestplate(getColorArmor(Material.LEATHER_CHESTPLATE, bukkitcolor));
                    p.getInventory().setLeggings(getColorArmor(Material.LEATHER_LEGGINGS, bukkitcolor));
                    p.getInventory().setBoots(getColorArmor(Material.LEATHER_BOOTS, bukkitcolor));
                }

                i[0]++;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
    }

    public void chestAnimationDiagonally(Location l1) {
        final int[] i = {0};

        new BukkitRunnable() {

            @Override
            public void run() {
                if (i[0] >= colors.size()) {
                    i[0] = 0;
                }

                if (l1.getBlock().getType() == Material.CHEST) {
                    Chest chest = (Chest) l1.getBlock().getState();
                    int a = i[0];

                    for (int[] slots : diagnal) {
                        java.awt.Color c = colors.get(a % colors.size());
                        int r = c.getRed();
                        int g = c.getGreen();
                        int b = c.getBlue();
                        org.bukkit.Color bukkitcolor = org.bukkit.Color.fromRGB(r, g, b);

                        for (int slot : slots) {
                            chest.getInventory().setItem(slot, getColorArmor(Material.LEATHER_CHESTPLATE, bukkitcolor));
                        }
                        a += 3;
                    }
                }

                i[0] += 3;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
    }

    public static void chestAnimationVertically(Location l1) {
        final int[] i = {0};

        new BukkitRunnable() {

            @Override
            public void run() {
                if (i[0] >= colors.size()) {
                    i[0] = 0;
                }

                if (l1.getBlock().getType() == Material.CHEST) {
                    Chest chest = (Chest) l1.getBlock().getState();
                    int a = i[0];

                    for (int i = 0; i < 6; i++) {
                        java.awt.Color c = colors.get(a % colors.size());
                        int r = c.getRed();
                        int g = c.getGreen();
                        int b = c.getBlue();
                        org.bukkit.Color bukkitcolor = org.bukkit.Color.fromRGB(r, g, b);

                        for (int j = 0; j < 9; j++) {
                            chest.getInventory().setItem(i * 9 + j, getColorArmor(Material.LEATHER_CHESTPLATE, bukkitcolor));
                        }
                        a += 3;
                    }
                }

                i[0] += 3;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
    }

    public static ItemStack getColorArmor(Material m, org.bukkit.Color c) {
        ItemStack i = new ItemStack(m, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta) i.getItemMeta();
        meta.setColor(c);
        i.setItemMeta(meta);
        return i;
    }

    public static void makeSphere(Player p, Location l1, int radius) {
        ArrayList<Location> locations = new ArrayList<>();

        for (int xy = 0; xy < 360; xy++) {
            for (int xz = 0; xz < 360; xz++) {
                double x = Math.cos(Math.toRadians(xy)) * Math.sin(Math.toRadians(xz)) * radius;
                double y = Math.sin(Math.toRadians(xy)) * Math.sin(Math.toRadians(xz)) * radius;
                double z = Math.cos(Math.toRadians(xz)) * radius;

                Location particleloc = l1.clone().add(x, y, z);
                locations.add(particleloc);
            }
        }

        int pointer = 0;
        Location lastloc = null;

        for (Location l : locations) {
            if (!l.equals(lastloc)) {
                l.getBlock().setType(rainbow[pointer]);
                pointer++;
                if (pointer == rainbow.length) pointer = 0;
                lastloc = l;
            }
        }

//        new BukkitRunnable() {
//
//            @Override
//            public void run() {
//
//                for (Location l : locations) {
//                    p.spawnParticle(Particle.FLAME, l, 1, 0, 0, 0, 0.001);
//                }
//            }
//        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }

    public static void primeSprials(Location center, Player p) {
        ArrayList<Location> locations = new ArrayList<>();

        new BukkitRunnable() {

            int[] i = {0};

            @Override
            public void run() {
                boolean isprime = false;
                int nownum = 0;
                i[0]++;

                while (!isprime) {
                    i[0]++;
                    isprime = isPrime(i[0]);
                }

                nownum = i[0];
                Bukkit.broadcastMessage(i[0] + " - PRIME");

                Location l = center.add(Math.cos(i[0]) * i[0] * 0.1f, 0, Math.sin(i[0]) * i[0] * 0.1f);
                locations.add(l);

                for (Location pl : locations) {
                    p.spawnParticle(Particle.VILLAGER_HAPPY, pl, 0);
                }

                Bukkit.broadcastMessage(locations.size() + ":");
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 5);
    }

    public static boolean isPrime(int num) {
        boolean result = true;

        for (int i = 2; i < num; i++) {

            if (num % i == 0) {
                return false;
            }
        }

        return true;
    }
}
