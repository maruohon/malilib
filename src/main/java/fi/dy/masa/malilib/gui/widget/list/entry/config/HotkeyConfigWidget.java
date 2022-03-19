package fi.dy.masa.malilib.gui.widget.list.entry.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.KeybindSettingsWidget;
import fi.dy.masa.malilib.gui.widget.button.KeyBindConfigButton;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class HotkeyConfigWidget extends BaseConfigWidget<HotkeyConfig>
{
    protected final HotkeyConfig config;
    protected final ImmutableList<Integer> initialValue;
    protected final KeyBindConfigButton keybindButton;
    protected final KeybindSettingsWidget settingsWidget;

    public HotkeyConfigWidget(HotkeyConfig config,
                              DataListEntryWidgetData constructData,
                              ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.config = config;
        this.initialValue = this.config.getKeyBind().getKeys();

        this.keybindButton = new KeyBindConfigButton(120, 20, this.config.getKeyBind(), ctx.getKeybindEditingScreen());
        this.keybindButton.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);
        this.keybindButton.setValueChangeListener(this::updateWidgetState);
        this.keybindButton.setHoverInfoRequiresShift(true);

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
        return this.config.getKeyBind().getKeys().equals(this.initialValue) == false;
    }
}
