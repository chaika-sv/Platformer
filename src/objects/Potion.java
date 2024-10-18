package objects;

import main.Game;

public class Potion extends GameObject {

    public Potion(int x, int y, int objType) {
        super(x, y, objType);
        doAnimation = true;
        initHitbox(7, 14);
        xDrawOffset = (int) (3 * Game.SCALE);       // pixels from the top of tile
        yDrawOffset = (int) (2 * Game.SCALE);       // pixels from the дуае of tile
    }

    public void update() {
        if (doAnimation)
            updateAnimationTick();
    }

}
