package malilib.render.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntUnaryOperator;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.client.renderer.GlStateManager;

import malilib.gui.icon.Icon;
import malilib.gui.icon.PositionedIcon;
import malilib.render.RenderUtils;
import malilib.util.inventory.InventoryView;
import malilib.util.position.Vec2i;

public class InventoryRenderDefinition
{
    protected final ImmutableList<PositionedIcon> backgroundTextures;
    protected final ImmutableList<InventoryRange> inventoryRanges;
    protected final Int2ObjectOpenHashMap<PositionedIcon> emptySlotTextures;
    protected final Int2ObjectOpenHashMap<Vec2i> customSlotPositions;
    protected final IntUnaryOperator renderWidthFunction;
    protected final IntUnaryOperator renderHeightFunction;
    protected final IntUnaryOperator slotsPerRowFunction;
    protected final Vec2i slotOffset;
    protected final boolean hasCustomSlotPositions;
    protected final boolean hasEmptySlotTextures;
    protected final boolean hasInventoryRanges;

    public InventoryRenderDefinition(IntUnaryOperator slotsPerRowFunction,
                                     IntUnaryOperator renderWidthFunction,
                                     IntUnaryOperator renderHeightFunction,
                                     Vec2i slotOffset,
                                     List<PositionedIcon> backgroundTextures,
                                     Int2ObjectOpenHashMap<Vec2i> customSlotPositions,
                                     List<InventoryRange> inventoryRanges,
                                     Int2ObjectOpenHashMap<PositionedIcon> emptySlotTextures)
    {
        this.slotsPerRowFunction = slotsPerRowFunction;
        this.renderWidthFunction = renderWidthFunction;
        this.renderHeightFunction = renderHeightFunction;
        this.slotOffset = slotOffset;
        this.backgroundTextures = ImmutableList.copyOf(backgroundTextures);
        this.inventoryRanges = ImmutableList.copyOf(inventoryRanges);
        this.customSlotPositions = customSlotPositions;
        this.emptySlotTextures = emptySlotTextures;
        this.hasCustomSlotPositions = customSlotPositions.isEmpty() == false;
        this.hasEmptySlotTextures = emptySlotTextures.isEmpty() == false;
        this.hasInventoryRanges = inventoryRanges.isEmpty() == false;
    }

    public int getRenderWidth(InventoryView inv)
    {
        return this.renderWidthFunction.applyAsInt(inv.getSize());
    }

    public int getRenderHeight(InventoryView inv)
    {
        return this.renderHeightFunction.applyAsInt(inv.getSize());
    }

    public void renderInventory(int x, int y, float z, int backgroundTintColor, InventoryView inv)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.disableItemLighting();

        this.renderInventoryBackground(x, y, z, backgroundTintColor, inv);

        if (this.hasEmptySlotTextures)
        {
            InventoryRenderUtils.renderEmptySlotBackgrounds(x, y, z, backgroundTintColor, inv, this.emptySlotTextures);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, z + 1);

        if (this.hasInventoryRanges)
        {
            InventoryRenderUtils.renderInventoryRanges(x, y, 0, inv, this.inventoryRanges, backgroundTintColor);
        }

        if (this.hasCustomSlotPositions)
        {
            InventoryRenderUtils.renderCustomPositionedSlots(x, y, 100f, inv, this.customSlotPositions);
        }

        if (this.hasCustomSlotPositions == false && this.hasInventoryRanges == false)
        {
            int slotsPerRow = this.slotsPerRowFunction.applyAsInt(inv.getSize());
            InventoryRenderUtils.renderGenericInventoryItems(x, y, 100f, 0, -1, slotsPerRow, this.slotOffset, inv);
        }

        GlStateManager.popMatrix();

