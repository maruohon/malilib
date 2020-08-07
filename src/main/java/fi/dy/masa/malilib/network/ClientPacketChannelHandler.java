package fi.dy.masa.malilib.network;

public interface ClientPacketChannelHandler
{
    ClientPacketChannelHandler INSTANCE = new ClientPacketChannelHandlerImpl();

    void registerClientChannelHandler(PluginChannelHandler handler);

    void unregisterClientChannelHandler(PluginChannelHandler handler);
}