package malilib.mixin.input;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;

import malilib.input.InputDispatcherImpl;
import malilib.input.KeyBindImpl;
import malilib.input.Keys;
import malilib.registry.Registry;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    @Shadow boolean hasTakenScreenshot;

    @ModifyExpressionValue(method = "tick", remap = false,
                           at = @At(value = "INVOKE",
                                    target = "Lorg/lwjgl/input/Keyboard;next()Z"))
    private boolean malilib_keyInputHandler(boolean original)
    {
        while (original)
        {
            // If not canceled (= false), return true to allow the vanilla code to handle the key
            if (((InputDispatcherImpl) Registry.INPUT_DISPATCHER).onKeyInput() == false)
            {
                return true;
            }

            // Key handled, and it was F2
            if (Keyboard.getEventKey() == Keys.KEY_F2)
            {
                this.hasTakenScreenshot = true;
            }

            // When the dispatcher says to cancel further handling, fetch the next key
            // to prevent vanilla from handling the just handled key
            original = Keyboard.next();
        }

        return original;
    }

    @ModifyExpressionValue(method = "tick", remap = false,
                           at = @At(value = "INVOKE",
                                    target = "Lorg/lwjgl/input/Mouse;next()Z"))
    private boolean malilib_mouseInputHandler(boolean original)
    {
        while (original)
        {
            // If not canceled (= false), return true to allow the vanilla code to handle the key
            if (((InputDispatcherImpl) Registry.INPUT_DISPATCHER).onMouseInput() == false)
            {
                return true;
            }

            // When the dispatcher says to cancel further handling, fetch the next key
            // to prevent vanilla from handling the just handled key
            original = Mouse.next();
        }

        return original;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onPostKeyboardInput(CallbackInfo ci)
    {
        KeyBindImpl.reCheckPressedKeys();
    }
}
