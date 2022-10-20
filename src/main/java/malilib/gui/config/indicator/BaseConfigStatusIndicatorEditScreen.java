package malilib.gui.config.indicator;

import malilib.MaLiLibReference;
import malilib.gui.BaseScreen;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.ColorIndicatorAndEditWidget;
import malilib.gui.widget.LabelWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.overlay.widget.sub.BaseConfigStatusIndicatorWidget;

public class BaseConfigStatusIndicatorEditScreen <WIDGET extends BaseConfigStatusIndicatorWidget<?>> extends BaseScreen
{
    protected final WIDGET widget;
    protected final LabelWidget nameLabel;
    protected final LabelWidget nameColorLabel;
    protected final LabelWidget valueColorLabel;
    protected final BaseTextFieldWidget nameTextFieldWidget;
    protected final GenericButton nameResetButton;
    protected final ColorIndicatorAndEditWidget nameColorWidget;
    protected final ColorIndicatorAndEditWidget valueColorWidget;

    public BaseConfigStatusIndicatorEditScreen(WIDGET widget)
    {
        this.widget = widget;

        this.useTitleHierarchy = false;
        this.setTitle("malilibdev.title.screen.configs.config_status_indicator_configuration", MaLiLibReference.MOD_VERSION);

        this.nameLabel = new LabelWidget("malilibdev.label.misc.name.colon");
        this.nameColorLabel = new LabelWidget("malilibdev.label.misc.name_color");
        this.valueColorLabel = new LabelWidget("malilibdev.label.misc.value_color");

        this.nameTextFieldWidget = new BaseTextFieldWidget(240, 16, widget.getName());
        this.nameTextFieldWidget.setListener(this.widget::setName);

        this.nameResetButton = GenericButton.create(DefaultIcons.RESET_12, this::resetName);
        this.nameResetButton.translateAndAddHoverString("malilibdev.hover.button.config.config_status_indicator.reset_name");

        this.nameColorWidget = new ColorIndicatorAndEditWidget(90, 16, this.widget::getNameColor, this.widget::setNameColor);
        this.valueColorWidget = new ColorIndicatorAndEditWidget(90, 16, this.widget::getValueColor, this.widget::setValueColor);
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.nameLabel);
        this.addWidget(this.nameTextFieldWidget);
        this.addWidget(this.nameResetButton);

        this.addWidget(this.nameColorLabel);
        this.addWidget(this.nameColorWidget);

        this.reAddTypeSpecificWidgets();
    }

    protected void reAddTypeSpecificWidgets()
    {
        this.addWidget(this.valueColorLabel);
        this.addWidget(this.valueColorWidget);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 30;

        this.nameLabel.setPosition(x, y + 3);
        this.nameTextFieldWidget.setPosition(this.nameLabel.getRight() + 6, y);
        this.nameResetButton.setPosition(this.nameTextFieldWidget.getRight() + 2, y + 2);

        y += 20;
        this.nameColorLabel.setPosition(x, y + 3);
        this.nameColorWidget.setPosition(this.nameColorLabel.getRight() + 6, y);

        this.updateTypeSpecificWidgetPositions();
    }

    protected void updateTypeSpecificWidgetPositions()
    {
        int x = this.x + 10;
        int y = this.y + 70;

        this.valueColorLabel.setPosition(x, y + 3);

        x = Math.max(this.nameColorLabel.getRight(), this.valueColorLabel.getRight()) + 6;
        this.valueColorWidget.setPosition(x, y);
        this.nameColorWidget.setX(x);
    }

    protected void resetName()
    {
        String name = this.widget.getConfigOnTab().getConfig().getDisplayName();
        this.widget.setName(name);
        this.nameTextFieldWidget.setText(name);
    }
}
