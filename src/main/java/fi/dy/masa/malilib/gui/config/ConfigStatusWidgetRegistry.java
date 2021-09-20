package fi.dy.masa.malilib.gui.config;

import java.util.HashMap;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.gui.config.indicator.ConfigStatusWidgetFactory;
import fi.dy.masa.malilib.overlay.widget.sub.BooleanConfigStatusWidget;
import fi.dy.masa.malilib.overlay.widget.sub.DoubleConfigStatusWidget;
import fi.dy.masa.malilib.overlay.widget.sub.HotkeyConfigStatusWidget;
import fi.dy.masa.malilib.overlay.widget.sub.HotkeyedBooleanConfigStatusWidget;
import fi.dy.masa.malilib.overlay.widget.sub.IntegerConfigStatusWidget;
import fi.dy.masa.malilib.overlay.widget.sub.OptionListConfigStatusWidget;
import fi.dy.masa.malilib.overlay.widget.sub.StringConfigStatusWidget;

public class ConfigStatusWidgetRegistry
{
    protected final HashMap<Class<? extends ConfigInfo>, ConfigStatusWidgetFactory<?>> configStatusWidgetFactories = new HashMap<>();
    protected final HashMap<String, ConfigStatusWidgetFactory<?>> configStatusWidgetFactoriesById = new HashMap<>();

    public ConfigStatusWidgetRegistry()
    {
        this.registerDefaultStatusWidgetFactories();
    }

    /**
     * Registers a config status widget factory for the given config type.
     * These status widgets can be used to show the current status of the
     * config option on the info HUD.
     */
    public <C extends ConfigInfo>
    void registerConfigStatusWidgetFactory(Class<C> type, ConfigStatusWidgetFactory<C> factory, String id)
    {
        this.configStatusWidgetFactories.put(type, factory);
        this.configStatusWidgetFactoriesById.put(id, factory);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <C extends ConfigInfo> ConfigStatusWidgetFactory<C> getConfigStatusWidgetFactory(C config)
    {
        return (ConfigStatusWidgetFactory<C>) this.configStatusWidgetFactories.get(config.getClass());
    }

    @Nullable
    public ConfigStatusWidgetFactory<?> getConfigStatusWidgetFactory(String id)
    {
        return this.configStatusWidgetFactoriesById.get(id);
    }

    protected void registerDefaultStatusWidgetFactories()
    {
        this.registerConfigStatusWidgetFactory(BooleanConfig.class,         BooleanConfigStatusWidget::new,         MaLiLibReference.MOD_ID + ":csi_value_boolean");
        this.registerConfigStatusWidgetFactory(DoubleConfig.class,          DoubleConfigStatusWidget::new,          MaLiLibReference.MOD_ID + ":csi_value_double");
        this.registerConfigStatusWidgetFactory(HotkeyConfig.class,          HotkeyConfigStatusWidget::new,          MaLiLibReference.MOD_ID + ":csi_value_hotkey");
        this.registerConfigStatusWidgetFactory(HotkeyedBooleanConfig.class, HotkeyedBooleanConfigStatusWidget::new, MaLiLibReference.MOD_ID + ":csi_value_hotkeyed_boolean");
        this.registerConfigStatusWidgetFactory(IntegerConfig.class,         IntegerConfigStatusWidget::new,         MaLiLibReference.MOD_ID + ":csi_value_integer");
        this.registerConfigStatusWidgetFactory(OptionListConfig.class,      OptionListConfigStatusWidget::new,      MaLiLibReference.MOD_ID + ":csi_value_option_list");
        this.registerConfigStatusWidgetFactory(StringConfig.class,          StringConfigStatusWidget::new,          MaLiLibReference.MOD_ID + ":csi_value_string");
    }
}
