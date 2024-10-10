package utils;

import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HelpMethods {

    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData) {

        // We need to make sure that all corners of hitbox don't hit anything solid
        // If they don't then we can move here
        if (!IsSolid(x, y, lvlData))
            if (!IsSolid(x + width, y + height, lvlData))
                if (!IsSolid(x + width, y, lvlData))
                    if (!IsSolid(x, y + height, lvlData))
                        return true;

        return false;
    }

    private static boolean IsSolid(float x, float y, int[][] lvlData) {

        int maxWidth = lvlData[0].length * Game.TILES_SIZE;

        // If out of screen then solid
        if (x < 0 || x >= maxWidth)
            return true;
        if (y < 0 || y >= Game.GAME_HEIGHT)
            return true;

        // Getting current position
        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;

        // What do we have in this position in the level
        int value = lvlData[(int)yIndex][(int)xIndex];

        // It's a tile (11 is white empty tile so it's not a tile)
        return value >= 0 && value < 48 && value != 11;

    }

    /**
     * Calculate X distance from player to wall assuming that player is near the wall
     * @param hitbox player's hitbox
     * @param xSpeed player's speed
     * @return offset distance between player and wall
     */
    public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {

        int currentTile = (int) (hitbox.x / Game.TILES_SIZE);

        if (xSpeed > 0) {
            // Right
            int tileXPos = currentTile * Game.TILES_SIZE;
            int xOffset = (int)(Game.TILES_SIZE - hitbox.width);
            return tileXPos + xOffset - 1;
        } else {
            // Left
            return currentTile * Game.TILES_SIZE;
        }
    }


    /**
     * Calculate Y distance from player to floor or ceiling assuming that player is near the wall
     * @param hitbox player's hitbox
     * @param airSpeed player's air speed
     * @return offset distance between player and wall
     */
    public static float GetEntityYPosUnderOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {

        int currentTile = (int) (hitbox.y / Game.TILES_SIZE);

        if (airSpeed > 0) {
            // Falling - touching floor
            int tileYPos = currentTile * Game.TILES_SIZE;
            int yOffset = (int)(Game.TILES_SIZE - hitbox.height);
            return tileYPos + yOffset - 1;
        } else {
            // Jumping
            return currentTile * Game.TILES_SIZE;
        }
    }

    /**
     * Check if player is on the floor by checking pixels below bottom-left and bottom-right corners
     */
    public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {

        if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData))
            if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData))
                return false;

        return true;
    }


}
