package fi.dy.masa.malilib.mixin;

import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient
{
    @Shadow
    public ClientWorld world;

    private ClientWorld worldBefore;

    @Inject(method = "init()V", at = @At("RETURN"))
    private void onInitComplete(CallbackInfo ci)
    {
        // Register all mod handlers
        ((InitializationHandler) InitializationHandler.getInstance()).onGameInitDone();
    }

    @Inject(method = "scheduleStop()V", at = @At("RETURN"))
    private void onStop(CallbackInfo ci)
    {
        ((ConfigManager) ConfigManager.getInstance()).saveAllConfigs();
    }

    @Inject(method = "tick()V", at = @At("RETURN"))
    private void onPostKeyboardInput(CallbackInfo ci)
    {
        KeybindMulti.reCheckPressedKeys();
        TickHandler.getInstance().onClientTick((MinecraftClient)(Object) this);
    }

    @Inject(method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;)V", at = @At("HEAD"))
    private void onLoadWorldPre(@Nullable ClientWorld worldClientIn, CallbackInfo ci)
    {
        this.worldBefore = this.world;
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(this.world, worldClientIn, (MinecraftClient)(Object) this);
    }

    @Inject(method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;)V", at = @At("RETURN"))
    private void onLoadWorldPost(@Nullable ClientWorld worldClientIn, CallbackInfo ci)
    {
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPost(this.worldBefore, worldClientIn, (MinecraftClient)(Object) this);
        this.worldBefore = null;
    }
}
