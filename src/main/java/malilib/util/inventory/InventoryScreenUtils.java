package malilib.util.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;

import malilib.mixin.access.GuiContainerMixin;

public class InventoryScreenUtils
{
    public static int getGuiPosX(GuiContainer gui)
    {
        return ((GuiContainerMixin) gui).getGuiPosX();
    }

    public static int getGuiPosY(GuiContainer gui)
    {
        return ((GuiContainerMixin) gui).getGuiPosY();
    }

    public static int getGuiSizeX(GuiContainer gui)
    {
        return ((GuiContainerMixin) gui).getGuiSizeX();
    }

    public static int getGuiSizeY(GuiContainer gui)
    {
        return ((GuiContainerMixin) gui).getGuiSizeY();
    }

    public static Slot getSlotUnderMouse(GuiContainer gui)
    {
        return ((GuiContainerMixin) gui).getHoveredSlot();
    }
}
