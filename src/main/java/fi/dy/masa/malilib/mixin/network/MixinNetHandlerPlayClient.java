package fi.dy.masa.malilib.mixin.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketCustomPayload;
import fi.dy.masa.malilib.network.ClientPacketChannelHandlerImpl;
import fi.dy.masa.malilib.registry.Registry;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient
{
    @Inject(method = "handleCustomPayload", at = @At("RETURN"))
    private void onCustomPayload(SPacketCustomPayload packet, CallbackInfo ci)
    {
        NetHandlerPlayClient handler = (NetHandlerPlayClient) (Object) this;
        ((ClientPacketChannelHandlerImpl) Registry.CLIENT_PACKET_CHANNEL_HANDLER).processPacketFromServer(packet, handler);
    }
}
