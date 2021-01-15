package fi.dy.masa.malilib.gui.widget.list.entry.config.list;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigOptionWidget;

public abstract class BaseValueListConfigWidget<TYPE, CFG extends ValueListConfig<TYPE>> extends BaseConfigOptionWidget<ImmutableList<TYPE>, CFG>
{
    protected final CFG config;
    protected final GenericButton button;

    public BaseValueListConfigWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, CFG config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.config = config;

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

        int x = this.getElementsStartPosition();
        int y = this.getY() + 1;
        int elementWidth = this.getElementWidth();

        this.button.setPosition(x, y);
        this.button.setWidth(elementWidth);
        this.button.updateDisplayString();

        this.updateResetButton(x + elementWidth + 4, y);

        this.addWidget(this.button);
        this.addWidget(this.resetButton);
    }

    public void onReset()
    {
        this.button.updateDisplayString();
        this.resetButton.setEnabled(this.config.isModified());
    }
}
