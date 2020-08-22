package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.BlackWhiteListEditButton;
import fi.dy.masa.malilib.listener.EventListener;

public class BlackWhiteListConfigWidget extends BaseConfigOptionWidget<BlackWhiteListConfig> implements EventListener
{
    protected final BlackWhiteListConfig config;
    protected final BlackWhiteListEditButton button;
    protected final BlackWhiteList initialValue;

    public BlackWhiteListConfigWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                      BlackWhiteListConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, 22, listIndex, originalListIndex, config, ctx);

        this.config = config;
        this.initialValue = this.config.getValue();

        this.button = new BlackWhiteListEditButton(x, y, this.getElementWidth(), 20, config, this, ctx.getDialogHandler());

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.button.updateDisplayString();
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
        int elementWidth = this.getElementWidth();

        this.button.setPosition(x + xOff, y);
        this.button.setWidth(elementWidth);
        this.button.updateDisplayString();

        this.updateResetButton(x + xOff + elementWidth + 4, y, this.config);

        this.addWidget(this.button);
        this.addWidget(this.resetButton);
    }

    @Override
    public void onEvent()
    {
        this.button.updateDisplayString();
        this.resetButton.setEnabled(this.config.isModified());
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getValue().equals(this.initialValue) == false;
    }
}
