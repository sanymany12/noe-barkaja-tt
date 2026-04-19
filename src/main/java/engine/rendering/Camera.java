package engine.rendering;

import world.tile.Point;


/*
A Camera osztály végzi az izometrikus nézethez szükséges számításokat
 */
public class Camera {
    private double offsetX;
    private double offsetY; //eltolás a térkép mozgatásához

    private double zoom = 1.0;
    private static final double MAX_ZOOM = 4.0;
    private static final double MIN_ZOOM = 0.5;

    private int screenWidth;
    private int screenHeight;

    private final int MARGIN = 15;

    private static final int TILE_WIDTH = 64;  //Egy mező szélessége
    private static final int TILE_HEIGHT = 64; //magassága

    public Camera(double offsetX, double offsetY, double zoom, int screenWidth, int screenHeight) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.zoom = zoom;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void setDimensions(int screenWidth, int screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void setZoom(double newZoom, int mapWidthTiles, int mapHeightTiles) {
        this.zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, newZoom));
        clampCamera(mapWidthTiles, mapHeightTiles);
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public double getZoom() {
        return zoom;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getTileHeight(){return TILE_HEIGHT;}
    public int getTileWidth(){return TILE_WIDTH;}

    /*
            Egy world koordinátának pl.: 1,2 számolja ki a képernyőn az elhelyezkedését pixelekben
             */
    public Point worldToScreen(int worldX, int worldY){
        double pixelX = worldX * TILE_WIDTH * zoom;
        double pixelY = worldY * TILE_HEIGHT * zoom;

        //Kamera eltolás (offset) és képernyő közepéhez igazítás
        int screenX = (int) Math.round(pixelX - offsetX);
        int screenY = (int) Math.round(pixelY - offsetY);

        return new Point(screenX, screenY);
    }

    public Point worldToScreen(double worldX, double worldY){
        double pixelX = worldX * TILE_WIDTH * zoom;
        double pixelY = worldY * TILE_HEIGHT * zoom;

        //Kamera eltolás (offset) és képernyő közepéhez igazítás
        int screenX = (int) Math.round(pixelX - offsetX);
        int screenY = (int) Math.round(pixelY - offsetY);

        return new Point(screenX, screenY);
    }
    /*
    Az előző metódus inverze. Egy kattintásnál ki lehet számolni melyik mezőt érintettük a rácson.
     */
    public Point screenToWorld(int screenX, int screenY){
        //Kamera eltolás visszafejtése
        double pixelX = screenX + offsetX;
        double pixelY = screenY + offsetY;

        //Visszaosztás a csempe méretével és a zoommal
        double worldX = pixelX / (TILE_WIDTH * zoom);
        double worldY = pixelY / (TILE_HEIGHT * zoom);

        //Kerekítés lefelé, hogy megkapjuk a pontos csempe indexét
        int tileX = (int) Math.floor(worldX);
        int tileY = (int) Math.floor(worldY);

        return new Point(tileX, tileY);
    }
/*
A kamera pozícióját clampeljük, hogy ne lehessen ki görgetni a világból.
 */
    public void clampCamera(int gridWidth, int gridHeight){

        //A teljes térkép tényleges mérete pixelekben, a jelenlegi nagyítással
        double mapPixelWidth = gridWidth * TILE_WIDTH * zoom;
        double mapPixelHeight = gridHeight * TILE_HEIGHT * zoom;

        //Kiszámoljuk, meddig mehet el a kamera anélkül, hogy lelépne a térképről
        double minOffsetX = -MARGIN;
        double maxOffsetX;

        double minOffsetY = -MARGIN;
        double maxOffsetY;

        //ha a térkép kisebb, mint a képernyő (nagyon kizoomoltunk)
        //Akkor fixen középen tartjuk
        if (mapPixelWidth <= screenWidth) {
            this.offsetX = -(screenWidth - mapPixelWidth) / 2.0;
        } else {
            maxOffsetX = (mapPixelWidth - screenWidth) + MARGIN;
            this.offsetX = Math.max(minOffsetX, Math.min(maxOffsetX, this.offsetX));
        }

        if (mapPixelHeight <= screenHeight) {
            this.offsetY = -(screenHeight - mapPixelHeight) / 2.0;
        } else {
            maxOffsetY = (mapPixelHeight - screenHeight) + MARGIN;
            this.offsetY = Math.max(minOffsetY, Math.min(maxOffsetY, this.offsetY));
        }

    }


    public void move(double dX, double dY, int gridWidth, int gridHeight){
        offsetX += dX;
        offsetY += dY;
        clampCamera(gridWidth, gridHeight);
    }
}
