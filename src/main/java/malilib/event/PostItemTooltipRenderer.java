package malilib.event;

import malilib.util.ProfilerSectionSupplierSupplier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public interface PostItemTooltipRenderer extends ProfilerSectionSupplierSupplier
{
    /**
     * Called after the tooltip text of an item has been rendered
     * <br><br>
     * The classes implementing this method should be registered to {@link malilib.event.dispatch.RenderEventDispatcherImpl}
     */
    void onPostRenderItemTooltip(ItemStack stack, int x, int y, MatrixStack matrices);
}
