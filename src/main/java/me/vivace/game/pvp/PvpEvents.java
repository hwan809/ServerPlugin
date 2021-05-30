package me.vivace.game.pvp;

import me.vivace.game.Main;
import me.vivace.game.VoidChunk;
import me.vivace.game.util.InventoryManager;
import me.vivace.game.util.ItemStackManager;
import me.vivace.game.util.PagedInventory;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;

public class PvpEvents implements Listener {
    public static RandomBox rb;

    public static HashMap<ItemStack, Integer> randombox = new HashMap<>();
    public static Map<World, PvpGameManager> rdpvp_worlds = new HashMap<>();
    public static PagedInventory rdpvp_multi = new PagedInventory(ChatColor.BLUE + "RandomBox PvP : MultiPlayer");

    public final static String BOXNAME = ChatColor.GOLD + "랜덤박스";
    public final static String[] MAPS = {"beach", "hell", "mine", "world", "fried_pigeon"};

    @EventHandler
    public void roomInvClickEvent(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        ClickType clicktype = e.getClick();
        Inventory inv = e.getInventory();
        ItemStack item = e.getCurrentItem();

        if (e.getClickedInventory() ==
                Main.ims.get("main_inv").getInventory()) {

            //Inventory maininv = Main.ims.get("main_inv").getInventory();

            if (slot == 1) {
                //랜덤박스 pvp: 로비
                p.closeInventory();
                p.openInventory(Main.getGameLobbyInv("RandomBox PvP", rdpvp_worlds.size()));
//                p.closeInventory();
//                p.setResourcePack(Main.MINIGAME_RESOURCEPACK);
//                genTetrisWorld(p);

            }

            e.setCancelled(true);
        } else if (ChatColor.stripColor(e.getView().getTitle()).equals("RandomBox PvP : Lobby")) {
            if (slot == 2) {
                //방 설정 인벤 이동
                p.closeInventory();
                p.openInventory(getRoomSetInv());
            } else if (slot == 4) {
                //환영합니다
            } else if (slot == 6) {
                //멀티플레이
                p.closeInventory();
                rdpvp_multi.openInventory(p);
            }

            e.setCancelled(true);
        } else if (ChatColor.stripColor(e.getView().getTitle()).contains("RandomBox PvP : Room Settings")) {

            if (4 <= slot && slot <= 7) {
                //인원수 설정
                if (slot == 4) {
                    Main.errorSound(p);
                    p.sendMessage(ChatColor.RED + "2 ~ 4명 인원 설정이 가능합니다.");
                } else {
                    int playeramount = slot - 3;

                    for (int i = 4; i < 7 + 1; i++) {
                        if (i <= slot) {
                            inv.setItem(i, new ItemStackManager(Material.PLAYER_HEAD, ChatColor.GREEN + "플레이어 " + (i - 3)).getItemStack());
                        } else {
                            inv.setItem(i, new ItemStackManager(Material.NETHER_STAR, ChatColor.BLUE + " ").getItemStack());
                        }
                    }

                    if (playeramount % 2 != 0) {
                        inv.setItem(14, new ItemStackManager(Material.ENCHANTED_BOOK, ChatColor.GREEN + "개인전").getItemStack());
                        inv.setItem(16, new ItemStackManager(Material.BOOK, ChatColor.RED + "팀전").getItemStack());

                        inv.setItem(41, new ItemStackManager(Material.BOOK, ChatColor.GREEN + "ON").getItemStack());
                        inv.setItem(43, new ItemStackManager(Material.BOOK, ChatColor.RED + "OFF").getItemStack());
                    }
                }
            } else if (slot == 14 || slot == 16) {
                //개인전 / 팀전 설정
                if (slot == 14) {
                    inv.setItem(14, new ItemStackManager(Material.ENCHANTED_BOOK, ChatColor.GREEN + "개인전").getItemStack());
                    inv.setItem(16, new ItemStackManager(Material.BOOK, ChatColor.RED + "팀전").getItemStack());

                    inv.setItem(41, new ItemStackManager(Material.BOOK, ChatColor.GREEN + "ON").getItemStack());
                    inv.setItem(43, new ItemStackManager(Material.BOOK, ChatColor.RED + "OFF").getItemStack());
                } else {
                    int playeramount = 0;
                    for (int i = 4; i < 7 + 1; i++) {
                        if (inv.getItem(i).getType() != Material.NETHER_STAR) {
                            playeramount++;
                        }
                    }

                    if (playeramount % 2 != 0) {
                        Main.errorSound(p);
                        p.sendMessage(ChatColor.RED + "방 인원이 짝수여야 합니다.");
                    } else {
                        inv.setItem(14, new ItemStackManager(Material.BOOK, ChatColor.GREEN + "개인전").getItemStack());
                        inv.setItem(16, new ItemStackManager(Material.ENCHANTED_BOOK, ChatColor.RED + "팀전").getItemStack());

                        inv.setItem(41, new ItemStackManager(Material.ENCHANTED_BOOK, ChatColor.GREEN + "ON").getItemStack());
                        inv.setItem(43, new ItemStackManager(Material.BOOK, ChatColor.RED + "OFF").getItemStack());
                    }
                }
            } else if (slot == 23 || slot == 25) {
                //맵 설정 (좌 우)
                String mapname = ChatColor.stripColor(inv.getItem(24).getItemMeta().getDisplayName())
                        .replace(" ", "")
                        .split(":")[1];

                int mapindex = 0;
                for (int i = 0; i < MAPS.length; i++) {
                    if (MAPS[i].equals(mapname)) {
                        mapindex = i;
                    }
                }

                if (slot == 23) {
                    if (mapindex > 0) {
                        mapindex--;
                    } else {
                        Main.errorSound(p);
                    }
                } else {
                    if (mapindex < MAPS.length - 1) {
                        mapindex++;
                    } else {
                        Main.errorSound(p);
                    }
                }

                inv.setItem(24, new ItemStackManager(Material.PAPER, ChatColor.RED + "MAP: " + ChatColor.GOLD + MAPS[mapindex]).getItemStack());
            } else if (slot == 32 || slot == 34) {
                //랜덤박스 개수 설정

                String box = ChatColor.stripColor(inv.getItem(33).getItemMeta().getDisplayName()).replace("개", "");
                int boxamount = Integer.parseInt(box);

                if (slot == 32) {
                    if (boxamount > 0) {
                        boxamount--;
                    } else {
                        Main.errorSound(p);
                    }
                } else {
                    if (boxamount < 36) {
                        boxamount++;
                    } else {
                        Main.errorSound(p);
                    }
                }

                inv.setItem(33, new ItemStackManager(Material.PAPER, ChatColor.RED + "" + boxamount + "개").getItemStack());
            } else if (slot == 41 || slot == 43) {
                //팀 데미지 끄기 : 켜기
                boolean isindividual = inv.getItem(14).getType() == Material.ENCHANTED_BOOK;

                if (isindividual) {
                    Main.errorSound(p);
                } else {
                    if (slot == 41) {
                        inv.setItem(41, new ItemStackManager(Material.ENCHANTED_BOOK, ChatColor.GREEN + "ON").getItemStack());
                        inv.setItem(43, new ItemStackManager(Material.BOOK, ChatColor.RED + "OFF").getItemStack());
                    } else {
                        inv.setItem(41, new ItemStackManager(Material.BOOK, ChatColor.GREEN + "ON").getItemStack());
                        inv.setItem(43, new ItemStackManager(Material.ENCHANTED_BOOK, ChatColor.RED + "OFF").getItemStack());
                    }
                }

            } else if (slot == 46) {
                //돌아가기

                p.closeInventory();
                p.openInventory(Main.getGameLobbyInv("RandomBox PvP", rdpvp_worlds.size()));
            } else if (slot == 52) {
                //방 생성

                String mapname = ChatColor.stripColor(inv.getItem(24).getItemMeta().getDisplayName())
                        .replace(" ", "")
                        .split(":")[1];
                int playeramount = 0;
                boolean isindividual = inv.getItem(14).getType() == Material.ENCHANTED_BOOK;
                boolean isteamdamage = inv.getItem(41).getType() == Material.ENCHANTED_BOOK;
                int boxamount = Integer.parseInt(ChatColor.stripColor(inv.getItem(33).getItemMeta().getDisplayName()).replace("개", ""));

                for (int i = 4; i < 7 + 1; i++) {
                    if (inv.getItem(i).getType() != Material.NETHER_STAR) {
                        playeramount++;
                    }
                }

                p.closeInventory();

                p.sendMessage(ChatColor.AQUA + "[RandomBox PvP] 방을 생성했습니다!");
                genRandomBoxPvPWorld(p, mapname, playeramount, isindividual, isteamdamage, boxamount, rb);

                ItemStackManager im = new ItemStackManager(Material.BOOK, ChatColor.AQUA + "[+] " +
                        ChatColor.GOLD + (rdpvp_worlds.size() - 1) + ChatColor.AQUA + "번 방");
                im.addLore("-------------------");
                im.addLore(ChatColor.WHITE + "현재 플레이어: " + ChatColor.BLUE + p.getName());
                im.addLore("-------------------");

                rdpvp_multi.setItem((rdpvp_worlds.size() - 1), im.getItemStack());

            }

            e.setCancelled(true);
        } else if (ChatColor.stripColor(e.getView().getTitle()).equals("RandomBox PvP : MultiPlayer")) {
            if (e.getCurrentItem() == null) return;

            if (e.getCurrentItem().getType() != Material.ENCHANTED_BOOK &&
                    e.getCurrentItem().getType() != Material.BOOK) return;

            ItemStack i = e.getCurrentItem();
            PvpGameManager b = null;

            for (Map.Entry<World, PvpGameManager> bw : rdpvp_worlds.entrySet()) {
                if (bw.getKey().getName().contains(Integer.toString(slot))) {
                    b = bw.getValue();
                }
            }

            p.closeInventory();
            p.openInventory(getRandomBoxRoomInv(b, slot));

            e.setCancelled(true);
        } else if (ChatColor.stripColor(e.getView().getTitle()).contains("RandomBox PvP Room")) {
            String pname = ChatColor.stripColor(e.getClickedInventory().getItem(4).getItemMeta().getDisplayName());
            Player gamep = Bukkit.getPlayer(pname);

            if (slot == 2) {
                p.closeInventory();

                for (PvpGameManager pgm : rdpvp_worlds.values()) {
                    if (pgm.admin.equals(gamep)) {
                        pgm.joinPlayer(p);
                    }
                }

            } else if (slot == 4) {
                p.sendMessage(ChatColor.RED + "TODO - 플레이어 정보 / 스탯 INV");
            } else if (slot == 6) {
                p.closeInventory();
                rdpvp_multi.openInventory(p);
            }

            e.setCancelled(true);
        } else if (ChatColor.stripColor(e.getView().getTitle()).equals("RandomBox PvP 게임 정보!")) {
            PvpGameManager pgm = null;

            for (PvpGameManager tpgm : rdpvp_worlds.values()) {
                if (tpgm.gameplayers.contains(p)) {
                    pgm = tpgm;
                }
            }

            assert pgm != null;

            if (slot == 2) {
                if (item.getType() == Material.BOOK) {
                    p.sendMessage(ChatColor.RED + "개인전 방입니다.");
                } else if (item.getType() == Material.ENCHANTED_BOOK) {
                    p.closeInventory();
                    p.openInventory(getTeamSetInv());
                } else {
                    Bukkit.broadcastMessage("CAN'T REACH HERE");
                }
            } else if (slot == 4) {
                p.closeInventory();
                p.openInventory(getRoomInfoInv());
            } else if (slot == 6) {
                p.closeInventory();
                pgm.gameplayers.remove(p);
            }

            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void openmenu(PlayerSwapHandItemsEvent e) {
        Player p = e.getPlayer();

        if (p.isSneaking()) {
            for (PvpGameManager pgm : rdpvp_worlds.values()) {
                if (pgm.getPlayers().contains(p)) {
                    p.openInventory(getRandomBoxGameInv(pgm));
                }
            }
        }
    }

    public Inventory getRoomSetInv() {
        InventoryManager m = new InventoryManager(54, ChatColor.AQUA + "RandomBox PvP : Room Settings");

        m.setitem(1, new ItemStackManager(Material.EMERALD, ChatColor.BLUE + "인원 수: ").getItemStack());
        m.setitem(4, new ItemStackManager(Material.PLAYER_HEAD, ChatColor.GREEN + "플레이어 1").getItemStack());
        m.setitem(5, new ItemStackManager(Material.PLAYER_HEAD, ChatColor.GREEN + "플레이어 2").getItemStack());
        m.setitem(6, new ItemStackManager(Material.NETHER_STAR, ChatColor.BLUE + " ").getItemStack());
        m.setitem(7, new ItemStackManager(Material.NETHER_STAR, ChatColor.BLUE + " ").getItemStack());
        m.setitem(10, new ItemStackManager(Material.TOTEM_OF_UNDYING, ChatColor.BLUE + "개인전 / 팀전: ").getItemStack());
        m.setitem(14, new ItemStackManager(Material.ENCHANTED_BOOK, ChatColor.GREEN + "개인전").getItemStack());
        m.setitem(16, new ItemStackManager(Material.BOOK, ChatColor.RED + "팀전").getItemStack());
        m.setitem(19, new ItemStackManager(Material.MAP, ChatColor.RED + "맵 설정: ").getItemStack());
        m.setitem(23, new ItemStackManager(Material.FEATHER, ChatColor.BLUE + "←").getItemStack());
        m.setitem(24, new ItemStackManager(Material.PAPER, ChatColor.RED + "MAP: " + ChatColor.GOLD + MAPS[0]).getItemStack());
        m.setitem(25, new ItemStackManager(Material.FEATHER, ChatColor.BLUE + "→").getItemStack());
        m.setitem(28, new ItemStackManager(Material.WHITE_SHULKER_BOX, ChatColor.BLUE + "랜덤박스 지급 개수: ").getItemStack());
        m.setitem(32, new ItemStackManager(Material.FEATHER, ChatColor.BLUE + "←").getItemStack());
        m.setitem(33, new ItemStackManager(Material.PAPER, ChatColor.RED + "0개").getItemStack());
        m.setitem(34, new ItemStackManager(Material.FEATHER, ChatColor.BLUE + "→").getItemStack());
        m.setitem(37, new ItemStackManager(Material.TNT, ChatColor.RED + "팀 데미지: ").getItemStack());
        m.setitem(41, new ItemStackManager(Material.BOOK, ChatColor.GREEN + "ON").getItemStack());
        m.setitem(43, new ItemStackManager(Material.BOOK, ChatColor.RED + "OFF").getItemStack());
        m.setitem(46, new ItemStackManager(Material.BARRIER, ChatColor.RED + "돌아가기").getItemStack());
        m.setitem(52, new ItemStackManager(Material.END_CRYSTAL, ChatColor.GREEN + "완료!").getItemStack());

        return m.getInventory();
    }

    public void genRandomBoxPvPWorld(Player p, String mapname,
                                     int playeramount, boolean isindividual, boolean isteamdamage,
                                     int boxamount, RandomBox rb) {
        World w = VoidChunk.generateNullWorld("randombox_" + rdpvp_worlds.size());

        PvpGameManager pgm = new PvpGameManager(p, new Location(w, 0, 61,0),
                mapname, playeramount, isindividual, isteamdamage, boxamount, rb);
        rdpvp_worlds.put(w, pgm);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;

        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) &&
                e.getMaterial() == Material.WHITE_SHULKER_BOX) {
            e.setCancelled(true);

            Player p = e.getPlayer();
            p.setItemInHand(new ItemStack(Material.AIR));
            p.getInventory().addItem(rb.getRandom());
        } else if (e.getAction().equals(Action.LEFT_CLICK_AIR) &&
                e.getMaterial() == Material.BAMBOO) {
            e.setCancelled(true);

            Snowball projectile = e.getPlayer().launchProjectile(Snowball.class);
            ((CraftSnowball) projectile).getHandle().setItem(CraftItemStack.asNMSCopy(new ItemStack(Material.BAMBOO)));
        }
    }

