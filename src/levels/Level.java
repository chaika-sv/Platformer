package levels;

import entities.Crabby;
import main.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utils.HelpMethods.*;

public class Level {

    private BufferedImage img;
    private int[][] lvlData;
    private ArrayList <Crabby> crabs;
    private int lvlTilesWide;       // whole level width (in tiles)
    private int maxTilesOffset;     // whole level width minus visible screen width (in tiles). Invisible part of the level
    private int maxLvlOffsetX;      // previous one in pixels
    private Point playerSpawn;

    public Level(BufferedImage img) {
        this.img = img;
        createLevelData();
        createEnemies();
        calcLevelOffset();
        calcPlayerSpwan();
    }

    private void calcPlayerSpwan() {
        playerSpawn = GetPlayerSpawn(img);
    }

    private void calcLevelOffset() {
        lvlTilesWide = img.getWidth();
        maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
        maxLvlOffsetX = Game.TILES_SIZE * maxTilesOffset;
    }

    private void createEnemies() {
        crabs = GetCrabs(img);
    }

    private void createLevelData() {
        lvlData = GetLevelData(img);
    }

    public int getSpriteIndex(int x, int y) {
        return lvlData[y][x];
    }

    public int[][] getLvlData() {
        return lvlData;
    }

    public ArrayList<Crabby> getCrabs() {
        return crabs;
    }

    public int getMaxLvlOffsetX() {
        return maxLvlOffsetX;
    }

    public Point getPlayerSpawn() {
        return playerSpawn;
    }
}
