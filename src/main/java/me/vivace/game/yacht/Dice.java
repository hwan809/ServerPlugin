package me.vivace.game.yacht;

import me.vivace.game.Main;
import me.vivace.game.util.Gradient;
import me.vivace.game.util.ItemStackManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.Random;

public class Dice {

    private Location l;

    private Material border;
    private Material background;
    private Material eye;

    public static final Color START = new Color(237, 41, 56);
    public static final Color END = new Color(0, 255, 127);
    public static final Gradient BAR = new Gradient(START, END, 40);

    public Dice(Location l, Material background, Material eye) {
        this.l = l;
        this.background = background;
        this.eye = eye;

        initialize();
    }

    public void initialize() {
        for (int x = -1; x <= 7; x++) {
            for (int y = -1; y <= 7; y++) {
                Block testblock = new Location(l.getWorld(), l.getX() + x, l.getY() - y, l.getZ()).getBlock();
                testblock.setType(Material.WHITE_CONCRETE);
            }
        }
    }

    public static final int[][][] DICE_EYES = { // [주사위 눈 - 1][n개의 눈][x, y]
                                                {{3, 3}},
                                                {{1, 1}, {5, 5}},
                                                {{1, 1}, {3, 3}, {5, 5}},
                                                {{1, 1}, {1, 5}, {5, 1}, {5, 5}},
                                                {{1, 1}, {1, 5}, {3, 3}, {5, 1}, {5, 5}},
                                                {{1, 1}, {1, 5}, {3, 1}, {3, 5}, {5, 1}, {5, 5}}
                                              };

    public int rollDice(Player p, int tick, YachtManager yachtManager) {
        setBorderColor(Material.WHITE_CONCRETE);
        getRerollLoc().getBlock().setType(Material.AIR);

        Random r = new Random();
        int dicenum = r.nextInt(6) + 1;

        if (tick == 0) {
            return dicenum;
        }

        int[] ticks = {0};
        float[] nowtick = {1};
        int[] lefttick = {0};

        new BukkitRunnable() {

            @Override
            public void run() {
                if (nowtick[0] <= lefttick[0]) {
                    setDiceEyes(l, r.nextInt(6) + 1);

                    lefttick[0] = 0;
                    nowtick[0] = nowtick[0] += 0.2;

                    yachtManager.playSound(Sound.BLOCK_NOTE_BLOCK_BELL);
                }

                lefttick[0]++;
                ticks[0]++;
                if (ticks[0] == tick) {
                    cancel();
                    setDiceEyes(l, dicenum);
                    setBorderColor(Material.BLACK_CONCRETE);

                    if (yachtManager.yachtScore.Yacht() != 0) {
                        yachtManager.sendTitle(ChatColor.GOLD + "YACHT", 50);
                    } else if (yachtManager.yachtScore.largeStraight() != 0) {
                        yachtManager.sendTitle("LARGE STRAIGHT", 50);
                    } else if (yachtManager.yachtScore.smallStraight() != 0) {
                        yachtManager.sendTitle("SMALL STRAIGHT", 50);
                    } else if (yachtManager.yachtScore.fullHouse() != 0) {
                        yachtManager.sendTitle("FULL HOUSE", 50);
                    } else if (yachtManager.yachtScore.fourCard() != 0) {
                        yachtManager.sendTitle("FOUR CARD", 50);
                    }

                    if (yachtManager.dicerollleft == 0) {
                        yachtManager.nowstatename = "SCORE_SELECT";
                        p.openInventory(yachtManager.yachtScore.getScoreBoardInv(false));
                    } else {
                        yachtManager.nowstatename = "DICE_SELECT";

                        Inventory i = p.getInventory();
                        for (int slot = 2; slot <= 6; slot++) {
                            i.setItem(slot, new ItemStack(YachtManager.DICE_SKIN[slot - 2]));
                        }
                        i.setItem(8, new ItemStackManager(Material.BLAZE_ROD, ChatColor.GOLD + "다시 굴릴래! " + ChatColor.AQUA + "《SHIFT》" + ChatColor.GOLD + " 키").getItemStack());
                    }

                    yachtManager.playSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH);
                }
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);

        return dicenum;
    }

    public void setDiceEyes(Location l, int nowdicenum) {
        int[][] eyes = DICE_EYES[nowdicenum - 1];

        for (int x = 0; x <= 6; x++) {
            for (int y = 0; y <= 6; y++) {
                Block testblock = new Location(l.getWorld(), l.getX() + x, l.getY() - y, l.getZ()).getBlock();
                boolean iseye = false;

                for (int[] eye : eyes) {
                    if (eye[0] == x && eye[1] == y) {
                        testblock.setType(this.eye);
                        iseye = true;
                    }
                }

                if (!iseye) {
                    testblock.setType(this.background);
                }
            }
        }
    }

    public void setBorderColor(Material m) {
        this.border = m;

        for (int x = -1; x <= 7; x++) {
            for (int y = -1; y <= 7; y++) {
                if (x == -1 || x == 7 || y == -1 || y == 7) {
                    Block testblock = new Location(l.getWorld(), l.getX() + x, l.getY() - y, l.getZ()).getBlock();
                    testblock.setType(m);
                }
            }
        }
    }

    public Material getBorderColor() {
        return border;
    }

    public Material getEye() {
        return eye;
    }

    public Location getRerollLoc() {
        return this.l.clone().add(3, -8, 1);
    }
}
