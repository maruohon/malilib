package fi.dy.masa.malilib.gui.widget;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.render.ItemRenderUtils;
import fi.dy.masa.malilib.util.ItemUtils;

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

        this.updateWidth();
        this.updateHeight();

        return this;
    }

    @Override
    protected void renderModel(int x, int y, float z, float scale, ScreenContext ctx)
    {
        if (this.stack.isEmpty() == false)
        {
            ItemRenderUtils.renderStackAt(this.stack, x, y, z, this.scale, this.mc);
        }
    }

    public static ItemStackWidget createItemWidget(int dimensions, Item item)
    {
        ItemStackWidget widget = new ItemStackWidget(dimensions, new ItemStack(item));
        widget.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, 0xFF505050);
        widget.getBackgroundRenderer().getHoverSettings().setEnabledAndColor(true, 0xFF505050);
        return widget;
    }

    public static String getItemDisplayName(Item item)
    {
        return ItemUtils.getItemRegistryName(item);
    }
}
