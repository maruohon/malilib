package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.gui.widget.button.OptionListConfigButton;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;

public class OptionListConfigWidget extends BaseConfigWidget<OptionListConfig<?>>
{
    protected final OptionListConfig<?> config;
    protected final OptionListConfigValue initialValue;
    protected final OptionListConfigButton optionListButton;

    public OptionListConfigWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, OptionListConfig<?> config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.config = config;
        this.initialValue = this.config.getValue();

        this.optionListButton = new OptionListConfigButton(x, y, 80, 20, this.config);
        this.optionListButton.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);
        this.optionListButton.setChangeListener(this::updateResetButtonState);

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.optionListButton.updateDisplayString();
            this.updateResetButtonState();
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getElementsStartPosition();
        int y = this.getY() + 1;
        int elementWidth = this.getElementWidth();

        this.optionListButton.setPosition(x, y);
        this.optionListButton.setWidth(elementWidth);
        this.optionListButton.setEnabled(this.config.isLocked() == false);
        this.optionListButton.updateHoverStrings();

        this.updateResetButton(x + elementWidth + 4, y);

        this.addWidget(this.optionListButton);
        this.addWidget(this.resetButton);
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getValue().equals(this.initialValue) == false;
    }
}
