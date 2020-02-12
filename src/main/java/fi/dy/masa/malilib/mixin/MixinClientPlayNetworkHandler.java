package fi.dy.masa.malilib.mixin;

import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.client.world.ClientWorld;
import fi.dy.masa.malilib.event.ClientCommandEventHandler;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import fi.dy.masa.malilib.network.ClientPacketChannelHandler;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler
{
    @Shadow private MinecraftClient client;
    @Shadow private ClientWorld world;

    @Nullable private ClientWorld worldBefore;

    @Shadow private CommandDispatcher<ServerCommandSource> commandDispatcher;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(MinecraftClient mc, Screen screen, ClientConnection connection, GameProfile profile, CallbackInfo ci)
    {
        ((ClientCommandEventHandler) ClientCommandEventHandler.getInstance()).onInit(commandDispatcher);
    }

    @Inject(method = "onCommandTree", at = @At("TAIL"))
    private void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci)
    {
        ((ClientCommandEventHandler) ClientCommandEventHandler.getInstance()).onOnCommandTree(commandDispatcher);
    }

    @Inject(method = "onGameJoin", at = @At("HEAD"))
    private void onPreJoinGameHead(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        // Need to grab the old world reference at the start of the method,
        // because the next injection point is right after the world has been assigned,
        // since we need the new world reference for the callback.
        this.worldBefore = this.world;
    }

    @Inject(method = "onGameJoin", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/MinecraftClient;joinWorld(" +
                         "Lnet/minecraft/client/world/ClientWorld;)V"))
    private void onPreGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(this.worldBefore, this.world, this.client);
    }

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void onPostGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPost(this.worldBefore, this.world, this.client);
        this.worldBefore = null;
    }

    @Inject(method = "onCustomPayload", cancellable = true,
                at = @At(value = "INVOKE",
                         target = "Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;getChannel()Lnet/minecraft/util/Identifier;"))
    private void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci)
    {
        if (((ClientPacketChannelHandler) ClientPacketChannelHandler.getInstance()).processPacketFromServer(packet, (ClientPlayNetworkHandler)(Object) this))
        {
            ci.cancel();
        }
    }
}
