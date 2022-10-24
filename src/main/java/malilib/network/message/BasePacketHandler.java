package malilib.network.message;

import malilib.network.PluginChannelHandler;

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
