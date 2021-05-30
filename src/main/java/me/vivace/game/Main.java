package me.vivace.game;

import me.vivace.game.Events.Events;
import me.vivace.game.commands.Commands;
import me.vivace.game.commands.TabComplete;
import me.vivace.game.pvp.PvpEvents;
import me.vivace.game.pvp.RandomBox;
import me.vivace.game.tetris.Board;
import me.vivace.game.tetris.TetrisEvents;
import me.vivace.game.util.InventoryManager;
import me.vivace.game.util.ItemStackManager;
import me.vivace.game.util.Test;
import me.vivace.game.yacht.YachtEvents;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Main extends JavaPlugin implements Listener {
    private Commands commands = new Commands();
    private TabComplete tabcommands = new TabComplete();
    public static JDA discordjda;

    public static HashMap<String, InventoryManager> ims = new HashMap<String, InventoryManager>();
    public static HashMap<Integer, GameStats> leaderboards = new HashMap<Integer, GameStats>();
    public static HashMap<Integer, String> gamecodes = new HashMap<Integer, String>();
    public static ArrayList<String> authp_uuids = new ArrayList<>();

    public static final String HEAD_TEXT_PIXEL = "⬛";
    public static final String PLAYER_HEAD_LINK = "https://minotar.net/";

    public static final String DISCORD_INVITE_LINK = "https://discord.gg/QqwCffmB";
    public static final String DISCORD_TOKEN = "NzUxNjQ0NzU0NjAwNzg4MDY4.X1MFug.IlGb_bZ7PsEAWzD_KlnvDz0Ie_Q";
    public static final String MAINCHANNEL_ID = "756475619662888971";
    public static final String MABIGCHANNEL_ID = "796611517013033010";

    public static final String MINIGAME_RESOURCEPACK = "https://www.dropbox.com/sh/5qihuydjjztf0va/AABymRIuzoIdU9FpKDIgXWI-a?dl=1";
    public static final String NULL_RESOURCEPACK = "https://www.dropbox.com/sh/a9aht08jjj8hvlw/AADHEvTH05C9bmBXteZoWs-qa?dl=1";
    public static final String SERVER_FOLDER_PATH = System.getProperty("user.dir");

    public static TextChannel mainchannel, mabigchannel;
    public static File f;

    // montedata[simulation number #n][circle radius r][dots num n]
    public static float[][][] montedata;

//	public static Scoreboard board;
//	public static Objective o;
//	public static HashMap<Player, Score> player_scoreboard = new HashMap<Player, Score>();

    public void onEnable() {
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n\n미니게임 onnnnnnnnnnnnnn\n\n");
        getServer().getPluginManager().registerEvents(new Events(), this);
        getServer().getPluginManager().registerEvents(new TetrisEvents(), this);
        getServer().getPluginManager().registerEvents(TetrisEvents.tetris_multi, this);
        getServer().getPluginManager().registerEvents(new PvpEvents(), this);
        getServer().getPluginManager().registerEvents(new YachtEvents(), this);
        getCommand(commands.cmd1).setExecutor(commands);
        getCommand(commands.cmd2).setExecutor(commands);
        getCommand(commands.cmd3).setExecutor(commands);
        getCommand(commands.cmd4).setExecutor(commands);
        getCommand(commands.cmd5).setExecutor(commands);
        getCommand(commands.cmd6).setExecutor(commands);
        getCommand(commands.cmd7).setExecutor(commands);
        getCommand(commands.cmd1).setTabCompleter(tabcommands);
        getCommand(commands.cmd3).setTabCompleter(tabcommands);

        try {
            JDABuilder builder = JDABuilder.createDefault(DISCORD_TOKEN);
            builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
            builder.addEventListeners(new DiscordListener());
            discordjda = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        f = new File(SERVER_FOLDER_PATH + "\\plugins\\MinigameServer\\b.jpg");

        //functions
        setGameCodes();
        loadConfig();
        randomboxDatas();

        //Inventories

        InventoryManager main = new InventoryManager(9, ChatColor.AQUA + "미니게임");
        InventoryManager leaderboard = new InventoryManager(54, ChatColor.AQUA + "LeaderBoard");

        main.setitem(0, new ItemStackManager(Material.SUNFLOWER, ChatColor.BLUE + "TETRIS").getItemStack());
        main.setitem(1, new ItemStackManager(Material.NETHERITE_SWORD, ChatColor.RED + "RANDOMBOX PVP").getItemStack());
        main.setitem(2, new ItemStackManager(Material.TARGET, ChatColor.BLUE + "YACHT DICE").getItemStack());
        main.setitem(5, new ItemStackManager(Material.PAPER, ChatColor.BOLD + "LEADERBOARD").getItemStack());
        main.setitem(8, new ItemStackManager(Material.BARRIER, "NULL WORLD").getItemStack());

        leaderboard.setitem(0, new ItemStackManager(Material.WOODEN_PICKAXE, gamecodes.get(0)).getItemStack());

        ims.put("main_inv", main);
        ims.put("lead_inv", leaderboard);

        World world = Bukkit.getWorld("world");

        File configfile = new File(getDataFolder(),  "auth_players.yml");
        FileConfiguration dataconfig = YamlConfiguration.loadConfiguration(configfile);
        InputStream defaultStream = getResource("auth_players.yml");

        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            dataconfig.setDefaults(defaultConfig);
        }

        for (String s : dataconfig.getStringList("players")) {
            authp_uuids.add(s);
        }

        for (int r=0; r<100; r = r + 5) Test.colors.add(new Color(r*255/100,       255,         0));
        for (int g=100; g>0; g = g - 5) Test.colors.add(new Color(      255, g*255/100,         0));
        for (int b=0; b<100; b = b + 5) Test.colors.add(new Color(      255,         0, b*255/100));
        for (int r=100; r>0; r = r - 5) Test.colors.add(new Color(r*255/100,         0,       255));
        for (int g=0; g<100; g = g + 5) Test.colors.add(new Color(        0, g*255/100,       255));
        for (int b=100; b>0; b = b - 5) Test.colors.add(new Color(        0,       255, b*255/100));
        Test.colors.add(new Color(        0,       255,         0));

        Test.chestAnimationVertically(new Location(world, -208, 4, 70));

        //summonMob();

        //마엉뽈
        //gamBak(new Location(world, -92, 30, -303), Material.LIGHT_BLUE_STAINED_GLASS, 18);
        //gamBak(new Location(world, -92, 27, -303), Material.LIGHT_BLUE_STAINED_GLASS, 22);
        //gamBak(new Location(world, -92, 24, -303), Material.LIGHT_BLUE_STAINED_GLASS, 16);
        //gamBak(new Location(world, -92, 31, -300), Material.LIGHT_BLUE_STAINED_GLASS, 21);

//        try {
//            BuildingSaver.saveBuilding(new Location(world, 315, 21, 850),
//                  new Location(world, 350, 0, 882), "hell");
//            BuildingSaver.saveBuilding(new Location(world, 309, 21, 945),
//                  new Location(world, 345, 0, 907), "world");
//            BuildingSaver.saveBuilding(new Location(world, 341, 0, 960),
//                  new Location(world, 313, 28, 987), "mine");
//            BuildingSaver.saveBuilding(new Location(world, 305, 39, 1051),
//                  new Location(world, 342, 0, 1012), "beach");
//            BuildingSaver.saveBuilding(new Location(world, 285, 26, 1014),
//                  new Location(world, 261, 4, 1065), "fried_pigeon");
//            BuildingSaver.saveBuilding(new Location(world, 369, 11, 969),
//                    new Location(world, 361, 4, 978), "mine_lobby");
//            BuildingSaver.saveBuilding(new Location(world, 359, 4, 934),
//                    new Location(world, 367, 11, 925), "world_lobby");
//            BuildingSaver.saveBuilding(new Location(world, 359, 4, 864),
//                    new Location(world, 367, 11, 873), "hell_lobby");
//            BuildingSaver.saveBuilding(new Location(world, 296, 4, 1046),
//                    new Location(world, 290, 11, 1035), "fried_pigeon_lobby");
//            BuildingSaver.saveBuilding(new Location(world, 364, 4, 1031),
//                    new Location(world, 356, 13, 1040), "beach_lobby");
//            BuildingSaver.saveBuilding(new Location(world, -98, 3, -310),
//                    new Location(world, -81, 38, -298), "water_afk");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "\n\n미니게임 0FF\n\n");

        //player to mainworld
//        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
//            p.teleport(Bukkit.getWorld("world").getSpawnLocation());
//        }

        //remove all tworlds
        for (World w : TetrisEvents.tetris_worlds.keySet()) {
            try {
                VoidChunk.deleteWorld(w);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //remove scoreboard
        for (Board b : TetrisEvents.tetris_worlds.values()) {
            b.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        //save authedplayers
        File configfile = new File(getDataFolder(),  "auth_players.yml");
        FileConfiguration dataconfig = YamlConfiguration.loadConfiguration(configfile);
        InputStream defaultStream = getResource("auth_players.yml");

        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            dataconfig.setDefaults(defaultConfig);
        }

        dataconfig.set("players", authp_uuids);
        try {
            dataconfig.save(configfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        configSave();
        discordjda.shutdown();
    }

    public void setGameCodes() {
        gamecodes.put(0, "TETRIS");
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        for (int i = 0; i < getConfig().getConfigurationSection("gamestats")
                .getValues(false).size(); i++) {

            //exception when config.yml doesn't contain gamevalue

            if (!gamecodes.keySet().contains(i)) {
                getServer().getConsoleSender().sendMessage(ChatColor.RED +
                        "스코어보드 에러: " + i + "번 게임의 게임 플레이 데이터가 존재하지 않습니다\n" +
                        "config.yml의 백업본을 확인해 주세요.");
                continue;
            }

            //add item in leaderboard inventory
            //get all gamestats on config.yml (Configuration)

            int gamesplayed = getConfig().getConfigurationSection("gamestats." + i)
                    .getValues(false).size();

            GameScore[] gamescores = new GameScore[gamesplayed];

            //마빅빅님은 언제 배워서 이런거 할까..

            for (int scores = 0; scores < gamesplayed; scores++) {
                String s = getConfig().getString("gamestats." + i + "." + scores + ".player");
                int score = getConfig().getInt("gamestats." + i + "." + scores + ".score");
                GameScore gs = new GameScore(i, s, score);
                gamescores[scores] = gs;
            }

            GameStats ngm = new GameStats(gamecodes.get(i), gamescores);
            leaderboards.put(i, ngm);
        }
    }

    public void configSave() {
        for (int i = 0; i < leaderboards.size(); i++) {
            for (int j = 0; j < leaderboards.get(i).getScores().length; j++) {
                String pname = leaderboards.get(i).getScores()[j].getPlayername();
                int score = leaderboards.get(i).getScores()[j].getScore();

                //getConfig().set("gamestats." + i + "." + j + ".player", pname);
                //getConfig().set("gamestats." + i + "." + j + ".score", score);
            }
        }

        saveConfig();
    }

    public static Inventory getGameLobbyInv(String gamename, int players) {
        InventoryManager multiplaylobby = new InventoryManager(9, ChatColor.BLUE + gamename + " : Lobby");

        multiplaylobby.setitem(2, new ItemStackManager(Material.END_CRYSTAL, ChatColor.GREEN + "새 방 만들기").getItemStack());
        multiplaylobby.setitem(6, new ItemStackManager(Material.PLAYER_HEAD, ChatColor.RED + "멀티플레이").getItemStack());

        ItemStackManager ism = new ItemStackManager(Material.PAPER, ChatColor.AQUA + "환영합니다!");
        ism.addLore("-------------------");
        ism.addLore(ChatColor.WHITE + "『" + gamename + "』" + " 를 플레이 중인 유저 : " + players);
        ism.addLore("-------------------");

        multiplaylobby.setitem(4, ism.getItemStack());

        return multiplaylobby.getInventory();
    }

    public static String randomString(int length) {
        StringBuffer temp = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < length; i++) {
            int rIndex = rnd.nextInt(3);
            switch (rIndex) {
                case 0:
                    // a-z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    // A-Z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    // 0-9
                    temp.append((rnd.nextInt(10)));
                    break;
            }
        }

        return temp.toString();
    }

    private void randomboxDatas() {
        PvpEvents.randombox.put(new ItemStack(Material.NETHERITE_HELMET), 5);
        PvpEvents.randombox.put(new ItemStack(Material.NETHERITE_CHESTPLATE), 5);
        PvpEvents.randombox.put(new ItemStack(Material.NETHERITE_LEGGINGS), 5);
        PvpEvents.randombox.put(new ItemStack(Material.NETHERITE_BOOTS), 5);
        PvpEvents.randombox.put(new ItemStack(Material.NETHERITE_SWORD), 5);

        PvpEvents.randombox.put(new ItemStack(Material.DIAMOND_HELMET), 10);
        PvpEvents.randombox.put(new ItemStack(Material.DIAMOND_CHESTPLATE), 10);
        PvpEvents.randombox.put(new ItemStack(Material.DIAMOND_LEGGINGS), 10);
        PvpEvents.randombox.put(new ItemStack(Material.DIAMOND_BOOTS), 10);
        PvpEvents.randombox.put(new ItemStack(Material.DIAMOND_SWORD), 10);
        PvpEvents.randombox.put(new ItemStack(Material.DIAMOND_AXE), 10);
        PvpEvents.randombox.put(new ItemStack(Material.SHIELD), 10);
        PvpEvents.randombox.put(new ItemStack(Material.BOW), 10);

        PvpEvents.randombox.put(new ItemStack(Material.IRON_HELMET), 25);
        PvpEvents.randombox.put(new ItemStack(Material.IRON_CHESTPLATE), 25);
        PvpEvents.randombox.put(new ItemStack(Material.IRON_LEGGINGS), 25);
        PvpEvents.randombox.put(new ItemStack(Material.IRON_BOOTS), 25);
        PvpEvents.randombox.put(new ItemStack(Material.IRON_SWORD), 25);
        PvpEvents.randombox.put(new ItemStack(Material.IRON_AXE), 25);
        PvpEvents.randombox.put(new ItemStackManager(Material.BAMBOO, ChatColor.STRIKETHROUGH + "《대한죽창연합회》").getItemStack(), 25);

        PvpEvents.randombox.put(new ItemStack(Material.GOLDEN_HELMET), 30);
        PvpEvents.randombox.put(new ItemStack(Material.GOLDEN_CHESTPLATE), 30);
        PvpEvents.randombox.put(new ItemStack(Material.GOLDEN_LEGGINGS), 30);
        PvpEvents.randombox.put(new ItemStack(Material.GOLDEN_BOOTS), 30);
        PvpEvents.randombox.put(new ItemStack(Material.GOLDEN_SWORD), 30);
        PvpEvents.randombox.put(new ItemStack(Material.GOLDEN_AXE), 30);
        PvpEvents.randombox.put(new ItemStack(Material.STONE_SWORD), 30);
        PvpEvents.randombox.put(new ItemStack(Material.STONE_AXE), 30);

        PvpEvents.randombox.put(new ItemStack(Material.CHAINMAIL_HELMET), 40);
        PvpEvents.randombox.put(new ItemStack(Material.CHAINMAIL_CHESTPLATE), 40);
        PvpEvents.randombox.put(new ItemStack(Material.CHAINMAIL_LEGGINGS), 40);
        PvpEvents.randombox.put(new ItemStack(Material.CHAINMAIL_BOOTS), 40);
        PvpEvents.randombox.put(new ItemStack(Material.LEATHER_HELMET), 40);
        PvpEvents.randombox.put(new ItemStack(Material.LEATHER_CHESTPLATE), 40);
        PvpEvents.randombox.put(new ItemStack(Material.LEATHER_LEGGINGS), 40);
        PvpEvents.randombox.put(new ItemStack(Material.LEATHER_BOOTS), 40);
        PvpEvents.randombox.put(new ItemStack(Material.WOODEN_SWORD), 40);
        PvpEvents.randombox.put(new ItemStack(Material.WOODEN_AXE), 40);

        PvpEvents.randombox.put(new ItemStackManager(Material.RABBIT_FOOT, "행운의 토끼발 획득!").getItemStack(), 80);
        PvpEvents.rb = new RandomBox(PvpEvents.randombox);
    }

    public static void errorSound(Player p) {
        p.playSound(p.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1f, 1f);
    }

    public static ItemStack getPlayerHead(String pname) {
        ItemStack p1head = new ItemStack(Material.PLAYER_HEAD, 1 , (short) 3);
        SkullMeta meta = (SkullMeta) p1head.getItemMeta();
        meta.setOwner(pname);
        p1head.setItemMeta(meta);

        return p1head;
    }
}
