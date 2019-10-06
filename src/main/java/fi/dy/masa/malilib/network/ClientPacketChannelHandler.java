package fi.dy.masa.malilib.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import com.google.common.base.Charsets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.LiteModMaLiLib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ClientPacketChannelHandler implements IClientPacketChannelHandler
{
    public static final ResourceLocation REGISTER = new ResourceLocation("minecraft:register");
    public static final ResourceLocation UNREGISTER = new ResourceLocation("minecraft:unregister");

    private static final ClientPacketChannelHandler INSTANCE = new ClientPacketChannelHandler();

    private final HashMap<ResourceLocation, IPluginChannelHandler> handlers;

    public static IClientPacketChannelHandler getInstance()
    {
        return INSTANCE;
    }

    private ClientPacketChannelHandler()
    {
        this.handlers = new HashMap<>();
    }

    @Override
    public void registerClientChannelHandler(IPluginChannelHandler handler)
    {
        List<ResourceLocation> toRegister = new ArrayList<>();

        for (ResourceLocation channel : handler.getChannels())
        {
            if (this.handlers.containsKey(channel) == false)
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
        IPluginChannelHandler handler = this.handlers.get(channel);

        if (handler != null)
        {
            PacketBuffer buf = PacketSplitter.receive(netHandler, packet);

            // Finished the complete packet
            if (buf != null)
            {
                handler.onPacketReceived(buf);
            }

            return true;
        }

        return false;
    }

    private void sendRegisterPacket(ResourceLocation type, List<ResourceLocation> channels)
    {
        String joinedChannels = channels.stream().map(ResourceLocation::toString).collect(Collectors.joining("\0"));
        ByteBuf payload = Unpooled.wrappedBuffer(joinedChannels.getBytes(Charsets.UTF_8));
        CPacketCustomPayload packet = new CPacketCustomPayload(type.toString(), new PacketBuffer(payload));

        NetHandlerPlayClient handler = Minecraft.getMinecraft().getConnection();

        if (handler != null)
        {
            handler.sendPacket(packet);
        }
        else
        {
            LiteModMaLiLib.logger.warn("Failed to send register channel packet - network handler was null");
        }
    }
}
