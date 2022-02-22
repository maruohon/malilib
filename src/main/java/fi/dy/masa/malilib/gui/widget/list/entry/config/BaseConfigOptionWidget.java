package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class BaseConfigOptionWidget<TYPE, CFG extends ConfigOption<TYPE>> extends BaseConfigWidget<CFG>
{
    protected final TYPE initialValue;

    public BaseConfigOptionWidget(CFG config,
                                  DataListEntryWidgetData constructData,
                                  ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.initialValue = config.getValue();
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getValue().equals(this.initialValue) == false;
    }

    @Override
    protected boolean isResetEnabled()
    {
        return this.config.isModified() && this.config.isLocked() == false;
    }
}
