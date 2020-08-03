package fi.dy.masa.malilib.gui.config.elementplacer;

import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseDataListEntryWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class MissingElementPlacer implements ConfigElementPlacer<ConfigInfo>
{
    @Override
    public void addScreenElements(ConfigInfo config, BaseDataListEntryWidget<ConfigInfo> containerWidget, BaseConfigScreen gui)
    {
        int x = containerWidget.getX();
        int y = containerWidget.getY();
        containerWidget.addLabel(x + 10, y + 4, 0xFFFFFFFF, StringUtils.translate(
                "malilib.gui.label_error.no_element_placer_for_config_type", config.getType().getName()));
    }
}
