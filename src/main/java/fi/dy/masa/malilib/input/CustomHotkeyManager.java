package fi.dy.masa.malilib.input;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.util.ConfigUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class CustomHotkeyManager implements HotkeyProvider
{
    public static final CustomHotkeyManager INSTANCE = new CustomHotkeyManager();

    protected final List<CustomHotkeyDefinition> hotkeys = new ArrayList<>();
    protected boolean dirty;

    @Override
    public List<? extends Hotkey> getAllHotkeys()
    {
        return this.hotkeys;
    }

    @Override
    public List<HotkeyCategory> getHotkeysByCategories()
    {
        return ImmutableList.of(new HotkeyCategory(ModInfo.NO_MOD, "malilib.hotkeys.category.custom" , this::getAllHotkeys));
    }

    public List<CustomHotkeyDefinition> getAllCustomHotkeys()
    {
        return this.hotkeys;
    }

    public void addCustomHotkey(CustomHotkeyDefinition hotkey)
    {
        this.hotkeys.add(hotkey);
        this.dirty = true;
    }

    public void removeCustomHotkey(CustomHotkeyDefinition hotkey)
    {
        this.hotkeys.remove(hotkey);
        this.dirty = true;
    }

    public void clear()
    {
        this.hotkeys.clear();
        this.dirty = false;
    }

    public void checkIfDirty()
    {
        for (CustomHotkeyDefinition hotkey : this.hotkeys)
        {
            if (hotkey.getKeyBind().isDirty())
            {
                this.dirty = true;
                break;
            }
        }
    }

    public boolean saveToFileIfDirty()
    {
        if (this.dirty)
        {
            this.dirty = false;
            return this.saveToFile();
        }

        return false;
    }

    protected JsonObject toJson()
    {
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();

        for (CustomHotkeyDefinition hotkey : this.hotkeys)
        {
            arr.add(hotkey.toJson());
            hotkey.getKeyBind().cacheSavedValue();
        }

        obj.add("hotkeys", arr);

        return obj;
    }

    protected void fromJson(JsonElement el)
    {
        this.clear();

        if (el.isJsonObject())
        {
            JsonObject obj = el.getAsJsonObject();

            if (JsonUtils.hasArray(obj, "hotkeys"))
            {
                JsonArray arr = obj.get("hotkeys").getAsJsonArray();
                int count = arr.size();

                for (int i = 0; i < count; ++i)
                {
                    JsonElement e = arr.get(i);

                    if (e.isJsonObject())
                    {
                        this.hotkeys.add(CustomHotkeyDefinition.fromJson(e.getAsJsonObject()));
                    }
                }
            }
        }
    }

    public boolean saveToFile()
    {
        File dir = ConfigUtils.getActiveConfigDirectory();
        File backupDir = new File(dir, "config_backups");
        File saveFile = new File(dir, MaLiLibReference.MOD_ID + "_custom_hotkeys.json");
        boolean antiDuplicate = MaLiLibConfigs.Generic.CONFIG_BACKUP_ANTI_DUPLICATE.getBooleanValue();

        return JsonUtils.saveToFile(dir, backupDir, saveFile, 10, antiDuplicate, this::toJson);
    }

    public void loadFromFile()
    {
        File dir = ConfigUtils.getActiveConfigDirectory();
        JsonUtils.loadFromFile(dir, MaLiLibReference.MOD_ID + "_custom_hotkeys.json", this::fromJson);
    }
}
