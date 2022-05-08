package fi.dy.masa.malilib.util.data;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * A wrapper around ItemStack, that implements hashCode() and equals().
 * Whether or not the NBT data and damage of damageable items are considered by those methods,
 * depends on the ignoreDamage and checkNbt arguments to the constructor.
 */
public class ItemType
{
    protected final ItemStack stack;
    protected final boolean checkNbt;
    protected final boolean ignoreDamage;
    protected final int hashCode;

    public ItemType(ItemStack stack)
    {
        this(stack, true, false, true);
    }

    public ItemType(ItemStack stack, boolean copy, boolean checkNbt)
    {
        this(stack, copy, false, checkNbt);
    }

    public ItemType(ItemStack stack, boolean copy, boolean ignoreDamage, boolean checkNbt)
    {
        this.stack = stack.isEmpty() ? ItemStack.EMPTY : (copy ? stack.copy() : stack);
        this.ignoreDamage = ignoreDamage;
        this.checkNbt = checkNbt;
        this.hashCode = this.calculateHashCode();
    }

    public ItemStack getStack()
    {
        return this.stack;
    }

    public boolean getIgnoreDamage()
    {
        return this.ignoreDamage;
    }

    public boolean checkNbt()
    {
        return this.checkNbt;
    }

    @Override
    public int hashCode()
    {
        return this.hashCode;
    }

    protected int calculateHashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.stack.getItem().hashCode();

        if (this.checkNbt())
        {
            result = prime * result + (this.stack.getNbt() != null ? this.stack.getNbt().hashCode() : 0);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;

        ItemType other = (ItemType) obj;

        if (this.stack.isEmpty() || other.stack.isEmpty())
        {
            return this.stack.isEmpty() == other.stack.isEmpty();
        }
        else
        {
            if (this.stack.getItem() != other.stack.getItem())
            {
                return false;
            }

            return this.checkNbt() == false || ItemStack.areNbtEqual(this.stack, other.stack);
        }
    }

    @Override
    public String toString()
    {
        Identifier rl = Registry.ITEM.getId(this.stack.getItem());

        if (this.checkNbt())
        {
            return rl + this.stack.getNbt().toString();
        }

        return rl.toString();
    }
}
