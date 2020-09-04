package fi.dy.masa.malilib.gui.widget.button;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import fi.dy.masa.malilib.config.option.BlockListConfig;
import fi.dy.masa.malilib.config.option.ValueListConfig;
import fi.dy.masa.malilib.gui.config.BaseValueListEditScreen;
import fi.dy.masa.malilib.gui.util.DialogHandler;
import fi.dy.masa.malilib.gui.widget.BlockModelWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseValueListEditEntryWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;

public class BlockListEditButton extends BaseValueListEditButton<Block>
{
    public BlockListEditButton(int x, int y, int width, int height, ValueListConfig<Block> config,
                              @Nullable EventListener saveListener, @Nullable DialogHandler dialogHandler)
    {
        super(x, y, width, height, config, saveListener, dialogHandler);
    }

    @Override
    protected BaseValueListEditScreen<Block> createScreen(@Nullable DialogHandler dialogHandler, @Nullable GuiScreen currentScreen)
    {
        String title = StringUtils.translate("malilib.gui.title.block_list_edit", this.config.getDisplayName());

        return new BaseValueListEditScreen<>(this.config, this.saveListener, dialogHandler, currentScreen,
                                             title, () -> Blocks.STONE, (wx, wy, ww, wh, li, oi, iv, dv, lw) ->
                                             new BaseValueListEditEntryWidget<>(wx, wy, ww, wh, li, oi, iv, dv,
                                                                                getSortedBlockList(),
                                                                                BlockListEditButton::getBlockDisplayName,
                                                                                BlockModelWidget::new,
                                                                                lw));
    }

    public static String getBlockDisplayName(Block block)
    {
        //ItemStack stack = new ItemStack(block);
        //return stack.getDisplayName();
        return BlockListConfig.blockToName(block);
    }

    public static List<Block> getSortedBlockList()
    {
        List<Block> blocks = new ArrayList<>();

        for (Block block : Block.REGISTRY)
        {
            blocks.add(block);
        }

        blocks.sort(Comparator.comparing(BlockListEditButton::getBlockDisplayName));

        return blocks;
    }
}
