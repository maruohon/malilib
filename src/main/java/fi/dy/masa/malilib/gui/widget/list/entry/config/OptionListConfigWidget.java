package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.value.IConfigOptionListEntry;
import fi.dy.masa.malilib.gui.button.ConfigButtonOptionList;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;

public class OptionListConfigWidget extends BaseConfigOptionWidget<OptionListConfig>
{
    protected final OptionListConfig<?> config;
    protected final IConfigOptionListEntry<?> initialValue;

    public OptionListConfigWidget(int x, int y, int width, int height, int listIndex, OptionListConfig<?> config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, config, gui);

        this.config = config;
        this.initialValue = this.config.getOptionListValue();

        // TODO config refactor
        this.addWidget(new ConfigButtonOptionList(x + 120, y + 1, 80, 20, config));
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getOptionListValue().equals(this.initialValue) == false;
    }
}
