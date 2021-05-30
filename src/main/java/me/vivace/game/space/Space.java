package me.vivace.game.space;

import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;

public class Space {

    public static final Material[] wools = {Material.RED_WOOL, Material.ORANGE_WOOL, Material.YELLOW_WOOL, Material.LIME_WOOL,
                                            Material.CYAN_WOOL, Material.LIGHT_BLUE_WOOL, Material.BLUE_WOOL, Material.PURPLE_WOOL};

    private World world;
    private boolean flag = false;
    private ArrayList<Orbit> orbits = new ArrayList<>();

    public Space(World world) {
        this.world = world;
    }

    public void addOrbit(Orbit o) {
        o.setSpace(this);
        this.orbits.add(o);
    }

//   public void run() {
//        new BukkitRunnable() {
//
//            @Override
//            public void run() {
//                for (Orbit o : orbits) {
//                    //TODO: add
//                }
//            }
//        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
//    }

    public void stop() {

    }
}
