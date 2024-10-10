package ui;

import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import static utils.Constants.UI.PauseButtons.*;

public class UrmButton extends PauseButton{

    private BufferedImage[] imgs;
    private UrmType urmType;
    private boolean mouseOver, mousePressed;
    int index;

    public UrmButton(UrmType urmType, int x, int y, int width, int height) {
        super(x, y, width, height);

        this.urmType = urmType;

        loadImgs();
    }

    private void loadImgs() {
        BufferedImage tmpImg = LoadSave.GetSpriteAtlas(LoadSave.URM_BUTTONS);
        imgs = new BufferedImage[3];

        for (int i = 0; i < imgs.length; i++)
            imgs[i] = tmpImg.getSubimage(i * URM_SIZE_DEFAULT, urmType.getId() * URM_SIZE_DEFAULT, URM_SIZE_DEFAULT, URM_SIZE_DEFAULT);

    }

    public void update() {
        index = 0;
        if (mouseOver)
            index = 1;
        if (mousePressed)
            index = 2;
    }

    public void draw(Graphics g) {
        g.drawImage(imgs[index], x, y, URM_SIZE, URM_SIZE, null);
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
