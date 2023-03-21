package malilib.util.inventory;

import java.util.function.Function;
import com.google.common.collect.ImmutableList;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class EquipmentInventoryView implements InventoryView
{
    /**
     * Note: this order is different from how they are stored in vanilla.
     * This is to make the InventoryRenderDefinitions a bit simpler, so that they can go from top down.
     */
    public static final ImmutableList<Function<LivingEntity, ItemStack>> SLOT_FETCHERS
            = ImmutableList.of((e) -> e.getItemBySlot(EquipmentSlot.HEAD),
                               (e) -> e.getItemBySlot(EquipmentSlot.CHEST),
                               (e) -> e.getItemBySlot(EquipmentSlot.LEGS),
                               (e) -> e.getItemBySlot(EquipmentSlot.FEET),
                               (e) -> e.getItemBySlot(EquipmentSlot.MAINHAND),
                               (e) -> e.getItemBySlot(EquipmentSlot.OFFHAND));

    protected final LivingEntity entity;

    public EquipmentInventoryView(LivingEntity entity)
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
        return slot >= 0 && slot < 6 ? SLOT_FETCHERS.get(slot).apply(this.entity) : ItemStack.EMPTY;
    }
}
