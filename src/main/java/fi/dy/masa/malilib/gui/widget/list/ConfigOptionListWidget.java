package fi.dy.masa.malilib.gui.widget.list;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.ConfigManagerImpl;
import fi.dy.masa.malilib.config.ModConfig;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.ConfigOptionWidgetFactory;
import fi.dy.masa.malilib.gui.config.ConfigTypeRegistry;
import fi.dy.masa.malilib.gui.icon.BaseIcon;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.widget.ConfigsSearchBarWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetFactory;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigOptionWidget;

public class ConfigOptionListWidget<C extends ConfigInfo> extends DataListWidget<C>
{
    protected final BaseConfigScreen gui;
    protected final ConfigsSearchBarWidget configsSearchBarWidget;
    protected final EnumMap<ConfigsSearchBarWidget.Scope, List<C>> cachedConfigs = new EnumMap<>(ConfigsSearchBarWidget.Scope.class);

    public ConfigOptionListWidget(int x, int y, int width, int height, Supplier<List<C>> entrySupplier, BaseConfigScreen gui)
    {
        super(x, y, width, height, entrySupplier);

        this.gui = gui;

        this.configsSearchBarWidget = new ConfigsSearchBarWidget(x, y, width, 32, 0,
                                                                 BaseIcon.SEARCH, HorizontalAlignment.LEFT,
                                                                 (scope) -> this.refreshEntries());

        this.setEntryWidgetFactory(new ConfigOptionListEntryWidgetFactory<>(entrySupplier, gui));
        this.setEntryFilterStringFactory(ConfigInfo::getSearchStrings);
        this.setEntryRefreshListener(gui);

        this.addDefaultSearchBar();
    }

    @Override
    protected void createSearchBarWidget()
    {
        this.searchBarWidget = this.configsSearchBarWidget;
    }

    public int getElementWidth()
    {
        // FIXME how to retrieve the correct max width from the config tabs?
        ConfigsSearchBarWidget.Scope scope = this.configsSearchBarWidget.getCurrentScope();
        return scope != ConfigsSearchBarWidget.Scope.CURRENT_CATEGORY ? 220 : -1;
    }

    @Override
    public List<C> getCurrentEntries()
    {
        ConfigsSearchBarWidget.Scope scope = this.configsSearchBarWidget.getCurrentScope();

        if (scope != ConfigsSearchBarWidget.Scope.CURRENT_CATEGORY)
        {
            List<C> list = this.cachedConfigs.get(scope);

            if (list == null)
            {
                final ArrayList<ConfigOption<?>> tmpList = new ArrayList<>();

                if (scope == ConfigsSearchBarWidget.Scope.ALL_MODS)
                {
                    List<ModConfig> allModConfigs = ((ConfigManagerImpl) ConfigManager.INSTANCE).getAllModConfigs();
                    allModConfigs.forEach((mc) -> mc.getConfigOptionCategories().forEach((cc) -> {
                        if (cc.showOnConfigScreen())
                        {
                            tmpList.addAll(cc.getConfigOptions());
                        }
                    }));
                }
                else
                {
                    ModConfig mc = ConfigManager.INSTANCE.getConfigHandler(this.gui.getModId());

                    if (mc != null)
                    {
                        mc.getConfigOptionCategories().forEach((cc) -> {
                            if (cc.showOnConfigScreen())
                            {
                                tmpList.addAll(cc.getConfigOptions());
                            }
                        });
                    }
                }

                list = new ArrayList<>();

                for (ConfigOption<?> cfg : tmpList)
                {
                    list.add((C) cfg);
                }

                this.cachedConfigs.put(scope, list);
            }

            return list;
        }

        return super.getCurrentEntries();
    }

    public static class ConfigOptionListEntryWidgetFactory<C extends ConfigInfo> implements DataListEntryWidgetFactory<C>
    {
        protected final Supplier<List<C>> entrySupplier;
        protected final BaseConfigScreen gui;

        public ConfigOptionListEntryWidgetFactory(Supplier<List<C>> entrySupplier, BaseConfigScreen gui)
        {
            this.entrySupplier = entrySupplier;
            this.gui = gui;
        }

        @Override
        @Nullable
        public BaseConfigOptionWidget<C> createWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                                      C config, DataListWidget<C> listWidget)
        {
            ConfigOptionWidgetFactory<C> factory = ConfigTypeRegistry.INSTANCE.getWidgetFactory(config);
            return factory.create(x, y, width, height, listIndex, originalListIndex, config, this.gui);
        }
    }
}
