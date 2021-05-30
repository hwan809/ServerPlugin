package me.vivace.game.yacht;

import me.vivace.game.Main;
import me.vivace.game.VoidChunk;
import me.vivace.game.util.ItemStackManager;
import me.vivace.game.util.PagedInventory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class YachtEvents implements Listener {

    public static ArrayList<YachtManager> yachtManagers = new ArrayList<>();
    public static PagedInventory yacht_inventory = new PagedInventory(ChatColor.BLUE + "Yacht Dice : MultiPlayer");

    @EventHandler
    public void clickInventory(InventoryClickEvent e) {

        if (e.getClickedInventory() == null) return;

        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        ClickType clicktype = e.getClick();

        if (e.getClickedInventory() ==
                Main.ims.get("main_inv").getInventory()) {

            if (slot == 2) {
                //다이스: 로비
                p.closeInventory();
                p.openInventory(Main.getGameLobbyInv("Yacht Dice", yachtManagers.size()));
            }

            e.setCancelled(true);
        } else if (ChatColor.stripColor(e.getView().getTitle()).equals("Yacht Dice : Lobby")) {
            if (slot == 2) {
                p.closeInventory();
                World w = VoidChunk.generateNullWorld("yacht_" + yachtManagers.size());
                w.setSpawnLocation(34, 64, 36);
                new Location(w, 34, 63, 36).getBlock().setType(Material.BLACK_CONCRETE);
                new Location(w, 38, 63, 36).getBlock().setType(Material.BLACK_CONCRETE);

                YachtManager yachtManager = new YachtManager(p, null, new Location(p.getWorld(), 15, 70, 15));

                ItemStackManager im = new ItemStackManager(Material.BOOK, ChatColor.AQUA + "[+] " +
                        ChatColor.GOLD + (yachtManagers.size() - 1) + ChatColor.AQUA + "번 방");
                im.addLore("-------------------");
                im.addLore(ChatColor.WHITE + "현재 플레이어: " + ChatColor.BLUE + p.getName());
                im.addLore("-------------------");

                yacht_inventory.setItem((yachtManagers.size() - 1), im.getItemStack());
            } else if (slot == 4) {

            } else if (slot == 6) {
                p.closeInventory();
            }

            e.setCancelled(true);
        } else if (ChatColor.stripColor(e.getView().getTitle()).equals("Yacht Dice : MultiPlayer")) {
            if (e.getCurrentItem() == null) return;

            if (e.getCurrentItem().getType() != Material.ENCHANTED_BOOK &&
                    e.getCurrentItem().getType() != Material.BOOK) return;

            ItemStack i = e.getCurrentItem();
            p.closeInventory();


            e.setCancelled(true);
        }
    }
}
