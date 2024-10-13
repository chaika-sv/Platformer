package entities;

import main.Game;

import static utils.Constants.EnemyConstants.*;
import static utils.HelpMethods.*;
import static utils.Constants.Directions.*;

public abstract class Enemy extends Entity{

    protected int aniIndex, enemyState, enemyType;
    protected int aniTick, aniSpeed = 25;
    protected boolean firstUpdate = true;
    protected boolean inAir;
    protected float fallSpeed;
    protected float gravity = 0.04f * Game.SCALE;
    protected float walkSpeed = 0.4f * Game.SCALE;
    protected int walkDir = LEFT;
    protected int tileY;
    protected float attackDistance = Game.TILES_SIZE;

    public Enemy(float x, float y, int width, int height, int enemyType) {
        super(x, y, width, height);
        this.enemyType = enemyType;
        initHitbox(x, y, width, height);
    }

    /**
     * If enemy started the game in the air just set inAir = true
     */
    protected void firstUpdateCheck(int[][] lvlData) {
        // If enemy started the game in the air
        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;

        firstUpdate = false;
    }

    /**
     * If enemy is still in the air then we need to fall down
     */
    protected void updateInAir(int[][] lvlData) {
        // Fall down
        if (CanMoveHere(hitbox.x, hitbox.y + fallSpeed, hitbox.width, hitbox.height, lvlData)) {
            // Still fall down
            hitbox.y += fallSpeed;
            fallSpeed += gravity;
        } else {
            // We are close to the floor so we just need to stick enemy to the floor
            inAir = false;
            hitbox.y = GetEntityYPosUnderOrAboveFloor(hitbox, fallSpeed);
            System.out.println(hitbox.y);
            tileY = (int) (y / Game.TILES_SIZE);
        }
    }

    /**
     * Enemy movement behavior (patrol, etc)
     */
    protected void move(int[][] lvlData) {
        float xSpeed = 0;

        if (walkDir == LEFT)
            xSpeed = -walkSpeed;
        else
            xSpeed = walkSpeed;

        // Checking if enemy can move to the position
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData))
            if (IsFloor(hitbox, xSpeed, lvlData)) {
                // If can move then move
                hitbox.x += xSpeed;
                return;
            }

        // If enemy cannot move then change his direction
        changeWalkDir();
    }

    /**
     * If enemy is moving to opposite side then he needs to turn to player
     */
    void turnTowardsPlayer(Player player) {
        if (player.hitbox.x > hitbox.x)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }

    /**
     * Check if enemy is close enough to player and there is nothing between them
     */
    protected boolean canSeePlayer(int[][] lvlData, Player player) {
        int playerTileY = (int) (player.getHitbox().y / Game.TILES_SIZE);

        // First checking if they are on the same line
        if (playerTileY == tileY)
            // Distance between player and enemy is close another
            if (isPlayerInRange(player))
                // There is nothing between them (no pit or solid object)
                if (IsSightClear(lvlData, hitbox, player.hitbox, tileY))
                    return true;

        return false;
    }

    /**
     * Check if enemy close enough to player to notice him
     */
    protected boolean isPlayerInRange(Player player) {
        int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
        return absValue <= attackDistance * 5;
    }

    /**
     * Check if enemy close enough to player to attack him
     */
    protected boolean isPlayerCloseForAttack(Player player) {
        int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
        return absValue <= attackDistance;
    }

    protected void newState(int enemyState) {
        this.enemyState = enemyState;
        aniTick = 0;
        aniIndex = 0;
    }


    protected void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(enemyType, enemyState)) {
                aniIndex = 0;
                if (enemyState == ATTACK)
                    // Change state to IDLE as soon as enemy is done with ATTACK
                    enemyState = IDLE;
            }

        }
    }

    protected void changeWalkDir() {
        if (walkDir == LEFT)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    };


    public int getAniIndex() {
        return aniIndex;
    }

    public int getEnemyState() {
        return enemyState;
    }

}
