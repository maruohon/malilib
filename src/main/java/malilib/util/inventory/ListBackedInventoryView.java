package malilib.util.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import malilib.util.game.wrap.ItemWrap;

public class ListBackedInventoryView implements InventoryView
{
    protected final List<ItemStack> items;

    public ListBackedInventoryView()
    {
        this(1);
    }

    public ListBackedInventoryView(int initialSize)
    {
        initialSize = Math.max(initialSize, 1);
        this.items = new ArrayList<>(initialSize);
    }

    public ListBackedInventoryView(List<ItemStack> itemsIn)
    {
        this.items = new ArrayList<>(itemsIn);
    }

    @Override
    public int getSize()
    {
        return this.items.size();
    }

    @Override
    public ItemStack getStack(int slot)
    {
        return slot >= 0 && slot < this.items.size() ? this.items.get(slot) : ItemWrap.EMPTY_STACK;
    }

    public void addStack(ItemStack stack)
    {
        this.items.add(stack);
    }

    public void setStackInSlot(int slot, ItemStack stack)
    {
        int currentSize = this.items.size();

        if (slot >= currentSize)
        {
            int toAdd = slot - currentSize + 1;

            for (int i = 0; i < toAdd; ++i)
            {
                this.items.add(ItemWrap.EMPTY_STACK);
            }
        }

        if (slot >= 0 && slot < this.items.size())
        {
            this.items.set(slot, stack);
        }
    }
}
