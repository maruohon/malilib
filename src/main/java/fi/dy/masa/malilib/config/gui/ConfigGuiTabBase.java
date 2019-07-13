package fi.dy.masa.malilib.config.gui;

import java.util.List;
import java.util.function.BiFunction;
import fi.dy.masa.malilib.config.options.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IConfigGuiTab;
import fi.dy.masa.malilib.gui.listener.ButtonListenerConfigGuiTab;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigGuiTabBase implements IConfigGuiTab
{
    private final String name;
    private final String translationKey;
    private final int configWidth;
    private final boolean useKeybindSearch;
    private final List<? extends IConfigBase> configs;
    private final BiFunction<IConfigGuiTab, GuiConfigsBase, IButtonActionListener> listenerFactory;

    public ConfigGuiTabBase(String translationKey, int configWidth, boolean useKeybindSearch, List<? extends IConfigBase> configs)
    {
        this(translationKey, configWidth, useKeybindSearch, configs, (tab, gui) -> new ButtonListenerConfigGuiTab(tab, gui));
    }

    public ConfigGuiTabBase(String translationKey, int configWidth, boolean useKeybindSearch,
            List<? extends IConfigBase> configs, BiFunction<IConfigGuiTab, GuiConfigsBase, IButtonActionListener> listenerFactory)
    {
        this.name = translationKey.substring(translationKey.lastIndexOf(".") + 1);
        this.translationKey = translationKey;
        this.configWidth = configWidth;
        this.useKeybindSearch = useKeybindSearch;
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
    public boolean useKeybindSearch()
    {
        return this.useKeybindSearch;
    }

    @Override
    public List<? extends IConfigBase> getConfigOptions()
    {
        return this.configs;
    }

    @Override
    public IButtonActionListener getButtonActionListener(GuiConfigsBase gui)
    {
        return this.listenerFactory.apply(this, gui);
    }
}
