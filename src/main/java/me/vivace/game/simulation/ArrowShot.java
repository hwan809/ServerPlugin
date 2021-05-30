package me.vivace.game.simulation;

import me.vivace.game.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ArrowShot {
    private float arrowspeed;
    private Arrow arrow;
    private Player p;
    private int startpointy;

    private ArrayList<Float> yaxis = new ArrayList<>();

    public ArrowShot(float arrowspeed) {
        this.arrowspeed = arrowspeed;
    }

    public void setSpeed(int a) {
        this.arrowspeed = a;
    }

    public boolean Shoot(Player p) {
        this.p = p;
        this.arrow = this.p.launchProjectile(Arrow.class, p.getLocation().getDirection().multiply(this.arrowspeed));
        this.arrow.setInvulnerable(true);
        this.arrow.setPassenger(this.p);
        this.startpointy = (int) this.arrow.getLocation().getY();

        sendY();

        return true;
    }

    public void Calculate() {

        Location base = this.arrow.getLocation();
        Vector velocity = this.arrow.getVelocity();
        //Vector drag = /* drag vector, don't need to do calcs here */;
        Vector downwardAccel = new Vector(0, -0.05, 0);
        BlockIterator itr = new BlockIterator(base.getWorld(), base.toVector(), base.getDirection(), 0, 3);
        int tick = 0;
        while (base.getY() > 0 && !intercepts(itr)) { //can do an extra check against velocity to ensure arrow isn't stationary
            velocity.add(downwardAccel);
            base.add(velocity);
            itr = new BlockIterator(base.getWorld(), base.toVector(), base.getDirection(), 0, 3);
            tick++;
        }

        this.p.sendMessage(velocity.toLocation(Bukkit.getWorld("world")).toString());
    }

    public boolean intercepts(BlockIterator itr) {
        while (itr.hasNext()) {
            if (itr.next().getType() != Material.AIR) { //can be more specific
                return true;
            }
        }
        return false;
    }

    public void sendY() {
        Player p = this.p;
        Arrow a = this.arrow;
        int startpointy = this.startpointy;

        new BukkitRunnable() {

            int tick = 1;
            int times = 0;
            int prey = 0;

            @Override
            public void run() {
                if (a.isOnGround()) {
                    a.remove();
                    createGraph();
                    cancel();
                }

                if (prey == a.getLocation().getY()) {
                    times++;
                    if (times == 3) {
                        cancel();
                    }
                } else {
                    times = 0;
                }
                yaxis.add((float) a.getLocation().getY());

                prey = (int) a.getLocation().getY();
                p.sendMessage("tick " + tick + ": " + a.getLocation().getY());
                tick++;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
    }

    public void createGraph() {
        final XYChart chart = new XYChartBuilder().width(1920).height(1080).xAxisTitle("dropped dot").yAxisTitle("float").build();

        // Customize Chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);

        chart.setTitle("Arrow Velocity Simulation");
        chart.setXAxisTitle("ticks");
        chart.setYAxisTitle("y axis");

        float[] ticks = new float[this.yaxis.size()];
        float[] ys = new float[this.yaxis.size()];

        for (int i = 0; i < yaxis.size(); i++) {
            ticks[i] = i + 1;
        }
        for (int i = 0; i < yaxis.size(); i++) {
            ys[i] = yaxis.get(i);
        }

        chart.addSeries("arrow", ticks, ys);

        try {
            BitmapEncoder.saveBitmap(chart, "./plugins/MinigameServer/graph", BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            Bukkit.broadcastMessage("error");
        }

        Main.f = new File(Main.SERVER_FOLDER_PATH + "\\plugins\\MinigameServer\\graph.png");
        Main.mainchannel.sendMessage("분석 결과입니다: ").addFile(Main.f).queue();
    }
}
