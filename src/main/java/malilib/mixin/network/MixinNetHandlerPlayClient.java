package malilib.mixin.network;

public abstract class MixinNetHandlerPlayClient {}
/*
@Mixin(ClientNetworkHandler.class)
public abstract class MixinNetHandlerPlayClient
{
    @Inject(method = "handleCustomPayload", at = @At("RETURN"))
    private void onCustomPayload(SPacketCustomPayload packet, CallbackInfo ci)
    {
        NetHandlerPlayClient handler = (NetHandlerPlayClient) (Object) this;
        ((ClientPacketChannelHandlerImpl) Registry.CLIENT_PACKET_CHANNEL_HANDLER).processPacketFromServer(packet, handler);
    }
}
*/
