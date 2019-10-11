package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.class_4587;
import net.minecraft.client.render.GameRenderer;
import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer
{
    @Inject(method = "renderWorld(FJLnet/minecraft/class_4587;)V", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z"
        ))
    private void onRenderWorldLast(float partialTicks, long finishTimeNano, class_4587 matrixQueue, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldLast(matrixQueue, partialTicks);
    }
}
