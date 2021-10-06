package fi.dy.masa.malilib.network;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.GameUtils;

public class ClientPacketChannelHandlerImpl implements ClientPacketChannelHandler
{
    public static final ResourceLocation REGISTER = new ResourceLocation("minecraft:register");
    public static final ResourceLocation UNREGISTER = new ResourceLocation("minecraft:unregister");

    protected final ArrayListMultimap<ResourceLocation, PluginChannelHandler> handlers = ArrayListMultimap.create();

    public ClientPacketChannelHandlerImpl()
    {
    }

    @Override
    public void registerClientChannelHandler(PluginChannelHandler handler)
    {
        List<ResourceLocation> toRegister = new ArrayList<>();

        for (ResourceLocation channel : handler.getChannels())
        {
            if (this.handlers.containsEntry(channel, handler) == false)
            {
                this.handlers.put(channel, handler);
                toRegister.add(channel);
            }
        }

        if (toRegister.isEmpty() == false)
        {
            this.sendRegisterPacket(REGISTER, toRegister);
        }
    }

    @Override
    public void unregisterClientChannelHandler(PluginChannelHandler handler)
    {
        List<ResourceLocation> toUnRegister = new ArrayList<>();

        for (ResourceLocation channel : handler.getChannels())
        {
            if (this.handlers.remove(channel, handler))
            {
                toUnRegister.add(channel);
            }
        }

        if (toUnRegister.isEmpty() == false)
        {
            this.sendRegisterPacket(UNREGISTER, toUnRegister);
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public boolean processPacketFromServer(SPacketCustomPayload packet, NetHandlerPlayClient netHandler)
    {
        ResourceLocation channel = new ResourceLocation(packet.getChannelName());
        List<PluginChannelHandler> handlers = this.handlers.get(channel);

        if (handlers.isEmpty() == false)
        {
            PacketBuffer buf = PacketSplitter.receive(netHandler, packet);

            // Finished the complete packet
            if (buf != null)
            {
                for (PluginChannelHandler handler : handlers)
                {
                    buf.readerIndex(0);
                    handler.onPacketReceived(buf);
                    buf.readerIndex(0);
                }
            }

            return true;
        }

        return false;
    }

    protected void sendRegisterPacket(ResourceLocation type, List<ResourceLocation> channels)
    {
        String joinedChannels = channels.stream().map(ResourceLocation::toString).collect(Collectors.joining("\0"));
        ByteBuf payload = Unpooled.wrappedBuffer(joinedChannels.getBytes(Charsets.UTF_8));
        CPacketCustomPayload packet = new CPacketCustomPayload(type.toString(), new PacketBuffer(payload));

        NetHandlerPlayClient handler = GameUtils.getClient().getConnection();

        if (handler != null)
        {
            handler.sendPacket(packet);
        }
        else
        {
            MaLiLib.LOGGER.warn("Failed to send register channel packet - network handler was null");
        }
    }
}
