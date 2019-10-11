package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.MatrixStack;
import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer
{
    @Inject(method = "renderWorld(FJLnet/minecraft/util/math/MatrixStack;)V", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z"
        ))
    private void onRenderWorldLast(float partialTicks, long finishTimeNano, MatrixStack matrixStack, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldLast(matrixStack, partialTicks);
    }
}
