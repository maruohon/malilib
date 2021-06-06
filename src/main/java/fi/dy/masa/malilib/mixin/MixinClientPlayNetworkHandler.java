package fi.dy.masa.malilib.mixin;

import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.play.server.SJoinGamePacket;
import fi.dy.masa.malilib.event.WorldLoadHandler;

@Mixin(ClientPlayNetHandler.class)
public abstract class MixinClientPlayNetworkHandler
{
    @Shadow private Minecraft client;
    @Shadow private ClientWorld world;

    @Nullable private ClientWorld worldBefore;

    @Inject(method = "handleJoinGame", at = @At("HEAD"))
    private void onPreJoinGameHead(SJoinGamePacket packet, CallbackInfo ci)
    {
        // Need to grab the old world reference at the start of the method,
        // because the next injection point is right after the world has been assigned,
        // since we need the new world reference for the callback.
        this.worldBefore = this.world;
    }

    @Inject(method = "handleJoinGame", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/Minecraft;loadWorld(" +
                         "Lnet/minecraft/client/world/ClientWorld;)V"))
    private void onPreGameJoin(SJoinGamePacket packet, CallbackInfo ci)
    {
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(this.worldBefore, this.world, this.client);
    }

    @Inject(method = "handleJoinGame", at = @At("RETURN"))
    private void onPostGameJoin(SJoinGamePacket packet, CallbackInfo ci)
    {
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPost(this.worldBefore, this.world, this.client);
        this.worldBefore = null;
    }
}
