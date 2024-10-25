package ui;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import static utils.Constants.UI.PauseButtons.*;

public class PauseOverLay {

    private BufferedImage backgroundImg;
    private int bgX, bgY, bgW, bgH;
    private UrmButton menuButton, replayButton, unpauseButton;
    private Playing playing;
    private AudioOptions audioOptions;

    public PauseOverLay(Playing playing) {
        this.playing = playing;
        loadBackground();
        audioOptions = playing.getGame().getAudioOptions();

        createUrmButtons();
    }

    private void createUrmButtons() {
        // Unpause, replay, menu buttons - on the same line
        int menuX = (int) (313 * Game.SCALE);
        int replayX = (int) (387 * Game.SCALE);
        int unpauseX = (int) (462 * Game.SCALE);
        int urmY = (int) (325 * Game.SCALE);

        menuButton = new UrmButton(UrmType.MENU, menuX, urmY, URM_SIZE, URM_SIZE);
        replayButton = new UrmButton(UrmType.REPLAY, replayX, urmY, URM_SIZE, URM_SIZE);
        unpauseButton = new UrmButton(UrmType.UNPAUSE, unpauseX, urmY, URM_SIZE, URM_SIZE);
    }

    private void loadBackground() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACKGROUND);
        bgW = (int) (backgroundImg.getWidth() * Game.SCALE);
        bgH = (int) (backgroundImg.getHeight() * Game.SCALE);
        bgX = Game.GAME_WIDTH / 2 - bgW / 2;
        bgY = (int ) (25 * Game.SCALE);
    }

    public void update() {

        unpauseButton.update();
        replayButton.update();
        menuButton.update();
        audioOptions.update();

    }

    public void draw(Graphics g) {
        // Background
        g.drawImage(backgroundImg, bgX, bgY, bgW, bgH, null);

        // Urm buttons
        unpauseButton.draw(g);
        replayButton.draw(g);
        menuButton.draw(g);

        // Volume, sfx and sounds controls
        audioOptions.draw(g);
    }



    public void mouseDragged(MouseEvent e) {
        audioOptions.mouseDragged(e);
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        if (isIn(e, unpauseButton))
            unpauseButton.setMousePressed(true);
        else if (isIn(e, replayButton))
            replayButton.setMousePressed(true);
        else if (isIn(e, menuButton))
            menuButton.setMousePressed(true);
        else
            audioOptions.mousePressed(e);
    }

    public void mouseReleased(MouseEvent e) {
        if (isIn(e, menuButton)) {
            if (menuButton.isMousePressed()) {
                Gamestate.state = Gamestate.MENU;
                playing.unpausedGame();
            }
        } else if (isIn(e, replayButton)) {
            if (replayButton.isMousePressed()) {
                playing.resetAll();
                playing.unpausedGame();
            }
        } else if (isIn(e, unpauseButton)) {
            if (unpauseButton.isMousePressed()) {
                playing.unpausedGame();
            }
        } else
            audioOptions.mouseReleased(e);

        unpauseButton.resetBools();
        replayButton.resetBools();
        menuButton.resetBools();
    }

    public void mouseMoved(MouseEvent e) {
        // Reset first
        unpauseButton.setMouseOver(false);
        replayButton.setMouseOver(false);
        menuButton.setMouseOver(false);

        if (isIn(e, unpauseButton))
            unpauseButton.setMouseOver(true);
        else if (isIn(e, replayButton))
            replayButton.setMouseOver(true);
        else if (isIn(e, menuButton))
            menuButton.setMouseOver(true);
        else
            audioOptions.mouseMoved(e);
    }

    private boolean isIn(MouseEvent e, PauseButton b) {
        return b.getBounds().contains(e.getX(), e.getY());
    }

}
