package malilib.network;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;

import malilib.MaLiLib;
import malilib.util.game.wrap.GameUtils;

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
        Set<ResourceLocation> toRegister = new HashSet<>();

        for (ResourceLocation channel : handler.getChannels())
        {
            if (this.handlers.containsEntry(channel, handler) == false)
            {
                this.handlers.put(channel, handler);

                if (handler.registerToServer())
                {
                    toRegister.add(channel);
                }
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
        Set<ResourceLocation> toUnRegister = new HashSet<>();

        for (ResourceLocation channel : handler.getChannels())
        {
            if (this.handlers.remove(channel, handler) && handler.registerToServer())
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
    public boolean processPacketFromServer(ClientboundCustomPayloadPacket packet, ClientPacketListener netHandler)
    {
        ResourceLocation channel = packet.getIdentifier();
        List<PluginChannelHandler> handlers = this.handlers.get(channel);

        if (handlers.isEmpty() == false)
        {
            for (PluginChannelHandler handler : handlers)
            {
                FriendlyByteBuf buf;

                if (handler.usePacketSplitter())
                {
                    buf = PacketSplitter.receive(netHandler, packet);
                }
                else
                {
                    buf = PacketUtils.retainedSlice(packet.getData());
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

    protected void sendRegisterPacket(ResourceLocation type, Collection<ResourceLocation> channels)
    {
        String joinedChannels = channels.stream().map(ResourceLocation::toString).collect(Collectors.joining("\0"));
        ByteBuf payload = Unpooled.wrappedBuffer(joinedChannels.getBytes(Charsets.UTF_8));
        ClientPacketListener handler = GameUtils.getClient().getConnection();
        ServerboundCustomPayloadPacket packet = new ServerboundCustomPayloadPacket(type, new FriendlyByteBuf(payload));

        if (handler != null)
        {
            MaLiLib.debugLog("(Un-)Registering packet handlers: type: '{}', '{}'", type, channels);
            handler.send(packet);
        }
        else
        {
            MaLiLib.debugLog("Failed to send register channel packet for '{}' - network handler was null", channels);
        }
    }
}
