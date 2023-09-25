package malilib.gui.widget.list.entry.config;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import malilib.config.option.BooleanConfig;
import malilib.config.option.ConfigInfo;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.KeybindSettingsWidget;
import malilib.gui.widget.button.BooleanConfigButton;
import malilib.gui.widget.button.KeyBindConfigButton;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.input.KeyBind;

public abstract class BaseHotkeyedBooleanConfigWidget<CFG extends ConfigInfo> extends BaseConfigWidget<CFG>
{
    protected final BooleanConfig booleanConfig;
    protected final KeyBind keyBind;
    protected final IntArrayList initialHotkeyValue = new IntArrayList();
    protected final BooleanConfigButton booleanButton;
    protected final KeyBindConfigButton hotkeyButton;
    protected final KeybindSettingsWidget settingsWidget;
    protected final boolean initialBooleanValue;

    public BaseHotkeyedBooleanConfigWidget(CFG baseConfig,
                                           BooleanConfig booleanConfig,
                                           KeyBind keyBind,
                                           DataListEntryWidgetData constructData,
                                           ConfigWidgetContext ctx)
    {
        super(baseConfig, constructData, ctx);

        this.booleanConfig = booleanConfig;
        this.keyBind = keyBind;
        this.initialBooleanValue = booleanConfig.getBooleanValue();
        this.keyBind.getKeysToList(this.initialHotkeyValue);

        this.booleanButton = new BooleanConfigButton(-1, 20, booleanConfig);
        this.booleanButton.setHoverStringProvider("locked", this.booleanConfig::getLockAndOverrideMessages);
        this.booleanButton.setActionListener(() -> {
            this.booleanConfig.toggleBooleanValue();
            this.updateWidgetState();
        });

        this.hotkeyButton = new KeyBindConfigButton(120, 20, keyBind);
        this.hotkeyButton.setHoverStringProvider("locked", this.booleanConfig::getLockAndOverrideMessages);
        this.hotkeyButton.setValueChangeListener(this::onKeybindModified);
        this.hotkeyButton.setHoverInfoRequiresShift(true);

        this.settingsWidget = new KeybindSettingsWidget(keyBind, booleanConfig.getDisplayName());
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.booleanButton);
        this.addWidget(this.hotkeyButton);
        this.addWidget(this.settingsWidget);
        this.addWidget(this.resetButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getElementsStartPosition();
        int y = this.getY() + 1;
        int elementWidth = this.getElementWidth();

        this.booleanButton.setPosition(x, y);
        this.hotkeyButton.setPosition(this.booleanButton.getRight() + 2, y);

        int w = elementWidth < 0 ? 120 : elementWidth - this.booleanButton.getWidth() - 20 - 4;
        this.hotkeyButton.setWidth(w);
        this.settingsWidget.setPosition(this.hotkeyButton.getRight() + 2, y);

        this.resetButton.setPosition(this.settingsWidget.getRight() + 4, y);
    }

    @Override
    public boolean wasModified()
    {
        return this.booleanConfig.getBooleanValue() != this.initialBooleanValue ||
               this.keyBind.matches(this.initialHotkeyValue) == false;
    }

    @Override
    public void updateWidgetState()
    {
        super.updateWidgetState();

        this.booleanButton.setEnabled(this.booleanConfig.isLocked() == false);
        this.booleanButton.updateButtonState();
        this.booleanButton.updateHoverStrings();

        this.hotkeyButton.updateButtonState();
        this.hotkeyButton.updateHoverStrings();
    }

    @Override
    protected boolean isResetEnabled()
    {
        return this.config.isModified() && this.booleanConfig.isLocked() == false;
    }

    @Override
    protected void onResetButtonClicked()
    {
        this.config.resetToDefault();
        this.ctx.getListWidget().refreshEntries();
    }

    protected void onKeybindModified()
    {
        this.ctx.getListWidget().refreshEntries();
    }
}
