package fi.dy.masa.malilib.gui.widget.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.ConfigOptionWidgetFactory;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTabRegistry;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.config.ConfigWidgetRegistry;
import fi.dy.masa.malilib.gui.config.KeybindEditingScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.widget.ConfigsSearchBarWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetFactory;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigWidget;
import fi.dy.masa.malilib.util.data.ConfigOnTab;
import fi.dy.masa.malilib.util.data.ModInfo;

public class ConfigOptionListWidget<C extends ConfigInfo> extends DataListWidget<C>
{
    protected final Map<ConfigsSearchBarWidget.Scope, List<ConfigOnTab>> cachedConfigs = new HashMap<>();
    protected final ModInfo modInfo;
    protected final IntSupplier defaultElementWidthSupplier;
    @Nullable protected ConfigsSearchBarWidget configsSearchBarWidget;
    protected int maxLabelWidth;

    protected ConfigOptionListWidget(int x, int y, int width, int height, IntSupplier defaultElementWidthSupplier,
                                     ModInfo modInfo, Supplier<List<C>> entrySupplier,
                                     ConfigWidgetContext ctx)
    {
        super(x, y, width, height, entrySupplier);

        this.modInfo = modInfo;
        this.defaultElementWidthSupplier = defaultElementWidthSupplier;
        this.fetchFromSupplierOnRefresh = true;
        this.allowKeyboardNavigation = true;

        this.setEntryWidgetFactory(new ConfigOptionListEntryWidgetFactory<>(ctx));
        this.setEntryFilterStringFactory(ConfigInfo::getSearchStrings);

        this.listPosition.setTop(0);
    }

    public int getMaxLabelWidth()
    {
        return this.maxLabelWidth;
    }

    public int getElementWidth()
    {
        if (this.isShowingOptionsFromOtherCategories())
        {
            // FIXME how to retrieve the correct max width from the config tabs?
            return 220;
        }

        return this.defaultElementWidthSupplier.getAsInt();
    }

    public boolean isShowingOptionsFromOtherCategories()
    {
        return this.configsSearchBarWidget != null &&
               this.configsSearchBarWidget.getCurrentScope() != ConfigsSearchBarWidget.Scope.CURRENT_CATEGORY;
    }

    public void addConfigSearchBarWidget(KeybindEditingScreen screen)
    {
        this.configsSearchBarWidget = new ConfigsSearchBarWidget(this.getX(), this.getY(), this.getWidth(), 32, 0,
                                                                 DefaultIcons.SEARCH, HorizontalAlignment.LEFT,
                                                                 this::onSearchBarChange,
                                                                 this::refreshEntries,
                                                                 this::resetFilteredConfigsToDefaults,
                                                                 screen);
        this.configsSearchBarWidget.setGeometryChangeListener(this::updatePositioningAndElements);
        this.configsSearchBarWidget.getMargin().setBottom(2);
        this.searchBarWidget = this.configsSearchBarWidget;
    }

    protected boolean resetFilteredConfigsToDefaults()
    {
        for (C config : this.filteredContents)
        {
            config.resetToDefault();
        }

        this.refreshEntries();

        return true;
    }

    @Nullable
    public String getModNameAndCategoryPrefix(int listIndex)
    {
        if (this.configsSearchBarWidget != null &&
            this.configsSearchBarWidget.getCurrentScope() != ConfigsSearchBarWidget.Scope.CURRENT_CATEGORY)
        {
            List<ConfigOnTab> configs = this.cachedConfigs.get(this.configsSearchBarWidget.getCurrentScope());

            if (configs != null && listIndex < configs.size())
            {
                ConfigTab tab = configs.get(listIndex).tab;
                return tab.getModInfo().getModName() + " > " + tab.getDisplayName();
            }
        }

        return null;
    }

    @Override
    protected boolean entryMatchesFilter(C entry, String filterText)
    {
        return super.entryMatchesFilter(entry, filterText) &&
               (this.configsSearchBarWidget == null || this.configsSearchBarWidget.passesFilter(entry));
    }

