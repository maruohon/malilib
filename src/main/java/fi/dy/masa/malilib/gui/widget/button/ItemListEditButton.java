package fi.dy.masa.malilib.gui.widget.button;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import fi.dy.masa.malilib.config.option.ValueListConfig;
import fi.dy.masa.malilib.gui.config.BaseValueListEditScreen;
import fi.dy.masa.malilib.gui.util.DialogHandler;
import fi.dy.masa.malilib.gui.widget.ItemStackWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseValueListEditEntryWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.ItemUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class ItemListEditButton extends BaseValueListEditButton<Item>
{
    public ItemListEditButton(int x, int y, int width, int height, ValueListConfig<Item> config,
                                @Nullable EventListener saveListener, @Nullable DialogHandler dialogHandler)
    {
        super(x, y, width, height, config, saveListener, dialogHandler);
    }

    @Override
    protected BaseValueListEditScreen<Item> createScreen(@Nullable DialogHandler dialogHandler, @Nullable GuiScreen currentScreen)
    {
        String title = StringUtils.translate("malilib.gui.title.item_list_edit", this.config.getDisplayName());

        return new BaseValueListEditScreen<>(this.config, this.saveListener, dialogHandler, currentScreen,
                                             title, () -> Items.STICK, (wx, wy, ww, wh, li, oi, iv, dv, lw) ->
                                             new BaseValueListEditEntryWidget<>(wx, wy, ww, wh, li, oi, iv, dv,
                                                                                ItemUtils.getSortedItemList(),
                                                                                ItemStackWidget::getItemDisplayName,
                                                                                ItemStackWidget::createItemWidget,
                                                                                lw));
    }
}
