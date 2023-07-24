package malilib.gui.widget.list;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import malilib.MaLiLibConfigScreen;
import malilib.MaLiLibConfigs;
import malilib.MaLiLibReference;
import malilib.config.option.ConfigInfo;
import malilib.gui.config.BaseConfigTab;
import malilib.gui.config.ConfigOptionWidgetFactory;
import malilib.gui.config.ConfigTab;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.config.KeybindEditScreen;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.widget.list.entry.BaseListEntryWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.gui.widget.list.entry.DataListEntryWidgetFactory;
import malilib.gui.widget.list.entry.config.BaseConfigWidget;
import malilib.gui.widget.list.search.ConfigsSearchBarWidget;
import malilib.input.CustomHotkeyManager;
import malilib.registry.Registry;
import malilib.util.StringUtils;
import malilib.util.data.ConfigOnTab;
import malilib.util.data.ModInfo;

public class ConfigOptionListWidget extends DataListWidget<ConfigOnTab>
{
    /*
    protected final Map<ConfigTab, List<ConfigOnTab>> cachedConfigsPerTab = new HashMap<>();
    protected final List<ConfigOnTab> cachedConfigsAllCategories = new ArrayList<>();
    protected final List<ConfigOnTab> cachedConfigsAllMods = new ArrayList<>();
    */
    protected final Supplier<List<ConfigOnTab>> nonExpandingEntrySupplier;
    protected final IntSupplier defaultElementWidthSupplier;
    protected final ModInfo modInfo;
    @Nullable protected ConfigsSearchBarWidget configsSearchBarWidget;
    protected boolean showInternalConfigName;
    protected int maxLabelWidth;

