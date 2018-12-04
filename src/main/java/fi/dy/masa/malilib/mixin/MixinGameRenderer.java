package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.event.RenderEventHandler;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer
{
    @Inject(method = "updateCameraAndRender(FJ)V", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/GameRenderer;renderHand:Z"
        ))
    private void onRenderWorldLast(float partialTicks, long finishTimeNano, CallbackInfo ci)
    {
        RenderEventHandler.getInstance().onRenderWorldLast(partialTicks);
    }

    /*
    @Inject(method = "updateCameraAndRender(FJZ)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V",
            shift = Shift.AFTER))
    private void onRenderGameOverlayPost(float partialTicks, long nanoTime, boolean renderWorldIn, CallbackInfo ci)
    {
        RenderEventHandler.getInstance().onRenderGameOverlayPost(partialTicks);
    }
    */
}
