package me.vivace.game.space;

import org.bukkit.Location;
import org.bukkit.Material;

public abstract class Orb {
    public Location midpoint;
    public int radius;
    public Material color;

    public void rotate() {

    }

    abstract void revolve();
}
