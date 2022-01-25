package fi.dy.masa.malilib.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(CreativeInventoryScreen.class)
public abstract class MixinCreativeInventoryScreen
{
    @Inject(method = "renderTooltip", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;Ljava/util/Optional;II)V"))
    private void onRenderTooltip(MatrixStack matrixStack, ItemStack stack, int x, int y, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipLast(matrixStack, stack, x, y);
    }
}
