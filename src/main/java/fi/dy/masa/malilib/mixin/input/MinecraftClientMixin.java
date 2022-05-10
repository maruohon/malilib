package fi.dy.masa.malilib.mixin.input;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import fi.dy.masa.malilib.event.dispatch.TickEventDispatcherImpl;
import fi.dy.masa.malilib.input.KeyBindImpl;
import fi.dy.masa.malilib.registry.Registry;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
    @Inject(method = "tick()V", at = @At("RETURN"))
    private void onPostKeyboardInput(CallbackInfo ci)
    {
        KeyBindImpl.reCheckPressedKeys();
        ((TickEventDispatcherImpl) Registry.TICK_EVENT_DISPATCHER).onClientTick((MinecraftClient)(Object) this);
    }
}
