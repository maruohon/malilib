package fi.dy.masa.malilib.gui.widget.list.entry.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.gui.button.BooleanConfigButton;
import fi.dy.masa.malilib.gui.button.KeyBindConfigButton;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.KeybindSettingsWidget;

public class HotkeyedBooleanConfigWidget extends BaseConfigOptionWidget<HotkeyedBooleanConfig>
{
    protected final HotkeyedBooleanConfig config;
    protected final ImmutableList<Integer> initialHotkeyValue;
    protected final BooleanConfigButton booleanButton;
    protected final KeyBindConfigButton hotkeyButton;
    protected final KeybindSettingsWidget settingsWidget;
    protected final boolean initialBooleanValue;

    public HotkeyedBooleanConfigWidget(int x, int y, int width, int height, int listIndex,
                                       int originalListIndex, HotkeyedBooleanConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, 22, listIndex, originalListIndex, config, ctx);

        this.config = config;
        this.initialBooleanValue = this.config.getBooleanValue();
        this.initialHotkeyValue = this.config.getKeyBind().getKeys();

        this.booleanButton = new BooleanConfigButton(x, y + 1, 60, 20, this.config);
        this.booleanButton.setActionListener((btn, mbtn) -> this.resetButton.setEnabled(this.config.isModified()));

        this.hotkeyButton = new KeyBindConfigButton(x, y + 1, 120, 20, this.config.getKeyBind(), ctx.gui);
        this.hotkeyButton.setValueChangeListener(() -> this.resetButton.setEnabled(this.config.isModified()));

        this.settingsWidget = new KeybindSettingsWidget(x, y, 20, 20, config.getKeyBind(),
                                                        config.getDisplayName(), ctx.gui.getDialogHandler());

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.resetButton.setEnabled(this.config.isModified());
            this.booleanButton.updateDisplayString();
            this.hotkeyButton.updateDisplayString();
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getX() + this.getMaxLabelWidth() + 10;
        int y = this.getY() + 1;
        int elementWidth = this.getElementWidth();

        this.booleanButton.setPosition(x, y);

        x += 62;
        this.hotkeyButton.setPosition(x, y);
        this.hotkeyButton.setWidth(elementWidth - 84);

        x += this.hotkeyButton.getWidth() + 2;
        this.settingsWidget.setPosition(x, y);

        x += this.settingsWidget.getWidth() + 4;
        this.updateResetButton(x, y, this.config);

        this.addWidget(this.booleanButton);
        this.addWidget(this.hotkeyButton);
        this.addWidget(this.settingsWidget);
        this.addWidget(this.resetButton);
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getBooleanValue() != this.initialBooleanValue ||
               this.config.getKeyBind().getKeys().equals(this.initialHotkeyValue) == false;
    }
}
