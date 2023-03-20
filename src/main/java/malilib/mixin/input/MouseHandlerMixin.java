package malilib.mixin.input;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MouseHandler;

import malilib.input.InputDispatcherImpl;
import malilib.registry.Registry;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin
{
    @Shadow private double accumulatedScroll;

    @Inject(method = "onMove",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHandler;ignoreFirstMove:Z", ordinal = 0))
    private void hookOnMouseMove(long handle, double xpos, double ypos, CallbackInfo ci)
    {
        ((InputDispatcherImpl) Registry.INPUT_DISPATCHER).onMouseMove(xpos, ypos);
    }

    @Inject(method = "onScroll", cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;", ordinal = 0))
    private void hookOnMouseScroll(long handle, double xOffset, double yOffset, CallbackInfo ci)
    {
        if (((InputDispatcherImpl) Registry.INPUT_DISPATCHER).onMouseScroll(xOffset, yOffset))
        {
            this.accumulatedScroll = 0.0;
            ci.cancel();
        }
    }

    @Inject(method = "onPress", cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ON_OSX:Z", ordinal = 0))
    private void hookOnMouseClick(long handle, final int button, final int action, int mods, CallbackInfo ci)
    {
        final boolean keyState = action == GLFW.GLFW_PRESS;

        if (((InputDispatcherImpl) Registry.INPUT_DISPATCHER).onMouseClick(button, keyState))
        {
            ci.cancel();
        }
    }
}
