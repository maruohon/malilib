package fi.dy.masa.malilib.network;

import java.util.List;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public interface IPluginChannelHandler
{
    List<Identifier> getChannels();

    void onPacketReceived(PacketByteBuf buf);
}
