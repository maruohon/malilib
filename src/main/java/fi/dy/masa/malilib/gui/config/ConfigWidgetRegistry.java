package fi.dy.masa.malilib.gui.config;

import java.util.HashMap;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ColorConfig;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.DirectoryConfig;
import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.config.option.FileConfig;
import fi.dy.masa.malilib.config.option.GenericButtonConfig;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.NestedConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.config.option.list.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.option.list.BlockListConfig;
import fi.dy.masa.malilib.config.option.list.EquipmentSlotListConfig;
import fi.dy.masa.malilib.config.option.list.IdentifierListConfig;
import fi.dy.masa.malilib.config.option.list.ItemListConfig;
import fi.dy.masa.malilib.config.option.list.StatusEffectListConfig;
import fi.dy.masa.malilib.config.option.list.StringListConfig;
import fi.dy.masa.malilib.gui.config.indicator.ConfigStatusWidgetFactory;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BlackWhiteListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BooleanConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.ColorConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.DirectoryConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.DoubleConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.ExpandableConfigGroupWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.FileConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.GenericButtonConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.HotkeyConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.HotkeyedBooleanConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.IntegerConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.OptionListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.StringConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.BlockListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.EquipmentSlotListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.IdentifierListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.ItemListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.StatusEffectListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.StringListConfigWidget;
import fi.dy.masa.malilib.overlay.widget.sub.BooleanConfigStatusWidget;
import fi.dy.masa.malilib.overlay.widget.sub.DoubleConfigStatusWidget;
import fi.dy.masa.malilib.overlay.widget.sub.HotkeyConfigStatusWidget;
import fi.dy.masa.malilib.overlay.widget.sub.HotkeyedBooleanConfigStatusWidget;
import fi.dy.masa.malilib.overlay.widget.sub.IntegerConfigStatusWidget;
import fi.dy.masa.malilib.overlay.widget.sub.OptionListConfigStatusWidget;
import fi.dy.masa.malilib.overlay.widget.sub.StringConfigStatusWidget;

public class ConfigWidgetRegistry
{
    public static final ConfigWidgetRegistry INSTANCE = new ConfigWidgetRegistry();

    private final HashMap<Class<? extends ConfigInfo>, ConfigOptionWidgetFactory<?>> configWidgetFactories = new HashMap<>();
    private final HashMap<Class<? extends ConfigInfo>, ConfigStatusWidgetFactory<?>> configStatusWidgetFactories = new HashMap<>();
    private final HashMap<String, ConfigStatusWidgetFactory<?>> configStatusWidgetFactoriesById = new HashMap<>();
    private final HashMap<Class<? extends ConfigInfo>, ConfigSearchInfo<?>> configSearchInfoMap = new HashMap<>();
    private final ConfigOptionWidgetFactory<?> missingTypeFactory = new MissingConfigTypeFactory();

    private ConfigWidgetRegistry()
    {
        this.registerDefaultWidgetFactories();
        this.registerDefaultSearchInfos();
        this.registerDefaultStatusWidgetFactories();
    }

    /**
     * Registers a config screen widget factory for the given config type
     * @param type
     * @param factory
     */
    public <C extends ConfigInfo> void registerConfigWidgetFactory(Class<C> type, ConfigOptionWidgetFactory<C> factory)
    {
        this.configWidgetFactories.put(type, factory);
    }

    /**
     * Registers a config status widget factory for the given config type.
     * These status widgets can be used to show the current status of the
     * config option on the info HUD.
     * @param type
     * @param factory
     */
    public <C extends ConfigInfo>
    void registerConfigStatusWidgetFactory(Class<C> type, Class<?> widgetType, ConfigStatusWidgetFactory<C> factory)
    {
        this.configStatusWidgetFactories.put(type, factory);
        this.configStatusWidgetFactoriesById.put(widgetType.getName(), factory);
    }

    /**
     * Registers a config search info provider.
     * These are only needed if your custom config type has boolean/toggle
     * option(s) or hotkey(s) and you want to have your custom config searchable/filterable
     * using the dropdown widget in the search bar, using the toggle and hotkey related options there.
     * @param type
     * @param info
     */
    public <C extends ConfigInfo> void registerConfigSearchInfo(Class<C> type, ConfigSearchInfo<C> info)
    {
        this.configSearchInfoMap.put(type, info);
    }

