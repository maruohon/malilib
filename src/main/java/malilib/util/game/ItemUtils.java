package malilib.util.game;

import net.minecraft.item.Item;

import malilib.util.StringUtils;

public class ItemUtils
{
    public static String getDisplayNameForItem(Item item)
    {
        return StringUtils.translate(item.getTranslationKey());
    }

    /* TODO b1.7.3
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
    */
}
