package fi.dy.masa.malilib.gui.config.indicator;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.ColorEditorWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.overlay.widget.sub.BaseConfigStatusIndicatorWidget;
import fi.dy.masa.malilib.util.StringUtils;

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

        this.setParent(parent);
        this.useTitleHierarchy = false;
        this.title = StringUtils.translate("malilib.gui.title.config_status_indicator_configuration");

        this.nameLabel = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.name.colon");
        this.nameColorLabel = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.name_color.colon");
        this.valueColorLabel = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.value_color.colon");

        this.nameTextFieldWidget = new BaseTextFieldWidget(0, 0, 240, 16, widget.getName());
        this.nameTextFieldWidget.setListener(this.widget::setName);

        this.nameResetButton = GenericButton.createIconOnly(0, 0, DefaultIcons.RESET_12);
        this.nameResetButton.addHoverStrings(StringUtils.translate("malilib.gui.button.hover.config_status_indicator.reset_name"));
        this.nameResetButton.setActionListener(this::resetName);

        this.nameColorWidget = new ColorEditorWidget(0, 0, 90, 16, this.widget::getNameColor, this.widget::setNameColor);
        this.valueColorWidget = new ColorEditorWidget(0, 0, 90, 16, this.widget::getValueColor, this.widget::setValueColor);
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        int x = this.x + 10;
        int y = this.y + 30;

        this.nameLabel.setPosition(x, y + 3);
        this.nameTextFieldWidget.setPosition(this.nameLabel.getRight() + 6, y);
        this.nameResetButton.setPosition(this.nameTextFieldWidget.getRight() + 2, y + 2);

        y += 20;
        this.nameColorLabel.setPosition(x, y + 3);
        this.nameColorWidget.setPosition(this.nameColorLabel.getRight() + 6, y);

        this.addWidget(this.nameLabel);
        this.addWidget(this.nameTextFieldWidget);
        this.addWidget(this.nameResetButton);

        this.addWidget(this.nameColorLabel);
        this.addWidget(this.nameColorWidget);

        this.addTypeSpecificWidgets();
    }

    protected void addTypeSpecificWidgets()
    {
        int x = this.x + 10;
        int y = this.y + 70;

        this.valueColorLabel.setPosition(x, y + 3);

        x = Math.max(this.nameColorLabel.getRight(), this.valueColorLabel.getRight()) + 6;
        this.valueColorWidget.setPosition(x, y);
        this.nameColorWidget.setX(x);

        this.addWidget(this.valueColorLabel);
        this.addWidget(this.valueColorWidget);
    }

    protected void resetName()
    {
        String name = this.widget.getConfigOnTab().config.getDisplayName();
        this.widget.setName(name);
        this.nameTextFieldWidget.setText(name);
    }
}
