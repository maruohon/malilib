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
import fi.dy.masa.malilib.registry.Registry;
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

    public void markDirty()
    {
        this.dirty = true;
    }

    public boolean checkIfDirtyAndSaveAndUpdate()
    {
        boolean dirty = false;

        for (CustomHotkeyDefinition hotkey : this.hotkeys)
        {
            if (hotkey.getKeyBind().isDirty())
            {
                dirty = true;
                break;
            }
        }

        if (dirty)
        {
            this.saveToFile();
            Registry.HOTKEY_MANAGER.updateUsedKeys();
        }

        return dirty;
    }

    public boolean saveToFileIfDirty()
    {
        if (this.dirty)
        {
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
            JsonUtils.readArrayElementsIfPresent(obj, "hotkeys", this::readAndAddHotkey);
        }
    }

    protected void readAndAddHotkey(JsonElement el)
    {
        CustomHotkeyDefinition hotkey = CustomHotkeyDefinition.fromJson(el);

        if (hotkey != null)
        {
            this.hotkeys.add(hotkey);
        }
    }

    public boolean saveToFile()
    {
        File dir = ConfigUtils.getActiveConfigDirectory();
        File backupDir = new File(dir, "config_backups");
        File saveFile = new File(dir, MaLiLibReference.MOD_ID + "_custom_hotkeys.json");
        boolean antiDuplicate = MaLiLibConfigs.Generic.CONFIG_BACKUP_ANTI_DUPLICATE.getBooleanValue();

        this.dirty = false;

        return JsonUtils.saveToFile(dir, backupDir, saveFile, 10, antiDuplicate, this::toJson);
    }

    public void loadFromFile()
    {
        File dir = ConfigUtils.getActiveConfigDirectory();
        JsonUtils.loadFromFile(dir, MaLiLibReference.MOD_ID + "_custom_hotkeys.json", this::fromJson);
    }
}
