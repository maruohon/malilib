package fi.dy.masa.malilib.util.inventory;

import java.util.function.Consumer;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import fi.dy.masa.malilib.util.ItemUtils;
import fi.dy.masa.malilib.util.data.ItemType;
import fi.dy.masa.malilib.util.wrap.DefaultedList;
import fi.dy.masa.malilib.util.wrap.NbtWrap;

public class StorageItemInventoryUtils
{
    /**
     * Checks if the given Shulker Box (or other storage item with the
     * same NBT data structure) currently contains any items.
     */
    public static boolean shulkerBoxHasItems(ItemStack stack)
    {
        NBTTagCompound nbt = ItemUtils.getTag(stack);

        if (nbt != null && NbtWrap.containsCompound(nbt, "BlockEntityTag"))
        {
            NBTTagCompound tag = NbtWrap.getCompound(nbt, "BlockEntityTag");

            if (NbtWrap.containsList(tag, "Items"))
            {
                NBTTagList tagList = NbtWrap.getListOfCompounds(tag, "Items");
                return NbtWrap.getListSize(tagList) > 0;
            }
        }

        return false;
    }

    /**
     * Returns the list of items currently stored in the given Shulker Box
     * (or other storage item with the same NBT data structure).
     * Does not keep empty slots.
     */
    public static DefaultedList<ItemStack> getStoredItemsNonEmpty(ItemStack stackIn)
    {
        NBTTagCompound nbt = ItemUtils.getTag(stackIn);

        if (nbt != null && NbtWrap.containsCompound(nbt, "BlockEntityTag"))
        {
            NBTTagCompound tagBlockEntity = NbtWrap.getCompound(nbt, "BlockEntityTag");

            if (NbtWrap.containsList(tagBlockEntity, "Items"))
            {
                DefaultedList<ItemStack> items = DefaultedList.empty();
                NBTTagList tagList = NbtWrap.getListOfCompounds(tagBlockEntity, "Items");
                final int count = NbtWrap.getListSize(tagList);

                for (int i = 0; i < count; ++i)
                {
                    ItemStack stack = ItemUtils.fromTag(NbtWrap.getCompoundAt(tagList, i));

                    if (ItemUtils.notEmpty(stack))
                    {
                        items.add(stack);
                    }
                }

                return items;
            }
        }

        return DefaultedList.empty();
    }

    public static void readStoredItems(ItemStack containerStack, Consumer<Pair<Integer, ItemStack>> consumer)
    {
        NBTTagCompound nbt = ItemUtils.getTag(containerStack);

        if (nbt != null && NbtWrap.containsCompound(nbt, "BlockEntityTag"))
        {
            NBTTagCompound tagBlockEntity = NbtWrap.getCompound(nbt, "BlockEntityTag");

            if (NbtWrap.containsList(tagBlockEntity, "Items"))
            {
                NBTTagList tagList = NbtWrap.getListOfCompounds(tagBlockEntity, "Items");
                final int count = NbtWrap.getListSize(tagList);

                for (int i = 0; i < count; ++i)
                {
                    NBTTagCompound tag = NbtWrap.getCompoundAt(tagList, i);
                    ItemStack stack = ItemUtils.fromTag(tag);
                    int slot = NbtWrap.getByte(tag, "Slot");

                    if (slot >= 0 && ItemUtils.notEmpty(stack))
                    {
                        consumer.accept(Pair.of(slot, stack));
                    }
                }
            }
        }
    }

    /**
     * Returns a map of the stored item counts in the given Shulker Box
     * (or other storage item with the same NBT data structure).
     */
    public static Object2IntOpenHashMap<ItemType> getStoredItemCounts(ItemStack stackIn)
    {
        DefaultedList<ItemStack> items = getStoredItemsNonEmpty(stackIn);
        return InventoryUtils.getInventoryItemCounts(new ListBackedInventoryView(items));
    }

    public static InventoryView getExactStoredItemsView(ItemStack stackIn)
    {
        ListBackedInventoryView inv = new ListBackedInventoryView();
        readStoredItems(stackIn, (pair) -> inv.setStackInSlot(pair.getKey(), pair.getValue()));
        return inv;
    }

    public static InventoryView getNonEmptyStoredItemsView(ItemStack stackIn)
    {
        ListBackedInventoryView inv = new ListBackedInventoryView();
        readStoredItems(stackIn, (pair) -> inv.addStack(pair.getValue()));
        return inv;
    }
}
