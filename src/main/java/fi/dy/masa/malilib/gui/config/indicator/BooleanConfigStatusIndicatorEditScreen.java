package fi.dy.masa.malilib.gui.config.indicator;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.overlay.widget.sub.BooleanConfigStatusWidget;

public class BooleanConfigStatusIndicatorEditScreen<WIDGET extends BooleanConfigStatusWidget>
extends BaseConfigStatusIndicatorEditScreen<WIDGET>
{
    protected final LabelWidget typeLabel;
    protected final LabelWidget conditionLabel;
    protected final DropDownListWidget<BooleanConfigStatusWidget.EnabledCondition> conditionDropdown;
    protected final DropDownListWidget<BooleanConfigStatusWidget.Style> typeDropdown;

    public BooleanConfigStatusIndicatorEditScreen(WIDGET widget, @Nullable GuiScreen parent)
    {
        super(widget, parent);

        this.typeLabel = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.boolean_config_status.render_type.colon");
        this.conditionLabel = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.boolean_config_status.enabled_condition.colon");

        this.typeDropdown = new DropDownListWidget<>(0, 0, -1, 16, 160, 10, BooleanConfigStatusWidget.Style.VALUES,
                                                     BooleanConfigStatusWidget.Style::getDisplayName, null);
        this.typeDropdown.setSelectedEntry(widget.getRenderStyle());
        this.typeDropdown.setSelectionListener(this.widget::setRenderStyle);

        this.conditionDropdown = new DropDownListWidget<>(0, 0, -1, 16, 160, 10,
                                                          BooleanConfigStatusWidget.EnabledCondition.VALUES,
                                                          BooleanConfigStatusWidget.EnabledCondition::getDisplayName, null);
        this.conditionDropdown.setSelectedEntry(widget.getEnabledCondition());
        this.conditionDropdown.setSelectionListener(this.widget::setEnabledCondition);
    }

    @Override
    protected void reAddTypeSpecificWidgets()
    {
        this.addWidget(this.typeLabel);
        this.addWidget(this.typeDropdown);

        this.addWidget(this.conditionLabel);
        this.addWidget(this.conditionDropdown);
    }

    @Override
    protected void updateTypeSpecificWidgetPositions()
    {
        super.updateTypeSpecificWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 70;

        this.typeLabel.setPosition(x, y + 3);
        this.typeDropdown.setPosition(this.typeLabel.getRight() + 6, y);

        y += 20;
        this.conditionLabel.setPosition(x, y + 3);
        this.conditionDropdown.setPosition(this.conditionLabel.getRight() + 6, y);
    }
}
