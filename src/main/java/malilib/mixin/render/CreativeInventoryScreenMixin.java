package malilib.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.ItemStack;

import malilib.event.dispatch.RenderEventDispatcherImpl;
import malilib.registry.Registry;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin
{
    @Inject(method = "renderTooltip", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;Ljava/util/Optional;II)V"))
    private void onRenderTooltip(PoseStack matrixStack, ItemStack stack, int x, int y, CallbackInfo ci)
    {
        ((RenderEventDispatcherImpl) Registry.RENDER_EVENT_DISPATCHER).onRenderTooltipPost(stack, x, y, matrixStack);
    }
}
