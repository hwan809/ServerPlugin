package me.vivace.game.commands;

import me.vivace.game.Main;
import me.vivace.game.VoidChunk;
import me.vivace.game.simulation.ArrowShot;
import me.vivace.game.simulation.MonteCarlo;
import me.vivace.game.util.Gradient;
import me.vivace.game.yacht.YachtEvents;
import me.vivace.game.yacht.YachtManager;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;

public class Commands implements Listener, CommandExecutor{

    public String cmd1 = "gradients";
    public String cmd2 = "bs";
    public String cmd3 = "monte";
    public String cmd4 = "head";
    public String cmd5 = "vector";
    public String cmd6 = "yacht";
    public String cmd7 = "graphs";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase(cmd1)) {

            if (args.length != 5 && args.length != 9) {Bukkit.broadcastMessage(args.length + ""); return false;}

            Color start, end;
            String target;
            int value;

            if (args.length == 5) {
                try {
                    start = Color.decode(args[1]);
                    end = Color.decode(args[2]);

                    target = args[3];
                    value = Integer.parseInt(args[4]);
                } catch (Exception e) {return false;}
            } else { // args.length == 9
                try {
                    start = new Color(Integer.parseInt(args[1]),
                            Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                    end = new Color(Integer.parseInt(args[4]),
                            Integer.parseInt(args[5]), Integer.parseInt(args[6]));

                    target = args[7];
                    value = Integer.parseInt(args[8]);
                } catch (Exception e) {return false;}
            }

            Gradient color = new Gradient(start, end, target.length());

            if (args[0].equals("title")) {
                Color[] c = color.getColors();
                summonTitle(value, c, target);

                return true;

            } else if (args[0].equals("chat")) {

                String newstring = color.stringGradient(target);
                for (int i = 0; i < value; i++) {
                    Bukkit.broadcastMessage(newstring);
                }
                return true;

            } else {
                sender.sendMessage("title, chat or nametag?");
                return false;
            }
        } else if (cmd.getName().equalsIgnoreCase(cmd2)) {
            if (!(sender instanceof Player)) return false;

            Player p = (Player) sender;
            Chunk chunk = p.getLocation().getChunk();

            analyizeChunk(chunk, Material.DIAMOND_ORE, Material.IRON_ORE, Material.EMERALD_ORE);

            return true;
        } else if (cmd.getName().equalsIgnoreCase(cmd3)) {
            if (args[0].equals("add")) {

                try {
                    int amount = Integer.parseInt(args[1]);
                    if (amount > 0) {
                        MonteCarlo.randomDots(MonteCarlo.thisloc, Material.BLUE_STAINED_GLASS, MonteCarlo.radius, amount, true);
                    }
                } catch (Exception e) {}
            } else if (args[0].equals("attr")) {
                try {
                    MonteCarlo.printAttr();
                } catch (Exception e) {}
            } else if (args[0].equals("pi")) {
                Bukkit.broadcastMessage("pi : " + Math.PI);
            } else {
                sender.sendMessage("add, attr or pi?");
                return false;
            }
        } else if (cmd.getName().equalsIgnoreCase(cmd4)) {
            try {
                String pname = args[0];

                BufferedImage image; //로컬 파일을 사용하는 경우

                //URL을 사용하는 경우
                URL url = new URL(Main.PLAYER_HEAD_LINK + "avatar/" + pname +"/16.png");
                image = ImageIO.read(url);

                StringBuilder s = new StringBuilder("----" + pname + "----\n");

                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        Color c = new Color(image.getRGB(y, x));

                        ChatColor chatColor = ChatColor.of(c);
                        s.append(chatColor + Main.HEAD_TEXT_PIXEL);
                    }

                    s.append("\n");
                }

                sender.sendMessage(s.toString());

            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "그런 유저명이 없습니다.");
            }
        } else if (cmd.getName().equalsIgnoreCase(cmd5)) {
            if (!(sender instanceof Player)) return false;

            try {
                Player p = (Player) sender;

                float amount = Float.parseFloat(args[0]);

                ArrowShot as = new ArrowShot(amount);
                as.Shoot(p);
                as.Calculate();
            } catch (Exception e) {}
        } else if (cmd.getName().equalsIgnoreCase(cmd6)) {
            if (!(sender instanceof Player)) return false;

            try {
                Player p1 = (Player) sender;
                Player p2 = Bukkit.getPlayer(args[0]);

                World w = VoidChunk.generateNullWorld("yacht_" + YachtEvents.yachtManagers.size());
                w.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                w.setTime(20000);
                Location p1loc = new Location(w, 34, 63, 36);
                Location p2loc = new Location(w, 38, 63, 36);
                p1loc.setPitch(0); p2loc.setPitch(0);
                p1loc.setYaw(180); p2loc.setYaw(180);

                p1loc.getBlock().setType(Material.BLACK_CONCRETE);
                p2loc.getBlock().setType(Material.BLACK_CONCRETE);

                p1.teleport(p1loc.add(0, 1, 0));
                p2.teleport(p2loc.add(0, 1, 0));

                YachtManager yachtManager = new YachtManager(p1, p2, new Location(p1.getWorld(), 15, 70, 15));
                YachtEvents.yachtManagers.add(yachtManager);
                Bukkit.getServer().getPluginManager().registerEvents(yachtManager, Main.getPlugin(Main.class));
                yachtManager.gameStart();
            } catch (Exception e) {}
        } else if (cmd.getName().equalsIgnoreCase(cmd7)) {
            if (!(sender instanceof Player)) return false;

            Player p = (Player) sender;
        }

        return false;
    }

    public void summonTitle(int time, Color[] colors, String text) {

        new BukkitRunnable() {

            int timecount = 0;
            int i = 0;

            @Override
            public void run() {
                if (i >= colors.length) {
                    ArrayUtils.reverse(colors);
                    i = 0;
                } else if (timecount >= time * 20) {
                    cancel();
                }

                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    p.sendTitle(ChatColor.of(colors[i]) + text, "", 0, 1, 0);
                }

                i++;
                timecount++;
            }

        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }

    public void analyizeChunk(Chunk chunk, Material... m) {
        World w = chunk.getWorld();
        int cx = chunk.getX() << 4; // chunks x
        int cz = chunk.getZ() << 4; // chunks z

        for (Entity entity : chunk.getEntities()) {
            if (!entity.getType().equals(EntityType.PLAYER)) return;
            entity.remove();
        }

        for (int x = cx; x < cx + 16; x++) {
            for (int z = cz; z < cz + 16; z++) {
                for (int y = 0; y < 128; y++) {

                    Block b = new Location(w, x, y, z).getBlock();

                    if (!Arrays.asList(m).contains(b.getType())) {
                        b.setType(Material.AIR);
                    }
                }
            }
        }

    }
}
