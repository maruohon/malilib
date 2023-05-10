package fi.dy.masa.malilib.mixin;

import fi.dy.masa.malilib.event.RenderEventHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public class MixinDrawContext {
    @Inject(method = "drawItemTooltip", at = @At(value = "RETURN"))
    private void onRenderTooltip(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipLast((DrawContext) (Object) this, stack, x, y);
    }
}
