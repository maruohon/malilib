package fi.dy.masa.malilib.gui.widget.list.entry.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.KeybindSettingsWidget;
import fi.dy.masa.malilib.gui.widget.button.BooleanConfigButton;
import fi.dy.masa.malilib.gui.widget.button.KeyBindConfigButton;
import fi.dy.masa.malilib.input.KeyBind;

public abstract class BaseHotkeyedBooleanConfigWidget<C extends ConfigInfo> extends BaseConfigOptionWidget<C>
{
    protected final C config;
    protected final BooleanConfig booleanConfig;
    protected final KeyBind keyBind;
    protected final ImmutableList<Integer> initialHotkeyValue;
    protected final BooleanConfigButton booleanButton;
    protected final KeyBindConfigButton hotkeyButton;
    protected final KeybindSettingsWidget settingsWidget;
    protected final boolean initialBooleanValue;

    public BaseHotkeyedBooleanConfigWidget(int x, int y, int width, int height, int listIndex,
                                           int originalListIndex, C config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.config = config;

        this.booleanConfig = this.getBooleanConfig(config);
        this.keyBind = this.getKeyBind(config);
        this.initialBooleanValue = this.booleanConfig.getBooleanValue();
        this.initialHotkeyValue = this.keyBind.getKeys();

        this.booleanButton = new BooleanConfigButton(x, y + 1, -1, 20, this.booleanConfig);
        this.booleanButton.setActionListener((btn, mbtn) -> this.resetButton.setEnabled(this.config.isModified()));

        this.hotkeyButton = new KeyBindConfigButton(x, y + 1, 120, 20, this.keyBind, ctx.gui);
        this.hotkeyButton.setValueChangeListener(() -> this.resetButton.setEnabled(this.config.isModified()));

        this.settingsWidget = new KeybindSettingsWidget(x, y, 20, 20, this.keyBind, config.getDisplayName(), ctx.gui.getDialogHandler());

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.resetButton.setEnabled(this.config.isModified());
            this.booleanButton.updateDisplayString();
            this.hotkeyButton.updateDisplayString();
        });
    }

    protected abstract BooleanConfig getBooleanConfig(C config);

    protected abstract KeyBind getKeyBind(C config);

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getX() + this.getMaxLabelWidth() + 10;
        int y = this.getY() + 1;
        int elementWidth = this.getElementWidth();

        this.booleanButton.setPosition(x, y);

        int w = this.booleanButton.getWidth();
        x += w + 2;
        this.hotkeyButton.setPosition(x, y);

        w = elementWidth - w - 20 - 4;
        this.hotkeyButton.setWidth(w);

        x += w + 2;
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
        return this.booleanConfig.getBooleanValue() != this.initialBooleanValue ||
               this.keyBind.getKeys().equals(this.initialHotkeyValue) == false;
    }
}
