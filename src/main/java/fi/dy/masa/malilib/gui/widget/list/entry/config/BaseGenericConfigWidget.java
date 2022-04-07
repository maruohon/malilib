package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.BaseGenericConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class BaseGenericConfigWidget<TYPE, CFG extends BaseGenericConfig<TYPE>> extends BaseConfigWidget<CFG>
{
    protected final TYPE initialValue;

    public BaseGenericConfigWidget(CFG config,
                                   DataListEntryWidgetData constructData,
                                   ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.initialValue = config.getValueForSerialization();
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getValueForSerialization().equals(this.initialValue) == false;
    }

    @Override
    protected boolean isResetEnabled()
    {
        return this.config.isModified() && this.config.isLocked() == false;
    }
}
