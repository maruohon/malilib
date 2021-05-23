package fi.dy.masa.malilib.gui.widget.list.entry.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.KeybindSettingsWidget;
import fi.dy.masa.malilib.gui.widget.button.BooleanConfigButton;
import fi.dy.masa.malilib.gui.widget.button.KeyBindConfigButton;
import fi.dy.masa.malilib.input.KeyBind;

public abstract class BaseHotkeyedBooleanConfigWidget extends BaseConfigWidget<ConfigInfo>
{
    protected final BooleanConfig booleanConfig;
    protected final KeyBind keyBind;
    protected final ImmutableList<Integer> initialHotkeyValue;
    protected final BooleanConfigButton booleanButton;
    protected final KeyBindConfigButton hotkeyButton;
    protected final KeybindSettingsWidget settingsWidget;
    protected final boolean initialBooleanValue;

    public BaseHotkeyedBooleanConfigWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                           ConfigInfo baseConfig, BooleanConfig booleanConfig, KeyBind keyBind,
                                           ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, baseConfig, ctx);

        this.booleanConfig = booleanConfig;
        this.keyBind = keyBind;
        this.initialBooleanValue = booleanConfig.getBooleanValue();
        this.initialHotkeyValue = this.keyBind.getKeys();

        this.booleanButton = new BooleanConfigButton(-1, 20, booleanConfig);
        this.booleanButton.setHoverStringProvider("locked", this.booleanConfig::getLockAndOverrideMessages);
        this.booleanButton.setActionListener(() -> {
            this.booleanConfig.toggleBooleanValue();
            this.updateButtonStates();
        });

        this.hotkeyButton = new KeyBindConfigButton(120, 20, keyBind, ctx.getKeybindEditingScreen());
        this.hotkeyButton.setHoverStringProvider("locked", this.booleanConfig::getLockAndOverrideMessages);
        this.hotkeyButton.setValueChangeListener(this::updateButtonStates);

        this.settingsWidget = new KeybindSettingsWidget(keyBind, booleanConfig.getDisplayName(), ctx.getDialogHandler());

        this.resetButton.setActionListener(() -> {
            this.config.resetToDefault();
            this.updateButtonStates();
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getElementsStartPosition();
        int y = this.getY() + 1;
        int elementWidth = this.getElementWidth();

        this.booleanButton.setPosition(x, y);

        int w = this.booleanButton.getWidth();
        x += w + 4;
        this.hotkeyButton.setPosition(x, y);

        if (elementWidth < 0)
        {
            w = 120;
        }
        else
        {
            w = elementWidth - w - 20 - 4;
        }

        this.hotkeyButton.setWidth(w);

        x += w + 2;
        this.settingsWidget.setPosition(x, y);

        x += this.settingsWidget.getWidth() + 4;
        this.updateResetButton(x, y);

        this.addWidget(this.booleanButton);
        this.addWidget(this.hotkeyButton);
        this.addWidget(this.settingsWidget);
        this.addWidget(this.resetButton);

        this.updateButtonStates();
    }

    @Override
    public boolean wasModified()
    {
        return this.booleanConfig.getBooleanValue() != this.initialBooleanValue ||
               this.keyBind.getKeys().equals(this.initialHotkeyValue) == false;
    }

    protected void updateButtonStates()
    {
        this.booleanButton.setEnabled(this.booleanConfig.isLocked() == false);
        this.booleanButton.updateDisplayString();
        this.booleanButton.updateHoverStrings();

        this.hotkeyButton.setEnabled(this.booleanConfig.isLocked() == false);
        this.hotkeyButton.setHoverInfoRequiresShift(this.booleanConfig.isLocked() == false);
        this.hotkeyButton.updateDisplayString();
        this.hotkeyButton.updateHoverStrings();

        this.resetButton.setEnabled(this.config.isModified() && this.booleanConfig.isLocked() == false);
    }
}
