package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;

public class BooleanConfigWidget extends BaseConfigOptionWidget<BooleanConfig>
{
    protected final BooleanConfig config;
    protected final boolean initialValue;

    public BooleanConfigWidget(int x, int y, int width, int height, int listIndex, BooleanConfig config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, config, gui);

        this.config = config;
        this.initialValue = this.config.getBooleanValue();

        this.reCreateWidgets(x, y);
    }

    @Override
    protected void reCreateWidgets(int x, int y)
    {
        super.reCreateWidgets(x, y);

        int xOff = this.getMaxLabelWidth() + 10;
        int elementWidth = this.gui.getConfigElementsWidth();
        final ConfigButtonBoolean configButton = new ConfigButtonBoolean(x + xOff, y + 1, elementWidth, 20, this.config);

        this.addButtonsForButtonBasedConfigs(x + xOff + elementWidth + 4, y + 1, this.config, configButton);
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getBooleanValue() != this.initialValue;
    }
}
