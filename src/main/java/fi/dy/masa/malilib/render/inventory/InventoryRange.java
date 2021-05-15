package fi.dy.masa.malilib.render.inventory;

import fi.dy.masa.malilib.util.Int2IntFunction;
import fi.dy.masa.malilib.util.data.Vec2i;

public class InventoryRange
{
    public final Int2IntFunction slotsPerRowFunction;
    public final Vec2i startPos;
    public final boolean renderSlotBackgrounds;
    public final int startSlot;
    public final int slotCount;

    public InventoryRange(int startSlot, int slotCount, boolean renderSlotBackgrounds,
                          Vec2i startPos, Int2IntFunction slotsPerRowFunction)
    {
        this.startSlot = startSlot;
        this.slotCount = slotCount;
        this.renderSlotBackgrounds = renderSlotBackgrounds;
        this.startPos = startPos;
        this.slotsPerRowFunction = slotsPerRowFunction;
    }

    public static InventoryRange of(int startSlot, int slotCount, boolean renderSlots,
                                    Vec2i startPos, Int2IntFunction slotsPerRowFunction)
    {
        return new InventoryRange(startSlot, slotCount, renderSlots, startPos, slotsPerRowFunction);
    }
}
