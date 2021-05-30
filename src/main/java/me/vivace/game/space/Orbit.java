package me.vivace.game.space;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;

public class Orbit {
    public ArrayList<Planet> orbs = new ArrayList<>();

    public Space s;
    public Location midpoint;
    private float radius;
    private float degrees;
    private int thickness;
    private Material color;

    public Orbit(Location midpoint, float radius, float degrees, int thickness, Material color) {
        this.midpoint = midpoint;
        this.radius = radius;
        this.degrees = degrees % 360;
        this.thickness = thickness - 1;
        this.color = color;

        draw();
    }

    public void setSpace(Space s) {
        this.s = s;
    }

    private void draw() {
        for (float centerx = -thickness / 2f; centerx <= thickness / 2f; centerx++) {
            for (float centery = -thickness / 2f; centery <= thickness / 2f; centery++) {
                for (float xz = 0; xz < 360; xz += 0.2) {
                    double x = Math.cos(Math.toRadians(degrees)) * Math.sin(Math.toRadians(xz)) * radius;
                    double y = Math.sin(Math.toRadians(degrees)) * Math.sin(Math.toRadians(xz)) * radius;
                    double z = Math.cos(Math.toRadians(xz)) * radius;

                    midpoint.clone().add(x + centerx, y + centery, z).getBlock().setType(color);
                }
            }
        }
    }

    public void addPlanet(Planet p) {
        this.orbs.add(p);
    }
}
