package malilib.util.data.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import malilib.MaLiLib;
import malilib.util.FileUtils;
import malilib.util.data.BooleanConsumer;
import malilib.util.data.FloatConsumer;

public class JsonUtils
{
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Nullable
    public static JsonObject getNestedObject(JsonObject parent, String key, boolean create)
    {
        if (parent.has(key) == false || parent.get(key).isJsonObject() == false)
        {
            if (create == false)
            {
                return null;
            }

            JsonObject obj = new JsonObject();
            parent.add(key, obj);
            return obj;
        }
        else
        {
            return parent.get(key).getAsJsonObject();
        }
    }

    public static boolean hasBoolean(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);

        if (el != null && el.isJsonPrimitive())
        {
            try
            {
                el.getAsBoolean();
                return true;
            }
            catch (Exception ignore) {}
        }

        return false;
    }

    public static boolean hasInteger(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);

        if (el != null && el.isJsonPrimitive())
        {
            try
            {
                el.getAsInt();
                return true;
            }
            catch (Exception ignore) {}
        }

        return false;
    }

    public static boolean hasLong(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);

        if (el != null && el.isJsonPrimitive())
        {
            try
            {
                el.getAsLong();
                return true;
            }
            catch (Exception ignore) {}
        }

        return false;
    }

    public static boolean hasFloat(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);

        if (el != null && el.isJsonPrimitive())
        {
            try
            {
                el.getAsFloat();
                return true;
            }
            catch (Exception ignore) {}
        }

        return false;
    }

    public static boolean hasDouble(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);

        if (el != null && el.isJsonPrimitive())
        {
            try
            {
                el.getAsDouble();
                return true;
            }
            catch (Exception ignore) {}
        }

        return false;
    }

    public static boolean hasString(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);

        if (el != null && el.isJsonPrimitive())
        {
            try
            {
                el.getAsString();
                return true;
            }
            catch (Exception ignore) {}
        }

        return false;
    }

    public static boolean hasObject(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);
        return el != null && el.isJsonObject();
    }

    public static boolean hasArray(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);
        return el != null && el.isJsonArray();
    }

    public static boolean getBooleanOrDefault(JsonObject obj, String name, boolean defaultValue)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                return obj.get(name).getAsBoolean();
            }
            catch (Exception ignore) {}
        }

        return defaultValue;
    }

    public static int getIntegerOrDefault(JsonObject obj, String name, int defaultValue)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                return obj.get(name).getAsInt();
            }
            catch (Exception ignore) {}
        }

        return defaultValue;
    }

    public static long getLongOrDefault(JsonObject obj, String name, long defaultValue)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                return obj.get(name).getAsLong();
            }
            catch (Exception ignore) {}
        }

        return defaultValue;
    }

    public static float getFloatOrDefault(JsonObject obj, String name, float defaultValue)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                return obj.get(name).getAsFloat();
            }
            catch (Exception ignore) {}
        }

        return defaultValue;
    }

    public static double getDoubleOrDefault(JsonObject obj, String name, double defaultValue)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                return obj.get(name).getAsDouble();
            }
            catch (Exception ignore) {}
        }

        return defaultValue;
    }

    @Nullable
    public static String getStringOrDefault(JsonObject obj, String name, @Nullable String defaultValue)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                return obj.get(name).getAsString();
            }
            catch (Exception ignore) {}
        }

        return defaultValue;
    }

    public static void readBooleanIfExists(JsonObject obj, String name, BooleanConsumer consumer)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                consumer.accept(obj.get(name).getAsBoolean());
            }
            catch (Exception ignore) {}
        }
    }

    public static void readIntegerIfExists(JsonObject obj, String name, IntConsumer consumer)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                consumer.accept(obj.get(name).getAsInt());
            }
            catch (Exception ignore) {}
        }
    }

    public static void readLongIfExists(JsonObject obj, String name, LongConsumer consumer)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                consumer.accept(obj.get(name).getAsLong());
            }
            catch (Exception ignore) {}
        }
    }

    public static void readFloatIfExists(JsonObject obj, String name, FloatConsumer consumer)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                consumer.accept(obj.get(name).getAsFloat());
            }
            catch (Exception ignore) {}
        }
    }

    public static void readDoubleIfExists(JsonObject obj, String name, DoubleConsumer consumer)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                consumer.accept(obj.get(name).getAsDouble());
            }
            catch (Exception ignore) {}
        }
    }

    public static void readStringIfExists(JsonObject obj, String name, Consumer<String> consumer)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                consumer.accept(obj.get(name).getAsString());
            }
            catch (Exception ignore) {}
        }
    }

    public static boolean getBoolean(JsonObject obj, String name)
    {
        return getBooleanOrDefault(obj, name, false);
    }

    public static int getInteger(JsonObject obj, String name)
    {
        return getIntegerOrDefault(obj, name, 0);
    }

    public static long getLong(JsonObject obj, String name)
    {
        return getLongOrDefault(obj, name, 0);
    }

    public static float getFloat(JsonObject obj, String name)
    {
        return getFloatOrDefault(obj, name, 0);
    }

    public static double getDouble(JsonObject obj, String name)
    {
        return getDoubleOrDefault(obj, name, 0);
    }

    @Nullable
    public static String getString(JsonObject obj, String name)
    {
        return getStringOrDefault(obj, name, null);
    }

    public static void addIfNotEqual(JsonObject obj, String name, int value, int excludeValue)
    {
        if (value != excludeValue)
        {
            obj.addProperty(name, value);
        }
    }

    public static void addIfNotEqual(JsonObject obj, String name, float value, float excludeValue)
    {
        if (value != excludeValue)
        {
            obj.addProperty(name, value);
        }
    }

    public static void addIfNotEqual(JsonObject obj, String name, double value, double excludeValue)
    {
        if (value != excludeValue)
        {
            obj.addProperty(name, value);
        }
    }

    public static void addIfNotEqual(JsonObject obj, String name, boolean value, boolean excludeValue)
    {
        if (value != excludeValue)
        {
            obj.addProperty(name, value);
        }
    }

    public static void addIfNotEqual(JsonObject obj, String name, String value, String excludeValue)
    {
        if (value.equals(excludeValue) == false)
        {
            obj.addProperty(name, value);
        }
    }

    public static void addStringIfNotNull(JsonObject obj, String name, @Nullable String value)
    {
        if (value != null)
        {
            obj.addProperty(name, value);
        }
    }

    public static boolean hasBlockPos(JsonObject obj, String name)
    {
        return blockPosFromJson(obj, name) != null;
    }

    public static JsonArray blockPosToJson(Vec3i pos)
    {
        JsonArray arr = new JsonArray();

        arr.add(pos.getX());
        arr.add(pos.getY());
        arr.add(pos.getZ());

        return arr;
    }

    @Nullable
    public static BlockPos blockPosFromJson(JsonObject obj, String name)
    {
        if (hasArray(obj, name))
        {
            JsonArray arr = obj.getAsJsonArray(name);

            if (arr.size() == 3)
            {
                try
                {
                    return new BlockPos(arr.get(0).getAsInt(), arr.get(1).getAsInt(), arr.get(2).getAsInt());
                }
                catch (Exception ignore) {}
            }
        }

        return null;
    }

    public static void readBlockPosIfExists(JsonObject obj, String name, Consumer<BlockPos> consumer)
    {
        BlockPos pos = blockPosFromJson(obj, name);

        if (pos != null)
        {
            consumer.accept(pos);
        }
    }

    public static void writeBlockPosIfNotNull(JsonObject obj, String name, @Nullable BlockPos pos)
    {
        if (pos != null)
        {
            obj.add(name, blockPosToJson(pos));
        }
    }

    public static boolean hasVec3d(JsonObject obj, String name)
    {
        return vec3dFromJson(obj, name) != null;
    }

    public static JsonArray vec3dToJson(Vec3d vec)
    {
        JsonArray arr = new JsonArray();

        arr.add(vec.x);
        arr.add(vec.y);
        arr.add(vec.z);

        return arr;
    }

    @Nullable
    public static Vec3d vec3dFromJson(JsonObject obj, String name)
    {
        if (hasArray(obj, name))
        {
            JsonArray arr = obj.getAsJsonArray(name);

            if (arr.size() == 3)
            {
                try
                {
                    return new Vec3d(arr.get(0).getAsDouble(), arr.get(1).getAsDouble(), arr.get(2).getAsDouble());
                }
                catch (Exception ignore) {}
            }
        }

        return null;
    }

    public static void readVec3dIfExists(JsonObject obj, String name, Consumer<Vec3d> consumer)
    {
        Vec3d vec = vec3dFromJson(obj, name);

        if (vec != null)
        {
            consumer.accept(vec);
        }
    }

    public static JsonArray stringListAsArray(List<String> list)
    {
        JsonArray arr = new JsonArray();

        for (String str : list)
        {
            arr.add(str);
        }

        return arr;
    }

    public static List<String> arrayAsStringList(JsonArray arr)
    {
        List<String> list = new ArrayList<>();
        final int count = arr.size();

        for (int i = 0; i < count; ++i)
        {
            list.add(arr.get(i).getAsString());
        }

        return list;
    }

    public static void readObjectIfExists(JsonElement el, String arrayName, Consumer<JsonObject> objectConsumer)
    {
        if (el.isJsonObject() == false)
        {
            return;
        }

        JsonObject obj = el.getAsJsonObject();

        if (hasObject(obj, arrayName))
        {
            JsonObject arr = obj.get(arrayName).getAsJsonObject();
            objectConsumer.accept(arr);
        }
    }

    public static void readArrayIfExists(JsonElement el, String arrayName, Consumer<JsonArray> arrayConsumer)
    {
        if (el.isJsonObject() == false)
        {
            return;
        }

        JsonObject obj = el.getAsJsonObject();

        if (hasArray(obj, arrayName))
        {
            JsonArray arr = obj.get(arrayName).getAsJsonArray();
            arrayConsumer.accept(arr);
        }
    }

    public static void readArrayElementsIfExists(JsonElement el, String arrayName, Consumer<JsonElement> elementConsumer)
    {
        readArrayIfExists(el, arrayName, (arr) -> readArrayElements(arr, elementConsumer));
    }

    public static void readArrayElements(JsonArray arr, Consumer<JsonElement> elementConsumer)
    {
        int size = arr.size();

        for (int i = 0; i < size; ++i)
        {
            elementConsumer.accept(arr.get(i));
        }
    }

    public static void readArrayElementsIfObjects(JsonElement el, String arrayName, Consumer<JsonObject> elementConsumer)
    {
        readArrayIfExists(el, arrayName, (arr) -> readArrayElementsAsObjects(arr, elementConsumer));
    }

    public static void readArrayElementsAsObjects(JsonArray arr, Consumer<JsonObject> elementConsumer)
    {
        int size = arr.size();

        for (int i = 0; i < size; ++i)
        {
            JsonElement el = arr.get(i);

            if (el.isJsonObject())
            {
                elementConsumer.accept(el.getAsJsonObject());
            }
        }
    }

    public static <T> JsonArray toArray(Collection<T> values, Function<T, JsonElement> elementSerializer)
    {
        JsonArray arr = new JsonArray();

        for (T value : values)
        {
            arr.add(elementSerializer.apply(value));
        }

        return arr;
    }

    // https://stackoverflow.com/questions/29786197/gson-jsonobject-copy-value-affected-others-jsonobject-instance
    @Nonnull
    public static JsonObject deepCopy(@Nonnull JsonObject jsonObject)
    {
        JsonObject result = new JsonObject();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet())
        {
            result.add(entry.getKey(), deepCopy(entry.getValue()));
        }

        return result;
    }

    @Nonnull
    public static JsonArray deepCopy(@Nonnull JsonArray jsonArray)
    {
        JsonArray result = new JsonArray();

        for (JsonElement e : jsonArray)
        {
            result.add(deepCopy(e));
        }

        return result;
    }

    @Nonnull
    public static JsonElement deepCopy(@Nonnull JsonElement jsonElement)
    {
        if (jsonElement.isJsonPrimitive() || jsonElement.isJsonNull())
        {
            return jsonElement; // these are immutable anyway
        }
        else if (jsonElement.isJsonObject())
        {
            return deepCopy(jsonElement.getAsJsonObject());
        }
        else if (jsonElement.isJsonArray())
        {
            return deepCopy(jsonElement.getAsJsonArray());
        }
        else
        {
            throw new UnsupportedOperationException("Unsupported element: " + jsonElement);
        }
    }

    @Nullable
    public static JsonElement parseJsonFromString(String str)
    {
        try
        {
            JsonParser parser = new JsonParser();
            return parser.parse(str);
        }
        catch (Exception ignore) {}

        return null;
    }

    @Nullable
    public static JsonElement parseJsonFile(Path file)
    {
        if (Files.isRegularFile(file) && Files.isReadable(file))
        {
            try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8))
            {
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(reader);

                return element;
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("Failed to parse the JSON file '{}'", file.toAbsolutePath(), e);
            }
        }

        return null;
    }

    /**
     * Converts the given JsonElement tree into its string representation.
     * @param compact if true, then the output is written on one line without spaces or line breaks.
     * @return The string representation of the given JSON element
     */
    public static String jsonToString(JsonElement element, boolean compact)
    {
        Gson gson = compact ? new Gson() : GSON;
        return gson.toJson(element);
    }

    public static boolean writeJsonToFile(JsonElement root, Path file)
    {
        return writeJsonToFile(root, file, GSON);
    }

    public static boolean writeJsonToFile(final JsonElement root, final Path file, final Gson gson)
    {
        return FileUtils.writeDataToFile(file, w -> {
            try
            {
                w.write(gson.toJson(root));
            }
            catch (IOException e)
            {
                MaLiLib.LOGGER.warn("Failed to write JSON data to file '{}'", file.toAbsolutePath(), e);
            }
        });
    }

    public static void loadFromFile(Path dir, String fileName, Consumer<JsonElement> dataConsumer)
    {
        Path file = dir.resolve(fileName);
        loadFromFile(file, dataConsumer);
    }

    public static void loadFromFile(Path file, Consumer<JsonElement> dataConsumer)
    {
        JsonElement element = parseJsonFile(file);

        if (element != null)
        {
            dataConsumer.accept(element);
        }
    }
}
