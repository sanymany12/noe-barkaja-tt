package engine.rendering;

import world.tile.Point;


/*
A Camera osztály végzi az izometrikus nézethez szükséges számításokat
 */
public class Camera {
    private double offsetX;
    private double offsetY; //eltolás a térkép mozgatásához

    private double zoom = 1.0;
    private static final double MAX_ZOOM = 3.0;
    private static final double MIN_ZOOM = 0.5;

    private final int screenWidth;
    private final int screenHeight;

    private static final int TILE_WIDTH = 16;  //Egy mező szélessége
    private static final int TILE_HEIGHT = 16; //magassága

    public Camera(double offsetX, double offsetY, double zoom, int screenWidth, int screenHeight) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.zoom = zoom;
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

    /*
            Egy world koordinátának pl.: 1,2 számolja ki a képernyőn az elhelyezkedését pixelekben
             */
    public Point worldToScreen(int worldX, int worldY){
        double pixelX = worldX * TILE_WIDTH * zoom;
        double pixelY = worldY * TILE_HEIGHT * zoom;

        //Kamera eltolás (offset) és képernyő közepéhez igazítás
        int screenX = (int) Math.round(pixelX - offsetX + (screenWidth / 2.0));
        int screenY = (int) Math.round(pixelY - offsetY + (screenHeight / 2.0));

        return new Point(screenX, screenY);
    }
    /*
    Az előző metódus inverze. Egy kattintásnál ki lehet számolni melyik mezőt érintettük a rácson.
     */
    public Point screenToWorld(int screenX, int screenY){
        //Kamera eltolás visszafejtése
        double pixelX = screenX + offsetX - (screenWidth / 2.0);
        double pixelY = screenY + offsetY - (screenHeight / 2.0);

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
        double minOffsetX = 0 - (screenWidth / 2.0);
        double maxOffsetX = mapPixelWidth - (screenWidth / 2.0);

        double minOffsetY = 0 - (screenHeight / 2.0);
        double maxOffsetY = mapPixelHeight - (screenHeight / 2.0);

        //Biztonsági ellenőrzés: ha a térkép kisebb, mint a képernyő (nagyon kizoomoltunk)
        //Akkor fixen középen tartjuk, különben rángatózna a kamera
        if (mapPixelWidth < screenWidth) {
            this.offsetX = mapPixelWidth / 2.0;
        } else {
            this.offsetX = Math.max(minOffsetX, Math.min(maxOffsetX, this.offsetX));
        }

        if (mapPixelHeight < screenHeight) {
            this.offsetY = mapPixelHeight / 2.0;
        } else {
            this.offsetY = Math.max(minOffsetY, Math.min(maxOffsetY, this.offsetY));
        }
    }


    public void move(double dX, double dY, int gridWidth, int gridHeight){
        offsetX += dX;
        offsetY += dY;
        clampCamera(gridWidth, gridHeight);
    }
}
