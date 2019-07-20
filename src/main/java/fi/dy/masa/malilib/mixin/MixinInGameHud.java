package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.event.RenderEventHandler;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud
{
    @Inject(method = "render", at = @At("RETURN"))
    private void onGameOverlayPost(float partialTicks, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderGameOverlayPost(partialTicks);
    }
}
