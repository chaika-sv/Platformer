package entities;

import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utils.Constants.PlayerConstants.*;

public class Player extends Entity{

    private BufferedImage[][] animations;
    private int animTick, animIndex, animSpeed = 15;
    private int playerAction = ATTACK_1;
    private boolean left, right, up, down;
    private boolean moving = false, attacking = false;
    private float playerSpeed = 2.0f;
    private int lvlData[][];
    private float xDrawOffset = 21 * Game.SCALE;        // 21px - offset from tile border to actual player position
    private float yDrawOffset = 4 * Game.SCALE;         // 4px - offset from tile border to actual player position


    public Player(float x, float y, int width, int height) {
        super(x, y, width, height);
        loadAnimations();
        initHitbox(x, y, 20*Game.SCALE, 28*Game.SCALE);     // 20px x 28px - actual size of the player
    }

    /**
     * Update player's position and animation
     */
    public void update() {
        updatePosition();
        updateAnimationTick();
        setAnimation();
    }

    /**
     * Draw player depending on selected action (row on sprite) and current animation index (column on sprite)
     */
    public void render(Graphics g) {
        // Player image has a size of a tile so it's a normal rectangle
        // Hitbox is smaller than the image, so when we draw player itself we need to set some offsets
        g.drawImage(animations[playerAction][animIndex], (int)(hitbox.x - xDrawOffset), (int)(hitbox.y - yDrawOffset), width, height,null);
        drawHitbox(g);
    }

    /**
     * Load animation images to animations array
     */
    private void loadAnimations() {

        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

        animations = new BufferedImage[9][6];

        for (int j = 0; j < animations.length; j++) {
            for (int i = 0; i < animations[j].length; i++) {
                animations[j][i] = img.getSubimage(i*64, j*40, 64, 40);
            }

        }

    }

    /**
     * Load current level data for the player
     * It's not good, but ok for now
     */
    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
    }

    /**
     * Change x and y when moving left, right, up and down
     */
    private void updatePosition() {

        moving = false;
        if (!left && !right && !up && !down)
            return;

        float xSpeed = 0, ySpeed = 0;

        if (left && !right)
            xSpeed = -playerSpeed;
        else if (right && !left)
            xSpeed = playerSpeed;

        if (up && !down)
            ySpeed = -playerSpeed;
        else if (down && !up)
            ySpeed = playerSpeed;

//        if (utils.HelpMethods.CanMoveHere(x+xSpeed, y+ySpeed, width, height, lvlData)) {
//            this.x += xSpeed;
//            this.y += ySpeed;
//            moving = true;
//        }

        if (utils.HelpMethods.CanMoveHere(hitbox.x + xSpeed, hitbox.y + ySpeed, hitbox.width, hitbox.height, lvlData)) {
            hitbox.x += xSpeed;
            hitbox.y += ySpeed;
            moving = true;
        }


    }

    /**
     * Increment animTick and when we reach animSpeed then increment animIndex
     * We use animIndex to display next animation sprite
     */
    private void updateAnimationTick() {
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animIndex++;
            if (animIndex >= GetSpritesAmount(playerAction)) {
                animIndex = 0;
                attacking = false;
            }
        }
    }

    /**
     * Depending on booleans (moving, attacking, ...) set playerAction (RUNNING, IDLE, ATTACK_1)
     */
    private void setAnimation() {
        
        int startAni = playerAction;
        
        if (moving)
            playerAction = RUNNING;
        else
            playerAction = IDLE;

        if (attacking)
            playerAction = ATTACK_1;

        // In case we have new animation (i.e. another button was pressed) we need to reset previous animation
        if (startAni != playerAction)
            resetAnyTick();
        
    }

    /**
     * Reset current animation
     */
    private void resetAnyTick() {
        animTick = 0;
        animIndex = 0;
    }

    /**
     * Reset direction booleans up, down, left or right
     */
    public void resetDirBooleans() {
        left = false;
        up = false;
        right = false;
        down = false;
    }



    public void setAttacking(boolean attacking ) {
        this.attacking = attacking;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }
}
