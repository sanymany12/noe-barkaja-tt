package engine;

import com.google.gson.*;
import world.World;
import world.building.*; // Minden épület importálása
import world.resources.AnimalType;
import world.resources.ICargo;
import world.resources.PersonType;
import world.resources.ResourceType;
import world.vehicle.*; // Minden jármű importálása
import world.tile.road.*; // Road és Bridge

import java.io.*;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;
import world.resources.ICargo;

public class SaveManager {

    private static Gson getConfiguredGson() {


        RuntimeTypeAdapterFactory<Building> buildingFactory = RuntimeTypeAdapterFactory.of(Building.class, "BUILDING_TYPE_META")
                .registerSubtype(Farm.class, "Farm")
                .registerSubtype(City.class, "City")
                .registerSubtype(AgriculturalPlant.class, "AgriculturalPlant")
                .registerSubtype(Silo.class, "Silo")
                .registerSubtype(Enclosure.class, "Enclosure")
                .registerSubtype(ResearchLab.class, "ResearchLab")
                .registerSubtype(CloningFacility.class, "CloningFacility")
                .registerSubtype(BusStop.class, "BusStop")
                .registerSubtype(Station.class, "Station");


        RuntimeTypeAdapterFactory<Vehicle> vehicleFactory = RuntimeTypeAdapterFactory.of(Vehicle.class, "VEHICLE_TYPE_META")
                .registerSubtype(Bus.class, "Bus")
                .registerSubtype(FoodTruck.class, "FoodTruck")
                .registerSubtype(AnimalTruck.class, "AnimalTruck");


        RuntimeTypeAdapterFactory<Road> roadFactory = RuntimeTypeAdapterFactory.of(Road.class, "ROAD_TYPE_META")
                .registerSubtype(Road.class, "Road")
                .registerSubtype(Bridge.class, "Bridge");


        JsonSerializer<ICargo> cargoSerializer = (src, typeOfSrc, context) -> {
            if (src == null) return JsonNull.INSTANCE;
            JsonObject obj = new JsonObject();
            obj.addProperty("type", src.getClass().getName());          // pl. "world.resources.ResourceType"
            obj.addProperty("value", ((Enum<?>) src).name());           // pl. "GRAIN"
            return obj;
        };

        JsonDeserializer<ICargo> cargoDeserializer = (json, typeOfT, context) -> {
            if (json == null || json.isJsonNull()) return null;
            // Régi mentési formátum: sima "GRAIN" string
            if (json.isJsonPrimitive()) {
                String enumName = json.getAsString();
                // Végigpróbáljuk az összes lehetséges enum típust
                try { return world.resources.ResourceType.valueOf(enumName); } catch (IllegalArgumentException ignored) {}
                try { return world.resources.AnimalType.valueOf(enumName); } catch (IllegalArgumentException ignored) {}
                try { return world.resources.PersonType.valueOf(enumName); } catch (IllegalArgumentException ignored) {}
                throw new JsonParseException("Ismeretlen ICargo enum érték: " + enumName);
            }
            // Új mentési formátum: { "type": "...", "value": "..." }
            JsonObject obj = json.getAsJsonObject();
            String className = obj.get("type").getAsString();
            String enumName = obj.get("value").getAsString();
            try {
                Class<?> clazz = Class.forName(className);
                return (ICargo) Enum.valueOf((Class<Enum>) clazz, enumName);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Ismeretlen ICargo osztály: " + className, e);
            }
        };

        return new GsonBuilder()
                .registerTypeAdapterFactory(buildingFactory)
                .registerTypeAdapterFactory(vehicleFactory)
                .registerTypeAdapterFactory(roadFactory)
                .registerTypeHierarchyAdapter(ICargo.class, cargoSerializer)
                .registerTypeHierarchyAdapter(ICargo.class, cargoDeserializer)  // ← ÚJ SOR
                .setPrettyPrinting()
                .create();
    }

    public static void saveGame(World world, String fileName) {
        Gson gson = getConfiguredGson();
        try (Writer writer = new FileWriter(fileName)) {
            gson.toJson(world, writer);
            System.out.println("Sikeres mentés: " + fileName);
        } catch (IOException e) {
            System.err.println("Mentés hiba: " + e.getMessage());
        }
    }

    public static World loadGame(String fileName) {
        Gson gson = getConfiguredGson();
        try (Reader reader = new FileReader(fileName)) {
            World world = gson.fromJson(reader, World.class);
            System.out.println("Sikeres betöltés: " + fileName);
            return world;
        } catch (IOException e) {
            System.err.println("Betöltés hiba: " + e.getMessage());
            return null;
        }
    }
}