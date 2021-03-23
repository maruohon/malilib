package fi.dy.masa.malilib.gui.config;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.overlay.widget.sub.BooleanConfigStatusWidget;

public class BooleanConfigStatusIndicatorEditScreen
extends BaseConfigStatusIndicatorEditScreen<BooleanConfig, BooleanConfigStatusWidget>
{
    protected final LabelWidget typeLabel;
    protected final DropDownListWidget<BooleanConfigStatusWidget.Style> typeDropdown;

    public BooleanConfigStatusIndicatorEditScreen(BooleanConfigStatusWidget widget, @Nullable GuiScreen parent)
    {
        super(widget, parent);

        this.typeLabel = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.boolean_config_status_type.render_type.colon");

        this.typeDropdown = new DropDownListWidget<>(0, 0, -1, 16, 160, 10, BooleanConfigStatusWidget.Style.VALUES,
                                                     BooleanConfigStatusWidget.Style::getDisplayName, null);
        this.typeDropdown.setSelectedEntry(widget.getRenderStyle());
        this.typeDropdown.setSelectionListener(this.widget::setRenderStyle);
    }

    @Override
    protected void addTypeSpecificWidgets()
    {
        int x = this.x + 10;
        int y = this.y + 70;

        this.typeLabel.setPosition(x, y + 3);
        this.typeDropdown.setPosition(this.typeLabel.getRight() + 6, y);

        this.addWidget(this.typeLabel);
        this.addWidget(this.typeDropdown);
    }
}
