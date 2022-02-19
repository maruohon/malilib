package fi.dy.masa.malilib.gui.config.indicator;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.ColorEditorWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.overlay.widget.sub.BaseConfigStatusIndicatorWidget;

public class BaseConfigStatusIndicatorEditScreen <WIDGET extends BaseConfigStatusIndicatorWidget<?>> extends BaseScreen
{
    protected final WIDGET widget;
    protected final LabelWidget nameLabel;
    protected final LabelWidget nameColorLabel;
    protected final LabelWidget valueColorLabel;
    protected final BaseTextFieldWidget nameTextFieldWidget;
    protected final GenericButton nameResetButton;
    protected final ColorEditorWidget nameColorWidget;
    protected final ColorEditorWidget valueColorWidget;

    public BaseConfigStatusIndicatorEditScreen(WIDGET widget, @Nullable GuiScreen parent)
    {
        this.widget = widget;

        this.useTitleHierarchy = false;
        this.setTitle("malilib.title.screen.configs.config_status_indicator_configuration", MaLiLibReference.MOD_VERSION);
        this.setParent(parent);

        this.nameLabel = new LabelWidget("malilib.label.misc.name.colon");
        this.nameColorLabel = new LabelWidget("malilib.label.misc.name_color");
        this.valueColorLabel = new LabelWidget("malilib.label.misc.value_color");

        this.nameTextFieldWidget = new BaseTextFieldWidget(240, 16, widget.getName());
        this.nameTextFieldWidget.setListener(this.widget::setName);

        this.nameResetButton = GenericButton.create(DefaultIcons.RESET_12, this::resetName);
        this.nameResetButton.translateAndAddHoverString("malilib.hover.button.config.config_status_indicator.reset_name");

        this.nameColorWidget = new ColorEditorWidget(90, 16, this.widget::getNameColor, this.widget::setNameColor);
        this.valueColorWidget = new ColorEditorWidget(90, 16, this.widget::getValueColor, this.widget::setValueColor);
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
