package fi.dy.masa.malilib.interfaces;

import net.minecraft.item.ItemStack;

public interface IRenderer
{
    /**
     * Called after the vanilla overlays have been rendered
     * @param partialTicks
     */
    default void onRenderGameOverlayPost(float partialTicks) {}

    /**
     * Called after vanilla world rendering
     * @param partialTicks
     */
    default void onRenderWorldLast(float partialTicks) {}

    /**
     * Called after the tooltip text of an item has been rendered
     */
    default void onRenderTooltipLast(ItemStack stack, int x, int y) {}
}
