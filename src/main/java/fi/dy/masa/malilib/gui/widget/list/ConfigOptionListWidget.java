package fi.dy.masa.malilib.gui.widget.list;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.ConfigOptionWidgetFactory;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTabProvider;
import fi.dy.masa.malilib.gui.config.ConfigTabRegistry;
import fi.dy.masa.malilib.gui.config.ConfigTypeRegistry;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.config.KeybindEditingScreen;
import fi.dy.masa.malilib.gui.icon.BaseIcon;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.widget.ConfigsSearchBarWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetFactory;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigOptionWidget;

public class ConfigOptionListWidget<C extends ConfigInfo> extends DataListWidget<C>
{
    protected final EnumMap<ConfigsSearchBarWidget.Scope, List<C>> cachedConfigs = new EnumMap<>(ConfigsSearchBarWidget.Scope.class);
    protected final EnumMap<ConfigsSearchBarWidget.Scope, List<ConfigTab>> cachedCategories = new EnumMap<>(ConfigsSearchBarWidget.Scope.class);
    protected final String modId;
    protected final IntSupplier defaultElementWidthSupplier;
    @Nullable protected ConfigsSearchBarWidget configsSearchBarWidget;
    protected int maxLabelWidth;

    public ConfigOptionListWidget(int x, int y, int width, int height, String modId, Supplier<List<C>> entrySupplier,
                                  IntSupplier defaultElementWidthSupplier, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, entrySupplier);

        this.modId = modId;
        this.defaultElementWidthSupplier = defaultElementWidthSupplier;

        this.setEntryWidgetFactory(new ConfigOptionListEntryWidgetFactory<>(entrySupplier, ctx));
        this.setEntryFilterStringFactory(ConfigInfo::getSearchStrings);

        this.listPosition.setTopPadding(0);
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
                                                                 BaseIcon.SEARCH, HorizontalAlignment.LEFT,
                                                                 this::onSearchBarChange,
                                                                 this::refreshEntries,
                                                                 this::resetFilteredConfigsToDefaults,
                                                                 screen);
        this.configsSearchBarWidget.setGeometryChangeListener(this::updatePositioningAndElements);
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
            List<ConfigTab> categories = this.cachedCategories.get(this.configsSearchBarWidget.getCurrentScope());

            if (categories != null && listIndex < categories.size())
            {
                ConfigTab category = categories.get(listIndex);
                return category.getModName() + " > " + category.getDisplayName();
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

        List<C> list = this.getCurrentEntries();
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

            // The +10 here compensates for the X padding of the label widgets,
            // which is used because of the hover border used for the labels of configs with a click handler
            this.maxLabelWidth = Math.max(this.maxLabelWidth, this.getStringWidth(name) + 10);
        }

        this.resetScrollbarPosition();
    }

    @Override
    public List<C> getCurrentEntries()
    {
        if (this.configsSearchBarWidget != null &&
            this.configsSearchBarWidget.getCurrentScope() != ConfigsSearchBarWidget.Scope.CURRENT_CATEGORY)
        {
            ConfigsSearchBarWidget.Scope scope = this.configsSearchBarWidget.getCurrentScope();
            List<C> list = this.cachedConfigs.get(scope);

            if (list == null)
            {
                final ArrayList<ConfigInfo> configList = new ArrayList<>();
                final ArrayList<ConfigTab> tabList = new ArrayList<>();

                if (scope == ConfigsSearchBarWidget.Scope.ALL_MODS)
                {
                    List<ConfigTab> allModTabs = ConfigTabRegistry.INSTANCE.getAllRegisteredConfigTabs();
                    allModTabs.forEach((tab) -> this.addConfigsToLists(tab, configList, tabList));
                }
                else
                {
                    ConfigTabProvider tabProvider = ConfigTabRegistry.INSTANCE.getConfigTabProviderFor(this.modId);

                    if (tabProvider != null)
                    {
                        tabProvider.getConfigTabs().forEach((tab) -> this.addConfigsToLists(tab, configList, tabList));
                    }
                }

                list = new ArrayList<>();

                for (ConfigInfo cfg : configList)
                {
                    @SuppressWarnings("unchecked")
                    C c = (C) cfg;
                    list.add(c);
                }

                this.cachedConfigs.put(scope, list);
                this.cachedCategories.put(scope, tabList);
            }

            return list;
        }

        return super.getCurrentEntries();
    }

    protected void addConfigsToLists(@Nullable ConfigTab tab, ArrayList<ConfigInfo> configList, ArrayList<ConfigTab> tabList)
    {
        if (tab != null && tab.showOnConfigScreen())
        {
            for (ConfigInfo cfg : tab.getConfigsForDisplay())
            {
                configList.add(cfg);
                tabList.add(tab);
            }
        }
    }

    public static class ConfigOptionListEntryWidgetFactory<C extends ConfigInfo> implements DataListEntryWidgetFactory<C>
    {
        protected final Supplier<List<C>> entrySupplier;
        protected final ConfigWidgetContext ctx;

        public ConfigOptionListEntryWidgetFactory(Supplier<List<C>> entrySupplier, ConfigWidgetContext ctx)
        {
            this.entrySupplier = entrySupplier;
            this.ctx = ctx;
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
