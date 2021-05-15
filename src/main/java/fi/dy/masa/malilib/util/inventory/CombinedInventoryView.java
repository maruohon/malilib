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
    public int getSlots()
    {
        return this.inventory1.getSlots() + this.inventory2.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        final int firstInvSize = this.inventory1.getSlots();

        if (slot < firstInvSize)
        {
            return this.inventory1.getStackInSlot(slot);
        }

        return this.inventory2.getStackInSlot(slot - firstInvSize);
    }
}
