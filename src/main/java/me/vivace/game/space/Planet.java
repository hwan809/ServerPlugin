package me.vivace.game.space;

import org.bukkit.Location;
import org.bukkit.Material;

public class Planet extends Orb {

    public Orbit o;

    public Planet(Location midpoint, int radius, Material color) {
        this.midpoint = midpoint;
        this.radius = radius;
        this.color = color;
    }

    public void setOrbit(Orbit o) {
        this.o = o;
    }

    public void revolve() {

    }
}
