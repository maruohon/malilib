package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.EntityRenderer;
import fi.dy.masa.malilib.event.dispatch.RenderEventDispatcher;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer
{
    @Inject(method = "renderWorldPass(IFJ)V", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand:Z"
        ))
    private void onRenderWorldLast(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci)
    {
        ((RenderEventDispatcher) RenderEventDispatcher.INSTANCE).onRenderWorldPost(partialTicks);
    }

    @Inject(method = "updateCameraAndRender(FJ)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V",
            shift = Shift.AFTER))
    private void onRenderGameOverlayPost(float partialTicks, long nanoTime, CallbackInfo ci)
    {
        ((RenderEventDispatcher) RenderEventDispatcher.INSTANCE).onRenderGameOverlayPost(partialTicks);
    }
}
