package fi.dy.masa.malilib.gui.widget.list.entry.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.ValueListConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;

public abstract class BaseValueListConfigWidget<TYPE, CFG extends ValueListConfig<TYPE>> extends BaseConfigOptionWidget<CFG>
{
    protected final CFG config;
    protected final GenericButton button;
    protected final ImmutableList<TYPE> initialValue;

    public BaseValueListConfigWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, CFG config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.config = config;
        this.initialValue = this.config.getValues();

        this.button = this.createButton(this.getElementWidth(), 20, config, ctx);

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.button.updateDisplayString();
            this.resetButton.setEnabled(this.config.isModified());
        });
    }

    protected abstract GenericButton createButton(int width, int height, CFG config, ConfigWidgetContext ctx);

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

    public void onReset()
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
