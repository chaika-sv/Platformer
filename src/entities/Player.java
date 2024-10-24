package entities;

import gamestates.Playing;
import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utils.Constants.*;
import static utils.Constants.PlayerConstants.*;
import static utils.HelpMethods.*;

public class Player extends Entity{

    private Playing playing;

    private BufferedImage[][] animations;
    private boolean left, right, jump;
    private boolean moving = false, attacking = false;
    private int lvlData[][];
    private float xDrawOffset = 21 * Game.SCALE;        // 21px - offset from tile border to actual player position
    private float yDrawOffset = 4 * Game.SCALE;         // 4px - offset from tile border to actual player position

    // Jumping / Gravity
    private float jumpSpeed = -2.25f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;

    // Status bar
    private BufferedImage statusBarImg;

    private int statusBarWidth = (int) (192 * Game.SCALE);
    private int statusBarHeight = (int) (58 * Game.SCALE);
    private int statusBarX = (int) (10 * Game.SCALE);       // Actual pos of status bar on the sprite
    private int statusBarY = (int) (10 * Game.SCALE);       // Actual pos of status bar on the sprite

    private int healthBarWidth = (int) (150 * Game.SCALE);
    private int healthBarHeight = (int) (4 * Game.SCALE);
    private int healthBarXStart = (int) (34 * Game.SCALE);
    private int healthBarYStart = (int) (14 * Game.SCALE);

    private int healthWidth = healthBarWidth;

    private int flipX = 0;
    private int flipW = 1;
    private boolean attackChecked;

    private int tileY = 0;


    public Player(float x, float y, int width, int height, Playing playing) {
        super(x, y, width, height);

        this.playing = playing;
        this.state = IDLE;
        this.maxHealth = 100;
        this.currentHealth = maxHealth;
        this.walkSpeed = 1.0f * Game.SCALE;

        loadAnimations();
        initHitbox(20, 27);     // 20px x 27px - actual size of the player
        initAttackBox();
    }

    public void setSpawn(Point spawn) {
        this.x = spawn.x;
        this.y = spawn.y;
        hitbox.x = x;
        hitbox.y = y;
    }

