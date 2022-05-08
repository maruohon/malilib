package fi.dy.masa.malilib.util.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class VanillaInventoryView implements InventoryView
{
    protected final Inventory inv;

    public VanillaInventoryView(Inventory inv)
    {
        this.inv = inv;
    }

    @Override
    public int getSlots()
    {
        return this.inv.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return this.inv.getStack(slot);
    }
}
