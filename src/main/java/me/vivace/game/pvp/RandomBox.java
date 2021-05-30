package me.vivace.game.pvp;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomBox {
    private HashMap<ItemStack, Integer> chances;
    private int randomrange = 0;

    public RandomBox(HashMap<ItemStack, Integer> chances) {
        this.chances = chances;
        for (Map.Entry<ItemStack, Integer> reji : chances.entrySet()) {
            randomrange += reji.getValue();
        }
    }

    public void addValue(int i, ItemStack item) {
        chances.put(item, i + this.randomrange);
        this.randomrange += i;
    }

    public void addValues(HashMap<ItemStack, Integer> rejis) {
        for (Map.Entry<ItemStack, Integer> reji : rejis.entrySet()) {
            chances.put(reji.getKey(), reji.getValue() + this.randomrange);
            this.randomrange += reji.getValue();
        }
    }

    public ItemStack getRandom() {
        Random r = new Random();
        int a = r.nextInt(this.randomrange);
        ItemStack i;

        int leftrange = 0;

        for (Map.Entry<ItemStack, Integer> temp : this.chances.entrySet()) {
            int rightrange = temp.getValue() + leftrange;

            //Bukkit.broadcastMessage(leftrange + "<= " + a + " < " + rightrange);

            if (leftrange <= a && a < rightrange) {
                i = temp.getKey();
                if (i.getType().getMaxDurability() > 0) {
                    i.setDurability((short) r.nextInt(i.getType().getMaxDurability()));
                }

                return i;
            }

            leftrange = rightrange;
        }

        return new ItemStack(Material.AIR);
    }
}
