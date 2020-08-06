package fi.dy.masa.malilib.gui.widget.list.entry.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeyBind;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;

public class HotkeyedBooleanConfigWidget extends BaseConfigOptionWidget<HotkeyedBooleanConfig>
{
    protected final HotkeyedBooleanConfig config;
    protected final ImmutableList<Integer> initialHotkeyValue;
    protected final boolean initialBooleanValue;
    protected final ConfigButtonBoolean booleanButton;
    protected final ConfigButtonKeyBind hotkeyButton;

    public HotkeyedBooleanConfigWidget(int x, int y, int width, int height, int listIndex, HotkeyedBooleanConfig config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, config, gui);

        this.config = config;
        this.initialBooleanValue = this.config.getBooleanValue();
        this.initialHotkeyValue = this.config.getKeyBind().getKeys();

        this.booleanButton = new ConfigButtonBoolean(x, y + 1, 60, 20, this.config);
        this.booleanButton.setActionListener((btn, mbtn) -> this.resetButton.setEnabled(this.config.isModified()));

        this.hotkeyButton = new ConfigButtonKeyBind(x, y + 1, 120, 20, this.config.getKeyBind(), this.gui);
        this.hotkeyButton.setValueChangeListener(() -> this.resetButton.setEnabled(this.config.isModified()));

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.reAddSubWidgets();
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getX() + this.getMaxLabelWidth() + 10;
        int y = this.getY() + 1;
        int elementWidth = this.gui.getConfigElementsWidth();

        this.booleanButton.setPosition(x, y);

        x += 62;
        this.hotkeyButton.setPosition(x, y);
        this.hotkeyButton.setWidth(elementWidth - 62);

        x += this.hotkeyButton.getWidth() + 4;
        this.updateResetButton(x, y, this.config);

        this.addWidget(this.booleanButton);
        this.addWidget(this.hotkeyButton);
        this.addWidget(this.resetButton);
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getBooleanValue() != this.initialBooleanValue ||
               this.config.getKeyBind().getKeys().equals(this.initialHotkeyValue) == false;
    }
}
