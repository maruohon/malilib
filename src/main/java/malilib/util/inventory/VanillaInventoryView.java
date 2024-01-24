package malilib.util.inventory;

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
    public int getSize()
    {
        return this.inv.getSize();
    }

    @Override
    public ItemStack getStack(int slot)
    {
        return this.inv.getStack(slot);
    }
}
