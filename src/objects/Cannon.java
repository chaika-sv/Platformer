package objects;

import main.Game;

public class Cannon extends GameObject{

    private int tileY;

    public Cannon(int x, int y, int objType) {
        super(x, y, objType);
        this.tileY = y / Game.TILES_SIZE;
        initHitbox(40, 26);     // the checkbox itself doesn't matter, but we need it to have x, y and son on
        hitbox.x -= (int)(4 * Game.SCALE);  // to make sure the cannon is in the center of the tile
        hitbox.y += (int)(6 * Game.SCALE);  // to make sure the cannon is on the floor
    }

    public void update() {
        // Animate when the cannon can see the player and trying to shoot
        if (doAnimation)
            updateAnimationTick();
    }

    public int getTileY() {
        return tileY;
    }


}
