package objects;

import entities.Player;
import gamestates.Playing;
import levels.Level;
import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utils.Constants.ObjectConstants.*;
import static utils.Constants.Projectiles.*;
import static utils.HelpMethods.CanCannonSeePlayer;
import static utils.HelpMethods.IsProjectileHittingLevel;

public class ObjectManager {

    private Playing playing;
    private BufferedImage[][] potionImgs, containerImgs;
    private BufferedImage[] cannonImgs;
    private BufferedImage spikeImg;
    private BufferedImage cannonBallImg;
    private ArrayList<Potion> potions;
    private ArrayList<GameContainer> containers;
    private ArrayList<Spike> spikes;
    private ArrayList<Cannon> cannons;
    private ArrayList<Projectile> projectiles = new ArrayList<>();

    public ObjectManager(Playing playing) {
        this.playing = playing;
        loadImgs();
    }

    /**
     * Check if player touch any of spikes. If so, he must die
     * @param player
     */
    public void checkSpikesTouched(Player player) {
        for (Spike s : spikes)
            if (s.getHitbox().intersects(player.getHitbox()))
                player.kill();
    }

    /**
     * Player touched one of the potions
     */
    public void checkObjectTouched(Rectangle2D.Float hitbox) {
        for(Potion p : potions)
            if (p.isActive()) {
                if (hitbox.intersects(p.getHitbox())) {
                    p.setActive(false);
                    applyEffectToPlayer(p);
                }
            }
    }

    /**
     * Add some health or energy for the player
     */
    public void applyEffectToPlayer(Potion p) {
        if (p.getObjType() == RED_POTION)
            playing.getPlayer().changeHealth(RED_POTION_VALUE);
        else if (p.getObjType() == BLUE_POTION)
            playing.getPlayer().changePower(BLUE_POTION_VALUE);
    }

    /**
     * When player hit box or barrel we create on of the potions (at the place of the container)
     * @param attachBox player's attack box
     */
    public void checkObjectHit(Rectangle2D.Float attachBox) {
        for(GameContainer gc : containers)
            if (gc.isActive() && !gc.doAnimation) {
                if (gc.getHitbox().intersects(attachBox)) {
                    gc.setAnimation(true);

                    int type = 0;
                    if (gc.getObjType() == BARREL)
                        type = 1;
                    else if (gc.getObjType() == BOX)
                        type = 0;

                    potions.add(new Potion(
                            (int) (gc.getHitbox().x + gc.getHitbox().width / 2),
                            (int) (gc.getHitbox().y - gc.getHitbox().height / 2),
                            type));

                    return;
                }
            }

    }

    public void resetAllObjects() {

        // We need to load objects again because its number could be increased since they were created when we
        // started the level (we create new potion when we hit box or barrel)
        // So we need to reset the addition potions as well
        loadObjects(playing.getLevelManager().getCurrentLevel());

        for(Potion p : potions)
            p.reset();

        for(GameContainer gc : containers)
            gc.reset();

        for(Cannon c : cannons)
            c.reset();
    }

    public void loadObjects(Level level) {
        // Initially it's a copy of objects from level data
        potions = new ArrayList<>(level.getPotions());
        containers = new ArrayList<>(level.getContainers());

        // No need to copy it since these are static objects and we are not going to add new spikes or delete existing
        spikes = level.getSpikes();
        cannons = level.getCannons();

        projectiles.clear();
    }

    private void loadImgs() {
        BufferedImage potionSprite = LoadSave.GetSpriteAtlas(LoadSave.POTION_SPRITE);
        potionImgs = new BufferedImage[2][7];

        for (int j = 0; j < potionImgs.length; j++)
            for (int i = 0; i < potionImgs[j].length; i++)
                potionImgs[j][i] = potionSprite.getSubimage(12 * i, 16 * j, 12, 16);


        BufferedImage containerSprite = LoadSave.GetSpriteAtlas(LoadSave.CONTAINER_SPRITE);
        containerImgs = new BufferedImage[2][8];

        for (int j = 0; j < containerImgs.length; j++)
            for (int i = 0; i < containerImgs[j].length; i++)
                containerImgs[j][i] = containerSprite.getSubimage(40 * i, 30 * j, 40, 30);

        spikeImg = LoadSave.GetSpriteAtlas(LoadSave.SPIKE_SPRITE);

        BufferedImage cannonSprite = LoadSave.GetSpriteAtlas(LoadSave.CANNON_SPRITE);
        cannonImgs = new BufferedImage[7];

        for (int i = 0; i < cannonImgs.length; i++)
            cannonImgs[i] = cannonSprite.getSubimage(40 * i, 0, 40, 26);

        cannonBallImg = LoadSave.GetSpriteAtlas(LoadSave.BALL);
    }

    public void update(int[][] lvlData, Player player) {
        for(Potion p : potions)
            p.update();

        for(GameContainer gc : containers)
            gc.update();
        
        updateCannons(lvlData, player);
        updateProjectiles(lvlData, player);
    }

