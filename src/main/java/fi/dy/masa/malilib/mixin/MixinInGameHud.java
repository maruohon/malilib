package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.event.RenderEventHandler;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(net.minecraft.client.gui.hud.InGameHud.class)
public abstract class MixinInGameHud
{
    @Shadow @Final private net.minecraft.client.MinecraftClient client;

    @Inject(method = "render", at = @At("RETURN"))
    private void onGameOverlayPost(MatrixStack matrixStack, float partialTicks, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderGameOverlayPost(this.client, partialTicks, matrixStack);
    }
}
