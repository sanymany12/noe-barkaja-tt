package engine.rendering;

import UI.ingameGUI;
import world.World;
import world.tile.Point;
import world.tile.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Minimap {
    private World world;
    private final Camera camera;
    private BufferedImage mapImg;
    private int screenWidth;
    private int screenHeight;

    public Minimap(World world, Camera camera, int screenWidth, int screenHeight){
        this.world = world;
        this.camera = camera;
        this.mapImg = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        generateImage();

    }

    public void setDimensions(int screenWidth, int screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }
    /*
    Ellentétben a rendererrel, itt nem kell minden tickben kirajzolni a pályát, elég egyszer.
    BufferedImage-re rajzolunk és elmentjük.
     */
    public void generateImage(){

        Graphics2D g2d = mapImg.createGraphics();

        // Kiszámoljuk, hány pixel széles/magas legyen egy csempe
        float tileWidth = (float) screenWidth / world.getCols();
        float tileHeight = (float) screenHeight / world.getRows();

        for (int i = 0; i < world.getCols(); i++) {
            for (int j = 0; j < world.getRows(); j++) {

                Tile tile = world.get(i, j);

                int x = (int) (i * tileWidth);
                int y = (int) (j * tileHeight);
                int w = (int) Math.ceil(tileWidth);
                int h = (int) Math.ceil(tileHeight);
                // adott szín kiválasztása mező alapján
                switch(tile.getTerrainType()) {
                    case LAND:
                        if (tile.getTreeCount() > 0) {
                            g2d.setColor(new Color(25, 103, 25));
                        } else {
                            g2d.setColor(new Color(26, 189, 26));
                        }
                        break;
                    case ROAD:
                        g2d.setColor(Color.GRAY);
                        break;
                    case WATER:
                        g2d.setColor(new Color(0, 233, 255));
                        break;
                    case BUILDING:
                        g2d.setColor(Color.DARK_GRAY);
                        break;
                    default:
                        g2d.setColor(Color.BLACK);
                        break;
                }

                g2d.fillRect(x, y, w, h);
            }
        }

        g2d.dispose();
    }

    public void render(Graphics g) {

        g.drawImage(mapImg, 0, 0, null);

        //jelenlegi nézetet mutató fehér négyzet rajzolása
        int TILE_SIZE = 16;

        float totalWorldWidth = (float) (world.getCols() * TILE_SIZE * camera.getZoom());
        float totalWorldHeight = (float) (world.getRows() * TILE_SIZE * camera.getZoom());

        float ratioX = screenWidth / totalWorldWidth;
        float ratioY = screenHeight / totalWorldHeight;

        int viewX = (int) (camera.getOffsetX() * ratioX);
        int viewY = (int) (camera.getOffsetY() * ratioY);
        int viewWidth = (int) (camera.getScreenWidth() * ratioX);
        int viewHeight = (int) (camera.getScreenHeight() * ratioY);

        g.setColor(Color.WHITE);
        g.drawRect(viewX, viewY, viewWidth, viewHeight); // jelenlegi kép kirajzolása
    }

    public void setWorld(World world) { this.world = world;}
}
