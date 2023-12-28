package malilib.util.text;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import malilib.MaLiLibReference;
import malilib.config.util.ConfigUtils;
import malilib.util.BackupUtils;
import malilib.util.StringUtils;
import malilib.util.data.json.JsonUtils;

public class TranslationOverrideManager
{
    protected final Map<String, String> translationOverrides = new HashMap<>();
    protected boolean dirty;

    @Nullable
    public String getOverriddenTranslation(String translationKey, Object... args)
    {
        String translation = this.translationOverrides.get(translationKey);

        if (translation != null)
        {
            try
            {
                return String.format(translation, args);
            }
            catch (Exception e)
            {
                return StringUtils.translate("malilib.message.error.translation_override.format_error");
            }
        }

        return null;
    }

    public void addOverride(String translationKey, String override)
    {
        this.translationOverrides.put(translationKey, override);
        this.dirty = true;
    }

    public void removeOverride(String translationKey)
    {
        this.translationOverrides.remove(translationKey);
        this.dirty = true;
    }

    public List<Map.Entry<String, String>> getAllOverrides()
    {
        List<Map.Entry<String, String>> list = new ArrayList<>(this.translationOverrides.entrySet());
        list.sort(Map.Entry.comparingByKey());
        return list;
    }

    protected JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        for (Map.Entry<String, String> entry : this.translationOverrides.entrySet())
        {
            obj.addProperty(entry.getKey(), entry.getValue());
        }

        return obj;
    }

    protected void fromJson(JsonElement el)
    {
        if (el.isJsonObject() == false)
        {
            return;
        }

        this.translationOverrides.clear();

        JsonObject obj = el.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : obj.entrySet())
        {
            if (entry.getValue().isJsonPrimitive())
            {
                this.translationOverrides.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
    }

    public boolean saveToFileIfDirty()
    {
        if (this.dirty)
        {
            return this.saveToFile();
        }

        return false;
    }

    public boolean saveToFile()
    {
        Path configDir = ConfigUtils.getActiveConfigDirectory();
        Path saveFile = configDir.resolve(MaLiLibReference.MOD_ID).resolve("translation_overrides.json");
        Path backupDir = configDir.resolve("backups").resolve(MaLiLibReference.MOD_ID);

        if (BackupUtils.createRegularBackup(saveFile, backupDir) &&
            JsonUtils.writeJsonToFile(this.toJson(), saveFile))
        {
            this.dirty = false;
            return true;
        }

        return false;
    }

    public void loadFromFile()
    {
        Path configDir = ConfigUtils.getActiveConfigDirectory();
        Path saveFile = configDir.resolve(MaLiLibReference.MOD_ID).resolve("translation_overrides.json");
        JsonUtils.loadFromFile(saveFile, this::fromJson);
    }
}
