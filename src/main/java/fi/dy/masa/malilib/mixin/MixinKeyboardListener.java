package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.util.IF3KeyStateSetter;
import net.minecraft.client.KeyboardListener;
import net.minecraft.client.Minecraft;

@Mixin(KeyboardListener.class)
public abstract class MixinKeyboardListener implements IF3KeyStateSetter
{
    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    private boolean actionKeyF3;

    @Override
    public void setF3KeyState(boolean value)
    {
        this.actionKeyF3 = value;
    }

    @Inject(method = "onKeyEvent", cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/KeyboardListener;debugCrashKeyPressTime:J", ordinal = 0))
    private void onKeyboardInput(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci)
    {
        if (InputEventHandler.getInstance().onKeyInput(key, scanCode, action != 0))
        {
            ci.cancel();
        }
    }
}
