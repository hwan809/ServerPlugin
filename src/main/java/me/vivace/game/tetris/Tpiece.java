package me.vivace.game.tetris;

import org.bukkit.Material;

import java.util.Random;

public class Tpiece {

    //board_locs[블록 타입 (0 ~ 6)][블록 회전 (0 ~ 3)][블록 개수][0 : y, 1 : x]
    //game pointer = 19, 5

    final static int[][][][] board_locs = {

            //I piece
            {
                    {{0, -2}, {0, -1}, {0, 0}, {0, 1}},
                    {{-2, 0}, {-1, 0}, {0, 0}, {1, 0}},
                    {{-1, -2}, {-1, -1}, {-1, 0}, {-1, 1}},
                    {{-2, -1}, {-1, -1}, {0, -1}, {1, -1}}
            },

            //L piece
            {
                    {{0, 0}, {-1, -2}, {-1, -1}, {-1, 0}},
                    {{0, -1}, {-1, -1}, {-2, -1}, {-2, 0}},
                    {{-1, -2}, {-1, -1}, {-1, 0}, {-2, -2}},
                    {{0, -1}, {-1, -1}, {-2, -1}, {0, -2}}
            },

            //J piece
            {
                    {{0, -2}, {-1, -2}, {-1, -1}, {-1, 0}},
                    {{0, -1}, {-1, -1}, {-2, -1}, {0, 0}},
                    {{-2, 0}, {-1, -2}, {-1, -1}, {-1, 0}},
                    {{0, -1}, {-1, -1}, {-2, -1}, {-2, -2}}
            },

            //S piece
            {
                    {{0, -1}, {0, 0}, {-1, -2}, {-1, -1}},
                    {{0, -1}, {-1, -1}, {-1, 0}, {-2, 0}},
                    {{-1, -1}, {-1, 0}, {-2, -2}, {-2, -1}},
                    {{0, -2}, {-1, -2}, {-1, -1}, {-2, -1}}
            },

            //Z piece
            {
                    {{0, -2}, {0, -1}, {-1, -1}, {-1, 0}},
                    {{0, 0}, {-1, -1}, {-1, 0}, {-2, -1}},
                    {{-1, -2}, {-1, -1}, {-2, -1}, {-2, 0}},
                    {{0, -1}, {-1, -2}, {-1, -1}, {-2, -2}}
            },

            //T piece
            {
                    {{0, -1}, {-1, -2}, {-1, -1}, {-1, 0}},
                    {{0, -1}, {-2, -1}, {-1, -1}, {-1, 0}},
                    {{-2, -1}, {-1, -2}, {-1, -1}, {-1, 0}},
                    {{0, -1}, {-1, -2}, {-1, -1}, {-2, -1}}
            },

            //O piece
            {
                    {{0, -1}, {0, 0}, {-1, -1}, {-1, 0}},
                    {{0, -1}, {0, 0}, {-1, -1}, {-1, 0}},
                    {{0, -1}, {0, 0}, {-1, -1}, {-1, 0}},
                    {{0, -1}, {0, 0}, {-1, -1}, {-1, 0}}
            }};

    final static Material[][] block_skins = {
            {
                    Material.BLUE_GLAZED_TERRACOTTA,
                    Material.ORANGE_GLAZED_TERRACOTTA,
                    Material.PURPLE_GLAZED_TERRACOTTA,
                    Material.GREEN_GLAZED_TERRACOTTA,
                    Material.RED_GLAZED_TERRACOTTA,
                    Material.PINK_GLAZED_TERRACOTTA,
                    Material.YELLOW_GLAZED_TERRACOTTA,
                    Material.BLACK_GLAZED_TERRACOTTA
            },

            {
                    Material.BLUE_TERRACOTTA,
                    Material.ORANGE_TERRACOTTA,
                    Material.PURPLE_TERRACOTTA,
                    Material.GREEN_TERRACOTTA,
                    Material.RED_TERRACOTTA,
                    Material.PINK_TERRACOTTA,
                    Material.YELLOW_TERRACOTTA,
                    Material.BLACK_TERRACOTTA
            },

            {
                    Material.BLUE_WOOL,
                    Material.ORANGE_WOOL,
                    Material.PURPLE_WOOL,
                    Material.GREEN_WOOL,
                    Material.RED_WOOL,
                    Material.PINK_WOOL,
                    Material.YELLOW_WOOL,
                    Material.BLACK_WOOL
            }
    };

    //board_loc[블록 개수][0 : y, 1 : x]
    private int[][] board_loc;

    private int blocktype;
    private int rotation;

    Material block_color;

    private Board b;

    int skin;

    public Tpiece(Board b, boolean withrotation) {
        this.board_loc = get_random_piece(withrotation);
        this.block_color = block_skins[b.skinnum][this.blocktype];
        this.skin = b.skinnum;
        this.b = b;
        this.rotation = 0;
    }

    public void remove_piece() {
        for (int[] xy : this.board_loc.clone()) {

            int xx = xy.clone()[0] += b.pointer[0];
            int yy = xy.clone()[1] += b.pointer[1];

            this.b.edit_block(xx, yy, Material.AIR);
        }

        this.b.updateBoard();
    }

    public void summon_piece() {

        for (int[] xy : this.board_loc.clone()) {

            int xx = xy.clone()[0] += b.pointer[0];
            int yy = xy.clone()[1] += b.pointer[1];

            this.b.edit_block(xx, yy, this.block_color);
        }

        this.b.updateBoard();
    }

    public boolean rotate() {
        int newrotation = 0;
        Material[][] gameboard = this.b.getGameboard();

        if (this.rotation != 3) {
            newrotation = this.rotation + 1;
        }

        int[][] newlocs = board_locs[this.blocktype][newrotation];

        for (int[] newblock : newlocs) {
            int x = newblock[0] + this.b.pointer[0];
            int y = newblock[1] + this.b.pointer[1];

            if (x > 19 || x < 0 ||
                    y > 9 || y < 0) {
                return false;
            }

            boolean isair = gameboard[x][y] == Material.AIR;
            boolean isselfblock = false;

            for (int[] nowblock : this.board_loc) {
                int xx = nowblock[0] + this.b.pointer[0];
                int yy = nowblock[1] + this.b.pointer[1];

                if (x == xx && y == yy) {
                    isselfblock = true;
                }
            }
            if (!isair && !isselfblock) {
                return false;
            }
        }

        this.remove_piece();
        this.board_loc = newlocs;
        this.summon_piece();
        this.b.updateBoard();
        this.rotation = newrotation;

        return true;
    }

    public int[][] get_random_piece(boolean withrotation) {
        Random r = new Random();
        int i = r.nextInt(7);
        this.blocktype = i;

        if (withrotation) {
            return board_locs[i][r.nextInt(3)];
        } else {
            return board_locs[i][0];
        }
    }

    public int[] getblock(int i) {
        return this.board_loc[i].clone();
    }

    public int getBlockType() {
        return this.blocktype;
    }
}
