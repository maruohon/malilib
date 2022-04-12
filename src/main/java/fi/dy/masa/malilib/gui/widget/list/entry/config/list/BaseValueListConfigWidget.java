package fi.dy.masa.malilib.gui.widget.list.entry.config.list;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseGenericConfigWidget;

public abstract class BaseValueListConfigWidget<TYPE, CFG extends ValueListConfig<TYPE>> extends BaseGenericConfigWidget<ImmutableList<TYPE>, CFG>
{
    protected final CFG config;
    protected final GenericButton button;

    public BaseValueListConfigWidget(CFG config,
                                     DataListEntryWidgetData constructData,
                                     ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.config = config;

        this.button = this.createButton(this.getElementWidth(), 20, config, ctx);
        this.button.setHoverStringProvider("locked", config::getLockAndOverrideMessages);
    }

    protected abstract GenericButton createButton(int width, int height, CFG config, ConfigWidgetContext ctx);

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
        this.button.setEnabled(this.config.isLocked() == false);

        this.resetButton.setPosition(this.button.getRight() + 4, y);
    }

    @Override
    public void updateWidgetState()
    {
        super.updateWidgetState();
        this.button.updateWidgetState();
    }
}
