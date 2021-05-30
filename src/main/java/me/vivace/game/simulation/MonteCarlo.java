package me.vivace.game.simulation;

import org.bukkit.*;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import java.io.IOException;
import java.util.ArrayList;

public class MonteCarlo {

    public static double maxy = 0;
    public static long radius = 0;
    public static double randomdots = 0;
    public static double insidedots = 0;
    public static Location thisloc;
    public static ArrayList<Float> estimatepi = new ArrayList<Float>();
    public static ArrayList<Long> insidedotslist = new ArrayList<Long>();

    public static void init() {
        maxy = 0;
        radius = 0;
        randomdots = 0;
        insidedots = 0;
        thisloc = null;
        estimatepi.clear(); insidedotslist.clear();
        estimatepi.add(0f);
        insidedotslist.add(0L);
    }

    public static void drawCircle(Location loc, Material mat, int r) {
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        World w = loc.getWorld();
        int rSquared = r * r;

        for (int x = cx - r; x <= cx + r; x++) {
            for (int z = cz - r; z <= cz + r; z++) {
                if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
                    w.getBlockAt(x, cy, z).setType(mat, false);
                }
            }
        }
    }

    public static void drawSquare(Location loc, Material mat, int half_h) {
        int sx = loc.getBlockX();
        int sy = loc.getBlockY();
        int sz = loc.getBlockZ();
        World w = loc.getWorld();

        for (int x = sx - half_h; x <= sx + half_h; x++) {
            for (int z = sz - half_h; z <= sz + half_h; z++) {
                w.getBlockAt(x, sy, z).setType(mat, false);
            }
        }
    }

    public static float[] monteData(long r, int k) {
        float[] data = new float[k + 1];

        data[0] = 0;

        float insidedots = 0;
        float randomdots = 0;

        for (int i = 0; i < k; i++) {
            randomdots++;

            long dotx = (long) (-r + (Math.random() * 2 * r + 1));
            long dotz = (long) (-r + (Math.random()) * 2 * r + 1);

            if (isInsideCircle(0, 0, dotx, dotz, r)) insidedots++;
            float notpi = insidedots * 4 / randomdots;

            data[(int) randomdots] = notpi;
        }

        return data;
    }

    public static void randomDots(Location loc, Material mat, long r, int amount, boolean setblock) {
        World w = loc.getWorld();
        int sx = loc.getBlockX();
        int sy = loc.getBlockY();
        int sz = loc.getBlockZ();

        radius = r;
        maxy = sy;

        long minx = sx - r;
        long minz = sz - r;
        long maxx = sx + r;
        long maxz = sz + r;

        for (int count = 0; count < amount; count++) {
            randomdots++;

            long dotx = minx + (long) (Math.random() * ((maxx - minx) + 1));
            long dotz = minz + (long) (Math.random() * ((maxz - minz) + 1));

            if (setblock) {
                Location dotloc = new Location(w, dotx, sy + 1, dotz);
                //Bukkit.broadcastMessage(dotloc.toString());

                boolean found = false;
                while (!found) {
                    if (dotloc.getBlock().getType() != mat) {
                        found = true;
                    } else {
                        dotloc.setY(dotloc.getY() + 1);
                    }

                }

                if (dotloc.getY() - 62 > maxy) {
                    maxy = dotloc.getY() - 62;
                }

                dotloc.getBlock().setType(mat);
            }

            if (isInsideCircle(loc.getX(), loc.getZ(), dotx, dotz, r)) insidedots++;
            float notpi = (float) (insidedots * 4 / randomdots);

            int index = (int) randomdots;

            estimatepi.add(index, notpi);
            insidedotslist.add(index, (long) insidedots);
        }
    }

    public static void toGraph() {
        for (int chartnum = 1; chartnum < (int) randomdots; chartnum += 1000) {
            // Create Chart
            final XYChart chart = new XYChartBuilder().width(1920).height(1080).xAxisTitle("dropped dot").yAxisTitle("float").build();

            // Customize Chart
            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
            chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);

            // Series

            float[] temppi = new float[1000];
            float[] tempconstant = new float[2];
            float[] tempf = new float[1000];
            long[] tempinside = new long[1000];
            int[] temp = new int[1000];

            tempconstant[0] = 0;
            tempconstant[1] = 0;

            Bukkit.broadcastMessage(chartnum + ": " + chartnum + " ~ " + (chartnum + 1000));

            for (int i = 1; i < 1000; i++) {
                temppi[i] = (float) (estimatepi.get(i + chartnum) - Math.PI);
                tempf[i] = i + chartnum;
                tempinside[i] = insidedotslist.get(i + chartnum);
                temp[i] = i + chartnum;
            }

            chart.setTitle("MonteCarlo graph_" + chartnum);
            chart.addSeries("zero", new float[] {chartnum, chartnum + 1000}, tempconstant);
            chart.addSeries("estimated pi", tempf, temppi);
            try {
                BitmapEncoder.saveBitmap(chart, "./plugins/MinigameServer/graph_" + chartnum, BitmapEncoder.BitmapFormat.PNG);
            } catch (IOException e) {
                Bukkit.broadcastMessage("error");
            }
        }
    }

    public static boolean isInsideCircle(double locx, double locz, long x, long z, long r) {
        long rSquared = r * r;

        if ((locx -  x) * (locx - x) + (locz - z) * (locz - z) <= rSquared) {
            return true;
        } else {
            return false;
        }
    }

    public static void printAttr() {
        float notpi = (float) (insidedots * 4 / randomdots);
        float dif = (float) Math.abs(notpi - Math.PI);
        float percentage = (float) Math.PI / notpi * 100;

        Bukkit.broadcastMessage(ChatColor.AQUA + "-----------[MonteCarlo]----------");

        Bukkit.broadcastMessage(ChatColor.BLUE + "" + randomdots + "개" +
                                ChatColor.WHITE + "의 표본 분석결과: ");
        Bukkit.broadcastMessage(ChatColor.RED + "    -원 안에 떨어진 점: " +
                                ChatColor.WHITE + insidedots);
        Bukkit.broadcastMessage(ChatColor.AQUA + "    -같은 곳에 찍힌 점 개수 최대" +
                                ChatColor.WHITE + maxy);
        Bukkit.broadcastMessage(ChatColor.GREEN + "    -계산한 비례식:");
        Bukkit.broadcastMessage(ChatColor.WHITE + "        " +
                                insidedots + " : " + randomdots + " = " +
                                "π * " + radius + " * " + radius +
                                " : " + (radius * radius * 4));
                                //정사각형 넓이 (radius * 2) ** 2

        Bukkit.broadcastMessage(ChatColor.GOLD + "    -계산된 π: " + ChatColor.WHITE + notpi);
        Bukkit.broadcastMessage(ChatColor.RED + "    -π와의 차: " + ChatColor.WHITE + dif);


        Bukkit.broadcastMessage(ChatColor.AQUA + "-----------[MonteCarlo]----------");

        toGraph();
    }
}