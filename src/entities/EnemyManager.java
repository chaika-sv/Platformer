package entities;

import gamestates.Playing;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utils.Constants.DEBUG_MODE;
import static utils.Constants.EnemyConstants.*;

public class EnemyManager {

    Playing playing;
    private BufferedImage[][] crabbyArr;
    private ArrayList<Crabby> crabbies = new ArrayList<>();

    public EnemyManager(Playing playing) {
        this.playing = playing;
        loadEnemyImgs();
        addEnemies();
    }

    private void addEnemies() {
        crabbies = LoadSave.GetCrabs();
        System.out.println("Size of crabs: " + crabbies.size());
    }

    private void loadEnemyImgs() {
        crabbyArr = new BufferedImage[5][9];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CRABBY_SPRITE);

        for (int j = 0; j < crabbyArr.length; j++)
            for (int i = 0; i < crabbyArr[j].length; i++)
                crabbyArr[j][i] = temp.getSubimage(i * CRABBY_WIDTH_DEFAULT, j * CRABBY_HEIGHT_DEFAULT, CRABBY_WIDTH_DEFAULT, CRABBY_HEIGHT_DEFAULT);

    }

    public void update(int[][] lvlData) {
        for(Crabby c : crabbies)
            if (c.isActive())
                c.update(lvlData, playing.getPlayer());
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawCrabs(g, xLvlOffset);
    }

    private void drawCrabs(Graphics g, int xLvlOffset) {
        for(Crabby c : crabbies) {
            if (c.isActive()) {
                g.drawImage(crabbyArr[c.getEnemyState()][c.getAniIndex()],
                        (int) c.getHitbox().x - xLvlOffset - CRABBY_DRAWOFFSET_X + c.flipX(),
                        (int) c.getHitbox().y - CRABBY_DRAWOFFSET_Y,
                        CRABBY_WIDTH * c.flipW(),
                        CRABBY_HEIGHT,
                        null);
                if (DEBUG_MODE) {
                    c.drawHitbox(g, xLvlOffset);
                    c.drawAttackBox(g, xLvlOffset);
                }
            }
        }
    }

    /**
     * Checking if one of the enemies was hit by the player. If it was then hurt the enemy
     * @param attackBox player's attack box
     */
    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        for(Crabby c : crabbies)
            if (c.isActive()) {
                // Check if player's attack box intersects enemy's hitbox
                if (attackBox.intersects(c.getHitbox())) {
                    c.hurt(10);
                    return;
                }
            }
    }

    public void resetAllEnemies() {
        for(Crabby c : crabbies)
            c.resetEnemy();
    }
}