    public Inventory getRandomBoxRoomInv(PvpGameManager pgm, int roomnumber) {
        Player p = pgm.getAdmin();
        String playername = p.getName();

        ItemStackManager skull = new ItemStackManager(Material.PLAYER_HEAD, ChatColor.RED + playername + "님의 방!");

        skull.addLore("-------------------");
        skull.addLore(ChatColor.WHITE + "현재 방장: " + ChatColor.BLUE + p.getName());
        skull.addLore(ChatColor.WHITE + "플레이어 수: " + ChatColor.BLUE + pgm.gameplayers.size() + "/" + pgm.playeramount);
        skull.addLore(ChatColor.WHITE + "개인 플레이: " + ChatColor.BLUE + pgm.isindividual);
        skull.addLore(ChatColor.WHITE + "맵: " + ChatColor.GOLD + pgm.mapname);
        skull.addLore("-------------------");

        ItemStack iskull = skull.getItemStack();

        SkullMeta meta = (SkullMeta) iskull.getItemMeta();
        meta.setOwningPlayer(p);
        meta.setDisplayName(playername);
        iskull.setItemMeta(meta);

        InventoryManager m = new InventoryManager(9, ChatColor.BLUE + "RandomBox PvP Room_" + ChatColor.GOLD + roomnumber);

        m.setitem(2, new ItemStackManager(Material.DIAMOND_SWORD, ChatColor.GREEN + "입장하기").getItemStack());
        m.setitem(4, iskull);
        m.setitem(6, new ItemStackManager(Material.BARRIER, ChatColor.RED + "돌아가기").getItemStack());

        return m.getInventory();
    }

