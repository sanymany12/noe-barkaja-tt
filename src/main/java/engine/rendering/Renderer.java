package engine.rendering;

import engine.AssetManager;
import world.World;
import world.building.Station;
import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;
import world.vehicle.Vehicle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Renderer {
    private final Camera camera;
    private World world;
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

        //végigmegyünk a tileokon (beolvassuk a statikus elemeket)
        for (int i = 0; i < world.getRows(); i++) {
            for (int j = 0; j < world.getCols(); j++) {

                Tile tile = world.get(i,j);

                if(tile != null){
                    //Összegyűjtjük a fákat
                    if(tile.getTreeCount() > 0
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

                    if(tile.isAnchor()){

                        int baseWidth = tile.getBuilding().getWidth();
                        int baseHeight = tile.getBuilding().getHeight();

                        //Kiszámoljuk a csempe bal felső sarkát
                        Point topLeft = camera.worldToScreen(i, j);

                        //jobb alsó sarok
                        Point bottomRight = camera.worldToScreen(i + baseWidth, j + baseHeight);

                        //A tényleges szélesség és magasság a két pont különbsége
                        int renderWidth = bottomRight.x - topLeft.x;

                        String sprite = tile.getBuilding().getSpriteName();
                        BufferedImage buildingImg = AssetManager.get(sprite);

                        // tényleges magasság kiszámolása a kép méretei alapján
                        float imageRatio = (float) buildingImg.getHeight(null) / buildingImg.getWidth(null);
                        int renderHeight = (int) (renderWidth * imageRatio);

                        // Az y koordinátán eltoljuk felfelé
                        int drawY = bottomRight.y - renderHeight;

                        if(tile.getBuilding() instanceof Station){
                            objectsToRender.add(new RenderObj(buildingImg,
                                    topLeft.x, drawY, renderWidth, renderHeight, topLeft.y));
                        }else{
                            objectsToRender.add(new RenderObj(buildingImg,
                                    topLeft.x, drawY, renderWidth, renderHeight, bottomRight.y));
                        }
                    }

                    if(tile.getTerrainType() == TerrainType.ROAD){
                        //Kiszámoljuk a csempe bal felső sarkát
                        Point topLeft = camera.worldToScreen(i, j);
                        //jobb alsó sarok
                        Point bottomRight = camera.worldToScreen(i + 1, j + 1);

                        //A tényleges szélesség és magasság a két pont különbsége
                        int renderWidth = bottomRight.x - topLeft.x;
                        int renderHeight = (int)((bottomRight.y - topLeft.y));

                        BufferedImage buildingImg = AssetManager.get(tile.getRoad().getSpriteName());

                        objectsToRender.add(new RenderObj(buildingImg,
                                topLeft.x, topLeft.y, renderWidth, renderHeight, topLeft.y));
                    }

                }

            }
        }

        for(Vehicle vehicle : world.getVehicles()){
            double eX = vehicle.getExactX();
            double eY = vehicle.getExactY();

            Point topLeft = camera.worldToScreen(eX, eY);

            //jobb alsó sarok
            Point bottomRight = camera.worldToScreen(eX + vehicle.getWidth(), eY + vehicle.getHeight());

            //A tényleges szélesség és magasság a két pont különbsége
            int renderWidth = (int)((bottomRight.x - topLeft.x) * vehicle.getWidth());
            int renderHeight = (int)((bottomRight.y - topLeft.y) * vehicle.getHeight()); // megnöveljük a cella magasságát
            objectsToRender.add(new RenderObj(AssetManager.get(vehicle.getSpriteName()),
                    topLeft.x, topLeft.y, renderWidth, renderHeight, bottomRight.y));
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
        graphics.drawImage(AssetManager.get(tile.getSpriteName()), screenPosition.x, screenPosition.y, width, height, null);
    }

    public void setWorld(World world) { this.world = world;}
}
