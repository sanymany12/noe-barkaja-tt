package engine.rendering;

import java.awt.image.BufferedImage;
/*Kirajzolandó objektumok adatai*/
public class RenderObj {
    private BufferedImage image;
    private int x, y;
    private int width, height;
    private int bottomY;

    public int getBottomY() {
        return bottomY;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public RenderObj(BufferedImage image, int x, int y, int width, int height, int bottomY) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bottomY = bottomY;
    }
}
