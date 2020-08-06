package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;

public class BooleanConfigWidget extends BaseConfigOptionWidget<BooleanConfig>
{
    protected final BooleanConfig config;
    protected final ConfigButtonBoolean booleanButton;
    protected final boolean initialValue;

    public BooleanConfigWidget(int x, int y, int width, int height, int listIndex, BooleanConfig config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, config, gui);

        this.config = config;
        this.initialValue = this.config.getBooleanValue();

        this.booleanButton = new ConfigButtonBoolean(x, y, 60, 20, this.config);
        this.booleanButton.setActionListener((btn, mbtn) -> this.resetButton.setEnabled(this.config.isModified()));

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.booleanButton.updateDisplayString();
            this.resetButton.setEnabled(this.config.isModified());
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getX();
        int y = this.getY() + 1;
        int xOff = this.getMaxLabelWidth() + 10;
        int elementWidth = this.gui.getConfigElementsWidth();

        this.booleanButton.setPosition(x + xOff, y);
        this.booleanButton.setWidth(elementWidth);

        this.updateResetButton(x + xOff + elementWidth + 4, y, this.config);

        this.addWidget(this.booleanButton);
        this.addWidget(this.resetButton);
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getBooleanValue() != this.initialValue;
    }
}
