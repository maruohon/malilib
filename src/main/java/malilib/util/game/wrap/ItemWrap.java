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

    public static String getStackString(ItemStack stack)
    {
        if (ItemWrap.notEmpty(stack))
        {
            String id = RegistryUtils.getItemIdStr(stack.getItem());
            NBTTagCompound tag = ItemWrap.getTag(stack);

            return String.format("[%s @ %d - display: %s - NBT: %s] (%s)",
                                 id != null ? id : "null", stack.getMetadata(), stack.getDisplayName(),
                                 tag != null ? tag.toString() : "<no NBT>", stack);
        }

        return "<empty>";
    }
}
