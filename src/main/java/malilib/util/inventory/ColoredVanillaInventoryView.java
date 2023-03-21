package malilib.util.inventory;

import net.minecraft.world.Container;

public class ColoredVanillaInventoryView extends VanillaInventoryView
{
    protected final int backgroundTintColor;

    public ColoredVanillaInventoryView(Container inv, int backgroundTintColor)
    {
        super(inv);

        this.backgroundTintColor = backgroundTintColor;
    }

    public int getBackgroundTintColor()
    {
        return this.backgroundTintColor;
    }
}
