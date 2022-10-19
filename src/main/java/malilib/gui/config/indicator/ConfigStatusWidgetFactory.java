package malilib.gui.config.indicator;

import malilib.config.option.ConfigInfo;
import malilib.overlay.widget.sub.BaseConfigStatusIndicatorWidget;
import malilib.util.data.ConfigOnTab;

public interface ConfigStatusWidgetFactory<C extends ConfigInfo>
{
    BaseConfigStatusIndicatorWidget<? extends ConfigInfo> create(C config, ConfigOnTab configOnTab);
}
