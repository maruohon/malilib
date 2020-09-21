package fi.dy.masa.malilib.gui.widget;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import fi.dy.masa.malilib.render.ItemRenderUtils;
import fi.dy.masa.malilib.util.ItemUtils;

public class ItemStackWidget extends BaseModelWidget
{
    protected ItemStack stack;

    public ItemStackWidget(int x, int y, ItemStack stack)
    {
        this(x, y, 16, stack);
    }

    public ItemStackWidget(int x, int y, int dimensions, ItemStack stack)
    {
        super(x, y, dimensions);

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
    protected void renderModel(int x, int y, float z, float scale)
    {
        if (this.stack.isEmpty() == false)
        {
            ItemRenderUtils.renderStackAt(this.stack, x, y, z, this.scale, this.mc);
        }
    }

    public static ItemStackWidget createItemWidget(int x, int y, int dimensions, Item item)
    {
        ItemStackWidget widget = new ItemStackWidget(x, y, dimensions, new ItemStack(item));
        widget.setBackgroundColor(0xFF505050);
        widget.setBackgroundColorHovered(0xFF505050);
        widget.setBackgroundEnabled(true);
        widget.setRenderHoverBackground(true);
        return widget;
    }

    public static String getItemDisplayName(Item item)
    {
        return ItemUtils.getItemRegistryName(item);
    }
}
