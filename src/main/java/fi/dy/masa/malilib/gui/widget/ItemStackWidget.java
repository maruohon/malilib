package fi.dy.masa.malilib.gui.widget;

import net.minecraft.item.ItemStack;
import fi.dy.masa.malilib.render.overlay.InventoryOverlay;

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
    protected void renderModel(int x, int y, int z, float scale)
    {
        if (this.stack.isEmpty() == false)
        {
            InventoryOverlay.renderStackAt(this.stack, x, y, z, this.scale, this.mc);
        }
    }
}
