package malilib.mixin.input;

import java.io.IOException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import malilib.input.InputDispatcherImpl;
import malilib.registry.Registry;

@Mixin(GuiScreen.class)
public abstract class GuiScreenMixin extends Gui
{
    @Shadow protected Minecraft mc;

    @Inject(method = "handleInput", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleKeyboardInput()V"))
    private void onKeyboardInputGui(CallbackInfo ci) throws IOException
    {
        InputDispatcherImpl handler = (InputDispatcherImpl) Registry.INPUT_DISPATCHER;

        if (handler.onKeyInput())
        {
            // Use up the rest of the events
            while (Keyboard.next())
            {
                if (handler.onKeyInput() == false)
                {
                    ((GuiScreen) (Object) this).handleKeyboardInput();
                }
            }

            ci.cancel();
        }
    }

    @Inject(method = "handleInput", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleMouseInput()V"))
    private void onMouseInputGui(CallbackInfo ci) throws IOException
    {
        InputDispatcherImpl handler = (InputDispatcherImpl) Registry.INPUT_DISPATCHER;

        if (handler.onMouseInput())
        {
            // Use up the rest of the events
            while (Mouse.next())
            {
                if (handler.onMouseInput() == false)
                {
                    ((GuiScreen) (Object) this).handleMouseInput();
                }
            }

            // Use up the rest of the events
            // The vanilla keyboard handling that comes after the mouse handling will get skipped when this method cancels,
            // and these events would then leak to the non-GUI handling code in Minecraft#runTick())
            while (Keyboard.next())
            {
                if (handler.onKeyInput() == false)
                {
                    ((GuiScreen) (Object) this).handleKeyboardInput();
                }
            }

            ci.cancel();
        }
    }
}
