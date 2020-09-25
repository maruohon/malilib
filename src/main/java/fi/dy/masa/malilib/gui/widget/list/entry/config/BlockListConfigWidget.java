package fi.dy.masa.malilib.gui.widget.list.entry.config;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import fi.dy.masa.malilib.config.option.BlockListConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.BlockModelWidget;
import fi.dy.masa.malilib.gui.widget.button.BaseValueListEditButton;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.util.BlockUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class BlockListConfigWidget extends BaseValueListConfigWidget<Block, BlockListConfig>
{
    public BlockListConfigWidget(int x, int y, int width, int height, int listIndex,
                                int originalListIndex, BlockListConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);
    }

    @Override
    protected GenericButton createButton(int width, int height, BlockListConfig config, ConfigWidgetContext ctx)
    {
        String title = StringUtils.translate("malilib.gui.title.block_list_edit", this.config.getDisplayName());

        return new BaseValueListEditButton<>(0, 0, width, height, config, this::onReset, ctx.getDialogHandler(),
                                             title, () -> Blocks.STONE,
                                             BlockUtils::getSortedBlockList,
                                             BlockUtils::getBlockRegistryName,
                                             BlockModelWidget::new);
    }
}
