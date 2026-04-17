package engine;

import com.google.gson.*;
import java.lang.reflect.Type;
/*
A json fájl betöltéskor nem tudja kezelni az absztakt osztályokat, interfaceeket, ezért hozzá kell adni
egy külön mezőt az azonosításhoz. Ezt teszi a universal adapter
 */
public class UniversalAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    private static final String CLASS_META_KEY = "CLASS_TYPE"; //osztály pontos típusát jelöli

    // mentés
    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        // lefordítjuk az objektumot osztály alapján
        JsonElement element = context.serialize(src, src.getClass());
        JsonObject result = element.getAsJsonObject();

        // hozzá tesszük a típust
        result.addProperty(CLASS_META_KEY, src.getClass().getName());

        return result;
    }

    // betöltés
    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement typeElement = jsonObject.get(CLASS_META_KEY);

        if (typeElement != null) {
            String className = typeElement.getAsString();
            try {
                // megkeressük az osztályt név alapján
                Class<?> clazz = Class.forName(className);

                // betöltés
                return context.deserialize(jsonObject, clazz);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("ismeretlen osztály a mentésben: " + className, e);
            }
        }

        throw new JsonParseException("hiányzik a CLASS_TYPE az objektumból");
    }
}