package malilib.util.game;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import malilib.util.game.wrap.ItemWrap;
import malilib.util.game.wrap.RegistryUtils;

public class ItemUtils
{
    public static String getStackString(ItemStack stack)
    {
        if (ItemWrap.notEmpty(stack))
        {
            String id = RegistryUtils.getItemIdStr(stack.getItem());
            CompoundTag tag = ItemWrap.getTag(stack);

            return String.format("[%s - display: %s - NBT: %s] (%s)",
                                 id != null ? id : "null", stack.getHoverName().getString(),
                                 tag != null ? tag.toString() : "<no NBT>", stack);
        }

        return "<empty>";
    }
}
