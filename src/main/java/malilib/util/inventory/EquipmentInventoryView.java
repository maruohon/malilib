package malilib.util.inventory;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import malilib.util.game.wrap.ItemWrap;

public class EquipmentInventoryView implements InventoryView
{
    /**
     * Note: this order is different from how they are stored in vanilla.
     * This is to make the InventoryRenderDefinitions a bit simpler, so that they can go from top down.
     */
    /*
    public static final ImmutableList<Function<EntityLivingBase, ItemStack>> SLOT_FETCHERS
            = ImmutableList.of((e) -> e.getItemStackFromSlot(EntityEquipmentSlot.HEAD),
                               (e) -> e.getItemStackFromSlot(EntityEquipmentSlot.CHEST),
                               (e) -> e.getItemStackFromSlot(EntityEquipmentSlot.LEGS),
                               (e) -> e.getItemStackFromSlot(EntityEquipmentSlot.FEET),
                               (e) -> e.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND),
                               (e) -> e.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND));
    */

    protected final EntityLivingBase entity;

    public EquipmentInventoryView(EntityLivingBase entity)
    {
        this.entity = entity;
    }

    @Override
    public int getSize()
    {
        return 6;
    }

    @Override
    public ItemStack getStack(int slot)
    {
        //return slot >= 0 && slot < 6 ? SLOT_FETCHERS.get(slot).apply(this.entity) : ItemStack.EMPTY;
        ItemStack[] slots = this.entity.getInventory();
        return slot >= 0 && slot < slots.length ? slots[slot] : ItemWrap.EMPTY_STACK;
    }
}
