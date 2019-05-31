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
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldLast(partialTicks);
    }
}
