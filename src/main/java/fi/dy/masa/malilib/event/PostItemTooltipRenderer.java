package fi.dy.masa.malilib.event;

import net.minecraft.item.ItemStack;

public interface PostItemTooltipRenderer
{
    /**
     * Called after the tooltip text of an item has been rendered
     * <br><br>
     * The classes implementing this method should be registered to {@link fi.dy.masa.malilib.event.dispatch.RenderEventDispatcherImpl}
     */
    void onPostRenderItemTooltip(ItemStack stack, int x, int y);
}
