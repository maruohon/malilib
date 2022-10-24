package malilib.util.game.wrap;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemWrap
{
    @Nullable
    public static NBTTagCompound getTag(ItemStack stack)
    {
        return stack.getTagCompound();
    }

    public static void setTag(ItemStack stack, @Nullable NBTTagCompound tag)
    {
        stack.setTagCompound(tag);
    }

    public static ItemStack fromTag(NBTTagCompound tag)
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
