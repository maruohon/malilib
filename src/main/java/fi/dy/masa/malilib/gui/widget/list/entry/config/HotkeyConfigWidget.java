package fi.dy.masa.malilib.gui.widget.list.entry.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.gui.widget.button.KeyBindConfigButton;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.KeybindSettingsWidget;

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

        this.keybindButton = new KeyBindConfigButton(x, y, 120, 20, this.config.getKeyBind(), ctx.getKeybindEditingScreen());
        this.keybindButton.setActionListener((btn, mbtn) -> this.resetButton.setEnabled(this.config.isModified()));
        this.keybindButton.setValueChangeListener(() -> this.resetButton.setEnabled(this.config.isModified()));

        this.settingsWidget = new KeybindSettingsWidget(x, y, 20, 20, config.getKeyBind(),
                                                        config.getDisplayName(), ctx.getDialogHandler());

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.keybindButton.updateDisplayString();
            this.resetButton.setEnabled(this.config.isModified());
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getElementsStartPosition();
        int y = this.getY() + 1;
        int elementWidth = this.getElementWidth();

        this.keybindButton.setPosition(x, y);
        this.keybindButton.setWidth(elementWidth - 22);
        this.keybindButton.setEnabled(this.config.isLocked() == false);
        this.keybindButton.setHoverInfoRequiresShift(this.config.isLocked() == false);

        if (this.config.isLocked())
        {
            this.keybindButton.setHoverStrings(this.config.getLockAndOverrideMessages());
        }

        x += this.keybindButton.getWidth() + 2;
        this.settingsWidget.setPosition(x, y);

        x += this.settingsWidget.getWidth() + 4;
        this.updateResetButton(x, y);

        this.addWidget(this.keybindButton);
        this.addWidget(this.settingsWidget);
        this.addWidget(this.resetButton);
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getKeyBind().getKeys().equals(this.initialValue) == false;
    }
}
