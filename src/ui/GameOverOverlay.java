package ui;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.PauseButtons.URM_SIZE;

public class GameOverOverlay {

    private Playing playing;
    private BufferedImage gameOverImg;
    private int imgX, imgY, imgW, imgH;
    private UrmButton menu, play;

    public GameOverOverlay(Playing playing) {
        this.playing = playing;
        createImg();
        createButtons();
    }

    private void createImg() {
        gameOverImg = LoadSave.GetSpriteAtlas(LoadSave.DEATH_SCREEN);
        imgW = (int) (gameOverImg.getWidth() * Game.SCALE);
        imgH = (int) (gameOverImg.getHeight() * Game.SCALE);
        imgX = Game.GAME_WIDTH / 2 - imgW / 2;
        imgY = (int) (100 * Game.SCALE);
    }

    private void createButtons() {
        int menuX = (int) (335 * Game.SCALE);
        int playX = (int) (440 * Game.SCALE);
        int y = (int) (195 * Game.SCALE);
        play = new UrmButton(UrmType.UNPAUSE, playX, y, URM_SIZE, URM_SIZE);
        menu = new UrmButton(UrmType.MENU, menuX, y, URM_SIZE, URM_SIZE);
    }

    public void draw(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));        // Transparent black
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        g.drawImage(gameOverImg, imgX, imgY, imgW, imgH, null);

        menu.draw(g);
        play.draw(g);

//        g.setColor(Color.white);
//        g.drawString("Game Over", Game.GAME_WIDTH / 2, 150);
//        g.drawString("Press esc to enter Main Menu!", Game.GAME_WIDTH / 2, 300);

    }

    public void update() {
        menu.update();
        play.update();
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            playing.resetAll();
            Gamestate.state = Gamestate.MENU;
        }
    }


    private boolean isIn(MouseEvent e, UrmButton b) {
        return b.getBounds().contains(e.getX(), e.getY());
    }

    public void mouseMoved(MouseEvent e) {
        // Reset first
        play.setMouseOver(false);
        menu.setMouseOver(false);

        if (isIn(e, play))
            play.setMouseOver(true);
        else if (isIn(e, menu))
            menu.setMouseOver(true);
    }

    public void mouseRelease(MouseEvent e) {
        if (isIn(e, menu)) {
            if (menu.isMousePressed()) {
                playing.resetAll();
                Gamestate.state = Gamestate.MENU;
            }
        } else if (isIn(e, play)) {
            if (play.isMousePressed()) {
                playing.resetAll();
            }
        }

        menu.resetBools();
        play.resetBools();
    }

    public void mousePressed(MouseEvent e) {
        if (isIn(e, play))
            play.setMousePressed(true);
        else if (isIn(e, menu))
            menu.setMousePressed(true);
    }


}
