package me.vivace.game.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InventoryManager{

    private Inventory i;
    private HashMap<Integer, InventoryManager> link_map = new HashMap<Integer, InventoryManager>();

    public InventoryManager(int slotsize, String s) {
        this.i = Bukkit.createInventory(null, slotsize, s);
        i.setItem(0, new ItemStack(Material.AIR));
    }

    public void setitem(int slot, ItemStack i) {
        this.i.setItem(slot, i);
    }

    public Inventory getInventory() {
        return this.i;
    }
}
