package me.vivace.game.yacht;

import me.vivace.game.Main;
import me.vivace.game.util.InventoryManager;
import me.vivace.game.util.ItemStackManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class YachtScore {

    public static final String INV_NAME = ChatColor.AQUA + "YACHT DICE SCOREBOARD";

    private Player p1;
    private Player p2;

    public int[] p1scores;
    public int[] p2scores;

    public boolean[] filledp1scores;
    public boolean[] filledp2scores;

    //'1s', '2s', '3s', '4s', '5s', '6s', 'Choice', '4-of-a-kind', 'Full House', 'S. Straight', 'L. Straight', 'Yacht';
    public int[] ablescores;

    private YachtManager yachtManager;

    public YachtScore(Player p1, Player p2, YachtManager yachtManager) {
        this.p1 = p1;
        this.p2 = p2;
        this.yachtManager = yachtManager;

        p1scores = new int[12];
        p2scores = new int[12];

        Arrays.fill(p1scores, 0);
        Arrays.fill(p2scores, 0);

        filledp1scores = new boolean[12];
        filledp2scores = new boolean[12];

        Arrays.fill(filledp1scores, false);
        Arrays.fill(filledp2scores, false);

        ablescores = new int[12];
    }

    public void updatePossible() {
        for (int i = 0; i < 6; i++) {
            int sumofi = sumofSingle(i + 1); // 1 ~ 6s

            ablescores[i] = sumofi;
        }

        ablescores[6] = sumOfDices(yachtManager.dicenums);
        ablescores[7] = fourCard();
        ablescores[8] = fullHouse();
        ablescores[9] = smallStraight();
        ablescores[10] = largeStraight();
        ablescores[11] = Yacht();
    }

    public static int getTypeof(Material m) {
        if (m == Material.IRON_NUGGET) {
            return 0;
        } else if (m == Material.NETHERITE_SCRAP) {
            return 1;
        } else if (m == Material.PRISMARINE_CRYSTALS) {
            return 2;
        } else if (m == Material.BEETROOT_SEEDS) {
            return 3;
        } else if (m == Material.HONEYCOMB) {
            return 4;
        } else if (m == Material.COCOA_BEANS) {
            return 5;
        } else if (m == Material.LODESTONE) {
            return 6;
        } else if (m == Material.NETHER_GOLD_ORE) {
            return 7;
        } else if (m == Material.CAKE) {
            return 8;
        } else if (m == Material.LANTERN) {
            return 9;
        } else if (m == Material.SOUL_LANTERN) {
            return 10;
        } else if (m == Material.FIREWORK_ROCKET) {
            return 11;
        } else {
            return -1;
        }
    }

    public void updateInventory() {
        if (p1.getOpenInventory().getTitle().equals(INV_NAME)) {
            p1.closeInventory();
            p1.openInventory(getScoreBoardInv(true));
        }

        if (p2.getOpenInventory().getTitle().equals(INV_NAME)) {
            p2.closeInventory();
            p2.openInventory(getScoreBoardInv(true));
        }
    }

    public Inventory getScoreBoardInv(boolean page1) {
        InventoryManager inv = new InventoryManager(54, INV_NAME);
        updatePossible();

        boolean isp1turn = yachtManager.isp1turn;

        int startpointer = 0;
        if (!page1) startpointer = 6;

        if (page1) { // 1 ~ 6 갯수 모음 및 보너스 +35점
            int p1subtotal = 0;
            int p2subtotal = 0;

            for (int i = 0 ; i < 6; i++) { p1subtotal += p1scores[i]; }
            for (int i = 0 ; i < 6; i++) { p2subtotal += p2scores[i]; }

            inv.setitem(1, new ItemStackManager(Material.IRON_NUGGET, ChatColor.RED + "ACES " + ChatColor.GOLD + "[1의 합]").getItemStack());
            inv.setitem(2, new ItemStackManager(Material.NETHERITE_SCRAP, ChatColor.RED + "DEUCES " + ChatColor.GOLD + "[2의 합]").getItemStack());
            inv.setitem(3, new ItemStackManager(Material.PRISMARINE_CRYSTALS, ChatColor.RED + "THREES " + ChatColor.GOLD + "[3의 합]").getItemStack());
            inv.setitem(4, new ItemStackManager(Material.BEETROOT_SEEDS, ChatColor.RED + "FOURS " + ChatColor.GOLD + "[4의 합]").getItemStack());
            inv.setitem(5, new ItemStackManager(Material.HONEYCOMB, ChatColor.RED + "FIVES " + ChatColor.GOLD + "[5의 합]").getItemStack());
            inv.setitem(6, new ItemStackManager(Material.COCOA_BEANS, ChatColor.RED + "SIXES " + ChatColor.GOLD + "[6의 합]").getItemStack());
            inv.setitem(8, new ItemStackManager(Material.GOLD_INGOT, ChatColor.AQUA + "BONUS " + ChatColor.GOLD + "[SUBTOTAL의 합이 <" + ChatColor.WHITE + "63" + ChatColor.GOLD + ">이상일 시 고정 +35]").getItemStack());

            inv.setitem(26, new ItemStackManager(Material.ENCHANTED_BOOK, ChatColor.RED + "SUBTOTAL : [ " + p1subtotal + "/63 ]").getItemStack());
            inv.setitem(35, new ItemStackManager(Material.FEATHER, ChatColor.GOLD + "다음 페이지").getItemStack());
            inv.setitem(44, new ItemStackManager(Material.ENCHANTED_BOOK, ChatColor.RED + "SUBTOTAL : [ " + p2subtotal + "/63 ]").getItemStack());
        } else {
            int p1total = getScoreSum(true);
            int p2total = getScoreSum(false);

            inv.setitem(1, new ItemStackManager(Material.LODESTONE, ChatColor.RED + "CHOICE " + ChatColor.GOLD + "[모든 주사위의 합]").getItemStack());
            inv.setitem(2, new ItemStackManager(Material.NETHER_GOLD_ORE, ChatColor.RED + "4 OF A KIND " + ChatColor.GOLD + "[같은 주사위가 4개일 때 모든 주사위의 합]").getItemStack());
            inv.setitem(3, new ItemStackManager(Material.CAKE, ChatColor.RED + "FULL HOUSE " + ChatColor.GOLD + "[같은 주사위가 각각 3개, 2개일 때의 합]").getItemStack());
            inv.setitem(4, new ItemStackManager(Material.LANTERN, ChatColor.RED + "S. STRAIGHT " + ChatColor.GOLD + "[이어지는 주사위가 4개 이상일 때 +15]").getItemStack());
            inv.setitem(5, new ItemStackManager(Material.SOUL_LANTERN, ChatColor.RED + "L. STRAIGHT " + ChatColor.GOLD + "[이어지는 주사위가 5개 이상일 때 +30]").getItemStack());
            inv.setitem(6, new ItemStackManager(Material.FIREWORK_ROCKET, ChatColor.RED + "YACHT " + ChatColor.GOLD + "[같은 주사위가 5개일 때 +50]").getItemStack());
            inv.setitem(8, new ItemStackManager(Material.DIAMOND, ChatColor.AQUA + "TOTAL").getItemStack());

            inv.setitem(26, new ItemStackManager(Material.ENCHANTED_BOOK, ChatColor.RED + "[ " + p1total + " ]").getItemStack());
            inv.setitem(35, new ItemStackManager(Material.FEATHER, ChatColor.GOLD + "이전 페이지").getItemStack());
            inv.setitem(44, new ItemStackManager(Material.ENCHANTED_BOOK, ChatColor.RED + "[ " + p2total + " ]").getItemStack());
        }

        for (int i = 0; i < 6; i++) {

            int p1score = p1scores[i + startpointer];
            boolean p1hasscore = filledp1scores[i + startpointer];
            int p2score = p2scores[i + startpointer];
            boolean p2hasscore = filledp2scores[i + startpointer];

            if (isp1turn) {
                if (p1hasscore) {
                    inv.setitem(19 + i, new ItemStackManager(Material.ENCHANTED_BOOK, ChatColor.GOLD + "" + p1score + ChatColor.WHITE + "점").getItemStack());
                } else {
                    inv.setitem(19 + i, new ItemStackManager(Material.BOOK, ChatColor.AQUA + "<" + ablescores[i + startpointer] + "> 점을 기록할 수 있습니다.").getItemStack());
                }

                Material material = Material.BOOK;
                if (p2hasscore) material = Material.ENCHANTED_BOOK;

                inv.setitem(37 + i, new ItemStackManager(material, ChatColor.GOLD + "" + p2score + ChatColor.WHITE + "점").getItemStack());
            } else {
                if (p2hasscore) {
                    inv.setitem(37 + i, new ItemStackManager(Material.ENCHANTED_BOOK, ChatColor.GOLD + "" + p2score + ChatColor.WHITE + "점").getItemStack());
                } else {
                    inv.setitem(37 + i, new ItemStackManager(Material.BOOK, ChatColor.AQUA + "<" + ablescores[i + startpointer] + "> 점을 기록할 수 있습니다.").getItemStack());
                }

                Material material = Material.BOOK;
                if (p1hasscore) material = Material.ENCHANTED_BOOK;

                inv.setitem(19 + i, new ItemStackManager(material, ChatColor.GOLD + "" + p1score + ChatColor.WHITE + "점").getItemStack());
            }
        }

        inv.setitem(18, Main.getPlayerHead(p1.getName()));
        inv.setitem(36, Main.getPlayerHead(p2.getName()));

        for (int b = 9; b < 18; b++) {
            inv.setitem(b, new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE));
        }

        for (int i = 0; i <= 5; i++) {
            inv.setitem(i * 9 + 7, new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE));
        }

        for (int d = 1; d <= 5; d++) {
            inv.setitem(9 * 5 + d, new ItemStackManager(YachtManager.DICE_SKIN[d - 1], ChatColor.RED + "[ 주사위 ] " + ChatColor.AQUA + yachtManager.dicenums[d - 1]).getItemStack());
        }

        for (int i = 0; i < inv.getInventory().getSize(); i++) {
            if (inv.getInventory().getItem(i) != null) {
                if (inv.getInventory().getItem(i).getType() == Material.AIR) {
                    inv.setitem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
                }
            } else {
                inv.setitem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            }
        }

        return inv.getInventory();
    }

    public int sumofSingle(int i) {
        int count = 0;

        for (int a : yachtManager.dicenums) {
            if (a == i) {
                count++;
            }
        }

        return count * i;
    }

    public int fourCard() {
        int[] sorteddice = yachtManager.dicenums.clone();
        Arrays.sort(sorteddice);

        if ((sorteddice[0] == sorteddice[1] && sorteddice[1] == sorteddice[2] && sorteddice[2] == sorteddice[3]) ||
                (sorteddice[1] == sorteddice[2] && sorteddice[2] == sorteddice[3] && sorteddice[3] == sorteddice[4])) {
            return sumOfDices(sorteddice);
        } else {
            return 0;
        }
    }

    public int fullHouse() {
        int[] sorteddice = yachtManager.dicenums.clone();
        Arrays.sort(sorteddice);

        int sum = sumOfDices(sorteddice);

        if (sorteddice[0] == sorteddice[1]) {
            if (sorteddice[1] == sorteddice[2]) {
                if (sorteddice[3] == sorteddice[4]) {
                    return sum;
                } else {
                    return 0;
                }
            } else {
                if (sorteddice[2] == sorteddice[3] && sorteddice[3] == sorteddice[4]) {
                    return sum;
                } else {
                    return 0;
                }
            }
        } else {
            return 0;
        }
    }

    public int smallStraight() {
        ArrayList<Integer> dices = new ArrayList<>();
        for (int i : yachtManager.dicenums) {
            dices.add(i);
        }

        if ((dices.contains(1) && dices.contains(2) && dices.contains(3) && dices.contains(4)) ||
                (dices.contains(2) && dices.contains(3) && dices.contains(4) && dices.contains(5)) ||
                (dices.contains(3) && dices.contains(4) && dices.contains(5) && dices.contains(6))) {
            return 15;
        } else {
            return 0;
        }
    }

    public int largeStraight() {
        ArrayList<Integer> dices = new ArrayList<>();
        for (int i : yachtManager.dicenums) {
            dices.add(i);
        }

        if ((dices.contains(1) && dices.contains(2) && dices.contains(3) && dices.contains(4) && dices.contains(5)) ||
                (dices.contains(2) && dices.contains(3) && dices.contains(4) && dices.contains(5) && dices.contains(6))) {
            return 30;
        } else {
            return 0;
        }
    }

    public int Yacht() {
        int[] dices = yachtManager.dicenums.clone();

        if (dices[0] == dices[1] && dices[1] == dices[2] && dices[2] == dices[3] && dices[3] == dices[4]) {
            return 50;
        } else {
            return 0;
        }
    }

    public int sumOfDices(int[] dices) {
        int count = 0;
        for (int i : dices) {
            count += i;
        }
        return count;

    }

    public int getScoreSum(boolean p1) {
        int sum = 0;
        int bonus = 0;
        int[] scores;

        if (p1) scores = p1scores; else scores = p2scores;

        for (int pointer = 0; pointer < 12; pointer++) {
            if (pointer < 6) { // 0 ~ 5
                bonus += scores[pointer];
            }

            sum += scores[pointer];
        }

        if (bonus >= 63) {
            sum += 35;
        }

        return sum;
    }
}
