package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.BooleanConfigButton;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class BooleanConfigWidget extends BaseConfigOptionWidget<Boolean, BooleanConfig>
{
    protected final BooleanConfigButton booleanButton;

    public BooleanConfigWidget(BooleanConfig config,
                               DataListEntryWidgetData constructData,
                               ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.booleanButton = new BooleanConfigButton(-1, 20, this.config);
        this.booleanButton.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);

        this.booleanButton.setActionListener(() -> {
            this.config.toggleBooleanValue();
            this.updateWidgetDisplayValues();
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.booleanButton);
        this.addWidget(this.resetButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getElementsStartPosition();
        int y = this.getY() + 1;
        int elementWidth = this.getElementWidth();

        if (elementWidth < 0)
        {
            elementWidth = this.booleanButton.getWidth();
        }

        this.booleanButton.setPosition(x, y);
        this.resetButton.setPosition(x + elementWidth + 4, y);
    }

    @Override
    public void updateWidgetDisplayValues()
    {
        super.updateWidgetDisplayValues();

        this.booleanButton.setEnabled(this.config.isLocked() == false);
        this.booleanButton.updateButtonState();
    }
}
