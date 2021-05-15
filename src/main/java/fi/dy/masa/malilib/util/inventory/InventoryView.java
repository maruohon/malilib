package fi.dy.masa.malilib.util.inventory;

import net.minecraft.item.ItemStack;

public interface InventoryView
{
    /**
     * @return the total number of slots in this inventory
     */
    int getSlots();

    /**
     * @param slot the slot number
     * @return the ItemStack from the requested slot number
     */
    ItemStack getStackInSlot(int slot);
}
