package malilib.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.Gui;

import malilib.event.dispatch.RenderEventDispatcherImpl;
import malilib.registry.Registry;

@Mixin(Gui.class)
public abstract class InGameHudMixin
{
    @Inject(method = "render", at = @At("RETURN"))
    private void onGameOverlayPost(PoseStack matrixStack, float partialTicks, CallbackInfo ci)
    {
        ((RenderEventDispatcherImpl) Registry.RENDER_EVENT_DISPATCHER).onRenderGameOverlayPost(matrixStack);
    }
}
