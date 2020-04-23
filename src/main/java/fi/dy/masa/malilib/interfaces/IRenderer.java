package fi.dy.masa.malilib.interfaces;

import java.util.function.Supplier;

public interface IRenderer
{
    /**
     * Called after the vanilla overlays have been rendered
     * @param partialTicks
     */
    default void onRenderGameOverlayPost(float partialTicks, net.minecraft.client.util.math.MatrixStack matrixStack) {}

    /**
     * Called after vanilla world rendering
     * @param partialTicks
     */
    default void onRenderWorldLast(float partialTicks, net.minecraft.client.util.math.MatrixStack matrixStack) {}

    /**
     * Called after the tooltip text of an item has been rendered
     */
    default void onRenderTooltipLast(net.minecraft.item.ItemStack stack, int x, int y) {}

    /**
     * Returns a supplier for the profiler section name that should be used for this renderer
     * @return
     */
    default Supplier<String> getProfilerSectionSupplier()
    {
        return () -> this.getClass().getName();
    }
}
