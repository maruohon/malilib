package fi.dy.masa.malilib.gui.widget.list.entry.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeyBind;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;

public class HotkeyConfigWidget extends BaseConfigOptionWidget<HotkeyConfig>
{
    protected final HotkeyConfig config;
    protected final ImmutableList<Integer> initialValue;

    public HotkeyConfigWidget(int x, int y, int width, int height, int listIndex, HotkeyConfig config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, config, gui);

        this.config = config;
        this.initialValue = this.config.getKeyBind().getKeys();

        this.reCreateWidgets(x, y);
    }

    @Override
    protected void reCreateWidgets(int x, int y)
    {
        super.reCreateWidgets(x, y);

        int xOff = this.getMaxLabelWidth() + 10;
        int elementWidth = this.gui.getConfigElementsWidth();
        final ConfigButtonKeyBind configButton = new ConfigButtonKeyBind(x + xOff, y + 1, elementWidth, 20, this.config.getKeyBind(), this.gui);

        this.addButtonsForButtonBasedConfigs(x + xOff + elementWidth + 4, y + 1, this.config, configButton);

        configButton.setValueChangeListener(() -> this.resetButton.setEnabled(this.config.isModified()));
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getKeyBind().getKeys().equals(this.initialValue) == false;
    }
}
