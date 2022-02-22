package fi.dy.masa.malilib.gui.config;

import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigWidget;

public interface ConfigOptionWidgetFactory<CFG extends ConfigInfo>
{
    BaseConfigWidget<? extends ConfigInfo> create(CFG config,
                                                  DataListEntryWidgetData constructData,
                                                  ConfigWidgetContext ctx);
}
