package fi.dy.masa.malilib.gui.config;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.ColorIndicatorWidget;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.IntegerEditWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.OnOffButton;
import fi.dy.masa.malilib.gui.widget.button.OnOffStyle;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.ConfigStatusIndicatorEntryWidget;
import fi.dy.masa.malilib.message.InfoOverlay;
import fi.dy.masa.malilib.overlay.widget.BaseConfigStatusIndicatorWidget;
import fi.dy.masa.malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigStatusIndicatorGroupEditScreen extends BaseListScreen<DataListWidget<BaseConfigStatusIndicatorWidget<?>>>
{
    protected final ConfigStatusIndicatorContainerWidget widget;
    protected final DropDownListWidget<ScreenLocation> locationDropdownWidget;
    protected final LabelWidget nameLabelWidget;
    protected final LabelWidget backgroundLabelWidget;
    protected final LabelWidget priorityLabelWidget;
    protected final LabelWidget lineHeightLabelWidget;
    protected final LabelWidget renderNameLabelWidget;
    protected final GenericButton groupEnabledToggleButton;
    protected final GenericButton backgroundEnabledToggleButton;
    protected final GenericButton renderNameToggleButton;
    protected final GenericButton addConfigsButton;
    protected final ColorIndicatorWidget backgroundColorWidget;
    protected final IntegerEditWidget priorityEditWidget;
    protected final IntegerEditWidget lineHeightEditWidget;
    protected final BaseTextFieldWidget nameTextField;

    public ConfigStatusIndicatorGroupEditScreen(ConfigStatusIndicatorContainerWidget widget)
    {
        super(10, 81, 20, 88);

        this.widget = widget;
        this.useTitleHierarchy = false;
        this.title = StringUtils.translate("malilib.gui.title.config_status_indicator_configuration");

        this.locationDropdownWidget = new DropDownListWidget<>(0, 0, -1, 16, 160, 10, ScreenLocation.VALUES,
                                                               OptionListConfigValue::getDisplayName, null);
        this.locationDropdownWidget.setSelectedEntry(widget.getScreenLocation());
        this.locationDropdownWidget.setSelectionListener(this::changeWidgetLocation);

        this.nameLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.name.colon");
        this.priorityLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.sort_index.colon");
        this.lineHeightLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.line_height.colon");
        this.backgroundLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.background.colon");
        this.renderNameLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.name.colon");

        this.nameTextField = new BaseTextFieldWidget(0, 0, 160, 16, widget.getName());
        this.nameTextField.setListener(widget::setName);

        this.priorityEditWidget = new IntegerEditWidget(0, 0, 72, 16, widget.getSortIndex(), -1000, 1000, widget::setSortIndex);
        this.lineHeightEditWidget = new IntegerEditWidget(0, 0, 72, 16, widget.getLineHeight(), 6, 40, widget::setLineHeight);

        this.addConfigsButton = new GenericButton(0, 0, -1, 20, "malilib.gui.button.add_configs", "malilib.gui.button.hover.add_configs_to_status_indicator_group");
        this.addConfigsButton.setActionListener(this::openAddConfigsScreen);

        this.groupEnabledToggleButton = new OnOffButton(0, 0, -1, 16, OnOffStyle.SLIDER_ON_OFF, widget::isEnabled, null);
        this.groupEnabledToggleButton.setActionListener(widget::toggleEnabled);

        this.backgroundEnabledToggleButton = new OnOffButton(0, 0, -1, 16, OnOffStyle.SLIDER_ON_OFF, widget::isBackgroundEnabled, null);
        this.backgroundEnabledToggleButton.setActionListener(widget::toggleBackgroundEnabled);

        this.renderNameToggleButton = new OnOffButton(0, 0, -1, 16, OnOffStyle.SLIDER_ON_OFF, widget::getRenderName, null);
        this.renderNameToggleButton.setActionListener(widget::toggleRenderName);

        this.backgroundColorWidget = new ColorIndicatorWidget(0, 0, 16, 16, widget::getBackgroundColor, widget::setBackgroundColor);
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        int x = this.x + 10;
        int y = this.y + 24;
        int tmpX;

        this.nameLabelWidget.setPosition(x, y + 3);
        tmpX = this.nameLabelWidget.getRight() + 6;

        this.nameTextField.setPosition(tmpX, y);

        this.locationDropdownWidget.setPosition(this.nameTextField.getRight() + 6, y);
        this.groupEnabledToggleButton.setPosition(this.locationDropdownWidget.getRight() + 6, y);

        y += 18;
        this.renderNameLabelWidget.setPosition(x, y + 3);
        this.renderNameToggleButton.setPosition(this.renderNameLabelWidget.getRight() + 6, y);

        this.backgroundLabelWidget.setPosition(this.renderNameToggleButton.getRight() + 10, y + 3);
        this.backgroundEnabledToggleButton.setPosition(this.backgroundLabelWidget.getRight() + 6, y);
        this.backgroundColorWidget.setPosition(this.backgroundEnabledToggleButton.getRight() + 2, y);

        this.lineHeightLabelWidget.setPosition(this.backgroundColorWidget.getRight() + 6, y + 3);
        this.lineHeightEditWidget.setPosition(this.lineHeightLabelWidget.getRight() + 6, y);

        y += 18;
        this.addConfigsButton.setPosition(x, y);

        this.priorityLabelWidget.setPosition(this.lineHeightLabelWidget.getX(), y + 5);
        this.priorityEditWidget.setPosition(this.priorityLabelWidget.getRight() + 6, y + 2);

        this.addWidget(this.nameLabelWidget);
        this.addWidget(this.nameTextField);
        this.addWidget(this.locationDropdownWidget);

        this.addWidget(this.priorityLabelWidget);
        this.addWidget(this.priorityEditWidget);

        this.addWidget(this.groupEnabledToggleButton);

        this.addWidget(this.lineHeightLabelWidget);
        this.addWidget(this.lineHeightEditWidget);

        this.addWidget(this.renderNameLabelWidget);
        this.addWidget(this.renderNameToggleButton);

        this.addWidget(this.backgroundLabelWidget);
        this.addWidget(this.backgroundEnabledToggleButton);
        this.addWidget(this.backgroundColorWidget);

        this.addWidget(this.addConfigsButton);

        this.getListWidget().refreshEntries();
    }

    @Override
    public void onGuiClosed()
    {
        this.widget.setStatusIndicatorWidgets(this.getListWidget().getCurrentContents());

        super.onGuiClosed();
    }

    protected void changeWidgetLocation(ScreenLocation location)
    {
        InfoOverlay.INSTANCE.getOrCreateInfoArea(this.widget.getScreenLocation()).removeWidget(this.widget);
        this.widget.setLocation(location);
        InfoOverlay.INSTANCE.getOrCreateInfoArea(this.widget.getScreenLocation()).addWidget(this.widget);
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
