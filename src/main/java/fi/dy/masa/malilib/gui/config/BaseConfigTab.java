package fi.dy.masa.malilib.gui.config;

import java.util.List;
import java.util.function.BiFunction;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.button.ButtonActionListener;
import fi.dy.masa.malilib.gui.listener.ButtonListenerConfigGuiTab;
import fi.dy.masa.malilib.util.StringUtils;

public class BaseConfigTab implements ConfigTab
{
    private final List<? extends ConfigInfo> configs;
    private final String name;
    private final String translationKey;
    private final int configWidth;
    private final boolean useKeyBindSearch;
    private final BiFunction<ConfigTab, BaseConfigScreen, ButtonActionListener> listenerFactory;

    public BaseConfigTab(String translationKey, int configWidth, boolean useKeyBindSearch, List<? extends ConfigInfo> configs)
    {
        this(translationKey, configWidth, useKeyBindSearch, configs, ButtonListenerConfigGuiTab::new);
    }

    public BaseConfigTab(String translationKey, int configWidth, boolean useKeyBindSearch,
                         List<? extends ConfigInfo> configs, BiFunction<ConfigTab, BaseConfigScreen, ButtonActionListener> listenerFactory)
    {
        this.name = translationKey.substring(translationKey.lastIndexOf(".") + 1);
        this.translationKey = translationKey;
        this.configWidth = configWidth;
        this.useKeyBindSearch = useKeyBindSearch;
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
    public int getConfigWidth()
    {
        return this.configWidth;
    }

    @Override
    public boolean useKeyBindSearch()
    {
        return this.useKeyBindSearch;
    }

    @Override
    public List<? extends ConfigInfo> getConfigOptions()
    {
        return this.configs;
    }

    @Override
    public ButtonActionListener getButtonActionListener(BaseConfigScreen gui)
    {
        return this.listenerFactory.apply(this, gui);
    }
}
