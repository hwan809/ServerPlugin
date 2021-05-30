package me.vivace.game.Events;

import me.vivace.game.*;
import me.vivace.game.simulation.MonteCarlo;
import me.vivace.game.util.InventoryManager;
import me.vivace.game.util.ItemStackManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Events implements Listener {

    //개발용
    @EventHandler
    public void break_block(BlockBreakEvent e) {
        if (!e.getPlayer().getItemInHand().getType().equals(Material.BLAZE_ROD)) return;

        Location b_loc = e.getBlock().getLocation();
        Bukkit.broadcastMessage(ChatColor.GOLD + "당신은 좌표 " + ChatColor.WHITE + b_loc.getBlockX() +" " +
                b_loc.getBlockY() + " " + b_loc.getBlockZ() + ChatColor.GOLD +" 에서 " +
                ChatColor.AQUA + e.getBlock().getType().toString() + ChatColor.GOLD + " 를 부쉈습니다!");

        MonteCarlo.init();
        MonteCarlo.thisloc = b_loc;
        MonteCarlo.radius = 150;
        MonteCarlo.drawSquare(b_loc, Material.BLACK_CONCRETE, 150);
        MonteCarlo.drawCircle(b_loc, Material.RED_CONCRETE, 150);

        Bukkit.broadcastMessage(Main.SERVER_FOLDER_PATH);
        Main.mainchannel.sendFile(Main.f).queue();

//        DanceMap danceMap = new DanceMap(e.getBlock(), e.getBlock(), e.getBlock(), 120);
//        FireIceManager fim = new FireIceManager(danceMap, e.getPlayer());
//        Bukkit.getServer().getPluginManager().registerEvents(fim, Main.getPlugin(Main.class));

        //Test.makeSphere(e.getPlayer(), b_loc, 60);

//        Space space = new Space(e.getPlayer().getWorld());
//
//        Orbit o_mercury = new Orbit(b_loc, 5, 3.38f * 5, 1, Material.RED_WOOL);
//        Orbit o_venus = new Orbit(b_loc, 7, 3.86f * 5, 1, Material.ORANGE_WOOL);
//        Orbit o_earth = new Orbit(b_loc, 10, 7.155f * 5, 1, Material.YELLOW_WOOL);
//        Orbit o_mars = new Orbit(b_loc, 15, 5.65f * 5, 1, Material.LIME_WOOL);
//        Orbit o_jupiter = new Orbit(b_loc, 50, 6.09f * 5, 2, Material.CYAN_WOOL);
//        Orbit o_saturn = new Orbit(b_loc, 100, 5.51f * 5, 3, Material.LIGHT_BLUE_WOOL);
//        Orbit o_uranus = new Orbit(b_loc, 200, 6.48f * 5, 5, Material.BLUE_WOOL);
//        Orbit o_neptune = new Orbit(b_loc, 300, 6.43f * 5, 10, Material.PURPLE_WOOL);
//
//        e.setCancelled(true);

    }

    public void createCircleParticle(Player p, Block b, BigDecimal r) {
        final BigDecimal[] x = {r};
        final boolean[] isup = {true};

        new BukkitRunnable() {

            @Override
            public void run() {
                BigDecimal value = new BigDecimal("0.2");
                double z = Math.sqrt(r.subtract(x[0]).multiply((r.add(x[0]))).doubleValue());

                if (!isup[0]) {
                    z = -z;
                    value = new BigDecimal("-0.2");
                }

                Location particleloc = b.getLocation().clone().add(x[0].doubleValue(), 0, z);
                p.spawnParticle(Particle.VILLAGER_HAPPY, particleloc, 1);

                x[0] = x[0].subtract(value);

                if (x[0].doubleValue() == -r.doubleValue() && isup[0]) {
                    isup[0] = false;
                } else if (x[0].doubleValue() == r.doubleValue() && !isup[0]) {
                    isup[0] = true;
                }
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }

    public void createCircleParticle2(Player p, Location l, int r) {
        final float[] degree = {0};

        new BukkitRunnable() {

            @Override
            public void run() {
                double x = Math.cos(Math.toRadians(degree[0])) * r;
                double z = Math.sin(Math.toRadians(degree[0])) * r;

                Location particleloc = l.clone().add(x, 0, z);
                p.spawnParticle(Particle.VILLAGER_HAPPY, particleloc, 1);

                degree[0] += 36f / r;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }

    @EventHandler
    public void loreTest(PlayerInteractEvent e) throws IOException {
        if (e.getAction() != Action.RIGHT_CLICK_AIR) return;

        ItemStack playeritem = e.getPlayer().getInventory().getItemInMainHand();
        ItemMeta itemmeta = playeritem.getItemMeta();
        List<String> lorelist = new ArrayList<>();

        BufferedImage image;
        URL url = new URL(Main.PLAYER_HEAD_LINK + "avatar/mabig/24.png");
        image = ImageIO.read(url);

        for (int x = 0; x < 24; x++) {
            StringBuilder s = new StringBuilder();

            for (int y = 0; y < 24; y++) {
                java.awt.Color c = new Color(image.getRGB(y, x));

                net.md_5.bungee.api.ChatColor chatColor = net.md_5.bungee.api.ChatColor.of(c);
                s.append(ChatColor.BOLD).append(chatColor).append(Main.HEAD_TEXT_PIXEL);
            }

            lorelist.add(s.toString());
        }

        itemmeta.setLore(lorelist);
        playeritem.setItemMeta(itemmeta);
    }

    @EventHandler
    public void authEvent(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!DiscordListener.authcode.containsKey(p)) return;
        String code = DiscordListener.authcode.get(p);

        if (e.getMessage().equals(code)) {
            Main.authp_uuids.add(p.getUniqueId().toString());
            DiscordListener.authcode.remove(p);

            p.sendMessage(ChatColor.BLUE + "[+] " + ChatColor.GREEN + "인증이 완료되었습니다!");

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void createItemEvent(CraftItemEvent e) {
        if (e.getClickedInventory() == null) return;
        e.getWhoClicked().sendMessage(e.getRecipe().toString());
        e.getWhoClicked().sendMessage(e.getInventory().getType().toString());
    }

    @EventHandler
    public void genWorld(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        ClickType clicktype = e.getClick();
        if (e.getClickedInventory() ==
                Main.ims.get("main_inv").getInventory()) {

            //Inventory maininv = Main.ims.get("main_inv").getInventory();

            if (slot == 8) {
                //개발용
                p.closeInventory();
                World w = VoidChunk.generateNullWorld(Main.randomString(20));
                w.setSpawnLocation(0, 62, 0);
                new Location(w, 0, 61, 0).getBlock().setType(Material.BLACK_CONCRETE);
                p.teleport(w.getSpawnLocation());
            } else if (slot == 5) {
                //리더보드
                p.closeInventory();
                p.openInventory(Main.ims.get("lead_inv").getInventory());
            }

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String puuid = p.getUniqueId().toString();

        if (!Main.authp_uuids.contains(puuid)) {
            //p.sendMessage(ChatColor.RED + "[+] 계정이 인증되지 않았습니다.");
            //p.sendMessage(ChatColor.GOLD + "공식 서버" + ChatColor.RED + "로 이동해 계정을 인증하세요.\n"
            //        + ChatColor.BLUE + Main.DISCORD_INVITE_LINK);
        }
    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        String puuid = p.getUniqueId().toString();

        //if (!Main.authp_uuids.contains(puuid)) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void openmenu(PlayerSwapHandItemsEvent e) {
        Player p = e.getPlayer();

        if (p.isSneaking()) {
            p.openInventory(Main.ims.get("main_inv").getInventory());
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void Leaderboard_inv_click(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        ClickType clicktype = e.getClick();

        if (e.getClickedInventory() == Main.ims.get("lead_inv").getInventory()) {
            for (int i = 0; i < Main.leaderboards.size(); i++) {
                if (slot == i) {
                    GameStats gs = Main.leaderboards.get(i);
                    Bukkit.broadcastMessage(gs.toString());

                    //generate leaderboard inventory
                    InventoryManager leaderboard_im = new InventoryManager(9,
                            Main.gamecodes.get(i) + ChatColor.GOLD + " - LEADERBOARD");

                    GameScore[] gamescore = gs.getLeaderBoard(3);
                    Bukkit.getConsoleSender().sendMessage(gamescore.length + "");

                    leaderboard_im.setitem(4, gen_leaderitem(1, gamescore[0]));
                    leaderboard_im.setitem(2, gen_leaderitem(2, gamescore[1]));
                    leaderboard_im.setitem(6, gen_leaderitem(3, gamescore[2]));

                    p.closeInventory();
                    p.openInventory(leaderboard_im.getInventory());
                }
            }
        } else if (e.getView().getTitle().contains("LEADERBOARD")) {
            e.setCancelled(true);
        }
    }

    public ItemStack gen_leaderitem(int rank, GameScore gamescore) {

        ChatColor rankcolor = ChatColor.WHITE;
        Material medal = Material.BRICK;
        String pname = gamescore.getPlayername();
        int score = gamescore.getScore();

        if (rank == 1) {
            rankcolor = ChatColor.GOLD;
            medal = Material.GOLD_INGOT;
        } else if (rank == 2) {
            rankcolor = ChatColor.GRAY;
            medal = Material.IRON_INGOT;
        } else if (rank == 3) {
            rankcolor = ChatColor.DARK_GRAY;
            medal = Material.NETHERITE_INGOT;
        }

        ItemStackManager ism = new ItemStackManager(medal, rankcolor + "::: #" + rank + " place:::");

        ism.addLore(ChatColor.WHITE + pname + rankcolor + "님의 기록!");
        ism.addLore("------------");
        ism.addLore(ChatColor.WHITE	 + "점수: " + rankcolor + score);
        ism.addLore("------------");

        return ism.getItemStack();
    }
}
