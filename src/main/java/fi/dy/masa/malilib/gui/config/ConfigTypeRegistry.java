package fi.dy.masa.malilib.gui.config;

import java.util.HashMap;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.option.BlockListConfig;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ColorConfig;
import fi.dy.masa.malilib.config.ConfigInfo;
import fi.dy.masa.malilib.config.option.DirectoryConfig;
import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.config.option.EquipmentSlotListConfig;
import fi.dy.masa.malilib.config.option.FileConfig;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.config.option.IdentifierListConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.ItemListConfig;
import fi.dy.masa.malilib.config.option.NestedConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.StatusEffectListConfig;
import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.config.option.StringListConfig;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BlackWhiteListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.BlockListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BooleanConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.ColorConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.ExpandableConfigGroupWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.DirectoryConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.DoubleConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.EquipmentSlotListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.FileConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.HotkeyConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.HotkeyedBooleanConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.IdentifierListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.IntegerConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.ItemListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.OptionListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.StatusEffectListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.StringConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.StringListConfigWidget;

public class ConfigTypeRegistry
{
    public static final ConfigTypeRegistry INSTANCE = new ConfigTypeRegistry();

    private final HashMap<Class<? extends ConfigInfo>, ConfigOptionWidgetFactory<?>> widgetFactories = new HashMap<>();
    private final HashMap<Class<? extends ConfigInfo>, ConfigSearchInfo<?>> configSearchInfoMap = new HashMap<>();
    private final ConfigOptionWidgetFactory<?> missingTypeFactory = new MissingConfigTypeFactory();

    private ConfigTypeRegistry()
    {
        this.registerDefaultWidgetFactories();
        this.registerDefaultSearchInfos();
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
        return (ConfigOptionWidgetFactory<C>) this.widgetFactories.getOrDefault(config.getClass(), this.missingTypeFactory);
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
        this.registerWidgetFactory(BlackWhiteListConfig.class, BlackWhiteListConfigWidget::new);
        this.registerWidgetFactory(BlockListConfig.class, BlockListConfigWidget::new);
        this.registerWidgetFactory(BooleanConfig.class, BooleanConfigWidget::new);
        this.registerWidgetFactory(ColorConfig.class, ColorConfigWidget::new);
        this.registerWidgetFactory(ExpandableConfigGroup.class, ExpandableConfigGroupWidget::new);
        this.registerWidgetFactory(DirectoryConfig.class, DirectoryConfigWidget::new);
        this.registerWidgetFactory(DoubleConfig.class, DoubleConfigWidget::new);
        this.registerWidgetFactory(EquipmentSlotListConfig.class, EquipmentSlotListConfigWidget::new);
        this.registerWidgetFactory(FileConfig.class, FileConfigWidget::new);
        this.registerWidgetFactory(HotkeyConfig.class, HotkeyConfigWidget::new);
        this.registerWidgetFactory(HotkeyedBooleanConfig.class, HotkeyedBooleanConfigWidget::new);
        this.registerWidgetFactory(IdentifierListConfig.class, IdentifierListConfigWidget::new);
        this.registerWidgetFactory(IntegerConfig.class, IntegerConfigWidget::new);
        this.registerWidgetFactory(ItemListConfig.class, ItemListConfigWidget::new);
        this.registerWidgetFactory(NestedConfig.class, new NestedConfigWidgetFactory());
        this.registerWidgetFactory(OptionListConfig.class, OptionListConfigWidget::new);
        this.registerWidgetFactory(StatusEffectListConfig.class, StatusEffectListConfigWidget::new);
        this.registerWidgetFactory(StringConfig.class, StringConfigWidget::new);
        this.registerWidgetFactory(StringListConfig.class, StringListConfigWidget::new);
    }

    private void registerDefaultSearchInfos()
    {
        this.registerConfigSearchInfo(BooleanConfig.class,          new ConfigSearchInfo<BooleanConfig>(true, false).setBooleanConfigGetter((c) -> c));
        this.registerConfigSearchInfo(HotkeyConfig.class,           new ConfigSearchInfo<HotkeyConfig>(false, true).setKeyBindGetter(HotkeyConfig::getKeyBind));
        this.registerConfigSearchInfo(HotkeyedBooleanConfig.class,  new ConfigSearchInfo<HotkeyedBooleanConfig>(true, true).setBooleanConfigGetter((c) -> c).setKeyBindGetter(HotkeyedBooleanConfig::getKeyBind));
    }
}
