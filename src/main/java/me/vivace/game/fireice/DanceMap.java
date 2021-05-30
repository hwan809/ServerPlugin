package me.vivace.game.fireice;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;

public class DanceMap {
    private int BPM = 120;

    public Block ball_start_block;
    private Block start_block;
    private Block end_block;
    public ArrayList<Block> all_mapblocks = new ArrayList<>();

    public DanceMap(Block ballstartloc, Block start_loc, Block end_loc, int BPM) {
        this.ball_start_block = ballstartloc;
        this.start_block = start_loc;
        this.end_block = end_loc;
        this.BPM = BPM;

        this.all_mapblocks.add(this.ball_start_block);
        setMap(Material.STONE);
    }

    public void setMap(Material m) {
        Block nowblock = this.ball_start_block;
        boolean hasnextblock = true;
        while (hasnextblock) {
            Block[] nextblocks = nextBlocks(nowblock);

            for (int i = 0; i < 4; i++) { //4방향 블록
                if (nextblocks[i].getType() == m) {
                    if (!this.all_mapblocks.contains(nextblocks[i])) {
                        nowblock = nextblocks[i];
                        this.all_mapblocks.add(nowblock);
                        hasnextblock = true;

                        break;
                    }
                } else {
                    hasnextblock = false;
                }
            }
        }
    }

    public Block[] nextBlocks(Block b) {
        Block t1 = b.getLocation().clone().add(0, 0, 1).getBlock();
        Block t2 = b.getLocation().clone().add(1, 0, 0).getBlock();
        Block t3 = b.getLocation().clone().add(0, 0, -1).getBlock();
        Block t4 = b.getLocation().clone().add(-1, 0, 0).getBlock();

        return new Block[] {t1, t2, t3, t4};
    }
}
