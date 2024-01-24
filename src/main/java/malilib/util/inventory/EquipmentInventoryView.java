package malilib.util.inventory;

import java.util.function.Function;
import com.google.common.collect.ImmutableList;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import malilib.util.game.wrap.ItemWrap;

public class EquipmentInventoryView implements InventoryView
{
    /**
     * Note: this order is different from how they are stored in vanilla.
     * This is to make the InventoryRenderDefinitions a bit simpler, so that they can go from top down.
     */
    public static final ImmutableList<Function<PlayerEntity, ItemStack>> SLOT_FETCHERS
            = ImmutableList.of((e) -> e.inventory.getArmor(0), // TODO b1.7.3 which is the correct order?
                               (e) -> e.inventory.getArmor(1),
                               (e) -> e.inventory.getArmor(2),
                               (e) -> e.inventory.getArmor(3),
                               (e) -> e.inventory.getMainHandStack());

    protected final PlayerEntity entity;

    public EquipmentInventoryView(PlayerEntity entity)
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
        return slot >= 0 && slot < SLOT_FETCHERS.size() ? SLOT_FETCHERS.get(slot).apply(this.entity) : ItemWrap.EMPTY_STACK;
    }
}