    public ConfigOptionListWidget(IntSupplier defaultElementWidthSupplier,
                                  ModInfo modInfo,
                                  Supplier<List<ConfigOnTab>> entrySupplier,
                                  @Nullable KeybindEditScreen keybindEditScreen)
    {
        super(createUnNestingConfigSupplier(entrySupplier), true);

        this.modInfo = modInfo;
        this.defaultElementWidthSupplier = defaultElementWidthSupplier;
        this.nonExpandingEntrySupplier = entrySupplier;
        this.allowKeyboardNavigation = true;
        this.showInternalConfigName = MaLiLibConfigs.Generic.SHOW_INTERNAL_CONFIG_NAME.getBooleanValue();

        this.setDataListEntryWidgetFactory(new ConfigOptionListEntryWidgetFactory(this, keybindEditScreen));
        this.setEntryFilterStringFunction(cot -> cot.getConfig().getSearchStrings());
        this.getBorderRenderer().getNormalSettings().setBorderWidth(0);

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

    public boolean getShowInternalConfigName()
    {
        return this.showInternalConfigName;
    }

    public void setShowInternalConfigName(boolean showInternalConfigName)
    {
        this.showInternalConfigName = showInternalConfigName;
    }

    public boolean isShowingOptionsFromOtherCategories()
    {
        return this.configsSearchBarWidget != null &&
               this.configsSearchBarWidget.getCurrentScope() != ConfigsSearchBarWidget.Scope.CURRENT_CATEGORY;
    }

    public void addConfigSearchBarWidget(KeybindEditScreen screen)
    {
        this.configsSearchBarWidget = new ConfigsSearchBarWidget(this.getWidth(), 32,
                                                                 this::onSearchBarTextChanged,
                                                                 this::onSearchOpenOrClose,
                                                                 this::refreshEntries,
                                                                 DefaultIcons.SEARCH,
                                                                 this::resetFilteredConfigsToDefaults,
                                                                 screen);
        this.configsSearchBarWidget.getMargin().setBottom(2);
        this.searchBarWidget = this.configsSearchBarWidget;
    }

    protected void onSearchOpenOrClose()
    {
        this.updateSubWidgetPositions();
        this.refreshEntries();
    }

    protected void resetFilteredConfigsToDefaults()
    {
        for (ConfigOnTab configWrapper : this.filteredDataList)
        {
            configWrapper.getConfig().resetToDefault();
        }

        this.refreshEntries();
    }

    @Override
    protected boolean entryMatchesFilter(ConfigOnTab entry, List<String> searchTerms)
    {
        return super.entryMatchesFilter(entry, searchTerms) &&
               (this.configsSearchBarWidget == null || this.configsSearchBarWidget.passesFilter(entry.getConfig()));
    }

    @Override
    protected void onEntriesRefreshed()
    {
        super.onEntriesRefreshed();

        List<ConfigOnTab> list = this.getNonFilteredDataList();
        boolean showCategory = this.isShowingOptionsFromOtherCategories();
        int maxLabelWidth = 0;

        for (ConfigOnTab configWrapper : list)
        {
            int width = this.getEntryNameColumnWidth(configWrapper, showCategory, this.showInternalConfigName);
            maxLabelWidth = Math.max(maxLabelWidth, width);
        }

        this.maxLabelWidth = maxLabelWidth;
    }

    protected int getEntryNameColumnWidth(ConfigOnTab configWrapper,
                                          boolean showCategory, boolean showInternalName)
    {
        int labelWidth = 0;

        if (showCategory)
        {
            String str = BaseConfigWidget.getOwnerText(configWrapper, showInternalName);
            labelWidth = this.getStringWidth(str);
        }

        if (showInternalName)
        {
            labelWidth = Math.max(labelWidth, this.getStringWidth(configWrapper.getConfig().getName()));
        }

        // The +10 here compensates for the left padding of the label widgets,
        // which is used because of the hover border used for the labels of configs with a click handler
        return Math.max(labelWidth, this.getStringWidth(configWrapper.getConfig().getDisplayName())) + 10;
    }

    @Override
    public ArrayList<ConfigOnTab> getNonFilteredDataList()
    {
        if (this.configsSearchBarWidget != null && this.configsSearchBarWidget.isSearchOpen())
        {
            ConfigsSearchBarWidget.Scope scope = this.configsSearchBarWidget.getCurrentScope();
            boolean expanded = this.configsSearchBarWidget.hasFilter();
            return new ArrayList<>(this.getConfigsForScope(scope, expanded));
        }

        return super.getNonFilteredDataList();
    }

    protected ArrayList<ConfigOnTab> getConfigsForScope(ConfigsSearchBarWidget.Scope scope, boolean expanded)
    {
        if (scope == ConfigsSearchBarWidget.Scope.CURRENT_CATEGORY)
        {
            return this.getConfigsForCurrentCategory(expanded);
        }

        if (scope == ConfigsSearchBarWidget.Scope.ALL_CATEGORIES)
        {
            return this.getConfigsForAllCategoriesInThisMod(expanded);
        }

        if (scope == ConfigsSearchBarWidget.Scope.ALL_MODS)
        {
            return this.getConfigsForAllMods(expanded);
        }

        return super.getNonFilteredDataList();
    }

    protected ArrayList<ConfigOnTab> getConfigsForCurrentCategory(boolean expanded)
    {
        if (expanded)
        {
            // Force expanded
            return getExpandedConfigs(this.nonExpandingEntrySupplier.get(), true);
        }

        return new ArrayList<>(this.entrySupplier.get());
    }

    protected ArrayList<ConfigOnTab> getConfigsForAllCategoriesInThisMod(boolean expanded)
    {
        final ArrayList<ConfigOnTab> tmpList = new ArrayList<>();
        Supplier<List<? extends ConfigTab>> tabProvider = Registry.CONFIG_TAB.getConfigTabSupplierFor(this.modInfo);

        if (tabProvider != null)
        {
            if (expanded)
            {
                tabProvider.get().forEach(tab -> tab.offerTabbedExpandedConfigs(tmpList::add));
            }
            else
            {
                tabProvider.get().forEach(tab -> tmpList.addAll(tab.getTabbedConfigs()));
            }

        }

        return tmpList;
    }

    protected ArrayList<ConfigOnTab> getConfigsForAllMods(boolean expanded)
    {
        List<ConfigTab> allModTabs = new ArrayList<>(Registry.CONFIG_TAB.getAllRegisteredConfigTabs());
        allModTabs.add(this.getAllCustomHotkeysAsTab());
        final ArrayList<ConfigOnTab> tmpList = new ArrayList<>();

        if (expanded)
        {
            allModTabs.forEach(tab -> tab.offerTabbedExpandedConfigs(tmpList::add));
        }
        else
        {
            allModTabs.forEach(tab -> tmpList.addAll(tab.getTabbedConfigs()));
        }

        return tmpList;
    }

    protected BaseConfigTab getAllCustomHotkeysAsTab()
    {
        String name = StringUtils.translate("malilib.screen.tab.custom_hotkeys");
        return new BaseConfigTab(MaLiLibReference.MOD_INFO, name, name, 200,
                                 CustomHotkeyManager.INSTANCE.getAllCustomHotkeys(),
                                 MaLiLibConfigScreen::create);
    }

    /**
     * Clears the cache of config options per search scopes
     */
    public void clearConfigSearchCache()
    {
        this.refreshEntries();
        /*
        this.cachedConfigsAllMods.clear();
        this.cachedConfigsAllCategories.clear();
        this.cachedConfigsPerTab.clear();
        */
    }

    public static
    Supplier<List<ConfigOnTab>> createUnNestingConfigSupplier(Supplier<List<ConfigOnTab>> entrySupplier)
    {
        return () -> getExpandedConfigs(entrySupplier.get(), false);
    }

    public static ArrayList<ConfigOnTab> getExpandedConfigs(List<ConfigOnTab> listIn, boolean expandAlways)
    {
        ArrayList<ConfigOnTab> expandedList = new ArrayList<>();

        for (ConfigOnTab configWrapper : listIn)
        {
            expandedList.add(configWrapper);
            configWrapper.getConfig().addNestedOptionsToList(expandedList, configWrapper.getTab(), 1, expandAlways);
        }

        return expandedList;
    }

    public static class ConfigOptionListEntryWidgetFactory implements DataListEntryWidgetFactory<ConfigOnTab>
    {
        protected final ConfigOptionListWidget listWidget;
        @Nullable protected final KeybindEditScreen keybindScreen;

        public ConfigOptionListEntryWidgetFactory(ConfigOptionListWidget listWidget,
                                                  @Nullable KeybindEditScreen keybindScreen)
        {
            this.listWidget = listWidget;
            this.keybindScreen = keybindScreen;
        }

        @Override
        @Nullable
        public BaseListEntryWidget createWidget(ConfigOnTab configWrapper, DataListEntryWidgetData constructData)
        {
            ConfigInfo config = configWrapper.getConfig();
            ConfigOptionWidgetFactory<ConfigInfo> factory = Registry.CONFIG_WIDGET.getWidgetFactory(config);
            ConfigWidgetContext ctx = new ConfigWidgetContext(configWrapper, this.listWidget, this.keybindScreen);
            return factory.create(config, constructData, ctx);
        }
    }
}