    private void updateProjectiles(int[][] lvlData, Player player) {
        for (Projectile p : projectiles)
            if (p.isActive()) {
                p.updatePos();

                if (p.getHitbox().intersects(player.getHitbox())) {
                    player.changeHealth(-25);
                    p.setActive(false);
                } else if (IsProjectileHittingLevel(p, lvlData)) {
                    p.setActive(false);
                }

            }
    }

    private void updateCannons(int[][] lvlData, Player player) {
        for(Cannon c : cannons) {

            // if the cannon is not already animating (shooting)
            if (!c.doAnimation)
                // is cannon's tileY is the same as player's y
                if (c.getTileY() == player.getTileY())
                    // is player in the range
                    if (isPlayerInRangeOfCannon(c, player))
                        // is player in front of cannon
                        if (isPlayerInFrontOfCannon(c, player))
                            // check line of sight
                            if (CanCannonSeePlayer(lvlData, player.getHitbox(), c.getHitbox(), c.getTileY()))
                                // SHOOT
                                c.setAnimation(true);

            c.update();

            if (c.getAniIndex() == 4 && c.getAniTick() == 0)
                shootCannon(c);
        }

    }

    private void shootCannon(Cannon c) {
        // If cannon left then dir -1
        // If cannon right then dir 1
        projectiles.add(new Projectile((int)c.getHitbox().x, (int)c.getHitbox().y, c.getObjType() == CANNON_LEFT ? -1 : 1));
    }

    private boolean isPlayerInFrontOfCannon(Cannon c, Player player) {
        if (c.getObjType() == CANNON_LEFT) {
            return c.getHitbox().x > player.getHitbox().x;
        } else if (c.getObjType() == CANNON_RIGHT) {
            return c.getHitbox().x < player.getHitbox().x;
        }

        return false;
    }

    private boolean isPlayerInRangeOfCannon(Cannon c, Player player) {
        int absValue = (int) Math.abs(player.getHitbox().x - c.getHitbox().x);
        return absValue <= Game.TILES_SIZE * 5;
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawPotions(g, xLvlOffset);
        drawContainers(g, xLvlOffset);
        drawTraps(g, xLvlOffset);
        drawCannons(g, xLvlOffset);
        drawProjectiles(g, xLvlOffset);
    }

    private void drawProjectiles(Graphics g, int xLvlOffset) {
        for(Projectile p : projectiles)
            if (p.isActive()) {
                g.drawImage(cannonBallImg,
                        (int) (p.getHitbox().x - xLvlOffset),
                        (int) (p.getHitbox().y),
                        CANNON_BALL_WIDTH,
                        CANNON_BALL_HEIGHT,
                        null
                );
            }
    }

    private void drawCannons(Graphics g, int xLvlOffset) {
        for(Cannon c : cannons)
            if (c.isActive()) {

                // If it's LEFT CANNON then use normal x and width
                int x = (int) (c.getHitbox().x - xLvlOffset);
                int width = CANNON_WIDTH;

                // If it's RIGHT CANNON then di the magic to flip it
                if (c.getObjType() == CANNON_RIGHT) {
                    x += width;
                    width *= -1;
                }

                g.drawImage(cannonImgs[c.getAniIndex()],
                        x,
                        (int) c.getHitbox().y,
                        width,
                        CANNON_HEIGHT,
                        null
                );
            }
    }

    private void drawPotions(Graphics g, int xLvlOffset) {
        for(Potion p : potions)
            if (p.isActive()) {
                int pType = 0;
                switch (p.getObjType()) {
                    case RED_POTION -> pType = 0;
                    case BLUE_POTION -> pType = 1;
                }

                g.drawImage(potionImgs[pType][p.getAniIndex()],
                        (int) (p.getHitbox().x - p.getxDrawOffset() - xLvlOffset),
                        (int) (p.getHitbox().y - p.getyDrawOffset()),
                        POTION_WIDTH,
                        POTION_HEIGHT,
                        null
                );
            }
    }

    private void drawContainers(Graphics g, int xLvlOffset) {
        for(GameContainer gc : containers)
            if (gc.isActive()) {
                int cType = 0;
                switch (gc.getObjType()) {
                    case BOX -> cType = 0;
                    case BARREL -> cType = 1;
                }

                g.drawImage(containerImgs[cType][gc.getAniIndex()],
                        (int) (gc.getHitbox().x - gc.getxDrawOffset() - xLvlOffset),
                        (int) (gc.getHitbox().y - gc.getyDrawOffset()),
                        CONTAINER_WIDTH,
                        CONTAINER_HEIGHT,
                        null
                );
            }
    }

    private void drawTraps(Graphics g, int xLvlOffset) {

        for(Spike s : spikes)
            if (s.isActive())
                g.drawImage(spikeImg,
                        (int) (s.getHitbox().x - s.getxDrawOffset() - xLvlOffset),
                        (int) (s.getHitbox().y - s.getyDrawOffset()),
                        SPIKE_WIDTH,
                        SPIKE_HEIGHT,
                        null
        );


    }


}
