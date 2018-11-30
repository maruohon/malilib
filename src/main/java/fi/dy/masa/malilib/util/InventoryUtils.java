package fi.dy.masa.malilib.util;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;

public class InventoryUtils
{
    private static final NonNullList<ItemStack> EMPTY_LIST = NonNullList.create();

    /**
     * Check whether the stacks are identical otherwise, but ignoring the stack size
     * @param stack1
     * @param stack2
     * @return
     */
    public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2)
    {
        return ItemStack.areItemsEqual(stack1, stack2) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    /**
     * Checks whether the stacks are identical otherwise, but ignoring the stack size,
     * and if the item is damageable, then ignoring the durability too.
     * @param stack1
     * @param stack2
     * @return
     */
    public static boolean areStacksEqualIgnoreDurability(ItemStack stack1, ItemStack stack2)
    {
        return ItemStack.areItemsEqualIgnoreDurability(stack1, stack2) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    /**
     * Swaps the stack from the slot <b>slotNum</b> to the given hotbar slot <b>hotbarSlot</b>
     * @param container
     * @param slot
     * @param hotbarSlot
     */
    public static void swapSlots(Container container, int slotNum, int hotbarSlot)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.playerController.windowClick(container.windowId, slotNum, hotbarSlot, ClickType.SWAP, mc.player);
    }

    /**
     * Assuming that the slot is from the ContainerPlayer container,
     * returns whether the given slot number is one of the regular inventory slots.
     * This means that the crafting slots and armor slots are not valid.
     * @param slotNumber
     * @param allowOffhand
     * @return
     */
    public static boolean isRegularInventorySlot(int slotNumber, boolean allowOffhand)
    {
        return slotNumber > 8 && (allowOffhand || slotNumber < 45);
    }

    /**
     * Finds an empty slot in the player inventory. Armor slots are not valid for the return value of this method.
     * Whether or not the offhand slot is valid, depends on the <b>allowOffhand</b> argument.
     * @param containerPlayer
     * @param allowOffhand
     * @param reverse
     * @return the slot number, or -1 if none were found
     */
    public static int findEmptySlotInPlayerInventory(Container containerPlayer, boolean allowOffhand, boolean reverse)
    {
        final int startSlot = reverse ? containerPlayer.inventorySlots.size() - 1 : 0;
        final int endSlot = reverse ? -1 : containerPlayer.inventorySlots.size();
        final int increment = reverse ? -1 : 1;

        for (int slotNum = startSlot; slotNum != endSlot; slotNum += increment)
        {
            Slot slot = containerPlayer.inventorySlots.get(slotNum);
            ItemStack stackSlot = slot.getStack();

            // Inventory crafting, armor and offhand slots are not valid
            if (stackSlot.isEmpty() && isRegularInventorySlot(slot.slotNumber, allowOffhand))
            {
                return slot.slotNumber;
            }
        }

        return -1;
    }

    /**
     * Finds a slot with an identical item than <b>stackReference</b>, ignoring the durability
     * of damageable items. Does not allow crafting or armor slots or the offhand slot
     * in the ContainerPlayer container.
     * @param container
     * @param stackReference
     * @param reverse
     * @return the slot number, or -1 if none were found
     */
    public static int findSlotWithItem(Container container, ItemStack stackReference, boolean reverse)
    {
        final int startSlot = reverse ? container.inventorySlots.size() - 1 : 0;
        final int endSlot = reverse ? -1 : container.inventorySlots.size();
        final int increment = reverse ? -1 : 1;
        final boolean isPlayerInv = container instanceof ContainerPlayer;

        for (int slotNum = startSlot; slotNum != endSlot; slotNum += increment)
        {
            Slot slot = container.inventorySlots.get(slotNum);

            if ((isPlayerInv == false || isRegularInventorySlot(slot.slotNumber, false)) &&
                areStacksEqualIgnoreDurability(slot.getStack(), stackReference))
            {
                return slot.slotNumber;
            }
        }

        return -1;
    }

    /**
     * Swap the given item to the player's main hand, if that item is found
     * in the player's inventory.
     * @param stackReference
     * @param mc
     * @return true if an item was swapped to the main hand, false if it was already in the hand, or was not found in the inventory
     */
    public static boolean swapItemToMainHand(ItemStack stackReference, Minecraft mc)
    {
        EntityPlayer player = mc.player;
        boolean isCreative = player.abilities.isCreativeMode;

        // Already holding the requested item
        if (areStacksEqual(stackReference, player.getHeldItemMainhand()))
        {
            return false;
        }

        if (isCreative)
        {
            player.inventory.setPickedItemStack(stackReference);
            mc.playerController.sendSlotPacket(player.getHeldItem(EnumHand.MAIN_HAND), 36 + player.inventory.currentItem);
            return true;
        }
        else
        {
            int slot = findSlotWithItem(player.inventoryContainer, stackReference, true);

            if (slot != -1)
            {
                int currentHotbarSlot = player.inventory.currentItem;
                mc.playerController.windowClick(player.inventoryContainer.windowId, slot, currentHotbarSlot, ClickType.SWAP, mc.player);
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the given Shulker Box (or other storage item with the
     * same NBT data structure) currently contains any items.
     * @param stackShulkerBox
     * @return
     */
    public static boolean shulkerBoxHasItems(ItemStack stackShulkerBox)
    {
        NBTTagCompound nbt = stackShulkerBox.getTag();

        if (nbt != null && nbt.contains("BlockEntityTag", Constants.NBT.TAG_COMPOUND))
        {
            NBTTagCompound tag = nbt.getCompound("BlockEntityTag");

            if (tag.contains("Items", Constants.NBT.TAG_LIST))
            {
                NBTTagList tagList = tag.getList("Items", Constants.NBT.TAG_COMPOUND);
                return tagList.size() > 0;
            }
        }

        return false;
    }

    /**
     * Returns the list of items currently stored in the given Shulker Box
     * (or other storage item with the same NBT data structure).
     * Does not keep empty slots.
     * @param stackShulkerBox
     * @return
     */
    public static NonNullList<ItemStack> getStoredItems(ItemStack stackIn)
    {
        NBTTagCompound nbt = stackIn.getTag();

        if (nbt != null && nbt.contains("BlockEntityTag", Constants.NBT.TAG_COMPOUND))
        {
            NBTTagCompound tagBlockEntity = nbt.getCompound("BlockEntityTag");

            if (tagBlockEntity.contains("Items", Constants.NBT.TAG_LIST))
            {
                NonNullList<ItemStack> items = NonNullList.create();
                NBTTagList tagList = tagBlockEntity.getList("Items", Constants.NBT.TAG_COMPOUND);
                final int count = tagList.size();

                for (int i = 0; i < count; ++i)
                {
                    ItemStack stack = ItemStack.read(tagList.getCompound(i));

                    if (stack.isEmpty() == false)
                    {
                        items.add(stack);
                    }
                }

                return items;
            }
        }

        return NonNullList.create();
    }

    /**
     * Returns the list of items currently stored in the given Shulker Box
     * (or other storage item with the same NBT data structure).
     * Preserves empty slots.
     * @param stackShulkerBox
     * @param slotCount the maximum number of slots, and thus also the size of the list to create
     * @return
     */
    public static NonNullList<ItemStack> getStoredItems(ItemStack stackIn, int slotCount)
    {
        NBTTagCompound nbt = stackIn.getTag();

        if (nbt != null && nbt.contains("BlockEntityTag", Constants.NBT.TAG_COMPOUND))
        {
            NBTTagCompound tagBlockEntity = nbt.getCompound("BlockEntityTag");

            if (tagBlockEntity.contains("Items", Constants.NBT.TAG_LIST))
            {
                NBTTagList tagList = tagBlockEntity.getList("Items", Constants.NBT.TAG_COMPOUND);
                final int count = tagList.size();
                int maxSlot = -1;

                if (slotCount <= 0)
                {
                    for (int i = 0; i < count; ++i)
                    {
                        NBTTagCompound tag = tagList.getCompound(i);
                        int slot = tag.getByte("Slot");

                        if (slot > maxSlot)
                        {
                            maxSlot = slot;
                        }
                    }

                    slotCount = maxSlot + 1;
                }

                NonNullList<ItemStack> items = NonNullList.withSize(slotCount, ItemStack.EMPTY);

                for (int i = 0; i < count; ++i)
                {
                    NBTTagCompound tag = tagList.getCompound(i);
                    ItemStack stack = ItemStack.read(tag);
                    int slot = tag.getByte("Slot");

                    if (slot >= 0 && slot < items.size() && stack.isEmpty() == false)
                    {
                        items.set(slot, stack);
                    }
                }

                return items;
            }
        }

        return EMPTY_LIST;
    }

    /**
     * Returns a map of the stored item counts in the given Shulker Box
     * (or other storage item with the same NBT data structure).
     * @param stackShulkerBox
     * @return
     */
    public static Object2IntOpenHashMap<ItemType> getStoredItemCounts(ItemStack stackShulkerBox)
    {
        Object2IntOpenHashMap<ItemType> map = new Object2IntOpenHashMap<>();
        NonNullList<ItemStack> items = getStoredItems(stackShulkerBox);

        for (int slot = 0; slot < items.size(); ++slot)
        {
            ItemStack stack = items.get(slot);

            if (stack.isEmpty() == false)
            {
                map.addTo(new ItemType(stack), stack.getCount());
            }
        }

        return map;
    }

    /**
     * Returns a map of the stored item counts in the given inventory.
     * This also counts the contents of any Shulker Boxes
     * (or other storage item with the same NBT data structure).
     * @param player
     * @return
     */
    public static Object2IntOpenHashMap<ItemType> getInventoryItemCounts(IInventory inv)
    {
        Object2IntOpenHashMap<ItemType> map = new Object2IntOpenHashMap<>();
        final int slots = inv.getSizeInventory();

        for (int slot = 0; slot < slots; ++slot)
        {
            ItemStack stack = inv.getStackInSlot(slot);

            if (stack.isEmpty() == false)
            {
                map.addTo(new ItemType(stack, false, true), stack.getCount());

                if (stack.getItem() instanceof ItemShulkerBox && shulkerBoxHasItems(stack))
                {
                    Object2IntOpenHashMap<ItemType> boxCounts = getStoredItemCounts(stack);

                    for (ItemType type : boxCounts.keySet())
                    {
                        map.addTo(type, boxCounts.getInt(type));
                    }
                }
            }
        }

        return map;
    }

    /**
     * Returns the given list of items wrapped as an InventoryBasic
     * @param items
     * @return
     */
    public static IInventory getAsInventory(NonNullList<ItemStack> items)
    {
        InventoryBasic inv = new InventoryBasic(new TextComponentString(""), items.size());

        for (int slot = 0; slot < items.size(); ++slot)
        {
            inv.setInventorySlotContents(slot, items.get(slot));
        }

        return inv;
    }
}
