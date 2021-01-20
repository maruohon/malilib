package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.gui.widget.button.BooleanConfigButton;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;

public class BooleanConfigWidget extends BaseConfigOptionWidget<Boolean, BooleanConfig>
{
    protected final BooleanConfigButton booleanButton;

    public BooleanConfigWidget(int x, int y, int width, int height, int listIndex,
                               int originalListIndex, BooleanConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.booleanButton = new BooleanConfigButton(x, y, -1, 20, this.config);
        this.booleanButton.setActionListener((btn, mbtn) -> {
            this.config.toggleBooleanValue();
            this.updateButtonStates();
        });

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.updateButtonStates();
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

        this.resetButton.setPosition(x + elementWidth + 4, y);
        this.updateButtonStates();

        this.addWidget(this.booleanButton);
        this.addWidget(this.resetButton);
    }

    protected void updateButtonStates()
    {
        this.booleanButton.setEnabled(this.config.isLocked() == false);
        this.booleanButton.setHoverStrings(this.config.getLockAndOverrideMessages());
        this.booleanButton.updateDisplayString();
        this.updateResetButtonState();
    }
}