    /**
     * It's a box ahead of the player. Inside the box (if player attacks) enemy receive damage
     */
    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int) (20 * Game.SCALE), (int) (20 * Game.SCALE));
    }

    /**
     * Update player's position and animation
     */
    public void update() {
        updateHealthBar();

        // The player is dead
        if (currentHealth <= 0) {
            playing.setGameOver(true);
            return;
        }

        updateAttackBox();

        updatePosition();

        if (moving) {
            checkPotionTouched();
            checkSpikesTouched();
            tileY = (int) (hitbox.y / Game.TILES_SIZE);
        }

        if (attacking)
            checkAttack();

        updateAnimationTick();
        setAnimation();
    }

    private void checkPotionTouched() {
        playing.checkPotionTouched(hitbox);
    }

    private void checkSpikesTouched() {
        playing.checkSpikesTouched(this);
    }

    /**
     * When player attacks we need to check if he hits someone
     */
    private void checkAttack() {
        // We need to check it just once per animation when animation index is 1
        // otherwise no need to do anything
        if (attackChecked || aniIndex != 1)
            return;

        attackChecked = true;
        playing.checkEnemyHit(attackBox);
        playing.checkObjectHit(attackBox);
    }

    /**
     * Stick attack box to the player (should be always ahead him)
     */
    private void updateAttackBox() {
        if (right) {
            attackBox.x = hitbox.x + hitbox.width + (int) (Game.SCALE * 10);
        } if (left) {
            attackBox.x = hitbox.x - hitbox.width - (int) (Game.SCALE * 10);
        }

        attackBox.y = hitbox.y + (Game.SCALE * 10);
    }

    /**
     * Adjust current health to actual health bar width depends on how many health left
     */
    private void updateHealthBar() {
        healthWidth = (int) ((currentHealth / (float)maxHealth) * healthBarWidth);
    }

    /**
     * Draw player depending on selected action (row on sprite) and current animation index (column on sprite)
     */
    public void render(Graphics g, int xLvlOffset) {
        // Player image has a size of a tile so it's a normal rectangle
        // Hitbox is smaller than the image, so when we draw player itself we need to set some offsets
        // We need xLvlOffset to move player relatively the level
        // We need flipX and flipW to flip player to the left (flipX = width, flipW = -1) or to the right (flipX = 0, flipW = 1)
        g.drawImage(animations[state][aniIndex],
                (int)(hitbox.x - xDrawOffset) - xLvlOffset + flipX,
                (int)(hitbox.y - yDrawOffset),
                width * flipW,
                height,
                null);
        if (DEBUG_MODE) {
            drawHitbox(g, xLvlOffset);
            drawAttackBox(g, xLvlOffset);
        }

        drawUI(g);
    }

    /**
     * Draw status bar, etc
     */
    private void drawUI(Graphics g) {
        // Draw the sprite itself (with heart icon, and two placeholders for health and energy)
        g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
        g.setColor(Color.red);
        // Draw status bar for health (with statusBarX and statusBarY offsets)
        // healthWidth - actual health (less or equal than healthBarWidth)
        g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
    }

    /**
     * Change health (increase or decrease and die if <= 0)
     */
    public void changeHealth(int value) {
        currentHealth += value;
        if (currentHealth <= 0) {
            currentHealth = 0;
            //gameOver();
        } else if (currentHealth >= maxHealth) {
            currentHealth = maxHealth;
        }
    }

    /**
     * Kill the player
     */
    public void kill() {
        currentHealth = 0;
    }

    public void changePower(int value) {
        System.out.println("Add power!");
    }

    /**
     * Load animation images to animations array
     */
    private void loadAnimations() {

        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

        // 7 animations with max animations count 8
        animations = new BufferedImage[7][8];

        for (int j = 0; j < animations.length; j++) {
            for (int i = 0; i < animations[j].length; i++) {
                animations[j][i] = img.getSubimage(i*64, j*40, 64, 40);
            }

        }

        statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);

    }

    /**
     * Load current level data for the player
     * It's not good, but ok for now
     */
    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
        // If we start the game in the air
        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
    }

    /**
     * Change x and y when moving left, right, up and down
     */
    private void updatePosition() {

        moving = false;
        
        if (jump)
            jump();

        if (!inAir)
            if ((!left && !right) || (left && right))
                return;;

        float xSpeed = 0;

        if (left) {
            xSpeed -= walkSpeed;
            flipX = width;
            flipW = -1;
        }
        if (right) {
            xSpeed += walkSpeed;
            flipX = 0;
            flipW = 1;
        }

        if (!inAir)
            if (!IsEntityOnFloor(hitbox, lvlData))
                // If we are not in air yet (not falling and not jumping) and we are not on the floor
                // then we need to fall
                inAir = true;

        if (inAir) {

            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
                // No collision
                hitbox.y += airSpeed;
                airSpeed += GRAVITY;
                updateXPos(xSpeed);
            } else {
                // Can't move but still some little space between the player and floor or ceiling so we want to move right to the collision
                hitbox.y = GetEntityYPosUnderOrAboveFloor(hitbox, airSpeed);
                if (airSpeed > 0)
                    resetInAir();
                else
                    airSpeed = fallSpeedAfterCollision;

                updateXPos(xSpeed);
            }

        } else {
            updateXPos(xSpeed);
        }

        moving = true;

    }

    /**
     * Jump
     */
    private void jump() {
        if (inAir)
            return;

        inAir = true;
        airSpeed = jumpSpeed;       // Initial speed of jumping
    }

    /**
     * Update x position from updatePosition() method
     */
    private void updateXPos(float xSpeed) {
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            hitbox.x += xSpeed;
        } else {
            // Can't move but still some little space between the player and wall so we want to move right to the wall
            hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
        }
    }

    /**
     * Reset in air
     */
    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    /**
     * Increment animTick and when we reach animSpeed then increment animIndex
     * We use animIndex to display next animation sprite
     */
    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= ANI_SPEED) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpritesAmount(state)) {
                aniIndex = 0;
                attacking = false;
                attackChecked = false;
            }
        }
    }

    /**
     * Depending on booleans (moving, attacking, ...) set playerAction (RUNNING, IDLE, ATTACK_1)
     */
    private void setAnimation() {
        
        int startAni = state;
        
        if (moving)
            state = RUNNING;
        else
            state = IDLE;

        if (inAir) {
            if (airSpeed < 0)
                state = JUMP;
            else
                state = FALLING;
        }

        if (attacking) {
            state = ATTACK;
            // If we just starting the attack animation (first tick)
            if (startAni != ATTACK) {
                // Then let's start showing attack sprites from the second one since it looks better
                aniIndex = 1;
                aniTick = 0;
                return;     // return to don't go to resetAnyTick() below since we already reset index and tick
            }
        }

        // In case we have new animation (i.e. another button was pressed) we need to reset previous animation
        if (startAni != state)
            resetAnyTick();
        
    }

    /**
     * Reset current animation
     */
    private void resetAnyTick() {
        aniTick = 0;
        aniIndex = 0;
    }

    /**
     * Reset direction booleans up, down, left or right
     */
    public void resetDirBooleans() {
        left = false;
        right = false;
    }

    /**
     * Reset everything for the player to be ready to start the game again
     */
    public void resetAll() {
        resetDirBooleans();
        inAir = false;
        attacking = false;
        moving = false;
        state = IDLE;
        currentHealth = maxHealth;

        hitbox.x = x;
        hitbox.y = y;

        // If we start the game in the air
        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
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

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public int getTileY() {
        return  tileY;
    }
}
