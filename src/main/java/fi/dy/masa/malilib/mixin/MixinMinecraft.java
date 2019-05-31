package fi.dy.masa.malilib.mixin;

import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.util.IMinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMinecraftAccessor
{
    @Shadow
    public WorldClient world;

    @Shadow
    private boolean actionKeyF3;

    private WorldClient worldBefore;

    @Override
    public void setActionKeyF3(boolean value)
    {
        this.actionKeyF3 = value;
    }

    @Inject(method = "runTickKeyboard", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V"))
    private void onKeyboardInput(CallbackInfo ci)
    {
        if (((InputEventHandler) InputEventHandler.getInputManager()).onKeyInput(false))
        {
            ci.cancel();
        }
    }

    @Inject(method = "runTickMouse", cancellable = true,
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventButton()I", remap = false))
    private void onMouseInput(CallbackInfo ci)
    {
        if (((InputEventHandler) InputEventHandler.getInputManager()).onMouseInput(false))
        {
            ci.cancel();
        }
    }

    @Inject(method = "runTick", at = @At("RETURN"))
    private void onPostKeyboardInput(CallbackInfo ci)
    {
        KeybindMulti.reCheckPressedKeys();
    }

    @Inject(method = "runTick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getSystemTime()J"))
    private void onRunTickEnd(CallbackInfo ci)
    {
        TickHandler.getInstance().onClientTick((Minecraft)(Object) this);
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void onLoadWorldPre(@Nullable WorldClient worldClientIn, String loadingMessage, CallbackInfo ci)
    {
        this.worldBefore = this.world;
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(this.world, worldClientIn, (Minecraft)(Object) this);
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("RETURN"))
    private void onLoadWorldPost(@Nullable WorldClient worldClientIn, String loadingMessage, CallbackInfo ci)
    {
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPost(this.worldBefore, worldClientIn, (Minecraft)(Object) this);
        this.worldBefore = null;
    }
}
