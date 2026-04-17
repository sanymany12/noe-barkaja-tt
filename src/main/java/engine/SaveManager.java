package engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import world.World;
import world.building.Building;
import world.resources.ICargo;
import world.vehicle.Vehicle;

import java.io.*;

public class SaveManager {

    private static final UniversalAdapter<?> universalAdapter = new UniversalAdapter<>();
    // A Gson objektum, ami a fordítást végzi
    private static final Gson gson = new GsonBuilder().
            setPrettyPrinting().
            registerTypeAdapter(Vehicle.class, universalAdapter).
            registerTypeAdapter(ICargo.class, universalAdapter).
            registerTypeAdapter(Building.class, universalAdapter).
            create();

    public static void saveGame(World world, String fileName) {
        try (Writer writer = new FileWriter(fileName)) {
            // Konvertálás json-fájlba
            gson.toJson(world, writer);
            System.out.println("Sikeres mentés: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static World loadGame(String fileName) {
        try (Reader reader = new FileReader(fileName)) {
            // olvasás
            World world = gson.fromJson(reader, World.class);
            System.out.println("Sikeres betöltés: " + fileName);
            return world;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}