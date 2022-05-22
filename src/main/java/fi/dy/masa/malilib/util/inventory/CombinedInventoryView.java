package fi.dy.masa.malilib.util.inventory;

import net.minecraft.item.ItemStack;

public class CombinedInventoryView implements InventoryView
{
    protected final InventoryView inventory1;
    protected final InventoryView inventory2;

    public CombinedInventoryView(InventoryView inventory1, InventoryView inventory2)
    {
        this.inventory1 = inventory1;
        this.inventory2 = inventory2;
    }

    @Override
    public int getSize()
    {
        return this.inventory1.getSize() + this.inventory2.getSize();
    }

    @Override
    public ItemStack getStack(int slot)
    {
        final int firstInvSize = this.inventory1.getSize();

        if (slot < firstInvSize)
        {
            return this.inventory1.getStack(slot);
        }

        return this.inventory2.getStack(slot - firstInvSize);
    }
}
