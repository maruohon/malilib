package malilib.util.game.wrap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class ItemWrap
{
    public static final ItemStack EMPTY_STACK = null;

    /*
    @Nullable
    public static NbtCompound getTag(ItemStack stack)
    {
        return stack.getTagCompound();
    }

    public static void setTag(ItemStack stack, @Nullable NbtCompound tag)
    {
        stack.setTagCompound(tag);
    }
    */

    public static ItemStack fromTag(NbtCompound tag)
    {
        return new ItemStack(tag);
    }

    public static boolean isEmpty(ItemStack stack)
    {
        return stack == null || stack.size <= 0;
    }

    public static boolean notEmpty(ItemStack stack)
    {
        return stack != null && stack.size > 0;
    }
}
