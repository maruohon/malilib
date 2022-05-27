package fi.dy.masa.malilib.gui.widget.list.entry.config.list;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import fi.dy.masa.malilib.config.option.list.BlockListConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.BlockModelWidget;
import fi.dy.masa.malilib.gui.widget.button.BaseValueListEditButton;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.game.BlockUtils;

public class BlockListConfigWidget extends BaseValueListConfigWidget<Block, BlockListConfig>
{
    public BlockListConfigWidget(BlockListConfig config,
                                 DataListEntryWidgetData constructData,
                                 ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);
    }

    @Override
    protected GenericButton createButton(int width, int height, BlockListConfig config, ConfigWidgetContext ctx)
    {
        String title = StringUtils.translate("malilib.title.screen.block_list_edit", this.config.getDisplayName());

        return new BaseValueListEditButton<>(width, height,
                                             config,
                                             this::updateWidgetState,
                                             () -> Blocks.STONE,
                                             BlockUtils::getSortedBlockList,
                                             BlockUtils::getBlockRegistryName,
                                             BlockModelWidget::new,
                                             title);
    }
}
