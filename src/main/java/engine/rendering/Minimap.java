package engine.rendering;

import UI.ingameGUI;
import world.World;
import world.tile.Point;
import world.tile.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Minimap {
    private final World world;
    private final Camera camera;
    private BufferedImage mapImg;
    private final int width;
    private final int height;

    public Minimap(World world, Camera camera, int width, int height){
        this.world = world;
        this.camera = camera;
        this.mapImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.width = width;
        this.height = height;
        generateImage();

    }
    /*
    Ellentétben a rendererrel, itt nem kell minden tickben kirajzolni a pályát, elég egyszer.
    BufferedImage-re rajzolunk és elmentjük.
     */
    public void generateImage(){

        Graphics2D g2d = mapImg.createGraphics();

        // Kiszámoljuk, hány pixel széles/magas legyen egy csempe
        float tileWidth = (float) width / world.getCols();
        float tileHeight = (float) height / world.getRows();

        for (int i = 0; i < world.getCols(); i++) {
            for (int j = 0; j < world.getRows(); j++) {

                Tile tile = world.get(i, j);

                int x = (int) (i * tileWidth);
                int y = (int) (j * tileHeight);
                int w = (int) Math.ceil(tileWidth);
                int h = (int) Math.ceil(tileHeight);

                switch(tile.getTerrainType()) {
                    case LAND:
                        if (tile.getTreeCount() > 0) {
                            g2d.setColor(new Color(34, 139, 34));
                        } else {
                            g2d.setColor(Color.GREEN);
                        }
                        break;
                    case ROAD:
                        g2d.setColor(Color.GRAY);
                        break;
                    case WATER:
                        g2d.setColor(Color.BLUE);
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

    public void render(Graphics g, int screenX, int screenY) {

        g.drawImage(mapImg, screenX, screenY, null);

        //jelenlegi nézetet mutató fehér négyzet rajzolása
        int TILE_SIZE = 16;

        float totalWorldWidth = world.getCols() * TILE_SIZE;
        float totalWorldHeight = world.getRows() * TILE_SIZE;

        float ratioX = width / totalWorldWidth;
        float ratioY = height / totalWorldHeight;

        int viewX = (int) (camera.getOffsetX() * ratioX);
        int viewY = (int) (camera.getOffsetY() * ratioY);
        int viewWidth = (int) (camera.getScreenWidth() * ratioX / camera.getZoom());
        int viewHeight = (int) (camera.getScreenHeight() * ratioY / camera.getZoom());

        g.setColor(Color.WHITE);
        g.drawRect(screenX + viewX, screenY + viewY, viewWidth, viewHeight);
    }

}
