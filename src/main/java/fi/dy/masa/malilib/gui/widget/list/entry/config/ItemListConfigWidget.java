package fi.dy.masa.malilib.gui.widget.list.entry.config;

import net.minecraft.item.Item;
import fi.dy.masa.malilib.config.option.ItemListConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.ItemListEditButton;

public class ItemListConfigWidget extends BaseValueListConfigWidget<Item, ItemListConfig>
{
    public ItemListConfigWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, ItemListConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, 22, listIndex, originalListIndex, config, ctx);
    }

    @Override
    protected GenericButton createButton(int width, int height, ItemListConfig config, ConfigWidgetContext ctx)
    {
        return new ItemListEditButton(0, 0, width, height, config, this::onReset, ctx.getDialogHandler());
    }
}
