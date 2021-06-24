package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Keyboard;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.util.IF3KeyStateSetter;

@Mixin(Keyboard.class)
public abstract class MixinKeyboard implements IF3KeyStateSetter
{
    @Shadow private boolean switchF3State;

    @Override
    public void setF3KeyState(boolean value)
    {
        this.switchF3State = value;
    }

    @Inject(method = "onKey", cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Keyboard;debugCrashStartTime:J", ordinal = 0))
    private void onKeyboardInput(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci)
    {
        boolean allowRepeat = MaLiLibConfigs.Generic.KEY_EVENT_ALLOW_REPEAT.getBooleanValue();

        if (allowRepeat == false && action == 2)
        {
            ci.cancel();
            return;
        }

        boolean press = action == 1 || (allowRepeat && action == 2);

        if (((InputEventHandler) InputEventHandler.getInputManager()).onKeyInput(key, scanCode, modifiers, press))
        {
            ci.cancel();
        }
    }
}
