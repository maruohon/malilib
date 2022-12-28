package malilib.gui.widget;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import malilib.gui.util.ScreenContext;
import malilib.render.ItemRenderUtils;
import malilib.util.game.wrap.ItemWrap;

public class ItemStackWidget extends BaseModelWidget
{
    protected ItemStack stack;

    public ItemStackWidget(ItemStack stack)
    {
        this(16, stack);
    }

    public ItemStackWidget(int dimensions, ItemStack stack)
    {
        super(dimensions);

        this.setStack(stack);
    }

    public ItemStackWidget setStack(ItemStack stack)
    {
        this.stack = stack;

        this.updateSize();

        return this;
    }

    @Override
    protected void renderModel(int x, int y, float z, float scale, ScreenContext ctx)
    {
        if (ItemWrap.notEmpty(this.stack))
        {
            ItemRenderUtils.renderStackAt(this.stack, x, y, z, this.scale, this.mc);
        }
    }

    public static ItemStackWidget createItemWidget(Item item)
    {
        ItemStackWidget widget = new ItemStackWidget(16, new ItemStack(item));
        widget.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, 0xFF505050);
        widget.getBackgroundRenderer().getHoverSettings().setEnabledAndColor(true, 0xFF505050);
        return widget;
    }
}
