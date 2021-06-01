package fi.dy.masa.malilib.interfaces;

import java.util.function.Supplier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Matrix4f;

public interface IRenderer
{
    /**
     * Called after the vanilla overlays have been rendered
     */
    default void onRenderGameOverlayPost(MatrixStack matrixStack) {}

    /**
     * Called after vanilla world rendering
     */
    default void onRenderWorldLast(MatrixStack matrixStack, Matrix4f projMatrix) {}

    /**
     * Called after the tooltip text of an item has been rendered
     */
    default void onRenderTooltipLast(ItemStack stack, int x, int y) {}

    /**
     * Returns a supplier for the profiler section name that should be used for this renderer
     */
    default Supplier<String> getProfilerSectionSupplier()
    {
        return () -> this.getClass().getName();
    }
}
