package fi.dy.masa.malilib.network;

import java.util.List;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface PluginChannelHandler
{
    /**
     * @return true if this packet should be registered to the other end of
     * the connection via a 'minecraft:register' packet.
     */
    boolean registerToServer();

    /**
     * @return true if this packet should be put through the PacketSplitter system,
     * which allows receiving much larger packets, as they will automatically get
     * split to the maximum custom payload packet size.
     * If false, then the packet is received directly as-is.
     */
    boolean usePacketSplitter();

    /**
     * @return a list of message channels this handler can handle
     */
    List<ResourceLocation> getChannels();

    /**
     * Called when the packet is received from the other end of the connection
     */
    void onPacketReceived(PacketBuffer buf);
}
