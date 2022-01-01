package fi.dy.masa.malilib.network;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.PacketUtils;

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
    public void unregisterClientChannelHandler(IPluginChannelHandler handler)
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
        Identifier channel = packet.getChannel();
        List<IPluginChannelHandler> handlers = this.handlers.get(channel);

        if (handlers.isEmpty() == false)
        {
            for (IPluginChannelHandler handler : handlers)
            {
                PacketByteBuf buf = handler.usePacketSplitter() ? PacketSplitter.receive(netHandler, packet) : PacketUtils.retainedSlice(packet.getData());

                // Finished the complete packet
                if (buf != null)
                {
                    handler.onPacketReceived(buf);
                }
            }

            //return true;
        }

        return false;
    }

    private void sendRegisterPacket(Identifier type, Collection<Identifier> channels)
    {
        String joinedChannels = channels.stream().map(Identifier::toString).collect(Collectors.joining("\0"));
        ByteBuf payload = Unpooled.wrappedBuffer(joinedChannels.getBytes(Charsets.UTF_8));
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(type, new PacketByteBuf(payload));

        ClientPlayNetworkHandler netHandler = MinecraftClient.getInstance().getNetworkHandler();

        if (netHandler != null)
        {
            netHandler.sendPacket(packet);
        }
        else
        {
            MaLiLib.logger.warn("Failed to send register channel packet - network handler was null");
        }
    }
}
