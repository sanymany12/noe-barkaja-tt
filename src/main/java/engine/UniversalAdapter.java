package engine;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/*
A json fájl betöltéskor nem tudja kezelni az absztakt osztályokat, interfaceeket, ezért hozzá kell adni
egy külön mezőt az azonosításhoz. Ez a Factory gondoskodik a bombabiztos mentésről/betöltésről!
*/
public class UniversalAdapter implements TypeAdapterFactory {

    private static final String CLASS_META_KEY = "CLASS_TYPE"; // osztály pontos típusát jelöli
    private final Class<?> baseClass;

    // Konstruktor, ami megkapja, melyik ősosztályra vagy interfészre figyeljen
    public UniversalAdapter(Class<?> baseClass) {
        this.baseClass = baseClass;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        // Ha az adott osztály nem az általunk figyelt ősosztály leszármazottja/implementációja, ignoráljuk
        if (!baseClass.isAssignableFrom(type.getRawType())) {
            return null;
        }

        // Elkérjük a Gson adapterét, így elkerüljük a végtelen hurkot!
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

        return new TypeAdapter<T>() {
            // MENTÉS
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }

                // Gyári adapterrel lefordítjuk JSON fába
                JsonElement jsonTree = delegate.toJsonTree(value);

                JsonObject wrapper = new JsonObject();
                wrapper.addProperty(CLASS_META_KEY, value.getClass().getName());
                System.out.println(value.getClass().getName());

                // Enumok/Primitívek vs. Normál Objektumok szétválasztása
                if (jsonTree.isJsonObject()) {
                    wrapper.add("PROPERTIES", jsonTree);
                } else {
                    wrapper.add("ENUM_VALUE", jsonTree);
                }

                elementAdapter.write(out, wrapper);
            }

            // BETÖLTÉS
            @Override
            public T read(JsonReader in) throws IOException {
                JsonElement jsonElement = elementAdapter.read(in);
                if (jsonElement.isJsonNull()) {
                    return null;
                }

                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonElement classTypeElement = jsonObject.get(CLASS_META_KEY);

                if (classTypeElement == null) {
                    throw new JsonParseException("Hiányzik a CLASS_TYPE az objektumból!");
                }

                String className = classTypeElement.getAsString();
                try {
                    // Megkeressük az osztályt név alapján
                    Class<?> clazz = Class.forName(className);

                    // Lekérjük a konkrét osztály adapterét a belső adatokhoz
                    @SuppressWarnings("unchecked")
                    TypeAdapter<T> specificAdapter = (TypeAdapter<T>) gson.getDelegateAdapter(UniversalAdapter.this, TypeToken.get(clazz));

                    // Ha Enum/Primitív volt, azt töltjük be, egyébként az objektum tulajdonságait
                    if (jsonObject.has("ENUM_VALUE")) {
                        return specificAdapter.fromJsonTree(jsonObject.get("ENUM_VALUE"));
                    }
                    return specificAdapter.fromJsonTree(jsonObject.get("PROPERTIES"));

                } catch (ClassNotFoundException e) {
                    throw new JsonParseException("Ismeretlen osztály a mentésben: " + className, e);
                }
            }
        };
    }
}