package levels;

import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static main.Game.TILES_SIZE;

public class LevelManager {

    private Game game;
    private BufferedImage[] levelSprite;
    private Level levelOne;

    public LevelManager(Game game) {
        this.game = game;
        importOutsideSprites();
        levelOne = new Level(LoadSave.GetLevelData());

    }

    /**
     * The sprite includes blocks for level building. There are 48 blocks on 12x4 grid.
     * We save them into the levelSprite array.
     */
    private void importOutsideSprites() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        levelSprite = new BufferedImage[48];        // 12 width * 4 height
        for (int j = 0; j < 4; j++)
            for (int i = 0; i < 12; i++) {
                int index = j*12 + i;
                levelSprite[index] = img.getSubimage(i*32, j*32, 32, 32);
            }
    }

    /**
     * Draw current level using building blocks from levelSprite and based on data from levelOne
     */
    public void draw(Graphics g, int xLvlOffset) {

        for (int j = 0; j < Game.TILES_IN_HEIGHT; j++)
            for (int i = 0; i < levelOne.getLvlData()[0].length; i++) {
                int index = levelOne.getSpriteIndex(i, j);
                g.drawImage(levelSprite[index], i*TILES_SIZE - xLvlOffset, j*TILES_SIZE, TILES_SIZE, TILES_SIZE, null);
            }    
        
    }

    public void update() {

    }

    public Level getCurrentLevel() {
        return levelOne;
    }

}
