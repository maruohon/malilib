package fi.dy.masa.malilib.gui.config;

import java.util.List;
import java.util.function.BiFunction;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.widget.button.ButtonActionListener;
import fi.dy.masa.malilib.gui.listener.ConfigScreenTabButtonListener;
import fi.dy.masa.malilib.util.StringUtils;

public class BaseConfigTab implements ConfigTab
{
    private final List<? extends ConfigInfo> configs;
    private final String name;
    private final String translationKey;
    private final String modName;
    private final int configWidth;
    private final BiFunction<ConfigTab, BaseConfigScreen, ButtonActionListener> listenerFactory;

    public BaseConfigTab(String translationKey, String modName, int configWidth, List<? extends ConfigInfo> configs)
    {
        this(translationKey, modName, configWidth, configs, ConfigScreenTabButtonListener::new);
    }

    public BaseConfigTab(String translationKey, String modName, int configWidth,
                         List<? extends ConfigInfo> configs, BiFunction<ConfigTab, BaseConfigScreen, ButtonActionListener> listenerFactory)
    {
        this.name = translationKey.substring(translationKey.lastIndexOf(".") + 1);
        this.translationKey = translationKey;
        this.modName = modName;
        this.configWidth = configWidth;
        this.configs = configs;
        this.listenerFactory = listenerFactory;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public String getModName()
    {
        return this.modName;
    }

    @Override
    public int getConfigWidth()
    {
        return this.configWidth;
    }

    @Override
    public boolean showOnConfigScreen()
    {
        return true;
    }

    @Override
    public List<? extends ConfigInfo> getConfigsForDisplay()
    {
        return this.configs;
    }

    @Override
    public ButtonActionListener getButtonActionListener(BaseConfigScreen gui)
    {
        return this.listenerFactory.apply(this, gui);
    }
}
