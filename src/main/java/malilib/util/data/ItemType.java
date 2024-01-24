package malilib.util.data;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import malilib.util.game.wrap.ItemWrap;

/**
 * A wrapper around ItemStack, that implements hashCode() and equals().
 * Whether the NBT data and damage of damageable items are considered by those methods,
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
        this.stack = ItemWrap.isEmpty(stack) ? ItemWrap.EMPTY_STACK : (copy ? stack.copy() : stack);
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

        if (this.stack != null)
        {
            result = prime * result + this.stack.getItem().hashCode();
        }

        if (this.ignoreDamage == false || this.stack.isDamageable() == false)
        {
            result = prime * result + this.stack.getMetadata();
        }

        /*
        if (this.checkNbt())
        {
            NBTTagCompound tag = ItemWrap.getTag(this.stack);
            result = prime * result + (tag != null ? tag.hashCode() : 0);
        }
        */

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

        if (ItemWrap.isEmpty(this.stack) || ItemWrap.isEmpty(other.stack))
        {
            return ItemWrap.isEmpty(this.stack) == ItemWrap.isEmpty(other.stack);
        }
        else
        {
            if (this.stack.getItem() != other.stack.getItem())
            {
                return false;
            }

            if ((this.ignoreDamage == false || this.stack.isDamageable() == false) &&
                this.stack.getMetadata() != other.stack.getMetadata())
            {
                return false;
            }

            return true;//this.checkNbt() == false || ItemStack.areItemStackTagsEqual(this.stack, other.stack);
        }
    }

    @Override
    public String toString()
    {
        /*
        if (this.checkNbt())
        {
            String id = RegistryUtils.getItemIdStr(this.stack.getItem());
            NBTTagCompound tag = ItemWrap.getTag(this.stack);
            return (id != null ? id : "<null>") + "@" + this.stack.getMetadata() + (tag != null ? tag.toString() : "");
        }
        else
        {
            String id = RegistryUtils.getItemIdStr(this.stack.getItem());
            return (id != null ? id : "<null>") + "@" + this.stack.getMetadata();
        }
        */
        Item item = this.stack.getItem();;
        return this.stack != null ? String.format("%s [id: %d]", item.getTranslationKey(), item.id)  : "<null>";
    }
}
