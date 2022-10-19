package malilib.gui.widget.list.entry.config;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import malilib.config.option.ConfigInfo;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.KeybindSettingsWidget;
import malilib.gui.widget.button.KeyBindConfigButton;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.input.KeyBind;

public abstract class BaseKeyBindConfigWidget extends BaseConfigWidget<ConfigInfo>
{
    protected final KeyBind keyBind;
    protected final IntArrayList initialValue = new IntArrayList();
    protected final KeyBindConfigButton keybindButton;
    protected final KeybindSettingsWidget settingsWidget;

    public BaseKeyBindConfigWidget(ConfigInfo config,
                                   DataListEntryWidgetData constructData,
                                   ConfigWidgetContext ctx,
                                   KeyBind keyBind)
    {
        super(config, constructData, ctx);

        this.keyBind = keyBind;
        keyBind.getKeysToList(this.initialValue);

        this.keybindButton = new KeyBindConfigButton(120, 20, keyBind, ctx.getKeybindEditingScreen());
        this.keybindButton.setValueChangeListener(this::updateWidgetState);
        this.keybindButton.setHoverInfoRequiresShift(true);

        this.settingsWidget = new KeybindSettingsWidget(keyBind, config.getDisplayName());
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.keybindButton);
        this.addWidget(this.settingsWidget);
        this.addWidget(this.resetButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int y = this.getY() + 1;

        this.keybindButton.setPosition(this.getElementsStartPosition(), y);
        this.keybindButton.setWidth(this.getElementWidth() - 22);

        this.settingsWidget.setPosition(this.keybindButton.getRight() + 2, y);
        this.resetButton.setPosition(this.settingsWidget.getRight() + 4, y);
    }

    @Override
    public void updateWidgetState()
    {
        super.updateWidgetState();
        this.keybindButton.updateButtonState();
    }

    @Override
    public boolean wasModified()
    {
        return this.keyBind.matches(this.initialValue) == false;
    }
}
