package fi.dy.masa.malilib;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.config.options.IConfigBase;
import fi.dy.masa.malilib.config.options.IConfigValue;

public class MaLiLibConfigs implements IConfigHandler
{
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

    @Override
    public String getConfigFileName()
    {
        return MaLiLibReference.MOD_ID + ".json";
    }

    @Override
    public Map<String, List<? extends IConfigBase>> getConfigsPerCategories()
    {
        Map<String, List<? extends IConfigBase>> map = new LinkedHashMap<>();

        map.put("Generic",  Generic.OPTIONS);
        map.put("Debug",    Debug.OPTIONS);

        return map;
    }

    @Override
    public boolean shouldSaveCategoryToFile(String category)
    {
        return category.equals("Debug") == false;
    }
}
