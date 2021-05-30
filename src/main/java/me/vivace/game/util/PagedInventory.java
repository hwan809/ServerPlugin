package me.vivace.game.util;

import me.vivace.game.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PagedInventory implements Listener {

    private ArrayList<InventoryManager> invs;
    private String invname;

    public PagedInventory(String invname) {
        this.invname = invname;
        this.invs = new ArrayList<InventoryManager>();
        InventoryManager initi = new InventoryManager(54, invname);

        ItemStackManager wagon = new ItemStackManager(Material.BARRIER, "");

        initi.setitem(45, wagon.getItemStack());
        initi.setitem(48, new ItemStackManager(Material.PAPER, ChatColor.BOLD + "1/1").getItemStack());
        initi.setitem(50, new ItemStackManager(Material.BEACON, ChatColor.GREEN + "새로고침").getItemStack());
        initi.setitem(53, wagon.getItemStack());

        invs.add(initi);

        Main.getPlugin(Main.class).getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
    }

    public void setItem(int i, ItemStack itemStack) {
        int pagenum = i / 54 + 1;
        int slot = i % 54;

        //페이지 이동 및 새로고침
        if (45 <= slot) return;
        if (pagenum > invs.size()) return;

        InventoryManager im = invs.get(pagenum - 1);
        im.setitem(i, itemStack);
    }

    public void removeItem(int i) {
        int pagenum = i / 54 + 1;
        int slot = i % 54;

        //페이지 이동 및 새로고침
        if (45 <= slot) return;
        if (pagenum > invs.size()) return;

        InventoryManager im = invs.get(pagenum - 1);
        im.setitem(i, new ItemStack(Material.AIR));
    }

    public void openInventory(Player p) {
        p.openInventory(invs.get(0).getInventory());
    }

    @EventHandler
    public void pageFunctions(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        ClickType clicktype = e.getClick();

        for (InventoryManager im : this.invs) {
            if (im.getInventory().equals(e.getClickedInventory())) {

                String snowpage = ChatColor.stripColor(e.getClickedInventory().getItem(48).getItemMeta().getDisplayName())
                        .split("/")[0];
                int nowpage = Integer.parseInt(snowpage) - 1;

                if (slot == 45 || slot == 48 || slot == 50 || slot == 53) {
                    e.setCancelled(true);
                }

                if (slot == 45 || slot == 53) {

                    if (e.getCurrentItem().getType() == Material.BARRIER) {
                        p.playSound(p.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1f ,1f);
                    } else if (e.getCurrentItem().getType() == Material.FEATHER) {
                        p.closeInventory();
                        if (slot == 45) {
                            nowpage--;
                        } else {
                            nowpage++;
                        }
                        p.openInventory(invs.get(nowpage).getInventory());
                    } else {
                        Bukkit.broadcastMessage("WTF");
                    }

                } else if (slot == 48) {
                    //paper
                } else if (slot == 50) {
                    p.closeInventory();
                    p.openInventory(invs.get(nowpage).getInventory());
                }
            }
        }
    }

//    public boolean addPage() {
//        InventoryManager initi = new InventoryManager(54, invname);
//
//        ItemStackManager wagon = new ItemStackManager(Material.BARRIER, "");
//
//        initi.setitem(45, wagon.getItemStack());
//        initi.setitem(48, new ItemStackManager(Material.PAPER, ChatColor.BOLD + "1/1").getItemStack());
//        initi.setitem(50, new ItemStackManager(Material.BEACON, ChatColor.GREEN + "새로고침").getItemStack());
//        initi.setitem(53, wagon.getItemStack());
//    }
}
