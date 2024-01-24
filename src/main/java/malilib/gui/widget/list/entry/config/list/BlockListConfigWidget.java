package malilib.gui.widget.list.entry.config.list;

import net.minecraft.block.Block;

import malilib.config.option.list.BlockListConfig;
import malilib.gui.config.ConfigWidgetContext;
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
                                             () -> Block.STONE,
                                             RegistryUtils::getSortedBlockList,
                                             RegistryUtils::getBlockIdStr,
                                             (p) -> null, //BlockModelWidget::new, // TODO b1.7.3
                                             title);
    }
}
