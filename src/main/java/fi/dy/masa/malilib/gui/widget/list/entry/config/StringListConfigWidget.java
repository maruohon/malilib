package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.StringListConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.StringListEditButton;

public class StringListConfigWidget extends BaseValueListConfigWidget<String, StringListConfig>
{
    public StringListConfigWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, StringListConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, 22, listIndex, originalListIndex, config, ctx);
    }

    @Override
    protected GenericButton createButton(int width, int height, StringListConfig config, ConfigWidgetContext ctx)
    {
        return new StringListEditButton(0, 0, width, height, config, this::onReset, ctx.getDialogHandler());
    }
}
