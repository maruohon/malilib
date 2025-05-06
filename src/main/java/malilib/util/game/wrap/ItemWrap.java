package malilib.util.game.wrap;

import javax.annotation.Nullable;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemWrap
{
    public static final ItemStack EMPTY_STACK = new ItemStack(Blocks.air);

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
        ItemStack stack = EMPTY_STACK.copy();
        stack.readFromNBT(tag);
        return stack;
    }

    public static boolean isEmpty(ItemStack stack)
    {
        return stack == null || stack == EMPTY_STACK;
    }

    public static boolean notEmpty(ItemStack stack)
    {
        return isEmpty(stack) == false;
    }

    public static int getStackSize(ItemStack stack)
    {
        return stack != null ? stack.stackSize : 0;
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
