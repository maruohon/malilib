package fi.dy.masa.malilib.gui.config.indicator;

import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.OnOffButton;
import fi.dy.masa.malilib.overlay.widget.sub.HotkeyedBooleanConfigStatusWidget;

public class HotkeyedBooleanConfigStatusIndicatorEditScreen
extends BooleanConfigStatusIndicatorEditScreen<HotkeyedBooleanConfigStatusWidget>
{
    protected final LabelWidget booleanEnabledLabel;
    protected final LabelWidget hotkeyEnabledLabel;
    protected final OnOffButton booleanEnabledButton;
    protected final OnOffButton hotkeyEnabledButton;

    public HotkeyedBooleanConfigStatusIndicatorEditScreen(HotkeyedBooleanConfigStatusWidget widget, @Nullable Screen parent)
    {
        super(widget, parent);

        this.booleanEnabledLabel = new LabelWidget("malilib.label.config_status_indicator.hotkeyed_boolean.show_toggle");
        this.hotkeyEnabledLabel = new LabelWidget("malilib.label.config_status_indicator.hotkeyed_boolean.show_hotkey");

        this.booleanEnabledButton = OnOffButton.simpleSlider(16, widget::getShowBoolean, widget::toggleShowBoolean);
        this.hotkeyEnabledButton = OnOffButton.simpleSlider(16, widget::getShowHotkey, widget::toggleShowHotkey);
    }

    @Override
    protected void reAddTypeSpecificWidgets()
    {
        super.reAddTypeSpecificWidgets();

        this.addWidget(this.booleanEnabledLabel);
        this.addWidget(this.booleanEnabledButton);

        this.addWidget(this.hotkeyEnabledLabel);
        this.addWidget(this.hotkeyEnabledButton);
    }

    @Override
    protected void updateTypeSpecificWidgetPositions()
    {
        super.updateTypeSpecificWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 110;

        this.booleanEnabledLabel.setPosition(x, y + 3);
        this.hotkeyEnabledLabel.setPosition(x, y + 23);

        int tmpX = Math.max(this.booleanEnabledLabel.getRight(), this.hotkeyEnabledLabel.getRight()) + 6;
        this.booleanEnabledButton.setPosition(tmpX, y);
        this.hotkeyEnabledButton.setPosition(tmpX, y + 20);
    }
}
