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
    private SoundButton musicButton, sfxButton;
    private UrmButton menuButton, replayButton, unpauseButton;
    private Playing playing;

    public PauseOverLay(Playing playing) {
        this.playing = playing;
        loadBackground();
        createSoundButtons();
        createUrmButtons();
    }

    private void createUrmButtons() {
        // Unpause, replay, menu buttons
        int menuX = (int) (313 * Game.SCALE);
        int replayX = (int) (387 * Game.SCALE);
        int unpauseX = (int) (462 * Game.SCALE);
        int urmY = (int) (325 * Game.SCALE);

        menuButton = new UrmButton(UrmType.MENU, menuX, urmY, URM_SIZE, URM_SIZE);
        replayButton = new UrmButton(UrmType.REPLAY, replayX, urmY, URM_SIZE, URM_SIZE);
        unpauseButton = new UrmButton(UrmType.UNPAUSE, unpauseX, urmY, URM_SIZE, URM_SIZE);
    }

    private void createSoundButtons() {
        // Sound, music buttons
        int soundX = (int) (450 * Game.SCALE);
        int musicY = (int) (140 * Game.SCALE);
        int sfxY = (int) (186 * Game.SCALE);

        musicButton = new SoundButton(soundX, musicY, SOUND_SIZE, SOUND_SIZE);
        sfxButton = new SoundButton(soundX, sfxY, SOUND_SIZE, SOUND_SIZE);

    }

    private void loadBackground() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACKGROUND);
        bgW = (int) (backgroundImg.getWidth() * Game.SCALE);
        bgH = (int) (backgroundImg.getHeight() * Game.SCALE);
        bgX = Game.GAME_WIDTH / 2 - bgW / 2;
        bgY = (int ) (25 * Game.SCALE);
    }

    public void update() {
        musicButton.update();
        sfxButton.update();

        unpauseButton.update();
        replayButton.update();
        menuButton.update();
    }

    public void draw(Graphics g) {
        // Background
        g.drawImage(backgroundImg, bgX, bgY, bgW, bgH, null);

        // Sounds buttons
        musicButton.draw(g);
        sfxButton.draw(g);

        // Urm buttons
        unpauseButton.draw(g);
        replayButton.draw(g);
        menuButton.draw(g);
    }



    public void mouseDragged(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        if (isIn(e, musicButton))
            musicButton.setMousePressed(true);
        else if (isIn(e, sfxButton))
            sfxButton.setMousePressed(true);
        else if (isIn(e, unpauseButton))
            unpauseButton.setMousePressed(true);
        else if (isIn(e, replayButton))
            replayButton.setMousePressed(true);
        else if (isIn(e, menuButton))
            menuButton.setMousePressed(true);
    }

    public void mouseReleased(MouseEvent e) {
        if (isIn(e, musicButton)) {
            if (musicButton.isMousePressed()) {
                musicButton.setMuted(!musicButton.isMuted());       // flip it: set muted if it's not muted and vice versa
            }
        } else if (isIn(e, sfxButton)) {
            if (sfxButton.isMousePressed()) {
                sfxButton.setMuted(!sfxButton.isMuted());       // flip it: set muted if it's not muted and vice versa
            }
        } else if (isIn(e, menuButton)) {
            if (menuButton.isMousePressed()) {
                Gamestate.state = Gamestate.MENU;
                playing.unpausedGame();
            }
        } else if (isIn(e, replayButton)) {
            if (replayButton.isMousePressed()) {
                System.out.println("REPLAY LEVEL");
            }
        } else if (isIn(e, unpauseButton)) {
            if (unpauseButton.isMousePressed()) {
                playing.unpausedGame();
            }
        }

        musicButton.resetBools();
        sfxButton.resetBools();

        unpauseButton.resetBools();
        replayButton.resetBools();
        menuButton.resetBools();
    }

    public void mouseMoved(MouseEvent e) {
        // Reset first
        musicButton.setMouseOver(false);
        sfxButton.setMouseOver(false);
        unpauseButton.setMouseOver(false);
        replayButton.setMouseOver(false);
        menuButton.setMouseOver(false);

        if (isIn(e, musicButton))
            musicButton.setMouseOver(true);
        else if (isIn(e, sfxButton))
            sfxButton.setMouseOver(true);
        else if (isIn(e, unpauseButton))
            unpauseButton.setMouseOver(true);
        else if (isIn(e, replayButton))
            replayButton.setMouseOver(true);
        else if (isIn(e, menuButton))
            menuButton.setMouseOver(true);
    }

    private boolean isIn(MouseEvent e, PauseButton b) {
        return b.getBounds().contains(e.getX(), e.getY());
    }

}
