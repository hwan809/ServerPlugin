package me.vivace.game.fireice;

import me.vivace.game.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class FireIceManager implements Listener {
    private DanceMap danceMap;
    private Player p;
    private boolean flag;
    private boolean alreadyplayed = false;
    private int nowblockpointer;
    private Balls balls;

    private ArmorStand ball1;
    private ArmorStand ball2;

    private int nowlefttick;

    private double addx;
    private double addz;

    private int timercode;

    public FireIceManager(DanceMap danceMap, Player p) {
        this.danceMap = danceMap;
        this.p = p;
        balls = new Balls(danceMap);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!e.getPlayer().getItemInHand().getType().equals(Material.MAGMA_CREAM)) return;
        if (!e.getPlayer().equals(p)) return;

        if (!flag && !alreadyplayed) {
            startGame();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(PlayerSwapHandItemsEvent e) {
        if (!e.getPlayer().equals(p)) return;

        if (flag) {
            clicked();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e) {
        if (!e.getPlayer().equals(p)) return;

        if (flag) {
            e.setCancelled(true);
        }
    }

    public void startGame() {
        this.nowblockpointer = 0;
        this.nowlefttick = 180 / this.balls.ballspeed;
        this.flag = true;
        this.alreadyplayed = true;
        balls.startGame();

        Location playerloc = balls.stopballloc.clone().add(0, 3, 0);
        playerloc.setPitch(90); playerloc.setYaw(0);

        p.teleport(playerloc);
        double[] adds = getaddLoc(this.balls.stopballloc, this.danceMap.all_mapblocks.get(1).getLocation(), 20);
        this.addx = adds[0];
        this.addz = adds[1];

        ball1 = (ArmorStand) p.getWorld().spawnEntity(balls.moveballloc, EntityType.ARMOR_STAND);
        ball2 = (ArmorStand) p.getWorld().spawnEntity(balls.stopballloc, EntityType.ARMOR_STAND);

        ball1.setVisible(false);
        ball1.setGravity(false);
        ball1.setCanPickupItems(false);
        ball1.setMarker(true);
        ball1.setSmall(true);

        ball2.setVisible(false);
        ball2.setGravity(false);
        ball2.setCanPickupItems(false);
        ball2.setMarker(true);
        ball2.setSmall(true);

        ball1.getEquipment().setHelmet(new ItemStack(Material.RED_WOOL));
        ball2.getEquipment().setHelmet(new ItemStack(Material.BLUE_WOOL));

        displayBalls();
    }

    public void clicked() {
        if (balls.isBallonBlock(this.nowlefttick)) {
            this.nowblockpointer++;
            this.balls.nowball = !this.balls.nowball;

            Block startblock = danceMap.all_mapblocks.get(nowblockpointer - 1);
            Block midblock = danceMap.all_mapblocks.get(nowblockpointer);
            Block endblock = danceMap.all_mapblocks.get(nowblockpointer + 1);

            this.balls.cancelBalls();

            this.balls.stopballloc = midblock.getLocation().clone().add(0.5, 1, 0.5);
            this.balls.moveballloc = startblock.getLocation().clone().add(0.5, 1, 0.5);

            if (this.balls.stopballloc.getBlock().getType() == Material.SKELETON_SKULL) {
                this.balls.ballspeed += 3;
            } else if (this.balls.stopballloc.getBlock().getType() == Material.CREEPER_HEAD) {
                if (this.balls.ballspeed > 3) {
                    this.balls.ballspeed -= 3;
                }
            }

            Location playerloc = balls.stopballloc.clone().add(0, 3, 0);
            playerloc.setPitch(90); playerloc.setYaw(0);

            p.teleport(playerloc);

            int degree = this.balls.getDegree(this.balls.stopballloc, this.balls.moveballloc);
            this.balls.moveBalls(degree);

            p.sendTitle(" ", getValueText(this.nowlefttick),0,100,0);
            this.nowlefttick = balls.getTicktoget(startblock.getLocation(), midblock.getLocation(), endblock.getLocation());

            double[] adds = getaddLoc(midblock.getLocation(), endblock.getLocation(), this.nowlefttick);
            this.addx = adds[0];
            this.addz = adds[1];

        } else {
            p.sendTitle("", ChatColor.RED + "부정확", 0, 100, 0);
        }
    }

    public void displayBalls() {
        new BukkitRunnable() {

            @Override
            public void run() {
                timercode = this.getTaskId();

                if (!flag) { return; }

                if (balls.nowball) {
                    ball1.teleport(balls.moveballloc);
                    ball2.teleport(balls.stopballloc);
                } else {
                    ball1.teleport(balls.stopballloc);
                    ball2.teleport(balls.moveballloc);
                }

                p.teleport(p.getLocation().clone().add(addx, 0, addz));

                nowlefttick--;
                if (nowlefttick < -5) {
                    gameOver();
                }
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
    }

    public String getValueText(int value) {
        if (value >= -1 && value <= 1) {
            return ChatColor.DARK_GREEN + "정확";
        }

        if (value >= 0) {
            if (value <= 2) {
                return ChatColor.GREEN + "빠름";
            } else if (value <= 3) {
                return ChatColor.YELLOW + "빠름!";
            } else if (value <= 5) {
                return ChatColor.RED + "너무 빠름";
            }
        }

        if (value < 0) {
            if (value < -5) {
                return ChatColor.RED + "너무 느림";
            } else if (value < -3) {
                return ChatColor.YELLOW + "느림!";
            } else {
                return ChatColor.GREEN + "느림";
            }
        }

        Bukkit.broadcastMessage(value + ": NULL");
        return "NULL";
    }

    public void gameOver() {
        this.flag = false;
        this.ball1.remove();
        this.ball2.remove();

        Bukkit.getServer().getScheduler().cancelTask(this.timercode);
        int percent = (int) ((float) this.nowblockpointer / danceMap.all_mapblocks.size() * 100);

        p.sendMessage(percent + "% 완료");
    }

    public double[] getaddLoc(Location stoploc, Location endloc, int tick) {
        Location diff = endloc.clone().subtract(stoploc);
        diff.setX(diff.getX() / tick);
        diff.setZ(diff.getZ() / tick);

        return new double[]{diff.getX(), diff.getZ()};
    }
}
