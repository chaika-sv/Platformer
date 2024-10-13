package entities;

import main.Game;

import static utils.Constants.Directions.LEFT;
import static utils.Constants.Directions.RIGHT;
import static utils.Constants.EnemyConstants.*;
import static utils.HelpMethods.*;

public class Crabby extends Enemy {

    public Crabby(float x, float y) {
        super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY);
        initHitbox(x, y, (int) (22 * Game.SCALE), (int) (19 * Game.SCALE));       // 22 x 19 actual size of crabby hitbox
    }



    public void update(int[][] lvlData, Player player) {
        updateMove(lvlData, player);
        updateAnimationTick();
    }

    private void updateMove(int[][] lvlData, Player player) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir)
            updateInAir(lvlData);
        else {
            // If not in the air then patrol
            switch (enemyState) {
                case IDLE:
                    newState(RUNNING);
                    break;
                case RUNNING:

                    if (canSeePlayer(lvlData, player))
                        turnTowardsPlayer(player);

                    if (isPlayerCloseForAttack(player))
                        newState(ATTACK);

                    move(lvlData);
                    break;
            }
        }
    }




}
