package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(net.minecraft.client.render.WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Shadow @Final private net.minecraft.client.MinecraftClient client;

    @Inject(method = "render",
            at = @At(value = "INVOKE_STRING", args = "ldc=weather",
                     target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"))
    private void onRenderWorldLast(
            net.minecraft.client.util.math.MatrixStack matrices,
            float tickDelta, long limitTime, boolean renderBlockOutline,
            net.minecraft.client.render.Camera camera,
            net.minecraft.client.render.GameRenderer gameRenderer,
            net.minecraft.client.render.LightmapTextureManager lightmapTextureManager,
            net.minecraft.util.math.Matrix4f matrix4f,
            CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldLast(matrices, this.client, tickDelta);
    }
}
