package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.event.InputEventHandler;
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
        int mouseX = (int) ((MouseHelper) (Object) this).getMouseX();
        int mouseY = (int) ((MouseHelper) (Object) this).getMouseY();

        InputEventHandler.getInstance().onMouseMove(mouseX, mouseY, this.minecraft.currentScreen != null);
    }

    @Inject(method = "scrollCallback", cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen", ordinal = 0))
    private void onMouseScroll(long handle, double xoffset, double yoffset, CallbackInfo ci)
    {
        int mouseX = (int) ((MouseHelper) (Object) this).getMouseX();
        int mouseY = (int) ((MouseHelper) (Object) this).getMouseY();
        double amount = yoffset * this.minecraft.gameSettings.mouseWheelSensitivity;

        if (InputEventHandler.getInstance().onMouseScroll(mouseX, mouseY, amount, this.minecraft.currentScreen != null))
        {
            ci.cancel();
        }
    }

    @Inject(method = "mouseButtonCallback", cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;IS_RUNNING_ON_MAC:Z", ordinal = 0))
    private void onMouseClick(long handle, int button, int action, int mods, CallbackInfo ci)
    {
        int mouseX = (int) ((MouseHelper) (Object) this).getMouseX();
        int mouseY = (int) ((MouseHelper) (Object) this).getMouseY();

        if (InputEventHandler.getInstance().onMouseClick(mouseX, mouseY, button, action == 1, this.minecraft.currentScreen != null))
        {
            ci.cancel();
        }
    }
}
