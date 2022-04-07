package fi.dy.masa.malilib.gui.config.registry;

import java.util.HashMap;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.group.ExpandableConfigGroup;
import fi.dy.masa.malilib.config.group.PopupConfigGroup;
import fi.dy.masa.malilib.config.option.BooleanAndDoubleConfig;
import fi.dy.masa.malilib.config.option.BooleanAndIntConfig;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ColorConfig;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.DirectoryConfig;
import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.config.option.DualColorConfig;
import fi.dy.masa.malilib.config.option.FileConfig;
import fi.dy.masa.malilib.config.option.GenericButtonConfig;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.NestedConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.OptionalDirectoryConfig;
import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.config.option.Vec2iConfig;
import fi.dy.masa.malilib.config.option.list.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.option.list.BlockListConfig;
import fi.dy.masa.malilib.config.option.list.EquipmentSlotListConfig;
import fi.dy.masa.malilib.config.option.list.IdentifierListConfig;
import fi.dy.masa.malilib.config.option.list.ItemListConfig;
import fi.dy.masa.malilib.config.option.list.StatusEffectListConfig;
import fi.dy.masa.malilib.config.option.list.StringListConfig;
import fi.dy.masa.malilib.gui.config.ConfigOptionWidgetFactory;
import fi.dy.masa.malilib.gui.config.ConfigSearchInfo;
import fi.dy.masa.malilib.gui.config.MissingConfigTypeFactory;
import fi.dy.masa.malilib.gui.config.NestedConfigWidgetFactory;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BlackWhiteListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BooleanAndDoubleConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BooleanAndIntConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BooleanConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.ColorConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.CustomHotkeyEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.DirectoryConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.DoubleConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.DualColorConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.ExpandableConfigGroupWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.FileConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.GenericButtonConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.HotkeyConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.HotkeyedBooleanConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.IntegerConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.OptionListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.OptionalDirectoryConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.PopupConfigGroupWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.StringConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.Vec2iConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.BlockListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.EquipmentSlotListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.IdentifierListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.ItemListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.StatusEffectListConfigWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.StringListConfigWidget;
import fi.dy.masa.malilib.input.CustomHotkeyDefinition;

public class ConfigWidgetRegistry
{
    protected final HashMap<Class<? extends ConfigInfo>, ConfigOptionWidgetFactory<?>> configWidgetFactories = new HashMap<>();
    protected final HashMap<Class<? extends ConfigInfo>, ConfigSearchInfo<?>> configSearchInfoMap = new HashMap<>();
    protected final ConfigOptionWidgetFactory<?> missingTypeFactory = new MissingConfigTypeFactory();

    public ConfigWidgetRegistry()
    {
        this.registerDefaultWidgetFactories();
        this.registerDefaultSearchInfos();
    }

    /**
     * Registers a config screen widget factory for the given config type
     */
    public <C extends ConfigInfo> void registerConfigWidgetFactory(Class<C> type, ConfigOptionWidgetFactory<C> factory)
    {
        this.configWidgetFactories.put(type, factory);
    }

