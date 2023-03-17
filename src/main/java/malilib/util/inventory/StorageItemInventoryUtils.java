package malilib.util.inventory;

import java.util.function.Consumer;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;

import malilib.util.data.ItemType;
import malilib.util.game.wrap.DefaultedList;
import malilib.util.game.wrap.ItemWrap;
import malilib.util.game.wrap.NbtWrap;

public class StorageItemInventoryUtils
{
    /**
     * Checks if the given Shulker Box (or other storage item with the
     * same NBT data structure) currently contains any items.
     */
    public static boolean shulkerBoxHasItems(ItemStack stack)
    {
        NbtCompound nbt = ItemWrap.getTag(stack);

        if (nbt != null && NbtWrap.containsCompound(nbt, "BlockEntityTag"))
        {
            NbtCompound tag = NbtWrap.getCompound(nbt, "BlockEntityTag");

            if (NbtWrap.containsList(tag, "Items"))
            {
                NbtList tagList = NbtWrap.getListOfCompounds(tag, "Items");
                return NbtWrap.getListSize(tagList) > 0;
            }
        }

        return false;
    }

    /**
     * @return true if the given shulker box item contains the given item
     */
    public static boolean doesShulkerBoxContainItem(ItemStack shulkerBoxStack,
                                                    ItemStack itemToFind,
                                                    boolean ignoreNbt)
    {
        DefaultedList<ItemStack> items = getNonEmptyStoredItems(shulkerBoxStack);

        if (items.size() > 0)
        {
            for (ItemStack item : items)
            {
                if (InventoryUtils.areStacksEqual(item, itemToFind, ignoreNbt))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean doesSlotContainShulkerBoxWithItem(Slot slot, ItemStack referenceStack, boolean ignoreNbt)
    {
        ItemStack stack = slot.getStack();

        if (ItemWrap.isEmpty(stack) == false &&
            stack.getItem() instanceof BlockItem &&
            ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock)
        {
            return StorageItemInventoryUtils.doesShulkerBoxContainItem(stack, referenceStack, ignoreNbt);
        }

        return false;
    }

    /**
     * Returns the list of items currently stored in the given Shulker Box
     * (or other storage item with the same NBT data structure).
     * Does not keep empty slots.
     */
    public static DefaultedList<ItemStack> getNonEmptyStoredItems(ItemStack stackIn)
    {
        NbtCompound nbt = ItemWrap.getTag(stackIn);

        if (nbt != null && NbtWrap.containsCompound(nbt, "BlockEntityTag"))
        {
            NbtCompound tagBlockEntity = NbtWrap.getCompound(nbt, "BlockEntityTag");

            if (NbtWrap.containsList(tagBlockEntity, "Items"))
            {
                DefaultedList<ItemStack> items = DefaultedList.empty();
                NbtList tagList = NbtWrap.getListOfCompounds(tagBlockEntity, "Items");
                final int count = NbtWrap.getListSize(tagList);

                for (int i = 0; i < count; ++i)
                {
                    ItemStack stack = ItemWrap.fromTag(NbtWrap.getCompoundAt(tagList, i));

                    if (ItemWrap.notEmpty(stack))
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
        NbtCompound nbt = ItemWrap.getTag(containerStack);

        if (nbt != null && NbtWrap.containsCompound(nbt, "BlockEntityTag"))
        {
            NbtCompound tagBlockEntity = NbtWrap.getCompound(nbt, "BlockEntityTag");

            if (NbtWrap.containsList(tagBlockEntity, "Items"))
            {
                NbtList tagList = NbtWrap.getListOfCompounds(tagBlockEntity, "Items");
                final int count = NbtWrap.getListSize(tagList);

                for (int i = 0; i < count; ++i)
                {
                    NbtCompound tag = NbtWrap.getCompoundAt(tagList, i);
                    ItemStack stack = ItemWrap.fromTag(tag);
                    int slot = NbtWrap.getByte(tag, "Slot");

                    if (slot >= 0 && ItemWrap.notEmpty(stack))
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
        DefaultedList<ItemStack> items = getNonEmptyStoredItems(stackIn);
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