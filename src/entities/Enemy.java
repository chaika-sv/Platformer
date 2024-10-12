package entities;

import main.Game;

import static utils.Constants.EnemyConstants.*;
import static utils.HelpMethods.*;
import static utils.Constants.Directions.*;

public abstract class Enemy extends Entity{

    private int aniIndex, enemyState, enemyType;
    private int aniTick, aniSpeed = 25;
    private boolean firstUpdate = true;
    private boolean inAir;
    private float fallSpeed;
    private float gravity = 0.04f * Game.SCALE;
    private float walkSpeed = 0.4f * Game.SCALE;
    private int walkDir = LEFT;

    public Enemy(float x, float y, int width, int height, int enemyType) {
        super(x, y, width, height);
        this.enemyType = enemyType;
        initHitbox(x, y, width, height);
    }

    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(enemyType, enemyState)) {
                aniIndex = 0;
            }

        }
    }

    public void update(int[][] lvlData) {
        updateMove(lvlData);
        updateAnimationTick();
    }


    private void updateMove(int[][] lvlData) {
        if (firstUpdate) {
            // If enemy started the game in the air
            if (!IsEntityOnFloor(hitbox, lvlData))
                inAir = true;

            firstUpdate = false;
        }

        if (inAir) {
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
            }
        } else {
            // If not in the air then patrol
            switch (enemyState) {
                case IDLE:
                    enemyState = RUNNING;
                    break;
                case RUNNING:
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

                    break;
            }
        }
    }



    private void changeWalkDir() {
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
