package fi.dy.masa.malilib.mixin;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;

import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render",
            at = @At(value = "INVOKE", ordinal = 1,
                     target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V"))
    private void onRenderWorldLastNormal(
            float tickDelta, long limitTime, boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f projMatrix,
            Matrix4f ignore,
            CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldLast(new MatrixStack(), projMatrix, this.client);
    }

    @Inject(method = "render",
            slice = @Slice(from = @At(value = "FIELD", ordinal = 1, // start from the endDrawing() call
                                      target = "Lnet/minecraft/client/render/RenderPhase;WEATHER_TARGET:Lnet/minecraft/client/render/RenderPhase$Target;"),
                            to = @At(value = "INVOKE", ordinal = 1, // end at the second renderWeather call
                                     target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V")),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gl/PostEffectProcessor;render(F)V"))
    private void onRenderWorldLastFabulous(
            float tickDelta, long limitTime, boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f projMatrix,
            Matrix4f unused,
            CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldLast(new MatrixStack(), projMatrix, this.client);
    }
}
