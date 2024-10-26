package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud
{
    @Inject(method = "render", at = @At("RETURN"))
    private void onGameOverlayPost(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderGameOverlayPost(context);
    }
}
