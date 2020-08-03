package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.ConfigOption;

public class ConfigOptionWidget extends BaseDataListEntryWidget<ConfigOption<?>>
{
    public ConfigOptionWidget(int x, int y, int width, int height, int listIndex, @Nullable ConfigOption<?> config)
    {
        super(x, y, width, height, listIndex, config);
    }
}
