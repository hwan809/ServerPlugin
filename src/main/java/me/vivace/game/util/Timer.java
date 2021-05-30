package me.vivace.game.util;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Timer {

    public static long hour, min, sec;

//    public static void time() {
//
//        new BukkitRunnable() {
//            @Override
//            public void run(){
//                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
//                Date source = new Date();
//                String s = f.format(source);
//
//                Date d1 = null;
//                Date d2 = null;
//                try {
//                    d1 = f.parse(s);
//                    d2 = f.parse("2021-01-07 20:00:00");
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                long diff = d1.getTime() - d2.getTime();
//                long sec = diff / 1000;
//
//                long min = sec / 60;
//                long hour = min / 60;
//                sec = sec % 60;
//                min = min % 60;
//
//                Timer.hour = Math.abs(hour);
//                Timer.min = Math.abs(min);
//                Timer.sec = Math.abs(sec);
//
//                if (hour == 0 && min == 0 && sec == 0) {
//                    Main.mabigchannel.sendMessage("타이머 종료: 마빅 보컬로이드").queue();
//                }
//
//
//                if (sec == 0) {
//                    EmbedBuilder eb = buildembed().setImage("attachment://name.jpg");
//                    Main.mabigchannel.sendMessage(eb.build()).addFile(Main.f, "name.jpg").queue();
//                }
//            }
//        }.runTaskTimer(Main.getPlugin(Main.class), 20, 20);
//
//    }

    public static EmbedBuilder buildembed() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        Date source = new Date();
        String s = f.format(source);

        EmbedBuilder avatarEmbed = new EmbedBuilder(); //Creates the embed.
        //Sets the contents of the embed
        avatarEmbed.setTitle("타이머: 마빅 보컬로이드");
        avatarEmbed.setColor(Color.RED);
        avatarEmbed.addField("현재 시간: ", s, true);
        avatarEmbed.addField("남은 시간: ", hour + "시 " + min + "분 " + sec + "초", true);
        avatarEmbed.setFooter("@마엉뽈뽈엉마마엉뽈뽈엉마@");

        return avatarEmbed;
    }
}
