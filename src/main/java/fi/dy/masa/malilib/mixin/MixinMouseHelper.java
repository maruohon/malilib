package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.event.InputEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;

@Mixin(MouseHelper.class)
public abstract class MixinMouseHelper
{
    @Inject(method = "cursorPosCallback",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MouseHelper;ignoreFirstMove:Z", ordinal = 0))
    private void onMouseMove(long handle, double xpos, double ypos, CallbackInfo ci)
    {
        Minecraft mc = Minecraft.getInstance();
        int mouseX = (int) ((MouseHelper) (Object) this).getMouseX();
        int mouseY = (int) ((MouseHelper) (Object) this).getMouseY();

        InputEventHandler.getInstance().onMouseMove(mouseX, mouseY, mc.currentScreen != null);
    }

    @Inject(method = "scrollCallback", cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen", ordinal = 0))
    private void onMouseScroll(long handle, double xoffset, double yoffset, CallbackInfo ci)
    {
        Minecraft mc = Minecraft.getInstance();
        int mouseX = (int) ((MouseHelper) (Object) this).getMouseX();
        int mouseY = (int) ((MouseHelper) (Object) this).getMouseY();
        double amount = yoffset * mc.gameSettings.mouseWheelSensitivity;

        if (InputEventHandler.getInstance().onMouseScroll(mouseX, mouseY, amount, mc.currentScreen != null))
        {
            ci.cancel();
        }
    }

    @Inject(method = "mouseButtonCallback", cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;IS_RUNNING_ON_MAC:Z", ordinal = 0))
    private void onMouseClick(long handle, int button, int action, int mods, CallbackInfo ci)
    {
        Minecraft mc = Minecraft.getInstance();
        int mouseX = (int) ((MouseHelper) (Object) this).getMouseX();
        int mouseY = (int) ((MouseHelper) (Object) this).getMouseY();

        if (InputEventHandler.getInstance().onMouseClick(mouseX, mouseY, button, action == 0, mc.currentScreen != null))
        {
            ci.cancel();
        }
    }
}
