package fi.dy.masa.malilib.network;

public interface ClientPacketChannelHandler
{
    void registerClientChannelHandler(PluginChannelHandler handler);

    void unregisterClientChannelHandler(PluginChannelHandler handler);
}