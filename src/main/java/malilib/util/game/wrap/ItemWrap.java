package malilib.util.game.wrap;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ItemWrap
{
    @Nullable
    public static CompoundTag getTag(ItemStack stack)
    {
        return stack.getTag();
    }

    public static void setTag(ItemStack stack, @Nullable CompoundTag tag)
    {
        stack.setTag(tag);
    }

    public static ItemStack fromTag(CompoundTag tag)
    {
        return ItemStack.of(tag);
    }

    public static boolean isEmpty(ItemStack stack)
    {
        return stack.isEmpty();
    }

    public static boolean notEmpty(ItemStack stack)
    {
        return stack.isEmpty() == false;
    }
}
