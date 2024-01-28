package malilib.mixin.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;

import malilib.event.dispatch.RenderEventDispatcherImpl;
import malilib.registry.Registry;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
    @Shadow private Minecraft minecraft;

    @Inject(method = "renderWorld",
            slice = @Slice(from = @At(value = "INVOKE",
                                      target = "Lnet/minecraft/client/render/world/WorldRenderer;renderClouds(F)V")),
            at = @At(value = "INVOKE", shift = Shift.AFTER,
                     target = "Lnet/minecraft/client/render/GameRenderer;renderFog(IF)V"
        ))
    private void onRenderWorldLast(float tickDelta, long renderTimeLimit, CallbackInfo ci)
    {
        if (this.minecraft.world != null && this.minecraft.player != null)
        {
            ((RenderEventDispatcherImpl) Registry.RENDER_EVENT_DISPATCHER).onRenderWorldLast(tickDelta);
        }
    }

    @Inject(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GameGui;render(FZII)V",
            shift = Shift.AFTER))
    private void onRenderGameOverlayPost(float tickDelta, CallbackInfo ci)
    {
        if (this.minecraft.world != null && this.minecraft.player != null)
        {
            ((RenderEventDispatcherImpl) Registry.RENDER_EVENT_DISPATCHER).onRenderGameOverlayPost();
        }
    }

    @Inject(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/Screen;render(IIF)V",
            shift = Shift.AFTER))
    private void onRenderScreenPost(float tickDelta, CallbackInfo ci)
    {
        if (this.minecraft.world != null && this.minecraft.player != null)
        {
            ((RenderEventDispatcherImpl) Registry.RENDER_EVENT_DISPATCHER).onRenderScreenPost(tickDelta);
        }
    }
}
