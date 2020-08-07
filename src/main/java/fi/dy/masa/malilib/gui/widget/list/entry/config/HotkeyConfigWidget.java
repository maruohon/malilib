package fi.dy.masa.malilib.gui.widget.list.entry.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.gui.button.KeyBindConfigButton;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.widget.KeybindSettingsWidget;

public class HotkeyConfigWidget extends BaseConfigOptionWidget<HotkeyConfig>
{
    protected final HotkeyConfig config;
    protected final ImmutableList<Integer> initialValue;
    protected final KeyBindConfigButton keybindButton;
    protected final KeybindSettingsWidget settingsWidget;

    public HotkeyConfigWidget(int x, int y, int width, int height, int listIndex,
                              int originalListIndex, HotkeyConfig config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, originalListIndex, config, gui);

        this.config = config;
        this.initialValue = this.config.getKeyBind().getKeys();

        this.keybindButton = new KeyBindConfigButton(x, y, 120, 20, this.config.getKeyBind(), this.gui);
        this.keybindButton.setActionListener((btn, mbtn) -> this.resetButton.setEnabled(this.config.isModified()));
        this.keybindButton.setValueChangeListener(() -> this.resetButton.setEnabled(this.config.isModified()));

        this.settingsWidget = new KeybindSettingsWidget(x, y, 20, 20, config.getKeyBind(),
                                                        config.getDisplayName(), gui.getDialogHandler());

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

        int x = this.getX() + this.getMaxLabelWidth() + 10;
        int y = this.getY() + 1;
        int elementWidth = this.gui.getConfigElementsWidth();

        this.keybindButton.setPosition(x, y);
        this.keybindButton.setWidth(elementWidth - 22);

        x += this.keybindButton.getWidth() + 2;
        this.settingsWidget.setPosition(x, y);

        x += this.settingsWidget.getWidth() + 4;
        this.updateResetButton(x, y, this.config);

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
