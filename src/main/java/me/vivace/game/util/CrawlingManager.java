package me.vivace.game.util;

import me.vivace.game.Main;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.scheduler.BukkitRunnable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CrawlingManager {

    public static String[] naverCrawling(int count) {

        Document doc = null;
        Elements ranks = null;

        try {
            doc = Jsoup.connect("https://datalab.naver.com/keyword/realtimeList.naver?").get();
            ranks = doc.select(".ranking_item");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String[] rankingarray = new String[count];

        for (Element rank : ranks) {

            Elements item_box = rank.select(".item_box");
            Elements item_title_wrap = item_box.select(".item_title_wrap");

            int ranknum = Integer.parseInt(item_box.select(".item_num").text());
            String rankedtext = item_title_wrap
                                .select(".item_title")
                                .html();

            if (ranknum > count) continue;

            rankingarray[ranknum - 1] = rankedtext;
        }

        return rankingarray;
    }

    public static void naverDisplay() {

        new BukkitRunnable() {

            @Override
            public void run() {
                SimpleDateFormat format = new SimpleDateFormat ( "HH:mm:ss");
                Date time = new Date();
                String nowtime = format.format(time);

                String[] s = CrawlingManager.naverCrawling(20);

                Bukkit.broadcastMessage(time + "에 측정한 네이버 실시간 검색어 -");

                for (int i = 0; i < s.length; i++) {
                    String text = s[i];
                    Bukkit.broadcastMessage((i + 1) + "위: " + text);
                }

            }

        }.runTaskTimer(Main.getPlugin(Main.class), 0, 20 * 60); // 20 ticks = 1 sec

    }
}
