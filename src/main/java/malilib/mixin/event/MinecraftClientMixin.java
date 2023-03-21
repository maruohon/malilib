package malilib.mixin.event;

import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ClientLevel;

import malilib.event.dispatch.ClientWorldChangeEventDispatcherImpl;
import malilib.event.dispatch.InitializationDispatcherImpl;
import malilib.registry.Registry;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin
{
    @Shadow public ClientLevel world;

    private ClientLevel worldBefore;

    @Inject(method = "<init>(Lnet/minecraft/client/RunArgs;)V", at = @At("RETURN"))
    private void onInitComplete(GameConfig args, CallbackInfo ci)
    {
        // Register all mod handlers
        ((InitializationDispatcherImpl) Registry.INITIALIZATION_DISPATCHER).onGameInitDone();
    }

    @Inject(method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;)V", at = @At("HEAD"))
    private void onLoadWorldPre(@Nullable ClientLevel worldClientIn, CallbackInfo ci)
    {
        // Only handle dimension changes/respawns here.
        // The initial join is handled in MixinClientPlayNetworkHandler onGameJoin 
        if (this.world != null)
        {
            this.worldBefore = this.world;
            ((ClientWorldChangeEventDispatcherImpl) Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER).onWorldLoadPre(this.world, worldClientIn);
        }
    }

    @Inject(method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;)V", at = @At("RETURN"))
    private void onLoadWorldPost(@Nullable ClientLevel worldClientIn, CallbackInfo ci)
    {
        if (this.worldBefore != null)
        {
            ((ClientWorldChangeEventDispatcherImpl) Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER).onWorldLoadPost(this.worldBefore, worldClientIn);
            this.worldBefore = null;
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void onDisconnectPre(Screen screen, CallbackInfo ci)
    {
        this.worldBefore = this.world;
        ((ClientWorldChangeEventDispatcherImpl) Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER).onWorldLoadPre(this.worldBefore, null);
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("RETURN"))
    private void onDisconnectPost(Screen screen, CallbackInfo ci)
    {
        ((ClientWorldChangeEventDispatcherImpl) Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER).onWorldLoadPost(this.worldBefore, null);
        this.worldBefore = null;
    }
}
