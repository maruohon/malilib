package malilib.gui.widget.list.entry.config.list;

import net.minecraft.item.Item;

import malilib.config.option.list.ItemListConfig;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.ItemStackWidget;
import malilib.gui.widget.button.BaseValueListEditButton;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.util.StringUtils;
import malilib.util.game.wrap.RegistryUtils;

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
                                             () -> Item.STICK,
                                             RegistryUtils::getSortedItemList,
                                             RegistryUtils::getItemIdStr,
                                             ItemStackWidget::createItemWidget,
                                             title);
    }
}
