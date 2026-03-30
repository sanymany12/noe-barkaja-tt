package engine;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/*
Ez az osztaly (singleton) felelos minden texturaert, ezen keresztul lehet majd elerni
 a kulonbozo BufferedImage-eket.
Igy nem kell minden osztalyban egy kulon field erre es jelentosen megkonnyitia kirajzolast is.
 */
public class AssetManager {

    private static AssetManager instance;
    private static Map<String, BufferedImage> textures;

    public AssetManager(){
        textures = new HashMap<>();
        init();
    }

    public static AssetManager getInstance(){
        if(instance == null){
            instance = new AssetManager();
        }
        return instance;
    }

    private void init(){

        //szarazfold vagy viz tile
        loadAsset("land", "/assets/CP_V1.0.4_nyknck/CP_V1.0.4_125.png"); //land 0 fával
        loadAsset("tree1", "/assets/CP_V1.0.4_nyknck/tree1.png"); //land 1 fával
        loadAsset("tree2", "/assets/CP_V1.0.4_nyknck/tree2.png"); //land 2 fával
        loadAsset("tree3", "/assets/CP_V1.0.4_nyknck/tree3.png"); //land 3 fával
        loadAsset("tree4", "/assets/CP_V1.0.4_nyknck/tree4.png"); //land 4 fával

        loadAsset("water", "/assets/CP_V1.0.4_nyknck/CP_V1.0.4_124.png");

        //road tipusok (road + NORTH EAST SOUTH WEST) amerre nyitott
        //loadAsset("roadN", "/assets/landscapeTiles_117.png"); régi assetek
        //loadAsset("roadE", "/assets/landscapeTiles_112.png");
        //loadAsset("roadS", "/assets/landscapeTiles_111.png");
        //loadAsset("roadW", "/assets/landscapeTiles_105.png");

        //loadAsset("roadNS", "/assets/landscapeTiles_082.png");
        //loadAsset("roadEW", "/assets/landscapeTiles_074.png");

        //loadAsset("roadNES", "/assets/landscapeTiles_089.png");
        //loadAsset("roadNEW", "/assets/landscapeTiles_104.png");
        //loadAsset("roadNSW", "/assets/landscapeTiles_096.png");
        //loadAsset("roadESW", "/assets/landscapeTiles_097.png");

        //loadAsset("roadNESW", "/assets/landscapeTiles_090.png");

        // ROAD típusok betöltése
        loadAsset("road-0-", "/assets-final/road-0-.png");
        loadAsset("road-1-n", "/assets-final/road-1-n.png");
        loadAsset("road-1-w", "/assets-final/road-1-w.png");
        loadAsset("road-1-s", "/assets-final/road-1-s.png");
        loadAsset("road-1-e", "/assets-final/road-1-e.png");
        loadAsset("road-2-nw", "/assets-final/road-2-nw.png");
        loadAsset("road-2-ns", "/assets-final/road-2-ns.png");
        loadAsset("road-2-ne", "/assets-final/road-2-ne.png");
        loadAsset("road-2-ws", "/assets-final/road-2-ws.png");
        loadAsset("road-2-we", "/assets-final/road-2-we.png");
        loadAsset("road-2-se", "/assets-final/road-2-se.png");
        loadAsset("road-3-nws", "/assets-final/road-3-nws.png");
        loadAsset("road-3-nse", "/assets-final/road-3-nse.png");
        loadAsset("road-3-nwe", "/assets-final/road-3-nwe.png");
        loadAsset("road-3-wse", "/assets-final/road-3-wse.png");
        loadAsset("road-4-nwse", "/assets-final/road-4-nwse.png");

        // STATION típusok betöltése
        loadAsset("industrial-stop-n", "/assets-final/industrial-stop-n.png");
        loadAsset("industrial-stop-e", "/assets-final/industrial-stop-e.png");
        loadAsset("industrial-stop-w", "/assets-final/industrial-stop-w.png");
        loadAsset("industrial-stop-s", "/assets-final/industrial-stop-s.png");

        // BUSSTOP típusok betöltése
        loadAsset("bus-stop-n-", "/assets-final/bus-stop-n-.png");
        loadAsset("bus-stop-n-start", "/assets-final/bus-stop-n-start.png");
        loadAsset("bus-stop-n-stop", "/assets-final/bus-stop-n-stop.png");
        loadAsset("bus-stop-s-", "/assets-final/bus-stop-s-.png");
        loadAsset("bus-stop-s-start", "/assets-final/bus-stop-s-start.png");
        loadAsset("bus-stop-s-stop", "/assets-final/bus-stop-s-stop.png");
        loadAsset("bus-stop-e-", "/assets-final/bus-stop-e-.png");
        loadAsset("bus-stop-e-start", "/assets-final/bus-stop-e-start.png");
        loadAsset("bus-stop-e-stop", "/assets-final/bus-stop-e-stop.png");
        loadAsset("bus-stop-w-", "/assets-final/bus-stop-w-.png");
        loadAsset("bus-stop-w-start", "/assets-final/bus-stop-w-start.png");
        loadAsset("bus-stop-w-stop", "/assets-final/bus-stop-w-stop.png");

        loadAsset("building", "/assets/CP_V1.0.4_nyknck/CP_V1.0.4_01.png"); //épület 3x9
        loadAsset("concrete", "/assets/CP_V1.0.4_nyknck/CP_V1.0.4_58.png"); //beton cella
    }
/*
kep betoltese a mapbe input streammel
*/
    public void loadAsset(String name, String path){

        if(textures.containsKey(name)){
            System.err.println("Mar letezik ez a kep");
        }

        try(InputStream is = getClass().getResourceAsStream(path)){

            if(is == null){
                throw new IOException("Nem talalhato a fajl: " + path);
            }
            BufferedImage image = ImageIO.read(is);
            textures.put(name,image);

        }catch (IOException e){
            System.err.println("Hiba tortent a kep beolvasasakor: " + e.getMessage());
        }

    }

    public static BufferedImage get(String name){
        if(!textures.containsKey(name)){
            System.err.println("Nem talalhato ilyen kep: "+ name);
        }
        return textures.get(name);
    }
}
