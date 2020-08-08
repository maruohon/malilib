package fi.dy.masa.malilib.config;

import java.util.List;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.util.StringUtils;

public class BaseConfigCategory implements ConfigOptionCategory
{
    protected final String name;
    protected final String displayName;
    protected final boolean saveToFile;
    protected final boolean showOnConfigScreen;
    protected final boolean useKeyBindSearch;
    protected final List<? extends ConfigOption<?>> configs;

    public BaseConfigCategory(String name, String displayName, boolean showOnConfigScreen,
                              boolean saveToFile, boolean useKeyBindSearch, List<? extends ConfigOption<?>> configs)
    {
        this.name = name;
        this.displayName = displayName;
        this.showOnConfigScreen = showOnConfigScreen;
        this.saveToFile = saveToFile;
        this.useKeyBindSearch = useKeyBindSearch;
        this.configs = configs;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.displayName);
    }

    @Override
    public boolean showOnConfigScreen()
    {
        return this.showOnConfigScreen;
    }

    @Override
    public boolean shouldSaveToFile()
    {
        return this.saveToFile;
    }

    @Override
    public boolean useKeyBindSearch()
    {
        return this.useKeyBindSearch;
    }

    @Override
    public List<? extends ConfigOption<?>> getConfigOptions()
    {
        return this.configs;
    }

    /**
     * Creates a normal config category that is shown on the config screen
     * and saved to a config file normally.
     */
    public static BaseConfigCategory normal(String name, String displayName, List<? extends ConfigOption<?>> configs)
    {
        return new BaseConfigCategory(name, displayName, true, true, hasHotkeys(configs), configs);
    }

    /**
     * Creates a config category that is not shown on the config screen,
     * but which is still saved to the config file
     */
    public static BaseConfigCategory hidden(String name, String displayName, List<? extends ConfigOption<?>> configs)
    {
        return new BaseConfigCategory(name, displayName, false, true, false, configs);
    }

    /**
     * Creates a config category that is neither shown on the config screen,
     * not saved to a file.
     */
    public static BaseConfigCategory hiddenNonSaved(String name, String displayName, List<? extends ConfigOption<?>> configs)
    {
        return new BaseConfigCategory(name, displayName, false, false, false, configs);
    }

    public static boolean hasHotkeys(List<? extends ConfigOption<?>> configs)
    {
        boolean hotkeys = false;

        for (ConfigInfo cfg : configs)
        {
            if (cfg instanceof Hotkey)
            {
                hotkeys = true;
                break;
            }
        }

        return hotkeys;
    }
}
