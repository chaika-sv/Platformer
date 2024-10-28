package gamestates;

import main.Game;
import ui.MenuButton;

import java.awt.event.MouseEvent;

import static audio.AudioPlayer.*;

public class State {

    protected Game game;

    public State(Game game) {
        this.game = game;
    }

    /**
     * Checking if mouse was clicked inside the menu button
     * @param e mouse event
     * @param mb menu button
     * @return true if clicked inside the button
     */
    public boolean isIn(MouseEvent e, MenuButton mb) {
        return mb.getBounds().contains(e.getX(), e.getY());
    }

    public Game getGame() {
        return game;
    }

    public void setGamestate(Gamestate state) {

        switch (state) {
            case PLAYING -> {
                game.getAudioPlayer().setLevelSong(game.getPlaying().getLevelManager().getLvlIndex());
            }
            case MENU -> {
                game.getAudioPlayer().playSong(MENU_1);
            }
        }

        Gamestate.state = state;

    }

}
