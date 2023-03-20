package malilib.mixin.event;

import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;

import malilib.event.dispatch.ClientWorldChangeEventDispatcherImpl;
import malilib.registry.Registry;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin
{
    @Shadow private ClientLevel level;

    @Nullable private ClientLevel worldBefore;

    @Inject(method = "handleLogin", at = @At("HEAD"))
    private void onPreJoinGameHead(ClientboundLoginPacket packet, CallbackInfo ci)
    {
        // Need to grab the old world reference at the start of the method,
        // because the next injection point is right after the world has been assigned,
        // since we need the new world reference for the callback.
        this.worldBefore = this.level;
    }

    @Inject(method = "handleLogin", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;setLevel(" +
                             "Lnet/minecraft/client/multiplayer/ClientLevel;)V"))
    private void onPreGameJoin(ClientboundLoginPacket packet, CallbackInfo ci)
    {
        ((ClientWorldChangeEventDispatcherImpl) Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER).onWorldLoadPre(this.worldBefore, this.level);
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void onPostGameJoin(ClientboundLoginPacket packet, CallbackInfo ci)
    {
        ((ClientWorldChangeEventDispatcherImpl) Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER).onWorldLoadPost(this.worldBefore, this.level);
        this.worldBefore = null;
    }
}
