package fi.dy.masa.malilib.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayChannelHandler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;

public interface IPluginChannelHandler
{
    Identifier getChannel();

    default PlayChannelHandler getClientPacketHandler()
    {
        if (this.usePacketSplitter())
        {
            return (mc, net, buf, responder) -> this.handleViaPacketSplitter(net, buf);
        }

        return (mc, net, buf, responder) -> MinecraftClient.getInstance().execute(() -> this.onPacketReceived(buf));
    }

    default void handleViaPacketSplitter(ClientPlayPacketListener netHandler, PacketByteBuf buf)
    {
        PacketByteBuf fullBuf = PacketSplitter.receive(netHandler, this.getChannel(), buf);

        if (fullBuf != null)
        {
            MinecraftClient.getInstance().execute(() -> this.onPacketReceived(fullBuf));
        }
    }

    void onPacketReceived(PacketByteBuf buf);

    default boolean usePacketSplitter()
    {
        return true;
    }

    default boolean registerToServer()
    {
        return true;
    }
}
