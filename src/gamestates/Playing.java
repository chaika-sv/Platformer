package gamestates;

import entities.Player;
import levels.LevelManager;
import main.Game;
import ui.PauseOverLay;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Playing extends State implements Statemethods{

    private Player player;
    private LevelManager levelManager;
    private boolean paused = false;
    private PauseOverLay pauseOverLay;

    private int xLvlOffset;
    private int leftBorder = (int) (0.2 * Game.GAME_WIDTH);     // left 20% of the visible screen (in pixels). when we reach it we need to move screen
    private int rightBorder = (int) (0.8 * Game.GAME_WIDTH);    // right 20% of the visible screen (in pixels). when we reach it we need to move screen
    private int lvlTilesWide = LoadSave.GetLevelData()[0].length;       // whole level width (in tiles)
    private int maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;    // whole level width minus visible screen width (in tiles). Invisible part of the level
    private int maxLvlOffsetX = maxTilesOffset * Game.TILES_SIZE;       // previous one in pixels

    public Playing(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        levelManager = new LevelManager(game);
        player = new Player(200, 200, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE));
        player.loadLvlData(levelManager.getCurrentLevel().getLvlData());
        pauseOverLay = new PauseOverLay(this);
    }


    public void unpausedGame() {
        paused = false;
    }

    @Override
    public void update() {
        if (!paused) {
            levelManager.update();
            player.update();
            checkCloseToBorder();
        } else {
            pauseOverLay.update();
        }
    }

    /**
     * Checking if we are close to border then we need to set some xLvlOffset to modify x coords of player and level manager
     * Example 1:
     *         pX = 85
     *         offset = 0
     *         rightBorder = 80
     *         The player is out the border so we need to add some offset
     *
     *         diff = 85 - 0 = 85
     *
     *         if (85 - 80)     offset += 85 - 80      ---> offset += 5
     *
     * Example 2:
     *         pX = 85
     *         offset = 5
     *         rightBorder = 80
     *         The player is out the border, but there is already some offset so need to add more offset
     *
     *         diff = 85 - 5 = 80
     *
     *         if (80 - 80)     ---> nothing to do with offset
     *
     * Example 3:
     *         pX = 30
     *         offset = 15
     *         leftBorder = 20
     *         The player is NOT out the border, but we have some offset so we need to correct it
     *
     *         diff = 30 - 15 = 15
     *
     *         if (15 - 20)     offset += 15 - 20      ---> offset -= 5
     */
    private void checkCloseToBorder() {

        int playerX = (int) player.getHitbox().x;
        int diff = playerX - xLvlOffset;

        // Checking screen borders (if we need to move it)
        if (diff > rightBorder)
            xLvlOffset += diff - rightBorder;
        else if (diff < leftBorder)
            xLvlOffset += diff - leftBorder;

        // Checking the level's borders
        if (xLvlOffset > maxLvlOffsetX)
            xLvlOffset = maxLvlOffsetX;
        else if (xLvlOffset < 0)
            xLvlOffset = 0;

    }

    @Override
    public void draw(Graphics g) {
        levelManager.draw(g, xLvlOffset);
        player.render(g, xLvlOffset);

        if (paused)
            pauseOverLay.draw(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1)
            player.setAttacking(true);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (paused)
            pauseOverLay.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (paused)
            pauseOverLay.mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (paused)
            pauseOverLay.mouseMoved(e);
    }

    public void mouseDragged(MouseEvent e) {
        if (paused)
            pauseOverLay.mouseDragged(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                player.setLeft(true);
                break;
            case KeyEvent.VK_D:
                player.setRight(true);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(true);
                break;
            case KeyEvent.VK_ESCAPE:
                paused = !paused;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                player.setLeft(false);
                break;
            case KeyEvent.VK_D:
                player.setRight(false);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(false);
                break;
        }
    }


    public Player getPlayer() {
        return player;
    }

    public void windowFocusLost() {
        player.resetDirBooleans();
    }
}
