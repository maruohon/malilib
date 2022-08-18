package fi.dy.masa.malilib.util.inventory;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import fi.dy.masa.malilib.util.data.IntRange;
import fi.dy.masa.malilib.util.data.ItemType;
import fi.dy.masa.malilib.util.game.wrap.GameUtils;
import fi.dy.masa.malilib.util.game.wrap.ItemWrap;

public class InventoryUtils
{
    /**
     * Check whether the stacks are identical otherwise, but ignoring the stack size
     */
    public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2)
    {
        return ItemStack.areItemsEqual(stack1, stack2) &&
               ItemStack.areNbtEqual(stack1, stack2);
    }

    /**
     * Check whether the stacks are identical otherwise, but ignoring the stack size,
     * and optionally ignoring the NBT data
     */
    public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2, boolean ignoreNbt)
    {
        return ItemStack.areItemsEqual(stack1, stack2) &&
               (ignoreNbt || ItemStack.areNbtEqual(stack1, stack2));
    }

    /**
     * Checks whether the given stacks are identical, ignoring the stack size and the durability of damageable items.
     */
    public static boolean areStacksEqualIgnoreDurability(ItemStack stack1, ItemStack stack2)
    {
        return ItemStack.areItemsEqualIgnoreDamage(stack1, stack2) &&
               ItemStack.areNbtEqual(stack1, stack2);
    }

    /**
     * Checks whether the given stacks are identical, ignoring the stack size and the durability of damageable items.
     * Optionally ignores the NBT data.
     */
    public static boolean areStacksEqualIgnoreDurability(ItemStack stack1, ItemStack stack2, boolean ignoreNbt)
    {
        return ItemStack.areItemsEqualIgnoreDamage(stack1, stack2) &&
               (ignoreNbt || ItemStack.areNbtEqual(stack1, stack2));
    }

    /**
     * Swaps the stack from the slot <b>slotNum</b> to the given hotbar slot <b>hotbarSlot</b>
     */
    public static void swapSlots(ScreenHandler container, int slotNum, int hotbarSlot)
    {
        clickSlot(container, slotNum, hotbarSlot, SlotActionType.SWAP);
    }

    /**
     * Assuming that the slot is from the ContainerPlayer container,
     * returns whether the given slot number is one of the regular inventory slots.
     * This means that the crafting slots and armor slots are not valid.
     */
    public static boolean isRegularInventorySlot(Slot slot, boolean allowOffhand)
    {
        return isRegularInventorySlot(getSlotId(slot), allowOffhand);
    }

    /**
     * Assuming that the slot is from the ContainerPlayer container,
     * returns whether the given slot number is one of the regular inventory slots.
     * This means that the crafting slots and armor slots are not valid.
     */
    public static boolean isRegularInventorySlot(int slotNumber, boolean allowOffhand)
    {
        return slotNumber > 8 && (slotNumber < 45 || (allowOffhand && slotNumber == 45));
    }

    public static int getSlotId(Slot slot)
    {
        return slot.id;
    }

    public static int getSlotCount(ScreenHandler container)
    {
        return container.slots.size();
    }

    public static List<Slot> getSlotList(ScreenHandler container)
    {
        return container.slots;
    }

    public static ScreenHandler getPlayerInventoryContainer()
    {
        return GameUtils.getClientPlayer().playerScreenHandler;
    }

    public static PlayerInventory getPlayerInventory()
    {
        return GameUtils.getClientPlayer().getInventory();
    }

    /**
     * Finds an empty slot in the player inventory.
     * Armor slots are not valid for this method.
     * The <b>allowOffhand</b> argument defines whether the offhand slot is valid.
     * @return the slot number, or -1 if none were found
     */
    public static int findEmptySlotInPlayerInventory(ScreenHandler containerPlayer,
                                                     boolean allowOffhand,
                                                     boolean reverseIteration)
    {
        // Inventory crafting, armor and offhand slots are not valid
        Predicate<Slot> slotTest = (slot) -> isRegularInventorySlot(slot, allowOffhand) &&
                                             ItemWrap.isEmpty(slot.getStack());

        return getSlotNumberOrDefault(findSlot(containerPlayer, slotTest, reverseIteration), -1);
    }

    /**
     * Finds a slot with an identical item to <b>stackReference</b> from the regular player inventory,
     * ignoring the durability of damageable items.
     * Does not allow crafting or armor slots or the offhand slot.
     * @param reverseIteration if true, then the slots are iterated in reverse order
     * @return the slot number, or -1 if none were found
     */
    public static int findPlayerInventorySlotWithItem(ScreenHandler container,
                                                      ItemStack stackReference,
                                                      boolean reverseIteration)
    {
        return findPlayerInventorySlotWithItem(container, stackReference, false, reverseIteration);
    }

    /**
     * Finds a slot with an identical item to <b>stackReference</b> from the regular player inventory,
     * ignoring the durability of damageable items and optionally ignoring NBT data.
     * Does not allow crafting or armor slots or the off hand slot.
     * @param reverse if true, then the slots are iterated in reverse order
     * @return the slot number, or -1 if none were found
     */
    public static int findPlayerInventorySlotWithItem(ScreenHandler container,
                                                      ItemStack stackReference,
                                                      boolean ignoreNbt,
                                                      boolean reverse)
    {
        if ((container instanceof PlayerScreenHandler) == false)
        {
            return -1;
        }

        Predicate<Slot> slotTest = (slot) -> isRegularInventorySlot(slot, false) &&
                                             areStacksEqualIgnoreDurability(slot.getStack(), stackReference, ignoreNbt);

        return getSlotNumberOrDefault(findSlot(container, slotTest, reverse), -1);
    }

    public static int getSlotNumberOrDefault(@Nullable Slot slot, int defaultSlotNumber)
    {
        return slot != null ? getSlotId(slot) : defaultSlotNumber;
    }

    /**
     * Iterates all the slots in the given container, until the first slot that
     * passes the given test is found, and then that slot is returned. If no
     * matches are found, then null is returned.
     * @return the first slot that passes the test, or null
     */
    @Nullable
    public static Slot findSlot(ScreenHandler container,
                                Predicate<Slot> slotTest,
                                boolean reverseIteration)
    {
        final int slotCount = getSlotCount(container);
        final int startSlot = reverseIteration ? slotCount - 1 : 0;
        final int endSlot = reverseIteration ? -1 : slotCount;
        final int increment = reverseIteration ? -1 : 1;

        for (int slotNum = startSlot; slotNum != endSlot; slotNum += increment)
        {
            Slot slot = container.getSlot(slotNum);

            if (slotTest.test(slot))
            {
                return slot;
            }
        }

        return null;
    }

    /**
     * Tries to find a slot with the given item for pick-blocking.
     * Prefers the hotbar to the rest of the inventory.
     */
    public static int findSlotWithItemToPickBlock(ScreenHandler container,
                                                  ItemStack stackReference,
                                                  boolean ignoreNbt)
    {
        if ((container instanceof PlayerScreenHandler) == false)
        {
            return -1;
        }

        // Hotbar
        int slot = findSlotWithItem(container, stackReference, IntRange.of(36, 9), true, ignoreNbt);

        if (slot != -1)
        {
            return slot;
        }

        // Regular player inventory and offhand
        return findSlotWithItem(container, stackReference, IntRange.of(9, 27 + 1), true, ignoreNbt);
    }

    public static int findSlotWithItem(ScreenHandler container,
                                       ItemStack stackReference,
                                       IntRange intRange,
                                       boolean ignoreDurability,
                                       boolean ignoreNbt)
    {
        final int startSlot = intRange.getFirst();
        final int endSlot = intRange.getLast();
        List<Slot> slots = getSlotList(container);

        if (startSlot < 0 || endSlot >= slots.size())
        {
            return -1;
        }

        for (int slotNum = startSlot; slotNum <= endSlot; ++slotNum)
        {
            Slot slot = slots.get(slotNum);

            if ((ignoreDurability          && areStacksEqualIgnoreDurability(slot.getStack(), stackReference, ignoreNbt)) ||
                (ignoreDurability == false && areStacksEqual(slot.getStack(), stackReference, ignoreNbt)))
            {
                return getSlotId(slot);
            }
        }

        return -1;
    }

    /**
     * Swap the given item to the player's main hand, if that item is found in the player's inventory.
     * @return true if an item was swapped to the main hand, false if it was already in the hand, or was not found in the inventory
     */
    public static boolean swapItemToMainHand(ItemStack stackReference)
    {
        return swapItemToMainHand(stackReference, false);
    }

    /**
     * Swap the given item to the player's main hand, if that item is found in the player's inventory.
     * @return true if an item was swapped to the main hand, false if it was already in the hand, or was not found in the inventory
     */
    public static boolean swapItemToMainHand(ItemStack stackReference, boolean ignoreNbt)
    {
        PlayerEntity player = GameUtils.getClientPlayer();

        // Already holding the requested item
        if (areStacksEqual(stackReference, player.getMainHandStack(), ignoreNbt))
        {
            return false;
        }

        PlayerInventory inventory = getPlayerInventory();

        if (GameUtils.isCreativeMode())
        {
            inventory.setPickedItemStack(stackReference.copy());
            GameUtils.getInteractionManager().sendSlotPacket(stackReference.copy(), 36 + inventory.currentItem);
            return true;
        }
        else
        {
            int slot = findPlayerInventorySlotWithItem(getPlayerInventoryContainer(), stackReference, true);

            if (slot != -1)
            {
                int currentHotbarSlot = inventory.selectedSlot;
                clickSlot(getPlayerInventoryContainer(), slot, currentHotbarSlot, SlotActionType.SWAP);
                return true;
            }
        }

        return false;
    }

    /**
     * Re-stocks more items to the stack in the player's current hotbar slot.
     * @param threshold the number of items at or below which the re-stocking will happen
     * @param allowHotbar whether to allow taking items from other hotbar slots
     */
    public static void preRestockHand(PlayerEntity player,
                                      Hand hand,
                                      int threshold,
                                      boolean allowHotbar)
    {
        final ItemStack stackHand = player.getStackInHand(hand);
        final int count = stackHand.getCount();
        final int max = stackHand.getMaxCount();
        ScreenHandler container = getPlayerInventoryContainer();
        PlayerInventory inventory = getPlayerInventory();

        if (ItemWrap.notEmpty(stackHand) &&
            player.openContainer == container &&
            ItemWrap.isEmpty(inventory.getItemStack()) &&
            (count <= threshold && count < max))
        {
            int endSlot = allowHotbar ? 44 : 35;
            int currentMainHandSlot = inventory.selectedSlot + 36;
            int currentSlot = hand == Hand.MAIN_HAND ? currentMainHandSlot : 45;

            for (int slotNum = 9; slotNum <= endSlot; ++slotNum)
            {
                if (slotNum == currentMainHandSlot)
                {
                    continue;
                }

                Slot slot = container.getSlot(slotNum);
                ItemStack stackSlot = slot.getStack();

                if (areStacksEqual(stackSlot, stackHand))
                {
                    // If all the items from the found slot can fit into the current
                    // stack in hand, then left click, otherwise right click to split the stack
                    int button = stackSlot.getCount() + count <= max ? 0 : 1;

                    clickSlot(container, slot, button, SlotActionType.PICKUP);
                    clickSlot(container, currentSlot, 0, SlotActionType.PICKUP);

                    break;
                }
            }
        }

    }

    /* TODO
    public static InventoryView getCompactedStoredItemsView(ItemStack stackIn)
    {
    }
    */

    /**
     * @return a combined view of both inventories appended after each other.
     * inventory1 will be first, and inventory2 will be appended after it.
     */
    public static InventoryView getCombinedInventoryView(InventoryView inventory1, InventoryView inventory2)
    {
        return new CombinedInventoryView(inventory1, inventory2);
    }

    /**
     * Returns a map of the stored item counts in the given inventory.
     * This also counts the contents of any Shulker Boxes
     * (or other storage item with the same NBT data structure).
     */
    public static Object2IntOpenHashMap<ItemType> getInventoryItemCounts(Inventory inv)
    {
        return getInventoryItemCounts(new VanillaInventoryView(inv));
    }

    /**
     * Returns a map of the stored item counts in the given inventory.
     * This also counts the contents of any Shulker Boxes
     * (or other storage item with the same NBT data structure).
     */
    public static Object2IntOpenHashMap<ItemType> getInventoryItemCounts(InventoryView inv)
    {
        Object2IntOpenHashMap<ItemType> map = new Object2IntOpenHashMap<>();
        final int slots = inv.getSize();

        for (int slot = 0; slot < slots; ++slot)
        {
            ItemStack stack = inv.getStack(slot);

            if (ItemWrap.notEmpty(stack))
            {
                map.addTo(new ItemType(stack, false, true), stack.getCount());

                if (StorageItemInventoryUtils.shulkerBoxHasItems(stack))
                {
                    Object2IntOpenHashMap<ItemType> boxCounts = StorageItemInventoryUtils.getStoredItemCounts(stack);

                    for (ItemType type : boxCounts.keySet())
                    {
                        map.addTo(type, boxCounts.getInt(type));
                    }
                }
            }
        }

        return map;
    }

    public static void clickSlot(ScreenHandler container, Slot slot, int mouseButton, SlotActionType clickType)
    {
        clickSlot(container, getSlotId(slot), mouseButton, clickType);
    }

    public static void clickSlot(ScreenHandler container, int slotNum, int mouseButton, SlotActionType clickType)
    {
        if (slotNum >= 0 && slotNum < getSlotCount(container))
        {
            GameUtils.getInteractionManager().clickSlot(container.syncId, slotNum, mouseButton,
                                                        clickType, GameUtils.getClientPlayer());
        }
    }
}
