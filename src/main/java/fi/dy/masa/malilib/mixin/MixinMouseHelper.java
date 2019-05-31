package fi.dy.masa.malilib.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.event.InputEventHandler;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;

@Mixin(MouseHelper.class)
public abstract class MixinMouseHelper
{
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "cursorPosCallback",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHelper;ignoreFirstMove:Z", ordinal = 0))
    private void onMouseMove(long handle, double xpos, double ypos, CallbackInfo ci)
    {
        MainWindow window = this.minecraft.mainWindow;
        int mouseX = (int) (((MouseHelper) (Object) this).getMouseX() * (double) window.getScaledWidth() / (double) window.getWidth());
        int mouseY = (int) (((MouseHelper) (Object) this).getMouseY() * (double) window.getScaledHeight() / (double) window.getHeight());

        ((InputEventHandler) InputEventHandler.getInputManager()).onMouseMove(mouseX, mouseY);
    }

    @Inject(method = "scrollCallback", cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", ordinal = 0))
    private void onMouseScroll(long handle, double xoffset, double yoffset, CallbackInfo ci)
    {
        MainWindow window = this.minecraft.mainWindow;
        int mouseX = (int) (((MouseHelper) (Object) this).getMouseX() * (double) window.getScaledWidth() / (double) window.getWidth());
        int mouseY = (int) (((MouseHelper) (Object) this).getMouseY() * (double) window.getScaledHeight() / (double) window.getHeight());
        double amount = yoffset * this.minecraft.gameSettings.mouseWheelSensitivity;

        if (((InputEventHandler) InputEventHandler.getInputManager()).onMouseScroll(mouseX, mouseY, amount))
        {
            ci.cancel();
        }
    }

    @Inject(method = "mouseButtonCallback", cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;IS_RUNNING_ON_MAC:Z", ordinal = 0))
    private void onMouseClick(long handle, final int button, final int action, int mods, CallbackInfo ci)
    {
        MainWindow window = this.minecraft.mainWindow;
        int mouseX = (int) (((MouseHelper) (Object) this).getMouseX() * (double) window.getScaledWidth() / (double) window.getWidth());
        int mouseY = (int) (((MouseHelper) (Object) this).getMouseY() * (double) window.getScaledHeight() / (double) window.getHeight());
        final boolean keyState = action == GLFW.GLFW_PRESS;

        if (((InputEventHandler) InputEventHandler.getInputManager()).onMouseClick(mouseX, mouseY, button, keyState))
        {
            ci.cancel();
        }
    }
}
