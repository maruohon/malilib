package malilib.gui.widget.list.entry.config.list;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import malilib.config.option.list.BlockListConfig;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.BlockModelWidget;
import malilib.gui.widget.button.BaseValueListEditButton;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.util.StringUtils;
import malilib.util.game.wrap.RegistryUtils;

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
                                             RegistryUtils::getSortedBlockList,
                                             RegistryUtils::getBlockIdStr,
                                             BlockModelWidget::new,
                                             title);
    }
}
