package fi.dy.masa.malilib.util.inventory;

import java.util.function.Function;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EquipmentInventoryView implements InventoryView
{
    /**
     * Note: this order is different from how they are stored in vanilla.
     * This is to make the InventoryRenderDefinitions a bit simpler, so that they can go from top down.
     */
    public static final ImmutableList<Function<EntityLivingBase, ItemStack>> SLOT_FETCHERS
            = ImmutableList.of((e) -> e.getItemStackFromSlot(EntityEquipmentSlot.HEAD),
                               (e) -> e.getItemStackFromSlot(EntityEquipmentSlot.CHEST),
                               (e) -> e.getItemStackFromSlot(EntityEquipmentSlot.LEGS),
                               (e) -> e.getItemStackFromSlot(EntityEquipmentSlot.FEET),
                               (e) -> e.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND),
                               (e) -> e.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND));

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
        return slot >= 0 && slot < 6 ? SLOT_FETCHERS.get(slot).apply(this.entity) : ItemStack.EMPTY;
    }
}
