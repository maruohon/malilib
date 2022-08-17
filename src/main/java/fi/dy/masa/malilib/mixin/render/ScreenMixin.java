package fi.dy.masa.malilib.mixin.render;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import fi.dy.masa.malilib.event.dispatch.RenderEventDispatcherImpl;
import fi.dy.masa.malilib.registry.Registry;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractParentElement
{
    @Shadow @Nullable protected MinecraftClient client;

    @Inject(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V",
            at = @At("RETURN"))
    private void onRenderTooltip(MatrixStack matrixStack, ItemStack stack, int x, int y, CallbackInfo ci)
    {
        ((RenderEventDispatcherImpl) Registry.RENDER_EVENT_DISPATCHER).onRenderTooltipPost(stack, x, y, matrixStack);
    }
}