package fi.dy.masa.malilib.input;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.MaLiLibConfigScreen;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.util.ConfigUtils;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.BackupUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.json.JsonUtils;

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
        return ImmutableList.of(new HotkeyCategory(MaLiLibReference.MOD_INFO,
                                                   "malilib.hotkeys.category.custom", this::getAllHotkeys));
    }

    public List<CustomHotkeyDefinition> getAllCustomHotkeys()
    {
        return this.hotkeys;
    }

    /**
     * Note: This is only intended to be used for the config search stuff, which needs
     * the configs to be wrapped in a tab to get the owning mod and category from it
     */
    public BaseConfigTab getAllCustomHotkeysAsTabForConfigSearch()
    {
        String name = StringUtils.translate("malilib.screen.tab.custom_hotkeys");
        return new BaseConfigTab(MaLiLibReference.MOD_INFO, name, name, 200,
                                 this.getAllCustomHotkeys(), MaLiLibConfigScreen::create);
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
            JsonUtils.readArrayElementsIfExists(obj, "hotkeys", this::readAndAddHotkey);
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
        Path configDir = ConfigUtils.getActiveConfigDirectoryPath();
        File saveFile = configDir.resolve(MaLiLibReference.MOD_ID).resolve("custom_hotkeys.json").toFile();
        File backupDir = configDir.resolve("backups").resolve(MaLiLibReference.MOD_ID).toFile();

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
        Path configDir = ConfigUtils.getActiveConfigDirectoryPath();
        File saveFile = configDir.resolve(MaLiLibReference.MOD_ID).resolve("custom_hotkeys.json").toFile();
        JsonUtils.loadFromFile(saveFile, this::fromJson);
    }
}
