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

        return IsTileSolid((int) xIndex, (int) yIndex, lvlData);

    }

    public static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData) {
        // What do we have in this position in the level
        int value = lvlData[yTile][xTile];

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

    /**
     * Check if (hitbox.x + x) is still floor
     */
    public static boolean IsFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData) {
        if (xSpeed > 0)
            return IsSolid(hitbox.x + hitbox.width + xSpeed, hitbox.y + hitbox.height + 1, lvlData);    // going to the right
        else
            return IsSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height + 1, lvlData);  // left
    }

    /**
     * Check if someone can move through all tiles between xStart and xEnd
     */
    public static boolean IsAllTilesWalkable(int xStart, int xEnd, int y, int[][] lvlData) {
        for (int i = 0; i < xEnd - xStart; i++) {
            // If there is solid tile on the way then we can't move
            if (IsTileSolid(xStart + i, y, lvlData))
                return false;
            // If there is no solid tile under next tile (it's a pit) then we can't move
            if (!IsTileSolid(xStart + i, y + 1, lvlData))
                return false;
        }

        return true;
    }

    /**
     * Check if we can move from firstHitbox to secondHitbox
     */
    public static boolean IsSightClear(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int tileY) {
        int firstXTile = (int) (firstHitbox.x / Game.TILES_SIZE);
        int secondXTile = (int) (secondHitbox.x / Game.TILES_SIZE);

        if (firstXTile > secondXTile)
            return IsAllTilesWalkable(secondXTile, firstXTile, tileY, lvlData);
        else
            return IsAllTilesWalkable(firstXTile, secondXTile, tileY, lvlData);

    }

}