    @SuppressWarnings("unchecked")
    public <C extends ConfigInfo> ConfigOptionWidgetFactory<C> getWidgetFactory(C config)
    {
        return (ConfigOptionWidgetFactory<C>) this.configWidgetFactories.getOrDefault(config.getClass(), this.missingTypeFactory);
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

    public ConfigOptionWidgetFactory<?> getMissingTypeFactory()
    {
        return this.missingTypeFactory;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <C extends ConfigInfo> ConfigSearchInfo<C> getSearchInfo(C config)
    {
        return (ConfigSearchInfo<C>) this.configSearchInfoMap.get(config.getClass());
    }

    private void registerDefaultWidgetFactories()
    {
        this.registerConfigWidgetFactory(BlackWhiteListConfig.class,    BlackWhiteListConfigWidget::new);
        this.registerConfigWidgetFactory(BlockListConfig.class,         BlockListConfigWidget::new);
        this.registerConfigWidgetFactory(BooleanConfig.class,           BooleanConfigWidget::new);
        this.registerConfigWidgetFactory(ColorConfig.class,             ColorConfigWidget::new);
        this.registerConfigWidgetFactory(ExpandableConfigGroup.class,   ExpandableConfigGroupWidget::new);
        this.registerConfigWidgetFactory(DirectoryConfig.class,         DirectoryConfigWidget::new);
        this.registerConfigWidgetFactory(DoubleConfig.class,            DoubleConfigWidget::new);
        this.registerConfigWidgetFactory(EquipmentSlotListConfig.class, EquipmentSlotListConfigWidget::new);
        this.registerConfigWidgetFactory(FileConfig.class,              FileConfigWidget::new);
        this.registerConfigWidgetFactory(GenericButtonConfig.class,     GenericButtonConfigWidget::new);
        this.registerConfigWidgetFactory(HotkeyConfig.class,            HotkeyConfigWidget::new);
        this.registerConfigWidgetFactory(HotkeyedBooleanConfig.class,   HotkeyedBooleanConfigWidget::new);
        this.registerConfigWidgetFactory(IdentifierListConfig.class,    IdentifierListConfigWidget::new);
        this.registerConfigWidgetFactory(IntegerConfig.class,           IntegerConfigWidget::new);
        this.registerConfigWidgetFactory(ItemListConfig.class,          ItemListConfigWidget::new);
        this.registerConfigWidgetFactory(NestedConfig.class,            new NestedConfigWidgetFactory());
        this.registerConfigWidgetFactory(OptionListConfig.class,        OptionListConfigWidget::new);
        this.registerConfigWidgetFactory(StatusEffectListConfig.class,  StatusEffectListConfigWidget::new);
        this.registerConfigWidgetFactory(StringConfig.class,            StringConfigWidget::new);
        this.registerConfigWidgetFactory(StringListConfig.class,        StringListConfigWidget::new);
    }

    private void registerDefaultSearchInfos()
    {
        this.registerConfigSearchInfo(BooleanConfig.class,          new ConfigSearchInfo<BooleanConfig>(true, false).setBooleanConfigGetter((c) -> c));
        this.registerConfigSearchInfo(HotkeyConfig.class,           new ConfigSearchInfo<HotkeyConfig>(false, true).setKeyBindGetter(HotkeyConfig::getKeyBind));
        this.registerConfigSearchInfo(HotkeyedBooleanConfig.class,  new ConfigSearchInfo<HotkeyedBooleanConfig>(true, true).setBooleanConfigGetter((c) -> c).setKeyBindGetter(HotkeyedBooleanConfig::getKeyBind));
    }

    private void registerDefaultStatusWidgetFactories()
    {
        this.registerConfigStatusWidgetFactory(BooleanConfig.class,         BooleanConfigStatusWidget.class,            BooleanConfigStatusWidget::new);
        this.registerConfigStatusWidgetFactory(DoubleConfig.class,          DoubleConfigStatusWidget.class,             DoubleConfigStatusWidget::new);
        this.registerConfigStatusWidgetFactory(HotkeyConfig.class,          HotkeyConfigStatusWidget.class,             HotkeyConfigStatusWidget::new);
        this.registerConfigStatusWidgetFactory(HotkeyedBooleanConfig.class, HotkeyedBooleanConfigStatusWidget.class,    HotkeyedBooleanConfigStatusWidget::new);
        this.registerConfigStatusWidgetFactory(IntegerConfig.class,         IntegerConfigStatusWidget.class,            IntegerConfigStatusWidget::new);
        this.registerConfigStatusWidgetFactory(OptionListConfig.class,      OptionListConfigStatusWidget.class,         OptionListConfigStatusWidget::new);
        this.registerConfigStatusWidgetFactory(StringConfig.class,          StringConfigStatusWidget.class,             StringConfigStatusWidget::new);
    }
}
