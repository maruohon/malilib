package fi.dy.masa.malilib.gui.widget.list.entry.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeyBind;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;

public class HotkeyedBooleanConfigWidget extends BaseConfigOptionWidget<HotkeyedBooleanConfig>
{
    protected final HotkeyedBooleanConfig config;
    protected final boolean initialBooleanValue;
    protected final ImmutableList<Integer> initialHotkeyValue;

    public HotkeyedBooleanConfigWidget(int x, int y, int width, int height, int listIndex, HotkeyedBooleanConfig config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, config, gui);

        this.config = config;
        this.initialBooleanValue = this.config.getBooleanValue();
        this.initialHotkeyValue = this.config.getKeyBind().getKeys();

        this.reCreateWidgets(x, y);
    }

    @Override
    protected void reCreateWidgets(int x, int y)
    {
        super.reCreateWidgets(x, y);

        x += this.getMaxLabelWidth() + 10;
        int elementWidth = this.gui.getConfigElementsWidth();
        final ConfigButtonBoolean booleanButton = new ConfigButtonBoolean(x, y + 1, 60, 20, this.config);

        x += 62;
        final ConfigButtonKeyBind hotkeyButton = new ConfigButtonKeyBind(x, y + 1, elementWidth - 60, 20, this.config.getKeyBind(), this.gui);
        hotkeyButton.setValueChangeListener(() -> this.resetButton.setEnabled(this.config.isModified()));

        x += hotkeyButton.getWidth() + 2;
        final ButtonGeneric resetButton = this.createResetButton(x, y + 1, this.config);
        this.resetButton = resetButton;

        this.addButton(booleanButton, (btn, mbtn) -> this.resetButton.setEnabled(this.config.isModified()));
        this.addWidget(hotkeyButton);
        this.addButton(resetButton, (btn, mbtn) -> {
            this.config.resetToDefault();
            this.reCreateWidgets(this.getX(), this.getY());
        });
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getBooleanValue() != this.initialBooleanValue ||
               this.config.getKeyBind().getKeys().equals(this.initialHotkeyValue) == false;
    }
}
