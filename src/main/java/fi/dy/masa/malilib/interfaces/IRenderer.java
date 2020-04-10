package fi.dy.masa.malilib.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;

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
    default void onRenderWorldLast(float partialTicks, MatrixStack matrixStack) {}

    /**
     * Called after the tooltip text of an item has been rendered
     */
    default void onRenderTooltipLast(net.minecraft.item.ItemStack stack, int x, int y) {}
}
