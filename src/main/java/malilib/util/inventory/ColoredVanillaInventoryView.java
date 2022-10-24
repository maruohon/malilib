package malilib.util.inventory;

import net.minecraft.inventory.IInventory;

public class ColoredVanillaInventoryView extends VanillaInventoryView
{
    protected final int backgroundTintColor;

    public ColoredVanillaInventoryView(IInventory inv, int backgroundTintColor)
    {
        super(inv);

        this.backgroundTintColor = backgroundTintColor;
    }

    public int getBackgroundTintColor()
    {
        return this.backgroundTintColor;
    }
}
