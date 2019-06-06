package fi.dy.masa.malilib;

import java.io.File;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;

public class MaLiLibConfigs implements IConfigHandler
{
    private static final String CONFIG_FILE_NAME = MaLiLibReference.MOD_ID + ".json";

    public static class Generic
    {
        public static final ConfigHotkey IGNORED_KEYS       = new ConfigHotkey("ignoredKeys", "", "Any keys set here will be completely ignored");
        public static final ConfigHotkey OPEN_GUI_CONFIGS   = new ConfigHotkey("openGuiConfigs", "A,C", "Open the in-game malilib config GUI");

        public static final ImmutableList<IConfigValue> OPTIONS = ImmutableList.of(
                IGNORED_KEYS,
                OPEN_GUI_CONFIGS
        );
    }

    public static class Debug
    {
        public static final ConfigBoolean KEYBIND_DEBUG             = new ConfigBoolean("keybindDebugging", false, "When enabled, key presses and held keys are\nprinted to the game console (and the action bar, if enabled)");
        public static final ConfigBoolean KEYBIND_DEBUG_ACTIONBAR   = new ConfigBoolean("keybindDebuggingIngame", true, "If enabled, then the messages from 'keybindDebugging'\nare also printed to the in-game action bar");

        public static final ImmutableList<IConfigValue> OPTIONS = ImmutableList.of(
                KEYBIND_DEBUG,
                KEYBIND_DEBUG_ACTIONBAR
        );
    }

    public static void loadFromFile()
    {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead())
        {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();

                ConfigUtils.readConfigBase(root, "Generic", Generic.OPTIONS);
            }
        }
    }

    public static void saveToFile()
    {
        File dir = FileUtils.getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs())
        {
            JsonObject root = new JsonObject();

            ConfigUtils.writeConfigBase(root, "Generic", Generic.OPTIONS);

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    @Override
    public void onConfigsChanged()
    {
        saveToFile();
        loadFromFile();
    }

    @Override
    public void load()
    {
        loadFromFile();
    }

    @Override
    public void save()
    {
        saveToFile();
    }
}
