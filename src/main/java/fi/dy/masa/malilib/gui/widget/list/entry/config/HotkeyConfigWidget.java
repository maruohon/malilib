package fi.dy.masa.malilib.gui.widget.list.entry.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.KeybindSettingsWidget;
import fi.dy.masa.malilib.gui.widget.button.KeyBindConfigButton;

public class HotkeyConfigWidget extends BaseConfigWidget<HotkeyConfig>
{
    protected final HotkeyConfig config;
    protected final ImmutableList<Integer> initialValue;
    protected final KeyBindConfigButton keybindButton;
    protected final KeybindSettingsWidget settingsWidget;

    public HotkeyConfigWidget(int x, int y, int width, int height, int listIndex,
                              int originalListIndex, HotkeyConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.config = config;
        this.initialValue = this.config.getKeyBind().getKeys();

        this.keybindButton = new KeyBindConfigButton(120, 20, this.config.getKeyBind(), ctx.getKeybindEditingScreen());
        this.keybindButton.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);
        this.keybindButton.setActionListener(() -> this.resetButton.setEnabled(this.config.isModified()));
        this.keybindButton.setValueChangeListener(() -> this.resetButton.setEnabled(this.config.isModified()));

        this.settingsWidget = new KeybindSettingsWidget(config.getKeyBind(), config.getDisplayName());
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
        this.keybindButton.setEnabled(this.config.isLocked() == false);
        this.keybindButton.setHoverInfoRequiresShift(this.config.isLocked() == false);

        this.settingsWidget.setPosition(this.keybindButton.getRight() + 2, y);
        this.resetButton.setPosition(this.settingsWidget.getRight() + 4, y);
    }

    @Override
    public void updateWidgetDisplayValues()
    {
        super.updateWidgetDisplayValues();

        this.keybindButton.updateButtonState();
        this.keybindButton.updateHoverStrings();
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getKeyBind().getKeys().equals(this.initialValue) == false;
    }
}
