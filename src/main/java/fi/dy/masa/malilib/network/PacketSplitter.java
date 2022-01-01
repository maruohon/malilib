package fi.dy.masa.malilib.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import fi.dy.masa.malilib.util.PacketUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Network packet splitter code from QuickCarpet by skyrising
 * @author skyrising
 *
 */
public class PacketSplitter
{
    public static final int MAX_TOTAL_PER_PACKET_S2C = 1048576;
    public static final int MAX_PAYLOAD_PER_PACKET_S2C = MAX_TOTAL_PER_PACKET_S2C - 5;
    public static final int MAX_TOTAL_PER_PACKET_C2S = 32767;
    public static final int MAX_PAYLOAD_PER_PACKET_C2S = MAX_TOTAL_PER_PACKET_C2S - 5;
    public static final int DEFAULT_MAX_RECEIVE_SIZE_C2S = 1048576;
    public static final int DEFAULT_MAX_RECEIVE_SIZE_S2C = 67108864;

    private static final Map<Pair<PacketListener, Identifier>, ReadingSession> READING_SESSIONS = new HashMap<>();

    public static void send(ServerPlayNetworkHandler networkHandler, Identifier channel, PacketByteBuf packet)
    {
        send(packet, MAX_PAYLOAD_PER_PACKET_S2C, buf -> networkHandler.sendPacket(new CustomPayloadS2CPacket(channel, buf)));
    }

    @Environment(EnvType.CLIENT)
    public static void send(ClientPlayNetworkHandler networkHandler, Identifier channel, PacketByteBuf packet)
    {
        send(packet, MAX_PAYLOAD_PER_PACKET_C2S, buf -> networkHandler.sendPacket(new CustomPayloadC2SPacket(channel, buf)));
    }

    private static void send(PacketByteBuf packet, int payloadLimit, Consumer<PacketByteBuf> sender)
    {
        int len = packet.writerIndex();

        packet.resetReaderIndex();

        for (int offset = 0; offset < len; offset += payloadLimit)
        {
            int thisLen = Math.min(len - offset, payloadLimit);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer(thisLen));

            buf.resetWriterIndex();

            if (offset == 0)
            {
                buf.writeVarInt(len);
            }

            buf.writeBytes(packet, thisLen);

            sender.accept(buf);
        }

        packet.release();
    }

    /*
    @Nullable
    public static PacketByteBuf receive(ServerPlayNetworkHandler networkHandler, CustomPayloadC2SPacket message)
    {
        return receive(networkHandler, message, DEFAULT_MAX_RECEIVE_SIZE_C2S);
    }

    @Nullable
    private static PacketByteBuf receive(ServerPlayNetworkHandler networkHandler, CustomPayloadC2SPacket message, int maxLength)
    {
        CustomPayloadC2SPacketAccessor messageAccessor = (CustomPayloadC2SPacketAccessor) message;
        Pair<PacketListener, Identifier> key = Pair.of(networkHandler, messageAccessor.getChannel());

        return READING_SESSIONS.computeIfAbsent(key, ReadingSession::new).receive(messageAccessor.getData(), maxLength);
    }
    */

    @Nullable
    public static PacketByteBuf receive(ClientPlayPacketListener networkHandler, CustomPayloadS2CPacket message)
    {
        return receive(networkHandler, message, DEFAULT_MAX_RECEIVE_SIZE_S2C);
    }

    @Nullable
    private static PacketByteBuf receive(ClientPlayPacketListener networkHandler, CustomPayloadS2CPacket message, int maxLength)
    {
        Pair<PacketListener, Identifier> key = Pair.of(networkHandler, message.getChannel());

        return READING_SESSIONS.computeIfAbsent(key, ReadingSession::new).receive(PacketUtils.retainedSlice(message.getData()), maxLength);
    }

    private static class ReadingSession
    {
        private final Pair<PacketListener, Identifier> key;
        private int expectedSize = -1;
        private PacketByteBuf received;

        private ReadingSession(Pair<PacketListener, Identifier> key)
        {
            this.key = key;
        }

        @Nullable
        private PacketByteBuf receive(PacketByteBuf data, int maxLength)
        {
            if (this.expectedSize < 0)
            {
                this.expectedSize = data.readVarInt();

                if (this.expectedSize > maxLength)
                {
                    throw new IllegalArgumentException("Payload too large");
                }

                this.received = new PacketByteBuf(Unpooled.buffer(this.expectedSize));
            }

            this.received.writeBytes(data.readBytes(data.readableBytes()));

            if (this.received.writerIndex() >= this.expectedSize)
            {
                READING_SESSIONS.remove(this.key);
                return this.received;
            }

            return null;
        }
    }
}
