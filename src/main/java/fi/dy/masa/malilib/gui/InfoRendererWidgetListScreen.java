package fi.dy.masa.malilib.gui;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.MaLiLibConfigScreen;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetFactory;
import fi.dy.masa.malilib.overlay.InfoWidgetManager;
import fi.dy.masa.malilib.overlay.message.MessageType;
import fi.dy.masa.malilib.overlay.message.MessageUtils;
import fi.dy.masa.malilib.overlay.widget.InfoRendererWidget;

public class InfoRendererWidgetListScreen<WIDGET extends InfoRendererWidget> extends BaseListScreen<DataListWidget<WIDGET>>
{
    protected final Supplier<List<WIDGET>> widgetSupplier;
    protected final DataListEntryWidgetFactory<WIDGET> entryWidgetFactory;
    protected final Supplier<WIDGET> widgetFactory;
    protected final DropDownListWidget<ScreenLocation> locationDropdownWidget;
    protected final GenericButton createWidgetButton;

    public InfoRendererWidgetListScreen(Supplier<List<WIDGET>> widgetSupplier,
                                        Supplier<WIDGET> widgetFactory,
                                        DataListEntryWidgetFactory<WIDGET> entryWidgetFactory)
    {
        this(10, 82, 20, 88,
             MaLiLibReference.MOD_ID, MaLiLibConfigScreen.ALL_TABS, MaLiLibConfigScreen.GENERIC,
             widgetSupplier, widgetFactory, entryWidgetFactory);
    }

    public InfoRendererWidgetListScreen(int listX, int listY, int totalListMarginX, int totalListMarginY,
                                        String screenId, List<ScreenTab> tabs, @Nullable ScreenTab defaultTab,
                                        Supplier<List<WIDGET>> widgetSupplier,
                                        Supplier<WIDGET> widgetFactory,
                                        DataListEntryWidgetFactory<WIDGET> entryWidgetFactory)
    {
        super(listX, listY, totalListMarginX, totalListMarginY, screenId, tabs, defaultTab);

        this.widgetSupplier = widgetSupplier;
        this.widgetFactory = widgetFactory;
        this.entryWidgetFactory = entryWidgetFactory;

        this.locationDropdownWidget = new DropDownListWidget<>(10, 50, -1, 14, 160, 10, ScreenLocation.VALUES, OptionListConfigValue::getDisplayName, null);

        int x = this.locationDropdownWidget.getRight() + 4;
        this.createWidgetButton = new GenericButton(x, 47, -1, 20, "malilib.gui.button.label.add_status_indicator_widget");
        this.createWidgetButton.setActionListener(this::createInfoRendererWidget);
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        this.addWidget(this.locationDropdownWidget);
        this.addWidget(this.createWidgetButton);
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        InfoWidgetManager.INSTANCE.saveToFile();
    }

    protected void createInfoRendererWidget()
    {
        this.addInfoRendererWidget(this.widgetFactory.get());
    }

    protected void addInfoRendererWidget(InfoRendererWidget widget)
    {
        ScreenLocation location = this.locationDropdownWidget.getSelectedEntry();

        if (location == null)
        {
            MessageUtils.showGuiOrInGameMessage(MessageType.ERROR, "malilib.message.error.select_screen_location_in_dropdown");
            return;
        }

        widget.setLocation(location);
        InfoWidgetManager.INSTANCE.addWidget(widget);
        this.getListWidget().refreshEntries();
    }

    @Override
    protected DataListWidget<WIDGET> createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        DataListWidget<WIDGET> listWidget = new DataListWidget<>(listX, listY, listWidth, listHeight, this.widgetSupplier);
        listWidget.setEntryWidgetFactory(this.entryWidgetFactory);
        listWidget.setFetchFromSupplierOnRefresh(true);

        return listWidget;
    }

    public static <WIDGET extends InfoRendererWidget> Supplier<List<WIDGET>> createSupplierFromInfoManager(final Class<WIDGET> clazz)
    {
        return () -> InfoWidgetManager.INSTANCE.getAllWidgetsOfType(clazz);
    }
}
