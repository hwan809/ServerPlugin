package me.vivace.game.fireice;

import me.vivace.game.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class Balls {
    private DanceMap ballmovingmap;

    public Location moveballloc; //움직이고 있는 공의 좌표
    public Location stopballloc; //블록에 부착된 공의 좌표;

    public boolean nowball; //현재 1번 공이 움직이고 있는지

    private int timercode;
    private int starteddegrees;
    public int ballspeed;

    public Balls(DanceMap ballmovingmap) {
        this.ballmovingmap = ballmovingmap;

        moveballloc = ballmovingmap.all_mapblocks.get(0).getLocation().add(1.5, 1, 0.5);
        stopballloc = ballmovingmap.all_mapblocks.get(0).getLocation().add(0.5, 1, 0.5);
        ballspeed = 9;
    }

    public void startGame() {
        nowball = true;
        moveBalls(0);
    }

    public boolean isBallonBlock(int tick) { // 움직일 수 있는지 (공이 다음 블록 근처 판별)
        if (-36 / ballspeed <= tick && tick <= 36 / ballspeed) {
            return true;
        } else {
            return false;
        }
    }

    public void moveBalls(int degrees) {
        final float[] degree = {degrees};

        new BukkitRunnable() {

            @Override
            public void run() {
                timercode = getTaskId();
                double x = Math.cos(Math.toRadians(degree[0]));
                double z = Math.sin(Math.toRadians(degree[0]));

                moveballloc = stopballloc.clone().add(x, 0, z);

                degree[0] += ballspeed;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
    }

    public void cancelBalls() {
        Bukkit.getServer().getScheduler().cancelTask(this.timercode);
    }

    public void gameOver() {
        cancelBalls();
    }

    public int getTicktoget(Location startloc, Location pinloc, Location endloc) {
        int startdeg = getDegree(pinloc, startloc);
        int enddeg = getDegree(pinloc, endloc);

        if (startdeg > enddeg) {
            enddeg += 360;
        }

        return (enddeg - startdeg) / ballspeed;
    }

    public int getDegree(Location stoploc, Location moveloc) {
        Location subloc = stoploc.clone().subtract(moveloc);
        int degree;

        if (subloc.getX() == -1 && subloc.getZ() == 0) {
            degree = 0;
        } else if (subloc.getX() == 0 && subloc.getZ() == -1) {
            degree = 90;
        } else if (subloc.getX() == 1 && subloc.getZ() == 0) {
            degree = 180;
        } else if (subloc.getX() == 0 && subloc.getZ() == 1) {
            degree = 270;
        } else {
            degree = 0;
        }

        return degree;
    }
}
