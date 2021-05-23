package fi.dy.masa.malilib.gui.widget.list.entry.config.list;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import fi.dy.masa.malilib.config.option.list.ItemListConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.ItemStackWidget;
import fi.dy.masa.malilib.gui.widget.button.BaseValueListEditButton;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.util.ItemUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class ItemListConfigWidget extends BaseValueListConfigWidget<Item, ItemListConfig>
{
    public ItemListConfigWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, ItemListConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);
    }

    @Override
    protected GenericButton createButton(int width, int height, ItemListConfig config, ConfigWidgetContext ctx)
    {
        String title = StringUtils.translate("malilib.gui.title.item_list_edit", this.config.getDisplayName());

        return new BaseValueListEditButton<>(width, height, config, this::updateButtonStates, ctx.getDialogHandler(),
                                             title, () -> Items.STICK,
                                             ItemUtils::getSortedItemList,
                                             ItemStackWidget::getItemDisplayName,
                                             ItemStackWidget::createItemWidget);
    }
}
