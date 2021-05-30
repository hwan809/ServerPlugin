package me.vivace.game.pvp;

import me.vivace.game.Main;
import me.vivace.game.util.BuildingSaver;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;

public class PvpGameManager {

    private boolean flag = false;

    private RandomBox rb;
    private Location maploc;
    private Location lobbyloc;
    private Location lobbyspawnloc;

    ArrayList<Player> gameplayers = new ArrayList<>();
    ArrayList<Player> gamespecplayers = new ArrayList<>();
    Player admin;

    int playeramount;
    boolean isindividual;
    private boolean isteamdamage;
    String mapname;
    private int boxamount;

    public PvpGameManager(Player admin, Location maploc, String mapname,
                          int playeramount, boolean isindividual, boolean isteamdamage,
                          int boxamount, RandomBox rb) {

        this.admin = admin;
        this.maploc = maploc;
        this.mapname = mapname;
        this.playeramount = playeramount;
        this.isindividual = isindividual;
        this.isteamdamage = isteamdamage;
        this.rb = rb;
        this.boxamount = boxamount;
        this.gameplayers.add(admin);

        BuildingSaver.pasteBuilding(maploc, mapname);

        Plugin plugin = Main.getPlugin(Main.class);

        File map = new File(plugin.getDataFolder(), mapname + ".yml");
        File lobby = new File(plugin.getDataFolder(), mapname + "_lobby.yml");
        FileConfiguration mapcon = YamlConfiguration.loadConfiguration(map);
        FileConfiguration lobbycon = YamlConfiguration.loadConfiguration(lobby);

        int xdif = mapcon.getInt("xdif");
        int ydif = mapcon.getInt("ydif");
        int zdif = mapcon.getInt("zdif");

        int lxdif = lobbycon.getInt("xdif");
        int lzdif = lobbycon.getInt("zdif");

        this.lobbyspawnloc = maploc.clone();
        this.lobbyspawnloc.add(xdif / 2,ydif + 10,zdif / 2);
        this.lobbyloc = this.lobbyspawnloc.clone();
        this.lobbyloc.add(-(lxdif / 2), -3, -(lzdif / 2));

        BuildingSaver.pasteBuilding(this.lobbyloc, mapname + "_lobby");
        admin.teleport(this.lobbyspawnloc);
        sendInfoMesasge(admin, true);
    }



    public boolean joinPlayer(Player p) {
        if (gameplayers.size() < playeramount) {
            this.gameplayers.add(p);
            p.teleport(this.lobbyspawnloc);
            sendInfoMesasge(p, true);

            return true;
        } else {
            this.gamespecplayers.add(p);
            p.teleport(this.lobbyspawnloc);
            p.setAllowFlight(true);
            p.setFlying(true);
            sendInfoMesasge(p, false);

            return false;
        }
    }

    public void sendInfoMesasge(Player p, boolean isplayer) {
        for (Player sendp : this.gameplayers) {
            if (isplayer) {
                sendp.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.AQUA + " 님이 입장했습니다. " +
                        ChatColor.GOLD + "(" + gameplayers.size() + "/" + playeramount + ")");
            } else {
                sendp.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.GREEN + " 님이 관전자로 입장했습니다.. ");
            }
        }


    }

    public Player getAdmin() {
        return this.admin;
    }
    public ArrayList<Player> getPlayers() { return this.gameplayers; }
}
