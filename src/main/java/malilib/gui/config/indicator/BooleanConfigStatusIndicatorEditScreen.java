package malilib.gui.config.indicator;

import malilib.gui.widget.DropDownListWidget;
import malilib.gui.widget.LabelWidget;
import malilib.overlay.widget.sub.BooleanConfigStatusWidget;
import malilib.overlay.widget.sub.BooleanConfigStatusWidget.EnabledCondition;

public class BooleanConfigStatusIndicatorEditScreen<WIDGET extends BooleanConfigStatusWidget>
extends BaseConfigStatusIndicatorEditScreen<WIDGET>
{
    protected final LabelWidget typeLabel;
    protected final LabelWidget conditionLabel;
    protected final DropDownListWidget<EnabledCondition> conditionDropdown;
    protected final DropDownListWidget<BooleanConfigStatusWidget.Style> typeDropdown;

    public BooleanConfigStatusIndicatorEditScreen(WIDGET widget)
    {
        super(widget);

        this.typeLabel = new LabelWidget("malilib.label.boolean_config_status.render_type");
        this.conditionLabel = new LabelWidget("malilib.label.boolean_config_status.enabled_condition");

        this.typeDropdown = new DropDownListWidget<>(16, 10, BooleanConfigStatusWidget.Style.VALUES,
                                                     BooleanConfigStatusWidget.Style::getDisplayName);
        this.typeDropdown.setSelectedEntry(widget.getRenderStyle());
        this.typeDropdown.setSelectionListener(this.widget::setRenderStyle);

        this.conditionDropdown = new DropDownListWidget<>(16, 10,
                                                          BooleanConfigStatusWidget.EnabledCondition.VALUES,
                                                          BooleanConfigStatusWidget.EnabledCondition::getDisplayName);
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
