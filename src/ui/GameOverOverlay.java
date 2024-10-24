package ui;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class GameOverOverlay {

    private Playing playing;
    private BufferedImage gameOverImg;
    private int imgX, imgY, imgW, imgH;

    public GameOverOverlay(Playing playing) {
        this.playing = playing;
        createImg();
    }

    private void createImg() {
        gameOverImg = LoadSave.GetSpriteAtlas(LoadSave.DEATH_SCREEN);
        imgW = (int) (gameOverImg.getWidth() * Game.SCALE);
        imgH = (int) (gameOverImg.getHeight() * Game.SCALE);
        imgX = Game.GAME_WIDTH / 2 - imgW / 2;
        imgY = (int) (100 * Game.SCALE);
    }

    public void draw(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));        // Transparent black
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        g.drawImage(gameOverImg, imgX, imgY, imgW, imgH, null);

//        g.setColor(Color.white);
//        g.drawString("Game Over", Game.GAME_WIDTH / 2, 150);
//        g.drawString("Press esc to enter Main Menu!", Game.GAME_WIDTH / 2, 300);

    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            playing.resetAll();
            Gamestate.state = Gamestate.MENU;
        }
    }

}
