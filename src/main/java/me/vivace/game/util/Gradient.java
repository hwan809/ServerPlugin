package me.vivace.game.util;

import java.awt.Color;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;

public class Gradient {

    public static int MIN_VALUE = 0;
    public static int MAX_VALUE = 255;

    private Color[] source_colors;
    private Color[] mixed_colors;

    public Gradient(Color c1, Color c2, int len) {
        source_colors = new Color[2];
        mixed_colors = new Color[len];
        source_colors[0] = c1; source_colors[1] = c2;

        for (int i = 0; i < len; i++) {
            float ratio = (float) i / (float) len;
            int red = (int) (c2.getRed() * ratio + c1.getRed() * (1 - ratio));
            int green = (int) (c2.getGreen() * ratio + c1.getGreen() * (1 - ratio));
            int blue = (int) (c2.getBlue() * ratio + c1.getBlue() * (1 - ratio));

            Color tempcolor = new Color(red, green, blue);
            mixed_colors[i] = tempcolor;
        }
    }

    public Color[] getColors() {
        return mixed_colors.clone();
    }

    public Color getColor(int num) {
        if (num >= mixed_colors.length) return null;

        return mixed_colors.clone()[num];
    }

    public String stringGradient(String s) {
        String newstring = "";

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            Color c = mixed_colors[i];
            newstring += ChatColor.of(c) + Character.toString(ch);
        }

        return newstring;
    }
}
