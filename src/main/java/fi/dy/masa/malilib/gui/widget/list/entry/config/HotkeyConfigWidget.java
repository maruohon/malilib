package fi.dy.masa.malilib.gui.widget.list.entry.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeyBind;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;

public class HotkeyConfigWidget extends BaseConfigOptionWidget<HotkeyConfig>
{
    protected final HotkeyConfig config;
    protected final ImmutableList<Integer> initialValue;
    protected final ConfigButtonKeyBind keybindButton;

    public HotkeyConfigWidget(int x, int y, int width, int height, int listIndex,
                              int originalListIndex, HotkeyConfig config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, originalListIndex, config, gui);

        this.config = config;
        this.initialValue = this.config.getKeyBind().getKeys();

        this.keybindButton = new ConfigButtonKeyBind(x, y, 120, 20, this.config.getKeyBind(), this.gui);
        this.keybindButton.setActionListener((btn, mbtn) -> this.resetButton.setEnabled(this.config.isModified()));
        this.keybindButton.setValueChangeListener(() -> this.resetButton.setEnabled(this.config.isModified()));

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
        this.keybindButton.setWidth(elementWidth);

        this.updateResetButton(x + elementWidth + 4, y, this.config);

        this.addWidget(this.keybindButton);
        this.addWidget(this.resetButton);
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getKeyBind().getKeys().equals(this.initialValue) == false;
    }
}
