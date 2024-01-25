package malilib.network;

import java.io.DataInputStream;
import com.google.common.collect.ArrayListMultimap;
import net.ornithemc.osl.networking.impl.client.ClientPlayNetworkingImpl;

import malilib.util.data.Identifier;

public class ClientPacketChannelHandlerImpl implements ClientPacketChannelHandler
{
    /*
    public static final Identifier REGISTER = new Identifier("minecraft:register");
    public static final Identifier UNREGISTER = new Identifier("minecraft:unregister");
    */

    protected final ArrayListMultimap<Identifier, PluginChannelHandler> handlers = ArrayListMultimap.create();

    public ClientPacketChannelHandlerImpl()
    {
    }

    @Override
    public void registerClientChannelHandler(PluginChannelHandler handler)
    {
        Identifier channel = handler.getChannel();

        if (this.handlers.containsEntry(channel, handler) == false)
        {
            this.handlers.put(channel, handler);

            if (handler.registerToServer())
            {
                if (handler.usePacketSplitter())
                {
                    ClientPlayNetworkingImpl.registerListener(channel.toString(), (mc, net, disIn) -> {
                        DataInputStream dis = PacketSplitter.receive(net, channel.toString(), disIn);
                        if (dis != null)
                        {
                            return handler.onPacketReceived(dis);
                        }
                        return true;
                    });
                }
                else
                {
                    ClientPlayNetworkingImpl.registerListener(channel.toString(), (mc, net, dis) -> handler.onPacketReceived(dis));
                }
            }
        }
    }

    @Override
    public void unregisterClientChannelHandler(PluginChannelHandler handler)
    {
        Identifier channel = handler.getChannel();

        if (this.handlers.remove(channel, handler) && handler.registerToServer())
        {
            ClientPlayNetworkingImpl.unregisterListener(channel.toString());
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    /*
    public boolean processPacketFromServer(SPacketCustomPayload packet, NetHandlerPlayClient netHandler)
    {
        Identifier channel = new Identifier(packet.getChannelName());
        List<PluginChannelHandler> handlers = this.handlers.get(channel);

        if (handlers.isEmpty() == false)
        {
            for (PluginChannelHandler handler : handlers)
            {
                PacketBuffer buf;

                if (handler.usePacketSplitter())
                {
                    buf = PacketSplitter.receive(netHandler, packet);
                }
                else
                {
                    buf = PacketUtils.retainedSlice(packet.getBufferData());
                }

                // Finished the complete packet
                if (buf != null)
                {
                    handler.onPacketReceived(buf);
                    buf.release();
                }
            }

            return true;
        }

        return false;
    }

    protected void sendRegisterPacket(Identifier type, Collection<Identifier> channels)
    {
        String joinedChannels = channels.stream().map(Identifier::toString).collect(Collectors.joining("\0"));
        ByteBuf payload = Unpooled.wrappedBuffer(joinedChannels.getBytes(Charsets.UTF_8));
        NetHandlerPlayClient handler = GameUtils.getClient().getConnection();
        CPacketCustomPayload packet = new CPacketCustomPayload(type.toString(), new PacketBuffer(payload));

        if (handler != null)
        {
            MaLiLib.debugLog("(Un-)Registering packet handlers: type: '{}', '{}'", type, channels);
            handler.sendPacket(packet);
        }
        else
        {
            MaLiLib.debugLog("Failed to send register channel packet for '{}' - network handler was null", channels);
        }
    }
    */
}
