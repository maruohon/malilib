package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseDataListEntryWidget;

public class BaseConfigOptionWidget extends BaseDataListEntryWidget<ConfigInfo>
{
    protected final BaseConfigScreen gui;

    public BaseConfigOptionWidget(int x, int y, int width, int height, int listIndex, ConfigInfo config, BaseConfigScreen gui)
    {
        super(x, y, width, height, listIndex, config);

        this.gui = gui;
    }

    public boolean wasModified()
    {
        return false;
    }
}
