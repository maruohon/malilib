package fi.dy.masa.malilib.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fi.dy.masa.malilib.MaLiLib;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

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
            catch (Exception e) {}
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
            catch (Exception e) {}
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
            catch (Exception e) {}
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
            catch (Exception e) {}
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
            catch (Exception e) {}
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
            catch (Exception e) {}
        }

        return false;
    }

    public static boolean hasObject(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);

        if (el != null && el.isJsonObject())
        {
            return true;
        }

        return false;
    }

    public static boolean hasArray(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);

        if (el != null && el.isJsonArray())
        {
            return true;
        }

        return false;
    }

    public static boolean getBooleanOrDefault(JsonObject obj, String name, boolean defaultValue)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                return obj.get(name).getAsBoolean();
            }
            catch (Exception e) {}
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
            catch (Exception e) {}
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
            catch (Exception e) {}
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
            catch (Exception e) {}
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
            catch (Exception e) {}
        }

        return defaultValue;
    }

    public static String getStringOrDefault(JsonObject obj, String name, String defaultValue)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                return obj.get(name).getAsString();
            }
            catch (Exception e) {}
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

    public static JsonArray blockPosToJson(BlockPos pos)
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
                catch (Exception e)
                {
                }
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
                catch (Exception e)
                {
                }
            }
        }

        return null;
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
    public static JsonElement parseJsonFile(File file)
    {
        if (file != null && file.exists() && file.isFile() && file.canRead())
        {
            String fileName = file.getAbsolutePath();

            try
            {
                JsonParser parser = new JsonParser();
                FileReader reader = new FileReader(file);

                JsonElement element = parser.parse(reader);
                reader.close();

                return element;
            }
            catch (Exception e)
            {
                MaLiLib.logger.error("Failed to parse the JSON file '{}'", fileName, e);
            }
        }

        return null;
    }

    public static boolean writeJsonToFile(JsonObject root, File file)
    {
        FileWriter writer = null;

        try
        {
            writer = new FileWriter(file);
            writer.write(GSON.toJson(root));
            writer.close();

            return true;
        }
        catch (IOException e)
        {
            MaLiLib.logger.warn("Failed to write JSON data to file '{}'", file.getAbsolutePath(), e);
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
                MaLiLib.logger.warn("Failed to close JSON file", e);
            }
        }

        return false;
    }
}
