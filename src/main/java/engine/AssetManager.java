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

    private AssetManager(){
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
        loadAsset("land", "/assets/landscapeTiles_067.png");
        loadAsset("water", "/assets/landscapeTiles_066.png");

        //road tipusok (road + NORTH EAST SOUTH WEST) amerre nyitott
        loadAsset("roadN", "/assets/landscapeTiles_117.png");
        loadAsset("roadE", "/assets/landscapeTiles_112.png");
        loadAsset("roadS", "/assets/landscapeTiles_111.png");
        loadAsset("roadW", "/assets/landscapeTiles_105.png");

        loadAsset("roadNS", "/assets/landscapeTiles_082.png");
        loadAsset("roadEW", "/assets/landscapeTiles_074.png");

        loadAsset("roadNES", "/assets/landscapeTiles_089.png");
        loadAsset("roadNEW", "/assets/landscapeTiles_104.png");
        loadAsset("roadNSW", "/assets/landscapeTiles_096.png");
        loadAsset("roadESW", "/assets/landscapeTiles_097.png");

        loadAsset("roadNESW", "/assets/landscapeTiles_090.png");


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
