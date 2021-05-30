package me.vivace.game;

import me.vivace.game.simulation.MonteCarlo;
import me.vivace.game.util.Timer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DiscordListener extends ListenerAdapter {

    public static Map<Player, String> authcode = new HashMap<>();

    @Override
    public void onReady(ReadyEvent event) {
        Main.mainchannel = event.getJDA().getTextChannelById(Main.MAINCHANNEL_ID);
        Main.mabigchannel = event.getJDA().getTextChannelById(Main.MABIGCHANNEL_ID);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {User u = event.getUser();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.BLUE);
        embed.setTitle(u.getAsTag() + "님, 환영합니다", "https://www.minecraft.net/ko-kr/");
        embed.addField("바체서버에 오신 것을 환영합니다!", "", true);
        embed.setImage("attachment://name.jpg");

        u.openPrivateChannel().queue(c -> {
            c.sendMessage(embed.build()).addFile(Main.f, "name.jpg").queue();
            c.sendMessage("인증을 위해 __!참여 <게임닉네임>__ 을 입력해주세요. (마인크래프트 연동 채널)").queue();
        });
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getAuthor().isFake() || event.isWebhookMessage()) return;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        TextChannel nowchannel = event.getChannel();
        String[] message = event.getMessage().getContentRaw().split(" ");

        if (message.length == 1 && message[0].equalsIgnoreCase("/user")){
            event.getChannel().sendMessage("명령어 형식: /user [이름]").queue(); //how to use command
        } else if(message.length == 2 && message[0].equalsIgnoreCase("/user")){
            String userName = message[1];

            try {
                User user = event.getGuild().getMemberByTag(userName).getUser(); //Gets user as object so we can grab info from it for embed
                String avatar = event.getGuild().getMemberByTag(userName).getUser().getAvatarUrl(); //gets url of user avatar so we can put in embed
                EmbedBuilder avatarEmbed = new EmbedBuilder(); //Creates the embed.
                //Sets the contents of the embed
                avatarEmbed.setTitle(userName + "님의 정보:", event.getGuild().getIconUrl());
                avatarEmbed.setColor(Color.GREEN);
                avatarEmbed.addField("NickName: ", user.getName(), true);
                avatarEmbed.addField("현재 상태: ", event.getGuild().getMemberByTag(userName).getOnlineStatus().toString(), true);
                avatarEmbed.addField("아바타: ", "아바타 멘션, " + event.getMember().getAsMention(), true);
                avatarEmbed.setImage(avatar);
                avatarEmbed.setFooter("Request made @ " + formatter.format(date), event.getGuild().getIconUrl());
                nowchannel.sendMessage(avatarEmbed.build()).queue(); //Send the embed as a message
            } catch (Exception e) {
                nowchannel.sendMessage("그런 유저가 없습니다.").queue();
            }


        } else if (message[0].equalsIgnoreCase("/monte")) {
            if (message[1].equalsIgnoreCase("redata")) {
                nowchannel.sendMessage("data를 리젠하고 있습니다..").queue();

                long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기

                Main.montedata = new float[1][1000][100000];

                for (int r = 0; r < 1000; r++) {
                    Main.montedata[0][r] = MonteCarlo.monteData(r, 100000);
                }

                long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
                long secDiffTime = (afterTime - beforeTime); //두 시간에 차 계산

                nowchannel.sendMessage("data 리젠 완료: " + secDiffTime + "ms").queue();
            } else if (message[1].equalsIgnoreCase("graph")) {
                int xstart;
                int xstop;

                float[] xaxis;
                int[] series;
                String[] seriestitle;

                boolean isr;

                String graphtitle = "MonteCarlo : ";

                if (message[3].equalsIgnoreCase("r")) {
                    isr = true;
                } else if (message[3].equalsIgnoreCase("n")) {
                    isr = false;
                } else {
                    nowchannel.sendMessage("r 또는 n이여야 합니다. r: 반지름 n: 점 개수").queue();
                    return;
                }

                if (message[4].contains("-")) {
                    try {
                        xstart = Integer.parseInt(message[4].split("-")[0]);
                        xstop = Integer.parseInt(message[4].split("-")[1]);
                    } catch (Exception e) {nowchannel.sendMessage("range 범위는 숫자여야 합니다.").queue(); return;}
                } else {
                    nowchannel.sendMessage("r 또는 n은 range를 수반해야 합니다.").queue();
                    return;
                }
                xaxis = new float[xstop - xstart + 1];

                for (int range = xstart; range < xstop + 1; range++) {
                    xaxis[range - xstart] = range;
                }

                if (message.length <= 5) {
                    nowchannel.sendMessage("데이터들을 쓰세요.").queue();
                    return;
                }
                series = new int[message.length - 5];
                seriestitle = new String[message.length - 5];

                for (int s = 5; s < message.length; s++) {
                    series[s - 5] = Integer.parseInt(message[s]);
                }

                //make graph

                final XYChart chart = new XYChartBuilder().width(1920).height(1080).build();

                //set texts

                if (isr) {
                    graphtitle += ("반지름이 " + message[4] + "일 때 " + "점 개수와 pi 값의 관계.");
                    for (int i = 0; i < series.length; i++) {
                        seriestitle[i] = "점 개수: " + series[i];
                    }
                    chart.setXAxisTitle("반지름 길이");
                    chart.setYAxisTitle("점 개수");
                } else {
                    graphtitle += ("점 갯수가 " + message[4] + "일 때 " + "반지름과 pi 값의 관계.");
                    for (int i = 0; i < series.length; i++) {
                        seriestitle[i] = "반지름 길이: " + series[i];
                    }
                    chart.setXAxisTitle("점 개수");
                    chart.setYAxisTitle("반지름 길이");
                }

                chart.setTitle(graphtitle);

                // Customize Chart
                chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
                chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);

                // Series

                float[] tempconstant = new float[2];
                float[][] ydata = new float[series.length][xaxis.length];

                tempconstant[0] = 3.141592653589793238462643384279f;
                tempconstant[1] = 3.141592653589793238462643384279f;

                for (int s = 0; s < series.length; s++) {
                    for (int x = 0; x < xaxis.length; x++) {
                        if (isr) {
                            ydata[s][x] = Main.montedata[0][(int) xaxis[x]][series[s]];
                        } else {
                            ydata[s][x] = Main.montedata[0][series[s]][(int) xaxis[x]];
                        }
                    }
                }

                chart.addSeries("pi", new float[] {xstart, xstop}, tempconstant);

                for (int count = 0; count < series.length; count++) {
                    chart.addSeries(seriestitle[count], xaxis, ydata[count]);
                }

                try {
                    BitmapEncoder.saveBitmap(chart, "./plugins/MinigameServer/montecarlo", BitmapEncoder.BitmapFormat.PNG);
                } catch (IOException e) {
                    Bukkit.broadcastMessage("error");
                }

                Main.f = new File(Main.SERVER_FOLDER_PATH + "\\plugins\\MinigameServer\\montecarlo.png");
                nowchannel.sendMessage("분석 결과입니다: ").addFile(Main.f).queue();
            }
        } else if (message[0].equalsIgnoreCase("/maung")) {
            if (nowchannel.equals(Main.mabigchannel)) {
                EmbedBuilder eb = Timer.buildembed().setImage("attachment://name.jpg");
                Main.mabigchannel.sendMessage(eb.build()).addFile(Main.f, "name.jpg").queue();
            }

//            if (nowchannel.getId().equals(Main.MABIGCHANNEL_ID)) {
//                nowchannel.sendMessage(Timer.buildembed());
//            }
        } else if (nowchannel.equals(Main.mainchannel)) {
            if (message[0].equalsIgnoreCase("!참여")) {
                User u = event.getMember().getUser();
                try {
                    Bukkit.getPlayer(message[1]);
                } catch (Exception e) {
                    u.openPrivateChannel().queue(c -> {
                        c.sendMessage("그런 유저명이 없습니다.").queue();
                    });

                    return;
                }

                Player p = Bukkit.getPlayer(message[1]);

                if (Main.authp_uuids.contains(p.getUniqueId().toString())) {
                    u.openPrivateChannel().queue(c -> {
                        c.sendMessage("이미 인증되어 있습니다.").queue();
                    });
                    return;
                }

                if (authcode.containsKey(p)) {
                    u.openPrivateChannel().queue(c -> {
                        c.sendMessage("이미 인증코드가 전송되어 있습니다.").queue();
                    });
                    return;
                }

                String s = Main.randomString(6);

                u.openPrivateChannel().queue(c -> {
                    c.sendMessage("인증코드: " + s + "\n" +
                            "인증코드를 **" + p.getName() + "** 계정으로 입력하세요.").queue();
                });

                authcode.put(p, s);

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        Bukkit.broadcastMessage("a");

                        if (!authcode.containsKey(p)) {cancel(); return;}

                        authcode.remove(p);
                        u.openPrivateChannel().flatMap(c -> c.sendMessage("인증코드가 만료되었습니다.")).queue();
                    }
                }.runTaskLater(Main.getPlugin(Main.class), 60 * 20);
            }
        } else if (message[0].equalsIgnoreCase("!maungbbol")) {

            try {
                String userName = message[1];

                Member member = event.getGuild().getMemberByTag(userName);
                assert member != null;
                member.kick().queue();
            } catch (Exception e) {}
        } else if (message[0].equalsIgnoreCase("!ssibal")) {
            if (event.getMember().getUser().getAsTag().equals("Vivace#2642")) {
                nowchannel.sendMessage("!skip").queue();
            }
        } else if(message[0].equalsIgnoreCase("!join")) {
            if (event.getMember().getUser().getAsTag().equals("Vivace#2642")) {
                VoiceChannel connectedChannel = event.getMember().getVoiceState().getChannel();
                if (connectedChannel == null) {
                    return;
                }
                // Gets the audio manager.
                AudioManager audioManager = event.getGuild().getAudioManager();
                audioManager.openAudioConnection(connectedChannel);
            }
        }
    }
}
