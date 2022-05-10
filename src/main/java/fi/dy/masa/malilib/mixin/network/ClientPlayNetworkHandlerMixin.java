package fi.dy.masa.malilib.mixin.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import fi.dy.masa.malilib.network.ClientPacketChannelHandlerImpl;
import fi.dy.masa.malilib.registry.Registry;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
{
    @Inject(method = "onCustomPayload", cancellable = true,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;getChannel()Lnet/minecraft/util/Identifier;"))
    private void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci)
    {
        if (((ClientPacketChannelHandlerImpl) Registry.CLIENT_PACKET_CHANNEL_HANDLER).processPacketFromServer(packet, (ClientPlayNetworkHandler)(Object) this))
        {
            ci.cancel();
        }
    }
}
