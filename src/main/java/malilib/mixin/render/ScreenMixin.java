package malilib.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

import malilib.event.dispatch.RenderEventDispatcherImpl;
import malilib.registry.Registry;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractContainerEventHandler
{
    @Shadow @Nullable protected Minecraft client;

    @Inject(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V",
            at = @At("RETURN"))
    private void onRenderTooltip(PoseStack matrixStack, ItemStack stack, int x, int y, CallbackInfo ci)
    {
        ((RenderEventDispatcherImpl) Registry.RENDER_EVENT_DISPATCHER).onRenderTooltipPost(stack, x, y, matrixStack);
    }
}