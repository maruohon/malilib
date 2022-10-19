package malilib.mixin.input;

import malilib.input.InputDispatcherImpl;
import malilib.registry.Registry;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public class MouseMixin
{
    @Shadow private double eventDeltaWheel;

    @Inject(method = "onCursorPos",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;hasResolutionChanged:Z", ordinal = 0))
    private void malilib_hookOnMouseMove(long handle, double xpos, double ypos, CallbackInfo ci)
    {
        ((InputDispatcherImpl) Registry.INPUT_DISPATCHER).onMouseMove(xpos, ypos);
    }

    @Inject(method = "onMouseScroll", cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 0))
    private void malilib_hookOnMouseScroll(long handle, double xOffset, double yOffset, CallbackInfo ci)
    {
        if (((InputDispatcherImpl) Registry.INPUT_DISPATCHER).onMouseScroll(xOffset, yOffset))
        {
            this.eventDeltaWheel = 0.0;
            ci.cancel();
        }
    }

    @Inject(method = "onMouseButton", cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;IS_SYSTEM_MAC:Z", ordinal = 0))
    private void malilib_hookOnMouseClick(long handle, final int button, final int action, int mods, CallbackInfo ci)
    {
        final boolean keyState = action == GLFW.GLFW_PRESS;

        if (((InputDispatcherImpl) Registry.INPUT_DISPATCHER).onMouseClick(button, keyState))
        {
            ci.cancel();
        }
    }
}
