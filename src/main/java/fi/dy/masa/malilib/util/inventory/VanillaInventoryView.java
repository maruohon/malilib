package fi.dy.masa.malilib.util.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class VanillaInventoryView implements InventoryView
{
    protected final IInventory inv;

    public VanillaInventoryView(IInventory inv)
    {
        this.inv = inv;
    }

    @Override
    public int getSlots()
    {
        return this.inv.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return this.inv.getStackInSlot(slot);
    }
}
