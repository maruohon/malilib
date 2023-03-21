package malilib.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;

import malilib.event.dispatch.RenderEventDispatcherImpl;
import malilib.registry.Registry;

@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin
{
    @Shadow @Final private Minecraft client;

    @Inject(method = "render",
            at = @At(value = "INVOKE", ordinal = 1,
                    target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V"))
    private void onRenderWorldLastNormal(
            PoseStack matrices,
            float tickDelta, long limitTime, boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightTexture lightmapTextureManager,
            Matrix4f projMatrix,
            CallbackInfo ci)
    {
        ((RenderEventDispatcherImpl) Registry.RENDER_EVENT_DISPATCHER).onRenderWorldLast(matrices, projMatrix, this.client.getFrameTime());
    }

    @Inject(method = "render",
            slice = @Slice(from = @At(value = "FIELD", ordinal = 1, // start from the endDrawing() call
                    target = "Lnet/minecraft/client/render/RenderPhase;WEATHER_TARGET:Lnet/minecraft/client/render/RenderPhase$Target;"),
                    to = @At(value = "INVOKE", ordinal = 1, // end at the second renderWeather call
                            target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V")),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gl/ShaderEffect;render(F)V"))
    private void onRenderWorldLastFabulous(
            PoseStack matrices,
            float tickDelta, long limitTime, boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightTexture lightmapTextureManager,
            Matrix4f projMatrix,
            CallbackInfo ci)
    {
        ((RenderEventDispatcherImpl) Registry.RENDER_EVENT_DISPATCHER).onRenderWorldLast(matrices, projMatrix, this.client.getFrameTime());
    }
}
