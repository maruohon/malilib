package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.ConfigOption;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;

public class BaseConfigOptionWidget<TYPE, CFG extends ConfigOption<TYPE>> extends BaseConfigWidget<CFG>
{
    protected final TYPE initialValue;

    public BaseConfigOptionWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, CFG config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.initialValue = config.getValue();
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getValue().equals(this.initialValue) == false;
    }
}
