package ui;

import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.PauseButtons.*;

public class VolumeButton extends PauseButton {

    private BufferedImage[] imgs;
    private BufferedImage slider;
    private boolean mouseOver, mousePressed;
    private int index = 0;
    private int buttonX;        // button of the slider
    private int minX, maxX;


    public VolumeButton(int x, int y, int width, int height) {
        // Place the volume button in the middle of the slider
        // When we call super we create hitbox rectangle for the volume button
        super(x + width / 2, y, VOLUME_WIDTH, height);
        bounds.x -= VOLUME_WIDTH / 2;
        buttonX = x + width / 2;

        // These are for slider
        this.x = x;
        this.width = width;

        minX = x + VOLUME_WIDTH / 2;
        maxX = x + width - VOLUME_WIDTH / 2;

        loadImgs();
    }


    private void loadImgs() {
        BufferedImage tmpImg = LoadSave.GetSpriteAtlas(LoadSave.VOLUME_BUTTON);
        imgs = new BufferedImage[3];

        // Volume button
        for (int i = 0; i < imgs.length; i++)
            imgs[i] = tmpImg.getSubimage(i * VOLUME_WIDTH_DEFAULT, 0, VOLUME_WIDTH_DEFAULT, VOLUME_HEIGHT_DEFAULT);

        // Slider
        slider = tmpImg.getSubimage(3 * VOLUME_WIDTH_DEFAULT, 0, SLIDER_WIDTH_DEFAULT, VOLUME_HEIGHT_DEFAULT);
    }

    public void update() {
        index = 0;
        if (mouseOver)
            index = 1;
        if (mousePressed)
            index = 2;

        bounds.x = buttonX - VOLUME_WIDTH / 2;
    }

    public void draw(Graphics g) {
        g.drawImage(slider, x, y, width, height, null);
        g.drawImage(imgs[index], buttonX - VOLUME_WIDTH / 2, y, VOLUME_WIDTH, height, null);
    }

    public void changeX(int x) {
        if (x < minX)
            buttonX = minX;
        else if (x > maxX)
            buttonX = maxX;
        else
            buttonX = x;
    }

    public void resetBools() {
        mouseOver = false;
        mousePressed = false;
    }


    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

}
