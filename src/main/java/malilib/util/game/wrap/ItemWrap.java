package malilib.util.game.wrap;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class ItemWrap
{
    @Nullable
    public static NbtCompound getTag(ItemStack stack)
    {
        return stack.getNbt();
    }

    public static void setTag(ItemStack stack, @Nullable NbtCompound tag)
    {
        stack.setNbt(tag);
    }

    public static ItemStack fromTag(NbtCompound tag)
    {
        return new ItemStack(tag);
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
