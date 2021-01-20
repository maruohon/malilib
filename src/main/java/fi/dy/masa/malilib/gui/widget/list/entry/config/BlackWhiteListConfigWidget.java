package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.list.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.BlackWhiteListEditButton;

public class BlackWhiteListConfigWidget extends BaseConfigWidget<BlackWhiteListConfig<?>>
{
    protected final BlackWhiteListConfig<?> config;
    protected final BlackWhiteListEditButton button;
    protected final BlackWhiteList<?> initialValue;

    public BlackWhiteListConfigWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                      BlackWhiteListConfig<?> config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.config = config;
        this.initialValue = this.config.getValue();

        this.button = new BlackWhiteListEditButton(x, y, this.getElementWidth(), 20, config, this::updateButtons, ctx.getDialogHandler());

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.updateButtons();
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getElementsStartPosition();
        int y = this.getY() + 1;
        int elementWidth = this.getElementWidth();

        this.button.setPosition(x, y);
        this.button.setWidth(elementWidth);
        this.button.setEnabled(this.config.isLocked() == false);
        this.button.setHoverStrings(this.config.getLockAndOverrideMessages());
        this.button.updateDisplayString();

        this.updateResetButton(x + elementWidth + 4, y);

        this.addWidget(this.button);
        this.addWidget(this.resetButton);
    }

    public void updateButtons()
    {
        this.button.updateDisplayString();
        this.updateResetButtonState();
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getValue().equals(this.initialValue) == false;
    }
}
