package malilib.util.inventory;

import net.minecraft.world.item.ItemStack;

public class SlicedInventoryView implements InventoryView
{
    protected final InventoryView baseInventory;
    protected final int startSlot;
    protected final int slotCount;

    public SlicedInventoryView(InventoryView baseInventory, int startSlot, int slotCount)
    {
        this.baseInventory = baseInventory;
        this.startSlot = startSlot;
        this.slotCount = Math.max(Math.min(slotCount, baseInventory.getSize() - startSlot), 0);
    }

    @Override
    public int getSize()
    {
        return this.slotCount;
    }

    @Override
    public ItemStack getStack(int slot)
    {
        return this.baseInventory.getStack(slot + this.startSlot);
    }
}
