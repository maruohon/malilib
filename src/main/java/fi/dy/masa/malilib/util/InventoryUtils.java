package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class InventoryUtils
{
    private static final DefaultedList<ItemStack> EMPTY_LIST = DefaultedList.of();

    /**
     * Check whether the stacks are identical otherwise, but ignoring the stack size
     * @param stack1
     * @param stack2
     * @return
     */
    public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2)
    {
        return ItemStack.areItemsEqual(stack1, stack2) && ItemStack.areNbtEqual(stack1, stack2);
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
        return ItemStack.areItemsEqualIgnoreDamage(stack1, stack2) && ItemStack.areNbtEqual(stack1, stack2);
    }

    /**
     * Swaps the stack from the slot <b>slotNum</b> to the given hotbar slot <b>hotbarSlot</b>
     * @param container
     * @param slotNum
     * @param hotbarSlot
     */
    public static void swapSlots(ScreenHandler container, int slotNum, int hotbarSlot)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.interactionManager.clickSlot(container.syncId, slotNum, hotbarSlot, SlotActionType.SWAP, mc.player);
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
    public static int findEmptySlotInPlayerInventory(ScreenHandler containerPlayer, boolean allowOffhand, boolean reverse)
    {
        final int startSlot = reverse ? containerPlayer.slots.size() - 1 : 0;
        final int endSlot = reverse ? -1 : containerPlayer.slots.size();
        final int increment = reverse ? -1 : 1;

        for (int slotNum = startSlot; slotNum != endSlot; slotNum += increment)
        {
            Slot slot = containerPlayer.slots.get(slotNum);
            ItemStack stackSlot = slot.getStack();

            // Inventory crafting, armor and offhand slots are not valid
            if (stackSlot.isEmpty() && isRegularInventorySlot(slot.id, allowOffhand))
            {
                return slot.id;
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
    public static int findSlotWithItem(ScreenHandler container, ItemStack stackReference, boolean reverse)
    {
        final int startSlot = reverse ? container.slots.size() - 1 : 0;
        final int endSlot = reverse ? -1 : container.slots.size();
        final int increment = reverse ? -1 : 1;
        final boolean isPlayerInv = container instanceof PlayerScreenHandler;

        for (int slotNum = startSlot; slotNum != endSlot; slotNum += increment)
        {
            Slot slot = container.slots.get(slotNum);

            if ((isPlayerInv == false || isRegularInventorySlot(slot.id, false)) &&
                areStacksEqualIgnoreDurability(slot.getStack(), stackReference))
            {
                return slot.id;
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
    public static boolean swapItemToMainHand(ItemStack stackReference, MinecraftClient mc)
    {
        PlayerEntity player = mc.player;
        boolean isCreative = player.isCreative();

        // Already holding the requested item
        if (areStacksEqual(stackReference, player.getMainHandStack()))
        {
            return false;
        }

        if (isCreative)
        {
            player.getInventory().addPickBlock(stackReference);
            mc.interactionManager.clickCreativeStack(player.getMainHandStack(), 36 + player.getInventory().selectedSlot); // sendSlotPacket
            return true;
        }
        else
        {
            int slot = findSlotWithItem(player.playerScreenHandler, stackReference, true);

            if (slot != -1)
            {
                int currentHotbarSlot = player.getInventory().selectedSlot;
                mc.interactionManager.clickSlot(player.playerScreenHandler.syncId, slot, currentHotbarSlot, SlotActionType.SWAP, mc.player);
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the inventory at the given position, if any.
     * Combines chest inventories into double chest inventories when applicable.
     * @param world
     * @param pos
     * @return
     */
    @Nullable
    public static Inventory getInventory(World world, BlockPos pos)
    {
        @SuppressWarnings("deprecation")
        boolean isLoaded = world.isChunkLoaded(pos);

        if (isLoaded == false)
        {
            return null;
        }

        // The method in World now checks that the caller is from the same thread...
        BlockEntity te = world.getWorldChunk(pos).getBlockEntity(pos);

        if (te instanceof Inventory inv)
        {
            BlockState state = world.getBlockState(pos);

            if (state.getBlock() instanceof ChestBlock && te instanceof ChestBlockEntity)
            {
                ChestType type = state.get(ChestBlock.CHEST_TYPE);

                if (type != ChestType.SINGLE)
                {
                    BlockPos posAdj = pos.offset(ChestBlock.getFacing(state));
                    @SuppressWarnings("deprecation")
                    boolean isLoadedAdj = world.isChunkLoaded(posAdj);

                    if (isLoadedAdj)
                    {
                        BlockState stateAdj = world.getBlockState(posAdj);
                        // The method in World now checks that the caller is from the same thread...
                        BlockEntity te2 = world.getWorldChunk(posAdj).getBlockEntity(posAdj);

                        if (stateAdj.getBlock() == state.getBlock() &&
                            te2 instanceof ChestBlockEntity &&
                            stateAdj.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE &&
                            stateAdj.get(ChestBlock.FACING) == state.get(ChestBlock.FACING))
                        {
                            Inventory invRight = type == ChestType.RIGHT ?             inv : (Inventory) te2;
                            Inventory invLeft  = type == ChestType.RIGHT ? (Inventory) te2 :             inv;
                            inv = new DoubleInventory(invRight, invLeft);
                        }
                    }
                }
            }

            return inv;
        }

        return null;
    }

    /**
     * Checks if the given Shulker Box (or other storage item with the
     * same NBT data structure) currently contains any items.
     * @param stackShulkerBox
     * @return
     */
    public static boolean shulkerBoxHasItems(ItemStack stackShulkerBox)
    {
        NbtCompound nbt = stackShulkerBox.getNbt();

        if (nbt != null && nbt.contains("BlockEntityTag", Constants.NBT.TAG_COMPOUND))
        {
            NbtCompound tag = nbt.getCompound("BlockEntityTag");

            if (tag.contains("Items", Constants.NBT.TAG_LIST))
            {
                NbtList tagList = tag.getList("Items", Constants.NBT.TAG_COMPOUND);
                return tagList.size() > 0;
            }
        }

        return false;
    }

    /**
     * Returns the list of items currently stored in the given Shulker Box
     * (or other storage item with the same NBT data structure).
     * Does not keep empty slots.
     * @param stackIn The item holding the inventory contents
     * @return
     */
    public static DefaultedList<ItemStack> getStoredItems(ItemStack stackIn)
    {
        NbtCompound nbt = stackIn.getNbt();

        if (nbt != null && nbt.contains("BlockEntityTag", Constants.NBT.TAG_COMPOUND))
        {
            NbtCompound tagBlockEntity = nbt.getCompound("BlockEntityTag");

            if (tagBlockEntity.contains("Items", Constants.NBT.TAG_LIST))
            {
                DefaultedList<ItemStack> items = DefaultedList.of();
                NbtList tagList = tagBlockEntity.getList("Items", Constants.NBT.TAG_COMPOUND);
                final int count = tagList.size();

                for (int i = 0; i < count; ++i)
                {
                    ItemStack stack = ItemStack.fromNbt(tagList.getCompound(i));

                    if (stack.isEmpty() == false)
                    {
                        items.add(stack);
                    }
                }

                return items;
            }
        }

        return DefaultedList.of();
    }

    /**
     * Returns the list of items currently stored in the given Shulker Box
     * (or other storage item with the same NBT data structure).
     * Preserves empty slots.
     * @param stackIn The item holding the inventory contents
     * @param slotCount the maximum number of slots, and thus also the size of the list to create
     * @return
     */
    public static DefaultedList<ItemStack> getStoredItems(ItemStack stackIn, int slotCount)
    {
        NbtCompound nbt = stackIn.getNbt();

        if (nbt != null && nbt.contains("BlockEntityTag", Constants.NBT.TAG_COMPOUND))
        {
            NbtCompound tagBlockEntity = nbt.getCompound("BlockEntityTag");

            if (tagBlockEntity.contains("Items", Constants.NBT.TAG_LIST))
            {
                NbtList tagList = tagBlockEntity.getList("Items", Constants.NBT.TAG_COMPOUND);
                final int count = tagList.size();
                int maxSlot = -1;

                if (slotCount <= 0)
                {
                    for (int i = 0; i < count; ++i)
                    {
                        NbtCompound tag = tagList.getCompound(i);
                        int slot = tag.getByte("Slot");

                        if (slot > maxSlot)
                        {
                            maxSlot = slot;
                        }
                    }

                    slotCount = maxSlot + 1;
                }

                DefaultedList<ItemStack> items = DefaultedList.ofSize(slotCount, ItemStack.EMPTY);

                for (int i = 0; i < count; ++i)
                {
                    NbtCompound tag = tagList.getCompound(i);
                    ItemStack stack = ItemStack.fromNbt(tag);
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
        DefaultedList<ItemStack> items = getStoredItems(stackShulkerBox);

        for (ItemStack stack : items)
        {
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
     * @param inv
     * @return
     */
    public static Object2IntOpenHashMap<ItemType> getInventoryItemCounts(Inventory inv)
    {
        Object2IntOpenHashMap<ItemType> map = new Object2IntOpenHashMap<>();
        final int slots = inv.size();

        for (int slot = 0; slot < slots; ++slot)
        {
            ItemStack stack = inv.getStack(slot);

            if (stack.isEmpty() == false)
            {
                map.addTo(new ItemType(stack, false, true), stack.getCount());

                if (stack.getItem() instanceof BlockItem &&
                    ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock &&
                    shulkerBoxHasItems(stack))
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
    public static Inventory getAsInventory(DefaultedList<ItemStack> items)
    {
        SimpleInventory inv = new SimpleInventory(items.size());

        for (int slot = 0; slot < items.size(); ++slot)
        {
            inv.setStack(slot, items.get(slot));
        }

        return inv;
    }
}
