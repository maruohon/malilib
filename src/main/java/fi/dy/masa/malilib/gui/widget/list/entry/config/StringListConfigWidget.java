package fi.dy.masa.malilib.gui.widget.list.entry.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.StringListConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.StringListEditButton;
import fi.dy.masa.malilib.listener.EventListener;

public class StringListConfigWidget extends BaseConfigOptionWidget<StringListConfig> implements EventListener
{
    protected final StringListConfig config;
    protected final StringListEditButton button;
    protected final ImmutableList<String> initialValue;

    public StringListConfigWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, StringListConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, 22, listIndex, originalListIndex, config, ctx);

        this.config = config;
        this.initialValue = this.config.getValues();

        this.button = new StringListEditButton(x, y, this.getElementWidth(), 20, config, this, ctx.getDialogHandler());

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
        return this.config.getValues().equals(this.initialValue) == false;
    }
}
