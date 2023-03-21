package malilib.util.inventory;

import net.minecraft.world.item.ItemStack;

public interface InventoryView
{
    /**
     * @return the total number of slots in this inventory
     */
    int getSize();

    /**
     * @param slot the slot number
     * @return the ItemStack from the requested slot number
     */
    ItemStack getStack(int slot);
}
