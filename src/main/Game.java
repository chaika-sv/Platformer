package main;

import entities.Player;
import levels.LevelManager;

import java.awt.*;

public class Game implements Runnable{

    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 200;        // updates per second

    private Player player;
    private LevelManager levelManager;

    public final static int TILES_DEFAULT_SIZE = 32;
    public final static float SCALE = 1.5f;
    public final static int TILES_IN_WIDTH = 26;
    public final static int TILES_IN_HEIGHT = 14;
    public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;


    public Game() {
        initClasses();

        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();           // to listen inputs

        startGameLoop();

    }

    private void initClasses() {
        levelManager = new LevelManager(this);
        player = new Player(200, 200, (int) (64 * SCALE), (int) (40 * SCALE));
        player.loadLvlData(levelManager.getCurrentLevel().getLvlData());
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }


    @Override
    public void run() {

        // Game loop is here

        double timePerFrame = 1000000000.0 / FPS_SET;       // nanosecond / fps
        double timePerUpdate = 1000000000.0 / UPS_SET;       // nanosecond / ups
        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;      // delta update
        double deltaF = 0;      // delta frame

        while (true) {
            long currentTime = System.nanoTime();

            // deltaU will be 1.0 or more WHEN the duration since last update is equal OR more than timePerUpdate
            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if (deltaU >= 1) {
                // it's time to update
                update();
                updates++;
                deltaU--;
            }

            if (deltaF >= 1) {
                // It's time for the next frame. Let's paint it
                gamePanel.repaint();
                frames++;
                deltaF--;
            }

            // At the same time we are calculating actual FPS (how many frames we paint per second)
            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0;
                updates = 0;
            }
        }

    }


    public void update() {
        player.update();
        levelManager.update();
    }

    public void render(Graphics g) {
        levelManager.draw(g);
        player.render(g);
    }

    public Player getPlayer() {
        return player;
    }


    public void windowFocusLost() {
        player.resetDirBooleans();
    }

}
