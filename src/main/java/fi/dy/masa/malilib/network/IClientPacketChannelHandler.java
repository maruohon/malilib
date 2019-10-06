package fi.dy.masa.malilib.network;

public interface IClientPacketChannelHandler
{
    void registerClientChannelHandler(IPluginChannelHandler handler);

    void unregisterClientChannelHandler(IPluginChannelHandler handler);
}