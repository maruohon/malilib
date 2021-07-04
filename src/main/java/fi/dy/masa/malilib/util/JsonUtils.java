package fi.dy.masa.malilib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
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
import fi.dy.masa.malilib.MaLiLib;

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

    public static void readArrayElementsIfPresent(JsonObject obj, String arrayName, Consumer<JsonElement> elementConsumer)
    {
        if (hasArray(obj, arrayName))
        {
            JsonArray arr = obj.get(arrayName).getAsJsonArray();
            readArrayElements(arr, elementConsumer);
        }
    }

    public static void readArrayElements(JsonArray arr, Consumer<JsonElement> elementConsumer)
    {
        int size = arr.size();

        for (int i = 0; i < size; ++i)
        {
            JsonElement e = arr.get(i);
            elementConsumer.accept(e);
        }
    }

    public static void readArrayIfPresent(JsonObject obj, String arrayName, Consumer<JsonArray> arrayConsumer)
    {
        if (hasArray(obj, arrayName))
        {
            JsonArray arr = obj.get(arrayName).getAsJsonArray();
            arrayConsumer.accept(arr);
        }
    }

    public static void readObjectIfPresent(JsonObject obj, String arrayName, Consumer<JsonObject> objectConsumer)
    {
        if (hasObject(obj, arrayName))
        {
            JsonObject arr = obj.get(arrayName).getAsJsonObject();
            objectConsumer.accept(arr);
        }
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
    public static JsonElement parseJsonFile(File file)
    {
        if (file != null && file.exists() && file.isFile() && file.canRead())
        {
            String fileName = file.getAbsolutePath();

            try
            {
                JsonParser parser = new JsonParser();
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);

                JsonElement element = parser.parse(reader);
                reader.close();

                return element;
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("Failed to parse the JSON file '{}'", fileName, e);
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

    public static boolean writeJsonToFile(JsonElement root, File file)
    {
        return writeJsonToFile(GSON, root, file);
    }

    public static boolean writeJsonToFile(Gson gson, JsonElement root, File file)
    {
        OutputStreamWriter writer = null;
        File fileTmp = new File(file.getParentFile(), file.getName() + ".tmp");

        if (fileTmp.exists())
        {
            fileTmp = new File(file.getParentFile(), UUID.randomUUID() + ".tmp");
        }

        try
        {
            writer = new OutputStreamWriter(new FileOutputStream(fileTmp), StandardCharsets.UTF_8);
            writer.write(gson.toJson(root));
            writer.close();

            if (file.exists() && file.isFile() && file.delete() == false)
            {
                MaLiLib.LOGGER.warn("Failed to delete file '{}'", file.getAbsolutePath());
            }

            return fileTmp.renameTo(file);
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to write JSON data to file '{}'", fileTmp.getAbsolutePath(), e);
        }
        finally
        {
            try
            {
                if (writer != null)
                {
                    writer.close();
                }
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.warn("Failed to close JSON file", e);
            }
        }

        return false;
    }

    /**
     * Saves JSON data to a file, optionally creating a rolling backup copy first.
     * @param dir the directory the save file is in. This will be created if it doesn't exist.
     * @param backupDir the directory to keep the rolling backups in, if enabled
     * @param saveFile the file to save teh data to
     * @param backupCount the number of backup copies to keep. Set to 0 to disable backups.
     * @param dataSource the source of the JSON data
     * @return true on success, false on failure
     */
    public static boolean saveToFile(File dir, File backupDir, File saveFile,
                                     int backupCount, Supplier<JsonElement> dataSource)
    {
        return saveToFile(dir, backupDir, saveFile, backupCount, true, dataSource);
    }

    /**
     * Saves JSON data to a file, optionally creating a rolling backup copy first.
     * @param dir the directory the save file is in. This will be created if it doesn't exist.
     * @param backupDir the directory to keep the rolling backups in, if enabled
     * @param saveFile the file to save teh data to
     * @param backupCount the number of backup copies to keep. Set to 0 to disable backups.
     * @param deDuplicate if true, then existing identical backups are searched for first and re-used
     * @param dataSource the source of the JSON data
     * @return true on success, false on failure
     */
    public static boolean saveToFile(File dir, File backupDir, File saveFile,
                                     int backupCount, boolean deDuplicate, Supplier<JsonElement> dataSource)
    {
        if (dir.exists() == false && dir.mkdirs() == false)
        {
            MaLiLib.LOGGER.error("Failed to create directory '{}'", dir.getName());
        }

        if (dir.exists() && dir.isDirectory())
        {
            if (backupCount > 0)
            {
                BackupUtils.createRollingBackup(saveFile, backupDir, ".bak_", backupCount, deDuplicate);
            }

            return JsonUtils.writeJsonToFile(dataSource.get(), saveFile);
        }

        return false;
    }

    public static void loadFromFile(File dir, String fileName, Consumer<JsonElement> dataConsumer)
    {
        File saveFile = new File(dir, fileName);

        if (saveFile.exists() && saveFile.isFile() && saveFile.canRead())
        {
            JsonElement element = JsonUtils.parseJsonFile(saveFile);

            if (element != null)
            {
                dataConsumer.accept(element);
            }
        }
    }
}
