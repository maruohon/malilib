package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;

public class BooleanConfigWidget extends BaseConfigOptionWidget
{
    protected final BooleanConfig config;
    protected final boolean initialValue;

    public BooleanConfigWidget(int x, int y, int width, int height, int listIndex, ConfigInfo config, BaseConfigScreen gui)
    {
        super(x, y, width, height, listIndex, config, gui);

        this.config = ((BooleanConfig) config);
        this.initialValue = this.config.getBooleanValue();
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getBooleanValue() != this.initialValue;
    }
}
