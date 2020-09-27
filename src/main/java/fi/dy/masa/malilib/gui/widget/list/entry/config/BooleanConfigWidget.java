package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.gui.widget.button.BooleanConfigButton;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;

public class BooleanConfigWidget extends BaseConfigOptionWidget<BooleanConfig>
{
    protected final BooleanConfig config;
    protected final BooleanConfigButton booleanButton;
    protected final boolean initialValue;

    public BooleanConfigWidget(int x, int y, int width, int height, int listIndex,
                               int originalListIndex, BooleanConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.config = config;
        this.initialValue = this.config.getBooleanValue();

        this.booleanButton = new BooleanConfigButton(x, y, -1, 20, this.config);
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

        int x = this.getElementsStartPosition();
        int y = this.getY() + 1;
        int elementWidth = this.getElementWidth();

        if (elementWidth < 0)
        {
            elementWidth = this.booleanButton.getWidth();
        }

        this.booleanButton.setPosition(x, y);
        //this.booleanButton.setWidth(elementWidth);

        this.updateResetButton(x + elementWidth + 4, y, this.config);

        this.addWidget(this.booleanButton);
        this.addWidget(this.resetButton);
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getBooleanValue() != this.initialValue;
    }
}
