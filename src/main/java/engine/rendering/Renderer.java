package engine.rendering;

import engine.AssetManager;
import world.World;
import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;

import java.awt.*;

public class Renderer {
    private final Camera camera;
    private final World world;
    private final float treeHeight = 1.7f;

    public Renderer(Camera camera, World world) {
        this.camera = camera;
        this.world = world;
    }

    public void renderMap(Graphics graphics){
        renderGround(graphics);
        renderTrees(graphics);
        //renderBuildings(graphics);
        renderVehicles(graphics);

    }
/*
pálya kirajzolása cellánként, TODO: culling (nincs szükség minden cellát kirajzolni)
 */
    private void renderGround(Graphics graphics){
        for (int i = 0; i < world.getRows(); i++) {
            for (int j = 0; j < world.getCols(); j++) {
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
    private void renderTrees(Graphics graphics){
        for (int i = 0; i < world.getRows(); i++) {
            for (int j = 0; j < world.getCols(); j++) {
                Tile tile = world.get(i,j);
                if(tile != null && tile.getTreeCount() > 0
                        && tile.getTerrainType() == TerrainType.LAND && tile.getBuilding() == null){
                    //Kiszámoljuk a csempe bal felső sarkát
                    Point topLeft = camera.worldToScreen(i, j);

                    //jobb alsó sarok
                    Point bottomRight = camera.worldToScreen(i + 1, j + 1);

                    //A tényleges szélesség és magasság a két pont különbsége
                    int renderWidth = bottomRight.x - topLeft.x;
                    int renderHeight = (int)((bottomRight.y - topLeft.y) * treeHeight); // megnöveljük a cella magasságát

                    // Az y koordinátán eltoljuk felfele
                    int drawY = bottomRight.y - renderHeight;
                    topLeft.y = drawY;
                    drawTree(graphics, tile, topLeft, renderWidth, renderHeight);
                }

            }
        }
    }

    private void drawTree(Graphics graphics, Tile tile, Point screenPosition, int width, int height){
        graphics.drawImage(AssetManager.get("tree" + tile.getTreeCount()), screenPosition.x, screenPosition.y, width, height, null);
    }


    /*
    épületek kirajzolása
     */
    private void renderBuildings(Graphics graphics){
        for (int i = 0; i < world.getRows(); i++) {
            for (int j = 0; j < world.getCols(); j++) {
                Tile tile = world.get(i,j);
               if(tile.isAnchor()){
                   int width = 3; //tile.getBuilding().getWidth();
                   int height = 9; //tile.getBuilding().getHeight();
                   //Kiszámoljuk a csempe bal felső sarkát
                   Point topLeft = camera.worldToScreen(i, j);

                   //jobb alsó sarok
                   Point bottomRight = camera.worldToScreen(i + 1, j + 1);

                   //A tényleges szélesség és magasság a két pont különbsége
                   int renderWidth = bottomRight.x - topLeft.x;
                   int renderHeight = bottomRight.y - topLeft.y;
                   drawBuilding(graphics,world.get(i,j),topLeft, renderWidth * width, renderHeight * height);
               }
            }
        }
    }

    private void drawBuilding(Graphics graphics, Tile tile, Point screenPosition, int width, int height){
        //TODO
        /*switch(tile.getBuilding().getType()){
            case SILO:
                break;
            case FARM:
                break;
            case STATION:
                break;
            case STB...:
                break;
        }*/
        graphics.drawImage(AssetManager.get("building"), screenPosition.x, screenPosition.y, width, height, null);

    }

    private void renderVehicles(Graphics graphics){
        //TODO
    }
    private void drawVehicle(Tile tile, Point screenPosition, int width, int height){
        //TODO
    }
}
