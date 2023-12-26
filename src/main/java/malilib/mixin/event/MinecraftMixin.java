package malilib.mixin.event;

import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;

import malilib.event.dispatch.ClientWorldChangeEventDispatcherImpl;
import malilib.event.dispatch.InitializationDispatcherImpl;
import malilib.event.dispatch.TickEventDispatcherImpl;
import malilib.registry.Registry;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    @Shadow public WorldClient world;
    @Shadow public EntityPlayerSP player;

    private WorldClient worldBefore;

    @Inject(method = "init", at = @At("RETURN"))
    private void onInitComplete(CallbackInfo ci)
    {
        // Register all mod handlers
        ((InitializationDispatcherImpl) Registry.INITIALIZATION_DISPATCHER).onGameInitDone();
    }

    @Inject(method = "runTick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getSystemTime()J"))
    private void onRunTickEnd(CallbackInfo ci)
    {
        if (this.world != null && this.player != null)
        {
            ((TickEventDispatcherImpl) Registry.TICK_EVENT_DISPATCHER).onClientTick();
        }
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void onLoadWorldPre(@Nullable WorldClient worldClientIn, String loadingMessage, CallbackInfo ci)
    {
        this.worldBefore = this.world;
        ((ClientWorldChangeEventDispatcherImpl) Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER).onWorldLoadPre(this.world, worldClientIn);
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("RETURN"))
    private void onLoadWorldPost(@Nullable WorldClient worldClientIn, String loadingMessage, CallbackInfo ci)
    {
        ((ClientWorldChangeEventDispatcherImpl) Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER).onWorldLoadPost(this.worldBefore, worldClientIn);
        this.worldBefore = null;
    }
}
