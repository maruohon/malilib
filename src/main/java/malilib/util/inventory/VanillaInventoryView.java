package malilib.util.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class VanillaInventoryView implements InventoryView
{
    protected final Container inv;

    public VanillaInventoryView(Container inv)
    {
        this.inv = inv;
    }

    @Override
    public int getSize()
    {
        return this.inv.getContainerSize();
    }

    @Override
    public ItemStack getStack(int slot)
    {
        return this.inv.getItem(slot);
    }
}
