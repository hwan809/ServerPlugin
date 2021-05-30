package me.vivace.game.tetris;

import me.vivace.game.Main;
import me.vivace.game.VoidChunk;
import me.vivace.game.util.InventoryManager;
import me.vivace.game.util.ItemStackManager;
import me.vivace.game.util.PagedInventory;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TetrisEvents implements Listener {

    public static Map<World, Board> tetris_worlds = new HashMap<>();
    public static PagedInventory tetris_multi = new PagedInventory(ChatColor.BLUE + "Tetris : MultiPlayer");

    @EventHandler
    public void leave_room(PlayerQuitEvent e) {
        if (tetris_worlds.values().contains(e.getPlayer())) {
            World w = e.getPlayer().getWorld();
            int roomnum = Integer.parseInt(w.getName().split("_")[1]);

            for (Player ps : w.getPlayers()) {
                ps.teleport(Bukkit.getWorld("world").getSpawnLocation());
                ps.setResourcePack(Main.NULL_RESOURCEPACK);
            }

            try {
                VoidChunk.deleteWorld(w);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            tetris_worlds.get(w).gameOver(false);
            tetris_worlds.remove(w);

            tetris_multi.removeItem(roomnum);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void openmenu(PlayerSwapHandItemsEvent e) {
        Player p = e.getPlayer();

        if (p.isSneaking()) {
            for (Board b : tetris_worlds.values()) {
                if (b.getPlayer().equals(p)) {
                    p.openInventory(getTetrisInv(b));
                }
            }
        }
    }

    @EventHandler
    public void playermove(PlayerMoveEvent e) {
        double x = 5.5;
        double y = 70;
        double z = 20.5;

        if (tetris_worlds.keySet().contains(e.getPlayer().getWorld())) {
            Board b = tetris_worlds.get(e.getPlayer().getWorld());
            if (!b.isFlag()) {e.setCancelled(true); return;}

            Tpiece tp = b.nowblock;
            Player p = b.getPlayer();
            if (e.getPlayer() != b.getPlayer()) {
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
            }

            if (p.getLocation().getX() != x || p.getLocation().getY() != y ||
                    p.getLocation().getZ() != z) {

                tp.remove_piece();

                if (p.getLocation().getX() > x) {
                    if (b.settled(tp, "D")) {
                        Main.errorSound(p);
                    } else {
                        //블록 오른쪽 1픽셀 이동
                        b.pointer[1] += 1;
                    }
                }
                if (p.getLocation().getX() < x) {
                    if (b.settled(tp, "A")) {
                        Main.errorSound(p);
                    } else {
                        //블록 왼쪽 1픽셀 이동
                        b.pointer[1] -= 1;
                    }
                }
                if (p.getLocation().getZ() > z && !b.settled(tp, "S")
                        && !p.isSprinting()) {
                    //소프트 드랍

                    b.pointer[0] -= 1;
                    b.softdrop += 1;
                }
                if (p.getLocation().getZ() < z - 0.03) {
                    //회전
                    if (!tp.rotate()) {
                        Main.errorSound(p);
                    }

                }
                if (p.getLocation().getY() > y) {
                    //하드 드랍
                }

                tp.summon_piece();
                b.updateBoard();

                p.teleport(new Location(p.getWorld(), x, y, z, 180, 0));
                p.setSprinting(false);
            }
        }
    }

    @EventHandler
    public void ClickEvent(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        ClickType clicktype = e.getClick();

        if (e.getClickedInventory() ==
                Main.ims.get("main_inv").getInventory()) {

            //Inventory maininv = Main.ims.get("main_inv").getInventory();

            if (slot == 0) {
                //테트리스: 로비
                p.closeInventory();
                p.openInventory(Main.getGameLobbyInv("Tetris", tetris_worlds.size()));
//                p.closeInventory();
//                p.setResourcePack(Main.MINIGAME_RESOURCEPACK);
//                genTetrisWorld(p);
            }

            e.setCancelled(true);
        } else if (ChatColor.stripColor(e.getView().getTitle()).equals("Tetris : Lobby")) {
            if (slot == 2) {
                p.closeInventory();
                p.setResourcePack(Main.MINIGAME_RESOURCEPACK);
                genTetrisWorld(p);

                ItemStackManager im = new ItemStackManager(Material.BOOK, ChatColor.AQUA + "[+] " +
                        ChatColor.GOLD + (tetris_worlds.size() - 1) + ChatColor.AQUA + "번 방");
                im.addLore("-------------------");
                im.addLore(ChatColor.WHITE + "현재 플레이어: " + ChatColor.BLUE + p.getName());
                im.addLore("-------------------");

                tetris_multi.setItem((tetris_worlds.size() - 1), im.getItemStack());
            } else if (slot == 4) {

            } else if (slot == 6) {
                p.closeInventory();
                tetris_multi.openInventory(p);
            }

            e.setCancelled(true);
        } else if (ChatColor.stripColor(e.getView().getTitle()).equals("Tetris : MultiPlayer")) {
            if (e.getCurrentItem() == null) return;

            if (e.getCurrentItem().getType() != Material.ENCHANTED_BOOK &&
                    e.getCurrentItem().getType() != Material.BOOK) return;

            ItemStack i = e.getCurrentItem();
            Board b = null;

            for (Map.Entry<World, Board> bw : tetris_worlds.entrySet()) {
                if (bw.getKey().getName().contains(Integer.toString(slot))) {
                    b = bw.getValue();
                }
            }

            p.closeInventory();
            p.openInventory(getTetrisRoomInv(b, slot));

            //e.setCancelled(true);
        } else if (ChatColor.stripColor(e.getView().getTitle()).contains("Tetris Room")) {
            String pname = ChatColor.stripColor(e.getClickedInventory().getItem(4).getItemMeta().getDisplayName());
            Player gamep = Bukkit.getPlayer(pname);

            if (slot == 2) {
                p.closeInventory();
                p.setGameMode(GameMode.SPECTATOR);
                p.setResourcePack(Main.MINIGAME_RESOURCEPACK);
                p.teleport(gamep);
            } else if (slot == 4) {
                p.sendMessage(ChatColor.RED + "TODO - 플레이어 정보 / 스탯 INV");
            } else if (slot == 6) {
                p.closeInventory();
                tetris_multi.openInventory(p);
            }

            e.setCancelled(true);
        } else if (ChatColor.stripColor(e.getView().getTitle()).equals("Tetris")) {

            World w = p.getWorld();
            Board b = tetris_worlds.get(w);

            if (e.getCurrentItem() != null) {
                if (slot == 2) {
                    //게임 시작
                    if (!b.isFlag()) {
                        b.startGame();
                    } else {
                        p.sendMessage(ChatColor.RED + "이미 게임 중입니다.");
                    }
                } else if (slot == 4) {
                    //환영 문구 등
                    //스킨 변경
                    p.closeInventory();
                    p.openInventory(getSkinchangeInv(b));

                    return;
                } else if (slot == 6) {
                    //나가기
                    int roomnum = Integer.parseInt(w.getName().split("_")[1]);

                    for (Player ps : w.getPlayers()) {
                        ps.teleport(Bukkit.getWorld("world").getSpawnLocation());
                        ps.setResourcePack(Main.NULL_RESOURCEPACK);
                    }
                    tetris_worlds.get(w).gameOver(false);
                    tetris_worlds.remove(w);
                    try {
                        VoidChunk.deleteWorld(w);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                    tetris_multi.removeItem(roomnum);
                }

                e.setCancelled(true);
                p.closeInventory();
            }
        } else if (ChatColor.stripColor(e.getView().getTitle()).equals("스킨 변경")) {
            World w = p.getWorld();
            Board b = tetris_worlds.get(w);

            int nowskinnum = b.skinnum;
            int max = Tpiece.block_skins.length - 1;

            if (e.getCurrentItem() != null) {
                if (slot == 1) {
                    if (nowskinnum > 0) {
                        b.skinnum--;
                    } else {
                        Main.errorSound(p);
                    }
                } else if (slot == 7) {
                    if (nowskinnum < max) {
                        b.skinnum++;
                    } else {
                        Main.errorSound(p);
                    }
                } else if (slot == 4) {
                    p.closeInventory();
                    if (b.isFlag()) {
                        b.updateSkin();
                    }
                }

                Material nowskin = Tpiece.block_skins[b.skinnum][new Random().nextInt(7)];
                e.getInventory().setItem(4, new ItemStackManager(nowskin, ChatColor.RED + "이 스킨으로 결정할래!").getItemStack());

                p.updateInventory();
                e.setCancelled(true);
            }
        }

    }

    public void genTetrisWorld(Player p) {
        World w = VoidChunk.generateNullWorld("tetris_" + tetris_worlds.size());

        Board b = new Board(new Location(w, 0, 62, 0),
                new Location(w, 9, 81, 0));
        b.setPlayer(p);

        tetris_worlds.put(w, b);
        p.teleport(new Location(w, 5.5, 70, 20.5, 180, 0));
    }

    public Inventory getTetrisInv(Board b) {
        InventoryManager tetris_room = new InventoryManager(9, ChatColor.RED + "Tetris");

        tetris_room.setitem(2, new ItemStackManager(Material.GOLD_NUGGET, ChatColor.GREEN + "시작").getItemStack());
        tetris_room.setitem(6, new ItemStackManager(Material.BARRIER, ChatColor.RED + "나가기").getItemStack());

        ItemStackManager ism = new ItemStackManager(Material.PAPER, ChatColor.AQUA + "[테트리스 미니게임] 환영합니다!");
        ism.addLore("-------------------");
        ism.addLore(ChatColor.WHITE + "방 번호: " + ChatColor.GOLD + b.startpoint.getWorld().getName());
        ism.addLore(ChatColor.WHITE + "현재 플레이어: " + ChatColor.BLUE + b.getPlayer().getName());
        ism.addLore("-------------------");

        tetris_room.setitem(4, ism.getItemStack());

        return tetris_room.getInventory();
    }

    public Inventory getSkinchangeInv(Board b) {
        InventoryManager m = new InventoryManager(9, ChatColor.AQUA + "스킨 변경");

        m.setitem(1, new ItemStackManager(Material.FEATHER, ChatColor.BLUE + "←").getItemStack());
        m.setitem(7, new ItemStackManager(Material.FEATHER, ChatColor.BLUE + "→").getItemStack());

        Material nowskin = Tpiece.block_skins[b.skinnum][new Random().nextInt(7)];

        m.setitem(4, new ItemStackManager(nowskin, ChatColor.RED + "이 스킨으로 결정할래!").getItemStack());

        return m.getInventory();
    }

    public Inventory getTetrisRoomInv(Board b, int roomnumber) {
        Player p = b.getPlayer();
        String playername = p.getName();

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(p);
        meta.setDisplayName(playername);
        skull.setItemMeta(meta);

        InventoryManager m = new InventoryManager(9, ChatColor.BLUE + "Tetris Room_" + ChatColor.GOLD + roomnumber);

        m.setitem(2, new ItemStackManager(Material.OBSERVER, ChatColor.GREEN + "관전하기").getItemStack());
        m.setitem(4, skull);
        m.setitem(6, new ItemStackManager(Material.BARRIER, ChatColor.RED + "돌아가기").getItemStack());

        return m.getInventory();
    }
}
