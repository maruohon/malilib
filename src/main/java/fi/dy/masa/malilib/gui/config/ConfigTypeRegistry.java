package fi.dy.masa.malilib.gui.config;

import java.util.HashMap;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ColorConfig;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.DirectoryConfig;
import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.config.option.FileConfig;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.config.option.StringListConfig;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BooleanConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.ColorConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.DirectoryConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.DoubleConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.FileConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.HotkeyConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.HotkeyedBooleanConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.IntegerConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.OptionListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.StringConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.StringListConfigWidget;

public class ConfigTypeRegistry
{
    public static final ConfigTypeRegistry INSTANCE = new ConfigTypeRegistry();

    private final HashMap<Class<? extends ConfigInfo>, ConfigOptionWidgetFactory<?>> widgetFactories = new HashMap<>();
    private final ConfigOptionWidgetFactory<?> missingTypeFactory = new MissingConfigTypeFactory();

    private ConfigTypeRegistry()
    {
        this.registerDefaultPlacers();
    }

    /**
     * Registers a config screen widget factory for the given config type
     * @param type
     * @param factory
     */
    public <C extends ConfigInfo> void registerWidgetFactory(Class<C> type, ConfigOptionWidgetFactory<C> factory)
    {
        this.widgetFactories.put(type, factory);
    }

    @SuppressWarnings("unchecked")
    public <C extends ConfigInfo> ConfigOptionWidgetFactory<C> getWidgetFactory(C config)
    {
        this.registerDefaultPlacers(); // TODO config refactor XXX remove!!
        return (ConfigOptionWidgetFactory<C>) this.widgetFactories.getOrDefault(config.getClass(), this.missingTypeFactory);
    }

    private void registerDefaultPlacers()
    {
        this.registerWidgetFactory(BooleanConfig.class, BooleanConfigWidget::new);
        this.registerWidgetFactory(ColorConfig.class, ColorConfigWidget::new);
        this.registerWidgetFactory(DirectoryConfig.class, DirectoryConfigWidget::new);
        this.registerWidgetFactory(DoubleConfig.class, DoubleConfigWidget::new);
        this.registerWidgetFactory(FileConfig.class, FileConfigWidget::new);
        this.registerWidgetFactory(HotkeyConfig.class, HotkeyConfigWidget::new);
        this.registerWidgetFactory(HotkeyedBooleanConfig.class, HotkeyedBooleanConfigWidget::new);
        this.registerWidgetFactory(IntegerConfig.class, IntegerConfigWidget::new);
        this.registerWidgetFactory(OptionListConfig.class, OptionListConfigWidget::new);
        this.registerWidgetFactory(StringConfig.class, StringConfigWidget::new);
        this.registerWidgetFactory(StringListConfig.class, StringListConfigWidget::new);
    }
}
