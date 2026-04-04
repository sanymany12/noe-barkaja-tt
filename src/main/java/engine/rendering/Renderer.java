package engine.rendering;

import engine.AssetManager;
import world.World;
import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Renderer {
    private final Camera camera;
    private final World world;
    private final float treeHeight = 1.7f;

    public Camera getCamera() {
        return camera;
    }

    public World getWorld() {
        return world;
    }

    public Renderer(Camera camera, World world) {
        this.camera = camera;
        this.world = world;
    }

    public void renderMap(Graphics graphics){
        renderGround(graphics);
        renderObjects(graphics);
    }

    private void renderObjects(Graphics g){
        List<RenderObj> objectsToRender = new ArrayList<>();

        for (int i = 0; i < world.getCols(); i++) {
            for (int j = 0; j < world.getRows(); j++) {

                Tile tile = world.get(i,j);
                //Összegyűjtjük a fákat
                if(tile != null && tile.getTreeCount() > 0
                        && tile.getTerrainType() == TerrainType.LAND && tile.getBuilding() == null){
                    //Kiszámoljuk a csempe bal felső sarkát
                    Point topLeft = camera.worldToScreen(i, j);

                    //jobb alsó sarok
                    Point bottomRight = camera.worldToScreen(i + 1, j + 1);

                    //A tényleges szélesség és magasság a két pont különbsége
                    int renderWidth = bottomRight.x - topLeft.x;
                    int renderHeight = (int)((bottomRight.y - topLeft.y) * treeHeight); // megnöveljük a cella magasságát

                    // Az y koordinátán eltoljuk felfelé
                    int drawY = bottomRight.y - renderHeight;


                    objectsToRender.add(new RenderObj(AssetManager.get("tree"+tile.getTreeCount()),
                            topLeft.x, drawY, renderWidth, renderHeight, bottomRight.y));
                }

                //Összegyűjtjük az épületeket:

                if(tile != null && tile.isAnchor()){

                    int baseWidth = 3;  // tile.getBuilding().getWidth()
                    int baseHeight = 3; // tile.getBuilding().getHeight()

                    //Kiszámoljuk a csempe bal felső sarkát
                    Point topLeft = camera.worldToScreen(i, j);

                    //jobb alsó sarok
                    Point bottomRight = camera.worldToScreen(i + baseWidth, j + baseHeight);

                    //A tényleges szélesség és magasság a két pont különbsége
                    int renderWidth = bottomRight.x - topLeft.x;

                    BufferedImage buildingImg = AssetManager.get("building");

                    // tényleges magasság kiszámolása a kép méretei alapján
                    float imageRatio = (float) buildingImg.getHeight(null) / buildingImg.getWidth(null);
                    int renderHeight = (int) (renderWidth * imageRatio);

                    // Az y koordinátán eltoljuk felfelé
                    int drawY = bottomRight.y - renderHeight;

                    objectsToRender.add(new RenderObj(buildingImg,
                            topLeft.x, drawY, renderWidth, renderHeight, bottomRight.y));
                }


                //TODO: Összegyűjtjük a járműveket
            }
        }
        // sortoljuk a listát bottomY alapján, így minél leljebb van annál később rajzoljuk ki
        objectsToRender.sort(Comparator.comparing(RenderObj::getBottomY));

        //kirajzolás
        for(RenderObj r : objectsToRender){
            g.drawImage(r.getImage(), r.getX(), r.getY(), r.getWidth(), r.getHeight(), null);
        }
    }
/*
pálya kirajzolása cellánként, TODO: culling (nincs szükség minden cellát kirajzolni)
 */
    private void renderGround(Graphics graphics){
        for (int i = 0; i < world.getCols(); i++) {
            for (int j = 0; j < world.getRows(); j++) {
                //Kiszámoljuk a csempe bal felső sarkát
                Point topLeft = camera.worldToScreen(i, j);

                //jobb alsó sarok
                Point bottomRight = camera.worldToScreen(i + 1, j + 1);

                //A tényleges szélesség és magasság a két pont különbsége
                int renderWidth = bottomRight.x - topLeft.x;
                int renderHeight = bottomRight.y - topLeft.y;
               drawTile(graphics, world.get(i,j),topLeft, renderWidth, renderHeight);
            }
        }
    }

//Talaj rajzolása
    private void drawTile(Graphics graphics, Tile tile, Point screenPosition, int width, int height){
        switch(tile.getTerrainType()){
            case LAND:
                graphics.drawImage(AssetManager.get("land"), screenPosition.x, screenPosition.y, width, height, null);
                break;
            case WATER:
                graphics.drawImage(AssetManager.get("water"), screenPosition.x, screenPosition.y, width, height, null);
                break;
            case ROAD:
                graphics.drawImage(AssetManager.get(tile.getRoad().getSpriteName()), screenPosition.x, screenPosition.y, width, height, null);
                break;
            case BUILDING:
                graphics.drawImage(AssetManager.get("concrete"), screenPosition.x, screenPosition.y, width, height, null);
                break;
            default:
                System.err.println("Nem található ilyen TerrainType");
        }
    }

}
