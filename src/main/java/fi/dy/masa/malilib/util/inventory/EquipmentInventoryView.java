package fi.dy.masa.malilib.util.inventory;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EquipmentInventoryView implements InventoryView
{
    protected final EntityLivingBase entity;

    public EquipmentInventoryView(EntityLivingBase entity)
    {
        this.entity = entity;
    }

    @Override
    public int getSlots()
    {
        return 6;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        // Note: this order is different from how they are stored in vanilla.
        // This is to make the InventoryRenderDefinitions a bit simpler, so that they can go from top down.
        switch (slot)
        {
            case 0: return this.entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            case 1: return this.entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            case 2: return this.entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
            case 3: return this.entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
            case 4: return this.entity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
            case 5: return this.entity.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
        }

        return ItemStack.EMPTY;
    }
}
