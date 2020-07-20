package fi.dy.masa.malilib.network;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import fi.dy.masa.malilib.MaLiLib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ClientPacketChannelHandler implements IClientPacketChannelHandler
{
    public static final Identifier REGISTER = new Identifier("minecraft:register");
    public static final Identifier UNREGISTER = new Identifier("minecraft:unregister");

    private static final ClientPacketChannelHandler INSTANCE = new ClientPacketChannelHandler();

    private final ArrayListMultimap<Identifier, IPluginChannelHandler> handlers = ArrayListMultimap.create();

    public static IClientPacketChannelHandler getInstance()
    {
        return INSTANCE;
    }

    private ClientPacketChannelHandler()
    {
    }

    @Override
    public void registerClientChannelHandler(IPluginChannelHandler handler)
    {
        List<Identifier> toRegister = new ArrayList<>();

        for (Identifier channel : handler.getChannels())
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
    public void unregisterClientChannelHandler(IPluginChannelHandler handler)
    {
        List<Identifier> toUnRegister = new ArrayList<>();

        for (Identifier channel : handler.getChannels())
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
    public boolean processPacketFromServer(CustomPayloadS2CPacket packet, ClientPlayNetworkHandler netHandler)
    {
        Identifier channel = packet.getChannel();
        List<IPluginChannelHandler> handlers = this.handlers.get(channel);

        if (handlers.isEmpty() == false)
        {
            PacketByteBuf buf = PacketSplitter.receive(netHandler, packet);

            // Finished the complete packet
            if (buf != null)
            {
                for (IPluginChannelHandler handler : handlers)
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

    private void sendRegisterPacket(Identifier type, List<Identifier> channels)
    {
        String joinedChannels = channels.stream().map(Identifier::toString).collect(Collectors.joining("\0"));
        ByteBuf payload = Unpooled.wrappedBuffer(joinedChannels.getBytes(Charsets.UTF_8));
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(type, new PacketByteBuf(payload));

        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();

        if (handler != null)
        {
            handler.sendPacket(packet);
        }
        else
        {
            MaLiLib.logger.warn("Failed to send register channel packet - network handler was null");
        }
    }
}
