package fi.dy.masa.malilib.network;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.game.wrap.GameUtils;

public class ClientPacketChannelHandlerImpl implements ClientPacketChannelHandler
{
    public static final Identifier REGISTER = new Identifier("minecraft:register");
    public static final Identifier UNREGISTER = new Identifier("minecraft:unregister");

    protected final ArrayListMultimap<Identifier, PluginChannelHandler> handlers = ArrayListMultimap.create();

    public ClientPacketChannelHandlerImpl()
    {
    }

    @Override
    public void registerClientChannelHandler(PluginChannelHandler handler)
    {
        Set<Identifier> toRegister = new HashSet<>();

        for (Identifier channel : handler.getChannels())
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
        Set<Identifier> toUnRegister = new HashSet<>();

        for (Identifier channel : handler.getChannels())
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
    public boolean processPacketFromServer(CustomPayloadS2CPacket packet, ClientPlayNetworkHandler netHandler)
    {
        ResourceLocation channel = new ResourceLocation(packet.getChannelName());
        List<PluginChannelHandler> handlers = this.handlers.get(channel);

        if (handlers.isEmpty() == false)
        {
            for (PluginChannelHandler handler : handlers)
            {
                PacketByteBuf buf;

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

    protected void sendRegisterPacket(Identifier type, Collection<Identifier> channels)
    {
        String joinedChannels = channels.stream().map(Identifier::toString).collect(Collectors.joining("\0"));
        ByteBuf payload = Unpooled.wrappedBuffer(joinedChannels.getBytes(Charsets.UTF_8));
        NetHandlerPlayClient handler = GameUtils.getClient().getConnection();
        CPacketCustomPayload packet = new CPacketCustomPayload(type.toString(), new PacketBuffer(payload));

        if (handler != null)
        {
            handler.sendPacket(packet);
        }
        else
        {
            MaLiLib.debugLog("Failed to send register channel packet for '{}' - network handler was null", channels);
        }
    }
}
