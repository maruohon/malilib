package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.value.IConfigOptionListEntry;
import fi.dy.masa.malilib.gui.button.ConfigButtonOptionList;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;

public class OptionListConfigWidget extends BaseConfigOptionWidget<OptionListConfig>
{
    protected final OptionListConfig<?> config;
    protected final IConfigOptionListEntry<?> initialValue;
    protected final ConfigButtonOptionList optionListButton;

    public OptionListConfigWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, OptionListConfig<?> config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, originalListIndex, config, gui);

        this.config = config;
        this.initialValue = this.config.getOptionListValue();

        this.optionListButton = new ConfigButtonOptionList(x, y, 80, 20, this.config);
        this.optionListButton.setActionListener((btn, mbtn) -> this.resetButton.setEnabled(this.config.isModified()));

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.optionListButton.updateDisplayString();
            this.resetButton.setEnabled(this.config.isModified());
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getX() + this.getMaxLabelWidth() + 10;
        int y = this.getY() + 1;
        int elementWidth = this.gui.getConfigElementsWidth();

        this.optionListButton.setPosition(x, y);
        this.optionListButton.setWidth(elementWidth);

        this.updateResetButton(x + elementWidth + 4, y, this.config);

        this.addWidget(this.optionListButton);
        this.addWidget(this.resetButton);
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getOptionListValue().equals(this.initialValue) == false;
    }
}
