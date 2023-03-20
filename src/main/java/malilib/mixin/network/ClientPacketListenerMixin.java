package malilib.mixin.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;

import malilib.network.ClientPacketChannelHandlerImpl;
import malilib.registry.Registry;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin
{
    @Inject(method = "handleCustomPayload", cancellable = true,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/game/ClientboundCustomPayloadPacket;getIdentifier()Lnet/minecraft/resources/ResourceLocation;"))
    private void onCustomPayload(ClientboundCustomPayloadPacket packet, CallbackInfo ci)
    {
        if (((ClientPacketChannelHandlerImpl) Registry.CLIENT_PACKET_CHANNEL_HANDLER).processPacketFromServer(packet, (ClientPacketListener)(Object) this))
        {
            ci.cancel();
        }
    }
}
