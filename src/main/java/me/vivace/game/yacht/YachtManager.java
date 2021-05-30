package me.vivace.game.yacht;

import me.vivace.game.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class YachtManager implements Listener {
    public static final Material[] DICE_SKIN = {Material.RED_CONCRETE, Material.ORANGE_CONCRETE, Material.YELLOW_CONCRETE, Material.LIME_CONCRETE, Material.LIGHT_BLUE_CONCRETE};

    public static final String STATE_PLAYER_DICE_WAIT = ChatColor.GOLD + "당신의 차례입니다! " + ChatColor.AQUA + "《SHIFT》" + ChatColor.GOLD + " 키로 주사위를 굴릴 수 있습니다.";
    public static final String STATE_OBSERVER_DICE_WAIT = ChatColor.AQUA + "상대방의 조작을 기다리고 있습니다..";
    public static final String STATE_OBSERVER_DICE_ROLL = ChatColor.AQUA + "상대방이 주사위를 굴리고 있습니다!";
    public static final String STATE_PLAYER_DICE_SELECT = ChatColor.GOLD + "나온 주사위를 아이템 창에서 선택해, " + ChatColor.AQUA + "《SHIFT》" + ChatColor.GOLD + " 키로 버릴 주사위를 선택하세요.";
    public static final String STATE_OBSERVER_DICE_SELECT = ChatColor.AQUA + "상대방이 다시 굴릴 주사위를 선택하고 있습니다..";
    public static final String STATE_PLAYER_SCORE_SELECT = ChatColor.GOLD + "나온 주사위로 점수판에 점수를 설정하세요.";
    public static final String STATE_OBSERVER_SCORE_SELECT = ChatColor.AQUA + "상대방이 점수 기록 위치를 선택하고 있습니다..";

    private Player p1;
    private Player p2;
    private ArrayList<Player> observers = new ArrayList<>();
    private World w;

    private boolean flag;
    public boolean isp1turn;
    public int dicerollleft;
    public String nowstatename; //DICE_WAIT, DICE_ROLL, DICE_SELECT, SCORE_SELECT

    private String p1state;
    private String p2state;

    private Location diceloc;

    private ArrayList<Dice> rollingdice = new ArrayList<>();

    private int p1score;
    private int p2score;

    private int turn; //1 ~ 12

    private Dice[] dices;
    public int[] dicenums;

    public YachtScore yachtScore;

    public YachtManager(Player p1, Player p2, Location l) {
        this.p1 = p1;
        this.p2 = p2;
        this.turn = 0;
        diceloc = l;

        yachtScore = new YachtScore(p1, p2, this);
    }

    public Dice getDices(int i) {
        return dices[i];
    }

    public void gameStart() {
        flag = true;
        isp1turn = true;
        dicerollleft = 3;

        dices = new Dice[5];
        dicenums = new int[5];

        for (int x = 0; x < 5; x++) {
            dices[x] = new Dice(diceloc.clone().add(9 * x, 0, 0), Material.WHITE_CONCRETE, DICE_SKIN[x]);
            rollingdice.add(dices[x]);
        }

        nowstatename = "DICE_WAIT";

        p1.sendTitle("YACHT DICE : 1 VS 1", "", 0, 100, 0);
        p2.sendTitle("YACHT DICE : 1 VS 1", "", 0, 100, 0);

        p1state = STATE_PLAYER_DICE_WAIT;
        p2state = STATE_OBSERVER_DICE_WAIT;

        nowStateTimer();
    }

    public void gameOver() {
        flag = false;

        int p1score = yachtScore.getScoreSum(true);
        int p2score = yachtScore.getScoreSum(false);
        String title;

        if (p1score > p2score) {
            title = p1.getName() + "님 승리!";
        } else if (p1score < p2score) {
            title = p2.getName() + "님 승리!";
        } else {
            title = "무승부";
        }

        p1.sendTitle(title, p1score + " : " + p2score, 0, 100, 0);
        p2.sendTitle(title, p1score + " : " + p2score, 0, 100, 0);
    }

    public void nowStateTimer() {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (nowstatename.equals("DICE_WAIT")) { //DICE_WAIT, DICE_ROLL, DICE_SELECT, SCORE_SELECT
                    if (isp1turn) {
                        p1state = STATE_PLAYER_DICE_WAIT;
                        p2state = STATE_OBSERVER_DICE_WAIT;
                    } else {
                        p1state = STATE_OBSERVER_DICE_WAIT;
                        p2state = STATE_PLAYER_DICE_WAIT;
                    }
                } else if (nowstatename.equals("DICE_ROLL")) {
                    if (isp1turn) {
                        p1state = "";
                        p2state = STATE_OBSERVER_DICE_ROLL;
                    } else {
                        p1state = STATE_OBSERVER_DICE_ROLL;
                        p2state = "";
                    }
                } else if (nowstatename.equals("DICE_SELECT")) {
                    if (isp1turn) {
                        p1state = STATE_PLAYER_DICE_SELECT;
                        p2state = STATE_OBSERVER_DICE_SELECT;
                    } else {
                        p1state = STATE_OBSERVER_DICE_SELECT;
                        p2state = STATE_PLAYER_DICE_SELECT;
                    }
                } else if (nowstatename.equals("SCORE_SELECT")) {
                    if (isp1turn) {
                        p1state = STATE_PLAYER_SCORE_SELECT;
                        p2state = STATE_OBSERVER_SCORE_SELECT;
                    } else {
                        p1state = STATE_OBSERVER_SCORE_SELECT;
                        p2state = STATE_PLAYER_SCORE_SELECT;
                    }
                }

                p1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(p1state));
                p2.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(p2state));
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }

    public Player getP1() {
        return p1;
    }

    public void playSound(Sound sound) {
        p1.playSound(p1.getLocation(), sound, 1f, 1f);
        p2.playSound(p2.getLocation(), sound, 1f, 1f);
    }

    public void sendTitle(String s, int times) {
        p1.sendTitle(s, "", 0, times, 0);
        p2.sendTitle(s, "", 0, times, 0);
    }

    public void joinPlayer(Player p2) {
        if (p1 == null) {
            this.p1 = p2;
        } else if (p2 == null) {
            this.p2 = p2;
        } else {
            observers.add(p2);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void openScoreBoard(PlayerSwapHandItemsEvent e) {
        if (e.getPlayer().equals(p1) || e.getPlayer().equals(p2)) {
            if (nowstatename.equals("DICE_ROLL")) return;

            e.getPlayer().openInventory(yachtScore.getScoreBoardInv(true));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerRollDice(PlayerToggleSneakEvent e) {
        if (e.isSneaking() && ((e.getPlayer().equals(p1) && isp1turn) || (e.getPlayer().equals(p2) && !isp1turn))) {
            if (nowstatename.equals("DICE_WAIT")) {
                if (e.getPlayer().equals(p1)) {
                    startRolling(e.getPlayer(), true);
                } else if (e.getPlayer().equals(p2)) {
                    startRolling(e.getPlayer(), false);
                }

                nowstatename = "DICE_ROLL";
            }
        }
    }

    @EventHandler
    public void playerMoveDicePointer(PlayerItemHeldEvent e) {
        if (nowstatename.equals("DICE_SELECT") && ((e.getPlayer().equals(this.p1) && isp1turn) || (e.getPlayer().equals(this.p2) && !isp1turn))) {
            int nowslot = e.getNewSlot();
            int lastslot = e.getPreviousSlot();

            if (2 <= nowslot && nowslot <= 6) {
                Dice nowdice = this.getDices(nowslot - 2);
                nowdice.setBorderColor(nowdice.getEye());
            }

            if (2 <= lastslot && lastslot <= 6) {
                Dice lastdice = this.getDices(lastslot - 2);
                lastdice.setBorderColor(Material.BLACK_CONCRETE);
            }
        }
    }

    @EventHandler
    public void playerSelectDice(PlayerToggleSneakEvent e) {
        if (e.isSneaking() && ((e.getPlayer().equals(p1) && isp1turn) || (e.getPlayer().equals(p2) && !isp1turn))) {
            if (nowstatename.equals("DICE_SELECT")) {
                int slot = e.getPlayer().getInventory().getHeldItemSlot();
                if (2 <= slot && slot <= 6) {
                    Dice nowdice = dices[slot - 2];

                    Block nowb = nowdice.getRerollLoc().getBlock();

                    if (nowb.getType() == Material.BEACON) {
                        nowb.setType(Material.AIR);
                    } else {
                        nowb.setType(Material.BEACON);
                    }
                } else if (slot == 8) {
                    rollingdice.clear();

                    for (Dice d : dices) {
                        if (d.getRerollLoc().getBlock().getType() == Material.BEACON) {
                            rollingdice.add(d);
                        }
                    }

                    if (rollingdice.isEmpty()) {
                        nowstatename = "SCORE_SELECT";
                        p1.openInventory(yachtScore.getScoreBoardInv(true));
                        p2.openInventory(yachtScore.getScoreBoardInv(true));
                    } else {
                        nowstatename = "DICE_WAIT";
                    }

                    for (int i = 0; i < 9; i++) {
                        e.getPlayer().getInventory().setItem(i, new ItemStack(Material.AIR));
                    }
                }
            }
        }
    }

    @EventHandler
    public void playerSelectScore(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        Inventory i = e.getClickedInventory();
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        ClickType clicktype = e.getClick();

        if (e.getView().getTitle().equals(YachtScore.INV_NAME)) {
            if (slot == 35) {
                p.closeInventory();

                if (i.getItem(35).getItemMeta().getDisplayName().equals(ChatColor.GOLD + "이전 페이지")) {
                    p.openInventory(yachtScore.getScoreBoardInv(true));
                } else if (i.getItem(35).getItemMeta().getDisplayName().equals(ChatColor.GOLD + "다음 페이지")) {
                    p.openInventory(yachtScore.getScoreBoardInv(false));
                } else {
                    //BUGFIX :
                }
            } else {
                if ((p.equals(p1) && isp1turn) || (p.equals(p2) && !isp1turn)) {
                    if (nowstatename.equals("SCORE_SELECT")) {


                        if (e.getCurrentItem().getType().equals(Material.BOOK)) {
                            int row = Math.floorDiv(slot, 9);
                            int column = Math.floorMod(slot, 9);

                            if ( !((row == 2 && isp1turn) || (row == 4 && !isp1turn)) ) return;

                            Material nowtype = i.getItem(column).getType();
                            int nowstype = YachtScore.getTypeof(nowtype);

                            yachtScore.updatePossible();
                            int gotpoints = yachtScore.ablescores[nowstype];

                            if (row == 2 && isp1turn) {
                                yachtScore.p1scores[nowstype] = gotpoints;
                                yachtScore.filledp1scores[nowstype] = true;
                                isp1turn = false;
                            } else if (row == 4 && !isp1turn) {
                                yachtScore.p2scores[nowstype] = gotpoints;
                                yachtScore.filledp2scores[nowstype] = true;
                                isp1turn = true;
                                turn++;
                            } else {
                                return;
                            }

                            if (turn == 12) {
                                nowstatename = "GAME_OVER";
                                flag = false;
                                gameOver();
                                return;
                            }

                            yachtScore.updateInventory();
                            for (int x = 0; x < 5; x++) {
                                dices[x].initialize();
                                dicenums[x] = 0;
                                rollingdice.add(dices[x]);
                            }
                            dicerollleft = 3;
                            nowstatename = "DICE_WAIT";
                        }
                    }
                }
            }

            e.setCancelled(true);
        }
    }

    public void startRolling(Player p, boolean isp1rolling) {
        int[] tick = {1};
        boolean[] isup = {true};
        YachtManager temp = this;

        new BukkitRunnable() {

            @Override
            public void run() {
                if (p.isSneaking()) {
                    if (isup[0]) {
                        tick[0]++;
                    } else {
                        tick[0]--;
                    }

                    StringBuilder nowbar = new StringBuilder();

                    for (int i = 1; i <= 20; i++) {
                        if (i <= tick[0]) {
                            nowbar.append(net.md_5.bungee.api.ChatColor.of(Dice.BAR.getColor(i * 2 - 2))).append("|").append(net.md_5.bungee.api.ChatColor.of(Dice.BAR.getColor(i * 2 - 1))).append("|");
                        } else {
                            nowbar.append(" ");
                        }
                    }

                    String message = ChatColor.RED + "주사위 굴리기 : " + ChatColor.WHITE + "[ " + nowbar.toString() + ChatColor.WHITE + " ]";

                    p.sendTitle("", message, 0, 10,10);

                    if (tick[0] == 20) {
                        isup[0] = false;
                    } else if (tick[0] == 1) {
                        isup[0] = true;
                    }

                } else {
                    int times = tick[0] * 10;

                    for (int t = 0; t < 5; t++) {
                        if (rollingdice.contains(dices[t])) {
                            dicenums[t] = dices[t].rollDice(p, times, temp);
                        }
                    }

                    dicerollleft -= 1;

                    cancel();
                    return;
                }
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }

    public void setP1state(String s) {
        p1state = s;
    }

    public void setP2state(String s) {
        p2state = s;
    }
}