    public Inventory getRandomBoxGameInv(PvpGameManager pgm) {
        Player p = pgm.getAdmin();

        InventoryManager m = new InventoryManager(9, ChatColor.BLUE + "RandomBox PvP " + ChatColor.GOLD + "게임 정보!");

        ItemStackManager skull = new ItemStackManager(Material.PLAYER_HEAD, ChatColor.RED + p.getName() + "님의 방!");

        skull.addLore("-------------------");
        skull.addLore(ChatColor.WHITE + "현재 방장: " + ChatColor.BLUE + p.getName());
        skull.addLore(ChatColor.WHITE + "플레이어 수: " + ChatColor.BLUE + pgm.gameplayers.size() + "/" + pgm.playeramount);
        skull.addLore(ChatColor.WHITE + "개인 플레이: " + ChatColor.BLUE + pgm.isindividual);
        skull.addLore(ChatColor.WHITE + "맵: " + ChatColor.GOLD + pgm.mapname);
        skull.addLore(ChatColor.AQUA + "§l플레이어 정보를 위해 " + ChatColor.RED + "클릭" + ChatColor.AQUA + "!");
        skull.addLore("-------------------");

        if (pgm.isindividual) {
            m.setitem(2, new ItemStackManager(Material.BOOK, ChatColor.WHITE + "§m팀 설정").getItemStack());

        } else {
            m.setitem(2, new ItemStackManager(Material.ENCHANTED_BOOK, ChatColor.GOLD + "팀 설정").getItemStack());
        }

        m.setitem(4, skull.getItemStack());
        m.setitem(6, new ItemStackManager(Material.BARRIER, ChatColor.RED + "방 나가기").getItemStack());
        return m.getInventory();
    }

    public Inventory getRoomInfoInv() {
        return null;
    }

    public Inventory getTeamSetInv() {
        InventoryManager m = new InventoryManager(9, ChatColor.BLUE + "RandomBox PvP " + ChatColor.GOLD + "게임 정보!");

        return null;
    }
}
