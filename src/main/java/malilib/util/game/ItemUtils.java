package malilib.util.game;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import malilib.util.game.wrap.ItemWrap;
import malilib.util.game.wrap.RegistryUtils;

public class ItemUtils
{
    public static String getStackString(ItemStack stack)
    {
        if (ItemWrap.notEmpty(stack))
        {
            String id = RegistryUtils.getItemIdStr(stack.getItem());
            NbtCompound tag = ItemWrap.getTag(stack);

            return String.format("[%s @ %d - display: %s - NBT: %s] (%s)",
                                 id != null ? id : "null", stack.getMetadata(), stack.getDisplayName(),
                                 tag != null ? tag.toString() : "<no NBT>", stack);
        }

        return "<empty>";
    }
}
