package malilib.mixin.input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.screen.Screen;

import malilib.input.InputDispatcherImpl;
import malilib.registry.Registry;

@Mixin(Screen.class)
public abstract class ScreenMixin extends GuiElement
{
    @Inject(method = "handleInputs", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;handleKeyboard()V"))
    private void onKeyboardInputGui(CallbackInfo ci)
    {
        InputDispatcherImpl handler = (InputDispatcherImpl) Registry.INPUT_DISPATCHER;

        if (handler.onKeyInput())
        {
            // Use up the rest of the events
            while (Keyboard.next())
            {
                if (handler.onKeyInput() == false)
                {
                    ((Screen) (Object) this).handleKeyboard();
                }
            }

            ci.cancel();
        }
    }

    @Inject(method = "handleInputs", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;handleMouse()V"))
    private void onMouseInputGui(CallbackInfo ci)
    {
        InputDispatcherImpl handler = (InputDispatcherImpl) Registry.INPUT_DISPATCHER;

        if (handler.onMouseInput())
        {
            // Use up the rest of the events
            while (Mouse.next())
            {
                if (handler.onMouseInput() == false)
                {
                    ((Screen) (Object) this).handleMouse();
                }
            }

            // Use up the rest of the events
            // The vanilla keyboard handling that comes after the mouse handling will get skipped when this method cancels,
            // and these events would then leak to the non-GUI handling code in Minecraft#runTick())
            while (Keyboard.next())
            {
                if (handler.onKeyInput() == false)
                {
                    ((Screen) (Object) this).handleKeyboard();
                }
            }

            ci.cancel();
        }
    }
}
