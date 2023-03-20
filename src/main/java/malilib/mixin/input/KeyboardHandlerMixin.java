package malilib.mixin.input;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.KeyboardHandler;

import malilib.input.InputDispatcherImpl;
import malilib.registry.Registry;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin// implements F3KeyStateSetter
{
    @Shadow private boolean handledDebugKey;

    //@Override
    public void setF3KeyState(boolean value)
    {
        this.handledDebugKey = value;
    }

    @Inject(method = "keyPress", cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/KeyboardHandler;debugCrashKeyTime:J", ordinal = 0))
    private void onKeyboardInput(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci)
    {
        if (((InputDispatcherImpl) Registry.INPUT_DISPATCHER).onKeyInput(key, scanCode, modifiers, action != 0))
        {
            ci.cancel();
        }
    }
}
