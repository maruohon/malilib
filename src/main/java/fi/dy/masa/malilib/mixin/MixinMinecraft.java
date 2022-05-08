package fi.dy.masa.malilib.mixin;

import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import fi.dy.masa.malilib.MinecraftClientAccessor;
import fi.dy.masa.malilib.event.dispatch.ClientWorldChangeEventDispatcherImpl;
import fi.dy.masa.malilib.event.dispatch.TickEventDispatcherImpl;
import fi.dy.masa.malilib.input.InputDispatcherImpl;
import fi.dy.masa.malilib.input.KeyBindImpl;
import fi.dy.masa.malilib.registry.Registry;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraft implements MinecraftClientAccessor
{
    @Shadow public ClientWorld world;
    @Shadow public ClientPlayerEntity player;
    @Shadow private boolean actionKeyF3;

    private ClientWorld worldBefore;

    @Override
    public void setActionKeyF3(boolean value)
    {
        this.actionKeyF3 = value;
    }

    @Inject(method = "runTickKeyboard", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;dispatchKeypresses()V"))
    private void onKeyboardInput(CallbackInfo ci)
    {
        if (((InputDispatcherImpl) Registry.INPUT_DISPATCHER).onKeyInput())
        {
            ci.cancel();
        }
    }

    @Inject(method = "runTickMouse", cancellable = true,
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventButton()I", remap = false))
    private void onMouseInput(CallbackInfo ci)
    {
        if (((InputDispatcherImpl) Registry.INPUT_DISPATCHER).onMouseInput())
        {
            ci.cancel();
        }
    }

    @Inject(method = "runTick", at = @At("RETURN"))
    private void onPostKeyboardInput(CallbackInfo ci)
    {
        KeyBindImpl.reCheckPressedKeys();
    }

    @Inject(method = "runTick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getSystemTime()J"))
    private void onRunTickEnd(CallbackInfo ci)
    {
        if (this.world != null && this.player != null)
        {
            ((TickEventDispatcherImpl) Registry.TICK_EVENT_DISPATCHER).onClientTick((MinecraftClient) (Object) this);
        }
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void onLoadWorldPre(@Nullable ClientWorld worldClientIn, String loadingMessage, CallbackInfo ci)
    {
        this.worldBefore = this.world;
        ((ClientWorldChangeEventDispatcherImpl) Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER).onWorldLoadPre(this.world, worldClientIn, (MinecraftClient)(Object) this);
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("RETURN"))
    private void onLoadWorldPost(@Nullable ClientWorld worldClientIn, String loadingMessage, CallbackInfo ci)
    {
        ((ClientWorldChangeEventDispatcherImpl) Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER).onWorldLoadPost(this.worldBefore, worldClientIn, (MinecraftClient)(Object) this);
        this.worldBefore = null;
    }
}
