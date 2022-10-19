package malilib.gui.config;

import malilib.config.option.ConfigInfo;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.gui.widget.list.entry.config.BaseConfigWidget;

public interface ConfigOptionWidgetFactory<CFG extends ConfigInfo>
{
    BaseConfigWidget<? extends ConfigInfo> create(CFG config,
                                                  DataListEntryWidgetData constructData,
                                                  ConfigWidgetContext ctx);
}
