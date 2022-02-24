package fi.dy.masa.malilib.gui.edit.overlay;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.MaLiLibConfigScreen;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.config.value.ScreenLocation;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.tab.ScreenTab;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetFactory;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.overlay.widget.InfoRendererWidget;
import fi.dy.masa.malilib.registry.Registry;

public class InfoRendererWidgetListScreen<WIDGET extends InfoRendererWidget> extends BaseListScreen<DataListWidget<WIDGET>>
{
    protected final Supplier<List<WIDGET>> widgetSupplier;
    protected final DataListEntryWidgetFactory<WIDGET> entryWidgetFactory;
    @Nullable protected final Supplier<WIDGET> widgetFactory;
    protected final DropDownListWidget<ScreenLocation> locationDropdownWidget;
    protected final GenericButton createWidgetButton;
    protected boolean canCreateNewWidgets;

    public InfoRendererWidgetListScreen(Supplier<List<WIDGET>> widgetSupplier,
                                        @Nullable Supplier<WIDGET> widgetFactory,
                                        DataListEntryWidgetFactory<WIDGET> entryWidgetFactory)
    {
        this(10, 74, 20, 80,
             MaLiLibReference.MOD_ID, MaLiLibConfigScreen.ALL_TABS, MaLiLibConfigScreen.GENERIC,
             widgetSupplier, widgetFactory, entryWidgetFactory);
    }

    public InfoRendererWidgetListScreen(int listX, int listY, int totalListMarginX, int totalListMarginY,
                                        String screenId, List<ScreenTab> tabs, @Nullable ScreenTab defaultTab,
                                        Supplier<List<WIDGET>> widgetSupplier,
                                        @Nullable Supplier<WIDGET> widgetFactory,
                                        DataListEntryWidgetFactory<WIDGET> entryWidgetFactory)
    {
        super(listX, listY, totalListMarginX, totalListMarginY, screenId, tabs, defaultTab);

        this.widgetSupplier = widgetSupplier;
        this.widgetFactory = widgetFactory;
        this.entryWidgetFactory = entryWidgetFactory;

        this.locationDropdownWidget = new DropDownListWidget<>(-1, 16, 160, 10, ScreenLocation.VALUES, OptionListConfigValue::getDisplayName);
        this.createWidgetButton = GenericButton.create(16, "malilib.button.csi_edit.add_csi_widget", this::createInfoRendererWidget);

        // Unconditionally save here, since we don't track the individual info container widget changes
        this.screenCloseListener = Registry.INFO_WIDGET_MANAGER::saveToFile;
    }

    @Override
    protected void initScreen()
    {
        if (this.canCreateNewWidgets == false)
        {
            this.totalListMarginY = 56;
            this.updateListPosition(10, 52);
        }

        super.initScreen();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        if (this.canCreateNewWidgets)
        {
            this.addWidget(this.locationDropdownWidget);
            this.addWidget(this.createWidgetButton);
        }
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 57;

        this.locationDropdownWidget.setPosition(x, y);
        this.createWidgetButton.setPosition(this.locationDropdownWidget.getRight() + 4, y);
    }

    protected void createInfoRendererWidget()
    {
        if (this.widgetFactory != null)
        {
            this.addInfoRendererWidget(this.widgetFactory.get());
        }
    }

    protected void addInfoRendererWidget(InfoRendererWidget widget)
    {
        ScreenLocation location = this.locationDropdownWidget.getSelectedEntry();

        if (location == null)
        {
            MessageDispatcher.error("malilib.message.error.select_screen_location_in_dropdown");
            return;
        }

        widget.setLocation(location);
        Registry.INFO_WIDGET_MANAGER.addWidget(widget);
        this.getListWidget().refreshEntries();
    }

    @Override
    protected DataListWidget<WIDGET> createListWidget()
    {
        DataListWidget<WIDGET> listWidget = new DataListWidget<>(this.widgetSupplier, true);

        listWidget.setEntryWidgetFactory(this.entryWidgetFactory);

        return listWidget;
    }

    public static <WIDGET extends InfoRendererWidget> Supplier<List<WIDGET>> createSupplierFromInfoManagerForExactType(final Class<WIDGET> clazz)
    {
        return () -> Registry.INFO_WIDGET_MANAGER.getAllWidgetsOfExactType(clazz);
    }

    public static <WIDGET extends InfoRendererWidget> Supplier<List<WIDGET>> createSupplierFromInfoManagerForSubtypes(final Class<WIDGET> clazz)
    {
        return () -> Registry.INFO_WIDGET_MANAGER.getAllWidgetsExtendingType(clazz);
    }
}
