package fi.dy.masa.malilib.gui.widget.list.entry.config;

import net.minecraft.block.Block;
import fi.dy.masa.malilib.config.option.BlockListConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.BlockListEditButton;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;

public class BlockListConfigWidget extends BaseValueListConfigWidget<Block, BlockListConfig>
{
    public BlockListConfigWidget(int x, int y, int width, int height, int listIndex,
                                int originalListIndex, BlockListConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, 22, listIndex, originalListIndex, config, ctx);
    }

    @Override
    protected GenericButton createButton(int width, int height, BlockListConfig config, ConfigWidgetContext ctx)
    {
        return new BlockListEditButton(0, 0, width, height, config, this::onReset, ctx.getDialogHandler());
    }
}
