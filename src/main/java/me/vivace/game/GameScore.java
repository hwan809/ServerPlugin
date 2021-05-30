package me.vivace.game;

public class GameScore {

    private int gamecode;
    private String playername;
    int score = Integer.MIN_VALUE;

    public GameScore(int gamecode, String playername, int score) {
        this.gamecode = gamecode;
        this.playername = playername;
        this.score = score;
    }

    public String getPlayername() {
        return playername;
    }

    public int getScore() {
        return score;
    }
}
