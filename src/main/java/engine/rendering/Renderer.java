package engine.rendering;

import engine.AssetManager;
import world.World;
import world.tile.Point;
import world.tile.Tile;

import java.awt.*;

public class Renderer {
    private final Camera camera;
    private final World world;
    private final Graphics graphics;

    public Renderer(Camera camera, World world, Graphics graphics) {
        this.camera = camera;
        this.world = world;
        this.graphics = graphics;
    }

    public void renderMap(){
        renderGround();
        renderBuildings();
        renderVehicles();
    }
/*
pálya kirajzolása cellánként, TODO: culling (nincs szükség minden cellát kirajzolni)
 */
    private void renderGround(){
        for (int i = 0; i < world.getRows(); i++) {
            for (int j = 0; j < world.getCols(); j++) {
                //Kiszámoljuk a csempe bal felső sarkát
                Point topLeft = camera.worldToScreen(i, j);

                //jobb alsó sarok
                Point bottomRight = camera.worldToScreen(i + 1, j + 1);

                //A tényleges szélesség és magasság a két pont különbsége
                int renderWidth = bottomRight.x - topLeft.x;
                int renderHeight = bottomRight.y - topLeft.y;
               drawTile(world.get(i,j),topLeft, renderWidth, renderHeight);
            }
        }
    }

//Talaj rajzolása
    private void drawTile(Tile tile, Point screenPosition, int width, int height){
        switch(tile.getTerrainType()){
            case LAND:
                graphics.drawImage(AssetManager.get("land" + tile.getTreeCount()), screenPosition.x, screenPosition.y, width, height, null);
                break;
            case WATER:
                graphics.drawImage(AssetManager.get("water"), screenPosition.x, screenPosition.y, width, height, null);
                break;
            case ROAD:
                //TODO: utak és hídak kirajzolása
                break;
            case BUILDING:
                graphics.drawImage(AssetManager.get("concrete"), screenPosition.x, screenPosition.y, width, height, null);
                break;
            default:
                System.err.println("Nem található ilyen TerrainType");
        }
    }

    /*
    épületek kirajzolása
     */
    private void renderBuildings(){
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
                   drawBuilding(world.get(i,j),topLeft, renderWidth * width, renderHeight * height);
               }
            }
        }
    }

    private void drawBuilding(Tile tile, Point screenPosition, int width, int height){
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

    private void renderVehicles(){
        //TODO
    }
    private void drawVehicle(Tile tile, Point screenPosition, int width, int height){
        //TODO
    }
}