        RenderUtils.color(1f, 1f, 1f, 1f);
    }

    protected void renderInventoryBackground(int x, int y, float z, int backgroundTintColor, InventoryView inv)
    {
        if (this.backgroundTextures.isEmpty() == false)
        {
            RenderUtils.renderPositionedIcons(x, y, z, backgroundTintColor, this.backgroundTextures);
        }
        else
        {
            int slotsPerRow = this.slotsPerRowFunction.applyAsInt(inv.getSize());
            int slotCount = inv.getSize();
            InventoryRenderUtils.renderDynamicInventoryBackground(x, y, z, backgroundTintColor, slotsPerRow, slotCount);
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        protected final Int2ObjectOpenHashMap<Vec2i> customSlotPositions = new Int2ObjectOpenHashMap<>(1, 1f);
        protected final Int2ObjectOpenHashMap<PositionedIcon> emptySlotTextures = new Int2ObjectOpenHashMap<>(1, 1f);
        protected final List<PositionedIcon> backgroundTextures = new ArrayList<>();
        protected final List<InventoryRange> inventoryRanges = new ArrayList<>();
        protected Vec2i slotOffset = new Vec2i(8, 8);
        protected IntUnaryOperator renderWidthFunction = (slots) -> 176;
        protected IntUnaryOperator renderHeightFunction = (slots) -> 68;
        protected IntUnaryOperator slotsPerRowFunction = (slots) -> 9;

        public Builder withSlotsPerRow(int slotsPerRow)
        {
            this.slotsPerRowFunction = (slots) -> slotsPerRow;
            return this;
        }

        public Builder withSlotsPerRowFunction(IntUnaryOperator slotsPerRowFunction)
        {
            this.slotsPerRowFunction = slotsPerRowFunction;
            return this;
        }

        public Builder withRenderSize(int renderWidth, int renderHeight)
        {
            this.renderWidthFunction = (slots) -> renderWidth;
            this.renderHeightFunction = (slots) -> renderHeight;
            return this;
        }

        public Builder withRenderSizeFunctions(IntUnaryOperator renderWidthFunction,
                                               IntUnaryOperator renderHeightFunction)
        {
            this.renderWidthFunction = renderWidthFunction;
            this.renderHeightFunction = renderHeightFunction;
            return this;
        }

        public Builder withSlotOffset(int slotOffsetX, int slotOffsetY)
        {
            this.slotOffset = new Vec2i(slotOffsetX, slotOffsetY);
            return this;
        }

        public Builder withSlotPosition(int slotNumber, int slotX, int slotY)
        {
            this.customSlotPositions.put(slotNumber, new Vec2i(slotX, slotY));
            return this;
        }

        public Builder withBackgroundTexturePiece(int x, int y, Icon icon)
        {
            this.backgroundTextures.add(PositionedIcon.of(new Vec2i(x, y), icon));
            return this;
        }

        public Builder withInventoryRange(int startSlot, int slotCount,
                                          int slotsPerRow, int offsetX, int offsetY)
        {
            return this.withInventoryRange(startSlot, slotCount, slotsPerRow, offsetX, offsetY, false);
        }

        public Builder withInventoryRange(int startSlot, int slotCount,
                                          int slotsPerRow, int offsetX, int offsetY, boolean renderSlots)
        {
            this.inventoryRanges.add(InventoryRange.of(startSlot, slotCount, renderSlots,
                                                       new Vec2i(offsetX, offsetY), (slots) -> slotsPerRow));
            return this;
        }

        public Builder withInventoryRange(int startSlot, int slotCount,
                                          IntUnaryOperator slotsPerRowFunction,
                                          int offsetX, int offsetY)
        {
            return this.withInventoryRange(startSlot, slotCount, slotsPerRowFunction, offsetX, offsetY, false);
        }

        public Builder withInventoryRange(int startSlot, int slotCount,
                                          IntUnaryOperator slotsPerRowFunction,
                                          int offsetX, int offsetY, boolean renderSlots)
        {
            this.inventoryRanges.add(InventoryRange.of(startSlot, slotCount, renderSlots,
                                                       new Vec2i(offsetX, offsetY), slotsPerRowFunction));
            return this;
        }

        public Builder withEmptySlotBackgroundTexture(int slotNum, int x, int y, Icon icon)
        {
            this.emptySlotTextures.put(slotNum, PositionedIcon.of(new Vec2i(x, y), icon));
            return this;
        }

        public InventoryRenderDefinition build()
        {
            return new InventoryRenderDefinition(this.slotsPerRowFunction,
                                                 this.renderWidthFunction, this.renderHeightFunction,
                                                 this.slotOffset, this.backgroundTextures,
                                                 this.customSlotPositions,
                                                 this.inventoryRanges,
                                                 this.emptySlotTextures);
        }
    }
}
