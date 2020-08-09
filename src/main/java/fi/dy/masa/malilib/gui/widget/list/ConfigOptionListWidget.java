package fi.dy.masa.malilib.gui.widget.list;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.ConfigCategory;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.ConfigManagerImpl;
import fi.dy.masa.malilib.config.ConfigOptionCategory;
import fi.dy.masa.malilib.config.ModConfig;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.ConfigOptionWidgetFactory;
import fi.dy.masa.malilib.gui.config.ConfigTypeRegistry;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
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
    protected final EnumMap<ConfigsSearchBarWidget.Scope, List<ConfigCategory>> cachedCategories = new EnumMap<>(ConfigsSearchBarWidget.Scope.class);
    protected int maxLabelWidth;

    public ConfigOptionListWidget(int x, int y, int width, int height, Supplier<List<C>> entrySupplier, BaseConfigScreen gui)
    {
        super(x, y, width, height, entrySupplier);

        this.gui = gui;

        this.configsSearchBarWidget = new ConfigsSearchBarWidget(x, y, width, 32, 0,
                                                                 BaseIcon.SEARCH, HorizontalAlignment.LEFT,
                                                                 (scope) -> this.refreshEntries());

        this.setEntryWidgetFactory(new ConfigOptionListEntryWidgetFactory<>(entrySupplier, gui));
        this.setEntryFilterStringFactory(ConfigInfo::getSearchStrings);

        this.addDefaultSearchBar();
    }

    @Override
    protected void createSearchBarWidget()
    {
        this.searchBarWidget = this.configsSearchBarWidget;
    }

    public int getMaxLabelWidth()
    {
        return this.maxLabelWidth;
    }

    public int getElementWidth()
    {
        // FIXME how to retrieve the correct max width from the config tabs?
        return this.isShowingOptionsFromOtherCategories() ? 220 : -1;
    }

    public boolean isShowingOptionsFromOtherCategories()
    {
        return this.configsSearchBarWidget.getCurrentScope() != ConfigsSearchBarWidget.Scope.CURRENT_CATEGORY;
    }

    @Nullable
    public String getModNameAndCategoryPrefix(int listIndex)
    {
        ConfigsSearchBarWidget.Scope scope = this.configsSearchBarWidget.getCurrentScope();

        if (scope != ConfigsSearchBarWidget.Scope.CURRENT_CATEGORY)
        {
            List<ConfigCategory> categories = this.cachedCategories.get(scope);

            if (categories != null && listIndex < categories.size())
            {
                ConfigCategory category = categories.get(listIndex);
                return category.getModName() + " > " + category.getDisplayName() + " > ";
            }
        }

        return null;
    }

    @Override
    protected void onEntriesRefreshed()
    {
        super.onEntriesRefreshed();

        List<C> list = this.getCurrentEntries();
        final int size = list.size();
        boolean hasPrefix = this.isShowingOptionsFromOtherCategories();
        this.maxLabelWidth = 0;

        for (int i = 0; i < size; ++i)
        {
            ConfigInfo config = list.get(i);
            String name = config.getDisplayName();

            if (hasPrefix)
            {
                name = this.getModNameAndCategoryPrefix(i) + name;
            }

            this.maxLabelWidth = Math.max(this.maxLabelWidth, this.getStringWidth(name));
        }
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
                final ArrayList<ConfigOption<?>> configList = new ArrayList<>();
                final ArrayList<ConfigCategory> categoryList = new ArrayList<>();

                if (scope == ConfigsSearchBarWidget.Scope.ALL_MODS)
                {
                    List<ModConfig> allModConfigs = ((ConfigManagerImpl) ConfigManager.INSTANCE).getAllModConfigs();
                    allModConfigs.forEach((mc) -> this.addConfigsToLists(mc, configList, categoryList));
                }
                else
                {
                    ModConfig mc = ConfigManager.INSTANCE.getConfigHandler(this.gui.getModId());
                    this.addConfigsToLists(mc, configList, categoryList);
                }

                list = new ArrayList<>();

                for (ConfigOption<?> cfg : configList)
                {
                    list.add((C) cfg);
                }

                this.cachedConfigs.put(scope, list);
                this.cachedCategories.put(scope, categoryList);
            }

            return list;
        }

        return super.getCurrentEntries();
    }

    protected void addConfigsToLists(ModConfig mc, ArrayList<ConfigOption<?>> configList, ArrayList<ConfigCategory> categoryList)
    {
        if (mc != null)
        {
            for (ConfigOptionCategory category : mc.getConfigOptionCategories())
            {
                if (category.showOnConfigScreen())
                {
                    for (ConfigOption<?> cfg : category.getConfigOptions())
                    {
                        configList.add(cfg);
                        categoryList.add(category);
                    }
                }
            }
        }
    }

    public static class ConfigOptionListEntryWidgetFactory<C extends ConfigInfo> implements DataListEntryWidgetFactory<C>
    {
        protected final Supplier<List<C>> entrySupplier;
        protected final ConfigWidgetContext ctx;

        public ConfigOptionListEntryWidgetFactory(Supplier<List<C>> entrySupplier, BaseConfigScreen gui)
        {
            this.entrySupplier = entrySupplier;
            this.ctx = new ConfigWidgetContext(gui);
        }

        @Override
        @Nullable
        public BaseConfigOptionWidget<C> createWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                                      C config, DataListWidget<C> listWidget)
        {
            ConfigOptionWidgetFactory<C> factory = ConfigTypeRegistry.INSTANCE.getWidgetFactory(config);
            return factory.create(x, y, width, height, listIndex, originalListIndex, config, this.ctx);
        }
    }
}
