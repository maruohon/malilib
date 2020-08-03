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

        // TODO config refactor
        this.addWidget(new ConfigButtonBoolean(x + 120, y + 1, 80, 20, config));
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getBooleanValue() != this.initialValue;
    }
}
