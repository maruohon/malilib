package fi.dy.masa.malilib.gui.widget.list.entry.config.list;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import fi.dy.masa.malilib.config.option.list.ItemListConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.ItemStackWidget;
import fi.dy.masa.malilib.gui.widget.button.BaseValueListEditButton;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.game.ItemUtils;

public class ItemListConfigWidget extends BaseValueListConfigWidget<Item, ItemListConfig>
{
    public ItemListConfigWidget(ItemListConfig config,
                                DataListEntryWidgetData constructData,
                                ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);
    }

    @Override
    protected GenericButton createButton(int width, int height, ItemListConfig config, ConfigWidgetContext ctx)
    {
        String title = StringUtils.translate("malilib.title.screen.item_list_edit", this.config.getDisplayName());

        return new BaseValueListEditButton<>(width, height,
                                             config,
                                             this::updateWidgetState,
                                             () -> Items.STICK,
                                             ItemUtils::getSortedItemList,
                                             ItemStackWidget::getItemDisplayName,
                                             ItemStackWidget::createItemWidget,
                                             title);
    }
}
