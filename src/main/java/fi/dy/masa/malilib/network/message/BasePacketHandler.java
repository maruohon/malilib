package fi.dy.masa.malilib.network.message;

import fi.dy.masa.malilib.network.PluginChannelHandler;

public abstract class BasePacketHandler implements PluginChannelHandler
{
    protected boolean registerToServer;
    protected boolean usePacketSplitter;

    @Override
    public boolean usePacketSplitter()
    {
        return this.usePacketSplitter;
    }

    @Override
    public boolean registerToServer()
    {
        return this.registerToServer;
    }
}
