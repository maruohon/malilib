package fi.dy.masa.malilib.util.inventory;

import net.minecraft.item.ItemStack;

public class SlicedInventoryView implements InventoryView
{
    protected final InventoryView baseInventory;
    protected final int startSlot;
    protected final int slotCount;

    public SlicedInventoryView(InventoryView baseInventory, int startSlot, int slotCount)
    {
        this.baseInventory = baseInventory;
        this.startSlot = startSlot;
        this.slotCount = Math.max(Math.min(slotCount, baseInventory.getSlots() - startSlot), 0);
    }

    @Override
    public int getSlots()
    {
        return this.slotCount;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return this.baseInventory.getStackInSlot(slot + this.startSlot);
    }
}