    /**
     * Registers a config search info provider.
     * These are only needed if your custom config type has boolean/toggle
     * option(s) or hotkey(s) and you want to have your custom config searchable/filterable
     * using the dropdown widget in the search bar, using the toggle and hotkey related options there.
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

    protected void registerDefaultWidgetFactories()
    {
        this.registerConfigWidgetFactory(BlackWhiteListConfig.class,    BlackWhiteListConfigWidget::new);
        this.registerConfigWidgetFactory(OptionListConfig.class,        OptionListConfigWidget::new);

        this.registerConfigWidgetFactory(BlockListConfig.class,         BlockListConfigWidget::new);
        this.registerConfigWidgetFactory(BooleanConfig.class,           BooleanConfigWidget::new);
        this.registerConfigWidgetFactory(BooleanAndIntConfig.class,     BooleanAndIntConfigWidget::new);
        this.registerConfigWidgetFactory(BooleanAndDoubleConfig.class,  BooleanAndDoubleConfigWidget::new);
        this.registerConfigWidgetFactory(ColorConfig.class,             ColorConfigWidget::new);
        this.registerConfigWidgetFactory(CustomHotkeyDefinition.class,  CustomHotkeyEntryWidget::new);
        this.registerConfigWidgetFactory(DirectoryConfig.class,         DirectoryConfigWidget::new);
        this.registerConfigWidgetFactory(DoubleConfig.class,            DoubleConfigWidget::new);
        this.registerConfigWidgetFactory(DualColorConfig.class,         DualColorConfigWidget::new);
        this.registerConfigWidgetFactory(EquipmentSlotListConfig.class, EquipmentSlotListConfigWidget::new);
        this.registerConfigWidgetFactory(ExpandableConfigGroup.class,   ExpandableConfigGroupWidget::new);
        this.registerConfigWidgetFactory(FileConfig.class,              FileConfigWidget::new);
        this.registerConfigWidgetFactory(GenericButtonConfig.class,     GenericButtonConfigWidget::new);
        this.registerConfigWidgetFactory(HotkeyConfig.class,            HotkeyConfigWidget::new);
        this.registerConfigWidgetFactory(HotkeyedBooleanConfig.class,   HotkeyedBooleanConfigWidget::new);
        this.registerConfigWidgetFactory(IdentifierListConfig.class,    IdentifierListConfigWidget::new);
        this.registerConfigWidgetFactory(IntegerConfig.class,           IntegerConfigWidget::new);
        this.registerConfigWidgetFactory(ItemListConfig.class,          ItemListConfigWidget::new);
        this.registerConfigWidgetFactory(NestedConfig.class,            new NestedConfigWidgetFactory());
        this.registerConfigWidgetFactory(OptionalDirectoryConfig.class, OptionalDirectoryConfigWidget::new);
        this.registerConfigWidgetFactory(PopupConfigGroup.class,        PopupConfigGroupWidget::new);
        this.registerConfigWidgetFactory(StatusEffectListConfig.class,  StatusEffectListConfigWidget::new);
        this.registerConfigWidgetFactory(StringConfig.class,            StringConfigWidget::new);
        this.registerConfigWidgetFactory(StringListConfig.class,        StringListConfigWidget::new);
        this.registerConfigWidgetFactory(Vec2iConfig.class,             Vec2iConfigWidget::new);
    }

    protected void registerDefaultSearchInfos()
    {
        this.registerConfigSearchInfo(BooleanConfig.class,          new ConfigSearchInfo<BooleanConfig>(true, false).setBooleanStorageGetter((c) -> c));
        this.registerConfigSearchInfo(BooleanAndDoubleConfig.class, new ConfigSearchInfo<BooleanAndDoubleConfig>(true, false).setBooleanStorageGetter((c) -> c));
        this.registerConfigSearchInfo(BooleanAndIntConfig.class,    new ConfigSearchInfo<BooleanAndIntConfig>(true, false).setBooleanStorageGetter((c) -> c));
        this.registerConfigSearchInfo(HotkeyConfig.class,           new ConfigSearchInfo<HotkeyConfig>(false, true).setKeyBindGetter(HotkeyConfig::getKeyBind));
        this.registerConfigSearchInfo(HotkeyedBooleanConfig.class,  new ConfigSearchInfo<HotkeyedBooleanConfig>(true, true).setBooleanStorageGetter((c) -> c).setKeyBindGetter(HotkeyedBooleanConfig::getKeyBind));
        this.registerConfigSearchInfo(OptionalDirectoryConfig.class,new ConfigSearchInfo<OptionalDirectoryConfig>(true, false).setBooleanStorageGetter((c) -> c));

        this.registerConfigSearchInfo(CustomHotkeyDefinition.class, new ConfigSearchInfo<CustomHotkeyDefinition>(false, true).setKeyBindGetter(CustomHotkeyDefinition::getKeyBind));
    }
}
