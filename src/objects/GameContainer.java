package objects;

import main.Game;

import static utils.Constants.ObjectConstants.*;

// Box or barrel
public class GameContainer extends GameObject{

    public GameContainer(int x, int y, int objType) {
        super(x, y, objType);
        doAnimation = false;
        createHitbox();
    }

    private void createHitbox() {
        if (objType == BOX) {
            initHitbox(25, 18);
            xDrawOffset = (int) (7 * Game.SCALE);       // pixels from the top of tile
            yDrawOffset = (int) (12 * Game.SCALE);       // pixels from the дуае of tile
        } else {
            // Barrel
            initHitbox(23, 25);
            xDrawOffset = (int) (6 * Game.SCALE);
            yDrawOffset = (int) (5 * Game.SCALE);
        }
    }


    public void update() {
        if (doAnimation)
            updateAnimationTick();
    }

}
