package utils;

import main.Game;

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

        // If out of screen then solid
        if (x < 0 || x >= Game.GAME_WIDTH)
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

}
