package me.vivace.game;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;

public class GameStats {

    String gamename;
    GameScore[] scores;

    public GameStats(String gamename, GameScore[] scores) {
        this.gamename = gamename;
        this.scores = scores;

        sort();
    }

    public void sort() {
        GameScore[] tempscores = new GameScore[this.getScores().length];

        int len = this.getScores().length;

        for (int i = 0; i < len; i++) {

            GameScore maxscore = null;
            int max = Integer.MIN_VALUE;

            for (GameScore s : this.getScores()) {
                if (max < s.score) {
                    maxscore = s;
                    max = s.score;
                }
            }

            tempscores[i] = maxscore;
            this.scores = (GameScore[]) ArrayUtils.removeElement(this.getScores(), maxscore);
        }

        scores = tempscores;
    }

    public GameScore[] getLeaderBoard(int amount) {
        if (amount < 1) return null;

        return Arrays.copyOfRange(getScores(), 0, amount);
    }

    public GameScore[] getScores() {
        return scores;
    }
}