    @Override
    protected void onEntriesRefreshed()
    {
        super.onEntriesRefreshed();

        List<C> list = this.getCurrentContents();
        final int size = list.size();
        boolean showOwner = this.isShowingOptionsFromOtherCategories();
        this.maxLabelWidth = 0;

        for (int i = 0; i < size; ++i)
        {
            ConfigInfo config = list.get(i);
            String name = config.getDisplayName();
            String owner = showOwner ? this.getModNameAndCategoryPrefix(i) : null;

            if (owner != null)
            {
                this.maxLabelWidth = Math.max(this.maxLabelWidth, this.getStringWidth(owner) + 10);
            }

            // The +10 here compensates for the left padding of the label widgets,
            // which is used because of the hover border used for the labels of configs with a click handler
            this.maxLabelWidth = Math.max(this.maxLabelWidth, this.getStringWidth(name) + 10);
        }
    }

    @Override
    public ArrayList<C> getCurrentContents()
    {
        if (this.configsSearchBarWidget != null &&
            this.configsSearchBarWidget.getCurrentScope() != ConfigsSearchBarWidget.Scope.CURRENT_CATEGORY)
        {
            ConfigsSearchBarWidget.Scope scope = this.configsSearchBarWidget.getCurrentScope();
            List<ConfigOnTab> configsInScope = this.cachedConfigs.get(scope);
            ArrayList<C> list = new ArrayList<>();

            if (configsInScope == null)
            {
                configsInScope = new ArrayList<>();

                if (scope == ConfigsSearchBarWidget.Scope.ALL_MODS)
                {
                    List<ConfigTab> allModTabs = ConfigTabRegistry.INSTANCE.getAllRegisteredConfigTabs();
                    final List<ConfigOnTab> tmpList = configsInScope;
                    allModTabs.forEach((tab) -> tab.getTabbedExpandedConfigs(tmpList::add));
                }
                else
                {
                    Supplier<List<ConfigTab>> tabProvider = ConfigTabRegistry.INSTANCE.getConfigTabProviderFor(this.modInfo);

                    if (tabProvider != null)
                    {
                        final List<ConfigOnTab> tmpList = configsInScope;
                        tabProvider.get().forEach((tab) -> tab.getTabbedExpandedConfigs(tmpList::add));
                    }
                }

                this.cachedConfigs.put(scope, configsInScope);
            }

            for (ConfigOnTab cfg : configsInScope)
            {
                @SuppressWarnings("unchecked")
                C c = (C) cfg.config;
                list.add(c);
            }

            return list;
        }

        return super.getCurrentContents();
    }

    /**
     * Clears the cache of config options per search scopes
     */
    public void clearConfigSearchCache()
    {
        this.cachedConfigs.clear();
    }

    public static <C extends ConfigInfo> ConfigOptionListWidget<C> createWithExpandedGroups(
            int listX, int listY, int listWidth, int listHeight, IntSupplier defaultElementWidthSupplier,
            ModInfo modInfo, Supplier<List<C>> entrySupplier, ConfigWidgetContext ctx)
    {
        return new ConfigOptionListWidget<>(listX, listY, listWidth, listHeight,
                                            defaultElementWidthSupplier, modInfo,
                                            createUnNestingConfigSupplier(entrySupplier), ctx);
    }

    public static <C extends ConfigInfo> Supplier<List<C>> createUnNestingConfigSupplier(Supplier<List<C>> entrySupplier)
    {
        return () -> {
            List<C> originalList = entrySupplier.get();
            ArrayList<C> expandedList = new ArrayList<>();

            for (C config : originalList)
            {
                expandedList.add(config);
                config.addNestedOptionsToList(expandedList, 1);
            }

            return expandedList;
        };
    }

    public static class ConfigOptionListEntryWidgetFactory<C extends ConfigInfo> implements DataListEntryWidgetFactory<C>
    {
        protected final ConfigWidgetContext ctx;

        public ConfigOptionListEntryWidgetFactory(ConfigWidgetContext ctx)
        {
            this.ctx = ctx;
        }

        @Override
        @Nullable
        public BaseConfigWidget<? extends ConfigInfo> createWidget(int x, int y, int width, int height,
                                                                   int listIndex, int originalListIndex,
                                                                   C config, DataListWidget<C> listWidget)
        {
            ConfigOptionWidgetFactory<C> factory = ConfigWidgetRegistry.INSTANCE.getWidgetFactory(config);
            return factory.create(x, y, width, height, listIndex, originalListIndex, config, this.ctx);
        }
    }
}
