package fi.dy.masa.malilib.mixin.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import fi.dy.masa.malilib.event.dispatch.RenderEventDispatcherImpl;
import fi.dy.masa.malilib.registry.Registry;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin
{
    @Inject(method = "render", at = @At("RETURN"))
    private void onGameOverlayPost(MatrixStack matrixStack, float partialTicks, CallbackInfo ci)
    {
        ((RenderEventDispatcherImpl) Registry.RENDER_EVENT_DISPATCHER).onRenderGameOverlayPost(matrixStack);
    }
}
