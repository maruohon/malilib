package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.widget.WidgetLabel;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseDataListEntryWidget;

public class BaseConfigOptionWidget<C extends ConfigInfo> extends BaseDataListEntryWidget<C>
{
    protected final BaseConfigScreen gui;

    public BaseConfigOptionWidget(int x, int y, int width, int height, int listIndex, C config, BaseConfigScreen gui)
    {
        super(x, y, width, height, listIndex, config);

        this.gui = gui;

        WidgetLabel label = new WidgetLabel(x + 2, y + 6, 0xFFFFFFFF, config.getDisplayName());
        label.addHoverStrings(config.getComment());
        this.addWidget(label);
    }

    public boolean wasModified()
    {
        return false;
    }
}
