package malilib.gui.config.indicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import malilib.MaLiLibReference;
import malilib.gui.BaseListScreen;
import malilib.gui.config.ConfigTab;
import malilib.gui.config.registry.ConfigTabRegistryImpl;
import malilib.gui.widget.DropDownListWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.entry.ConfigInfoEntryWidget;
import malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import malilib.registry.Registry;
import malilib.util.data.ConfigOnTab;
import malilib.util.data.ModInfo;
import malilib.util.data.NameIdentifiable;

public class ConfigStatusIndicatorGroupAddConfigsScreen extends BaseListScreen<DataListWidget<ConfigOnTab>>
{
    protected final ConfigStatusIndicatorContainerWidget widget;
    protected final DropDownListWidget<ModInfo> modsDropDownWidget;
    protected final DropDownListWidget<ConfigTab> categoriesDropDownWidget;
    protected final GenericButton addEntriesButton;

    protected ConfigStatusIndicatorGroupAddConfigsScreen(ConfigStatusIndicatorContainerWidget widget)
    {
        super(10, 68, 20, 70);

        this.widget = widget;
        this.useTitleHierarchy = false;
        this.setTitle("malilib.title.screen.configs.config_status_indicator_configuration", MaLiLibReference.MOD_VERSION);

        List<ModInfo> mods = new ArrayList<>(((ConfigTabRegistryImpl) Registry.CONFIG_TAB).getAllModsWithConfigTabs());
        this.modsDropDownWidget = new DropDownListWidget<>(14, 12, mods, ModInfo::getModName);
        this.modsDropDownWidget.setSelectionListener(this::onModSelected);

        this.categoriesDropDownWidget = new DropDownListWidget<>(14, 12, Collections.emptyList(), ConfigTab::getDisplayName);
        this.categoriesDropDownWidget.setSelectionListener(this::onCategorySelected);

        this.addEntriesButton = GenericButton.create("malilib.button.csi_edit.add_selected_configs", this::addSelectedConfigs);
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.modsDropDownWidget);
        this.addWidget(this.addEntriesButton);

        if (this.modsDropDownWidget.getSelectedEntry() != null)
        {
            this.addWidget(this.categoriesDropDownWidget);
        }
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 24;

        this.modsDropDownWidget.setPosition(x, y);
        this.categoriesDropDownWidget.setPosition(this.modsDropDownWidget.getRight() + 8, y);

        y += 20;
        this.addEntriesButton.setPosition(x, y);
    }

    @Override
    protected DataListWidget<ConfigOnTab> createListWidget()
    {
        DataListWidget<ConfigOnTab> listWidget = new DataListWidget<>(this::getFilteredConfigs, true);

        listWidget.setListEntryWidgetFixedHeight(15);
        listWidget.getEntrySelectionHandler().setAllowSelection(true);
        listWidget.getEntrySelectionHandler().setAllowMultiSelection(true);
        listWidget.setDataListEntryWidgetFactory(ConfigInfoEntryWidget::new);

        return listWidget;
    }

    protected void onModSelected(@Nullable ModInfo mod)
    {
        Supplier<List<ConfigTab>> tabProvider = mod != null ? Registry.CONFIG_TAB.getConfigTabProviderFor(mod) : null;
        ArrayList<ConfigTab> list = new ArrayList<>();

        if (tabProvider != null)
        {
            list.addAll(tabProvider.get());
            list.sort(Comparator.comparing(ConfigTab::getDisplayName));
        }

        this.categoriesDropDownWidget.replaceEntryList(list);
        this.categoriesDropDownWidget.setSelectedEntry(null);

        int x = this.modsDropDownWidget.getRight() + 8;
        int y = this.modsDropDownWidget.getY();
        this.categoriesDropDownWidget.setPosition(x, y);

        if (this.modsDropDownWidget.getSelectedEntry() != null)
        {
            this.addWidget(this.categoriesDropDownWidget);
        }
        else
        {
            this.removeWidget(this.categoriesDropDownWidget);
        }

        this.refreshList();
    }

    protected void onCategorySelected(@Nullable NameIdentifiable category)
    {
        this.refreshList();
    }

    protected void addSelectedConfigs()
    {
        ArrayList<ConfigOnTab> list = new ArrayList<>(this.getListWidget().getSelectedEntries());
        list.sort(Comparator.comparing(cot -> cot.getConfig().getDisplayName()));

        for (ConfigOnTab config : list)
        {
            this.widget.addWidgetForConfig(config);
        }

        this.refreshList();
    }

    protected void refreshList()
    {
        this.getListWidget().getEntrySelectionHandler().clearSelection();
        this.getListWidget().refreshEntries();
    }

    protected List<ConfigOnTab> getFilteredConfigs()
    {
        final List<ConfigOnTab> configsInScope = new ArrayList<>();

        ModInfo mod = this.modsDropDownWidget.getSelectedEntry();

        if (mod == null)
        {
            return configsInScope;
        }

        Supplier<List<ConfigTab>> tabProvider = Registry.CONFIG_TAB.getConfigTabProviderFor(mod);

        if (tabProvider != null)
        {
            ConfigTab configTab = this.categoriesDropDownWidget.getSelectedEntry();

            if (configTab == null)
            {
                tabProvider.get().forEach((tab) -> this.addConfigsHavingStatusWidgetFactory(tab, configsInScope::add));
            }
            else
            {
                for (ConfigTab tab : tabProvider.get())
                {
                    if (tab == configTab)
                    {
                        this.addConfigsHavingStatusWidgetFactory(tab, configsInScope::add);
                        break;
                    }
                }
            }
        }

        HashSet<ConfigOnTab> existingConfigs = new HashSet<>(this.widget.getConfigs());
        configsInScope.removeAll(existingConfigs);

        return configsInScope;
    }

    protected void addConfigsHavingStatusWidgetFactory(ConfigTab tab, Consumer<ConfigOnTab> consumer)
    {
        tab.getTabbedExpandedConfigs((c) -> this.addConfigIfHasStatusWidgetFactory(c, consumer));
    }

    protected void addConfigIfHasStatusWidgetFactory(ConfigOnTab cfg, Consumer<ConfigOnTab> consumer)
    {
        if (Registry.CONFIG_STATUS_WIDGET.getConfigStatusWidgetFactory(cfg.getConfig()) != null)
        {
            consumer.accept(cfg);
        }
    }
}
