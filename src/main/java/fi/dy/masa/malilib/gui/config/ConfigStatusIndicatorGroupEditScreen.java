package fi.dy.masa.malilib.gui.config;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.ConfigStatusIndicatorEntryWidget;
import fi.dy.masa.malilib.overlay.widget.BaseConfigStatusIndicatorWidget;
import fi.dy.masa.malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigStatusIndicatorGroupEditScreen extends BaseListScreen<DataListWidget<BaseConfigStatusIndicatorWidget<?>>>
{
    protected final ConfigStatusIndicatorContainerWidget widget;
    protected final DropDownListWidget<ScreenLocation> locationDropdownWidget;
    protected final LabelWidget nameLabelWidget;
    protected final BaseTextFieldWidget nameTextField;
    protected final GenericButton addConfigsButton;

    public ConfigStatusIndicatorGroupEditScreen(ConfigStatusIndicatorContainerWidget widget)
    {
        super(10, 68, 20, 88);

        this.widget = widget;
        this.useTitleHierarchy = false;
        this.title = StringUtils.translate("malilib.gui.title.config_status_indicator_configuration");

        this.locationDropdownWidget = new DropDownListWidget<>(0, 0, -1, 15, 160, 10, ScreenLocation.VALUES,
                                                               OptionListConfigValue::getDisplayName, null);
        this.locationDropdownWidget.setSelectedEntry(widget.getScreenLocation());
        this.locationDropdownWidget.setSelectionListener(widget::setLocation);

        this.nameLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.name.colon");
        this.nameTextField = new BaseTextFieldWidget(0, 0, 160, 15, widget.getName());
        this.nameTextField.setListener(widget::setName);

        this.addConfigsButton = new GenericButton(0, 0, -1, 20, "malilib.gui.button.add_configs", "malilib.gui.button.hover.add_configs_to_status_indicator_group");
        this.addConfigsButton.setActionListener(this::openAddConfigsScreen);
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        int x = this.x + 10;
        int y = this.y + 24;

        this.nameLabelWidget.setPosition(x, y + 3);
        this.nameTextField.setPosition(this.nameLabelWidget.getRight() + 6, y);

        this.locationDropdownWidget.setPosition(this.nameTextField.getRight() + 6, y);

        y += 20;
        this.addConfigsButton.setPosition(x, y);

        this.addWidget(this.locationDropdownWidget);
        this.addWidget(this.nameLabelWidget);
        this.addWidget(this.nameTextField);
        this.addWidget(this.addConfigsButton);

        this.getListWidget().refreshEntries();
    }

    @Override
    public void onGuiClosed()
    {
        this.widget.setStatusIndicatorWidgets(this.getListWidget().getCurrentContents());

        super.onGuiClosed();
    }

    @Nullable
    @Override
    protected DataListWidget<BaseConfigStatusIndicatorWidget<?>> createListWidget(int listX, int listY,
                                                                                  int listWidth, int listHeight)
    {
        DataListWidget<BaseConfigStatusIndicatorWidget<?>> listWidget 
                = new DataListWidget<>(listX, listY, listWidth, listHeight, this.widget::getStatusIndicatorWidgets);
        listWidget.setEntryWidgetFactory((wx, wy, ww, wh, li, oi, d, lw) ->
                                         new ConfigStatusIndicatorEntryWidget(wx, wy, ww, wh, li, oi,
                                                                              d, lw, this.widget));
        listWidget.setBorderWidth(1);
        listWidget.setFetchFromSupplierOnRefresh(true);

        return listWidget;
    }

    protected void openAddConfigsScreen()
    {
        ConfigStatusIndicatorGroupAddConfigsScreen screen = new ConfigStatusIndicatorGroupAddConfigsScreen(this.widget);
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }
}
