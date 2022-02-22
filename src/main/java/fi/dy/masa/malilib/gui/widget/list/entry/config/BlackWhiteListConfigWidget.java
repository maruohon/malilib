package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.list.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.BlackWhiteListEditButton;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class BlackWhiteListConfigWidget extends BaseConfigWidget<BlackWhiteListConfig<?>>
{
    protected final BlackWhiteListConfig<?> config;
    protected final BlackWhiteListEditButton button;
    protected final BlackWhiteList<?> initialValue;

    public BlackWhiteListConfigWidget(BlackWhiteListConfig<?> config,
                                      DataListEntryWidgetData constructData,
                                      ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.config = config;
        this.initialValue = this.config.getValue();

        this.button = new BlackWhiteListEditButton(this.getElementWidth(), 20, config, this::updateWidgetDisplayValues);
        this.button.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.button);
        this.addWidget(this.resetButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getElementsStartPosition();
        int y = this.getY() + 1;
        int elementWidth = this.getElementWidth();

        this.button.setPosition(x, y);
        this.button.setWidth(elementWidth);
        this.button.updateHoverStrings();
        this.button.setEnabled(this.config.isLocked() == false);

        this.resetButton.setPosition(this.button.getRight() + 4, y);
    }

    @Override
    public void updateWidgetDisplayValues()
    {
        super.updateWidgetDisplayValues();
        this.button.updateButtonState();
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getValue().equals(this.initialValue) == false;
    }
}
