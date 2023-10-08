package fi.dy.masa.malilib.network;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

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
        //send(packet, MAX_PAYLOAD_PER_PACKET_S2C, buf -> networkHandler.sendPacket(new CustomPayloadS2CPacket(channel, buf)));
    }

    public static void send(ClientPlayNetworkHandler networkHandler, Identifier channel, PacketByteBuf packet)
    {
        //ClientPlayNetworking.send(channel, packet);
        //send(packet, MAX_PAYLOAD_PER_PACKET_C2S, buf -> networkHandler.sendPacket(new CustomPayloadC2SPacket(new PacketByteBufPayload(channel, buf))));
        send(channel, packet, MAX_PAYLOAD_PER_PACKET_C2S);
    }

    private static void send(Identifier channel, PacketByteBuf packet, int payloadLimit)
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

            ClientPlayNetworking.send(channel, buf);
        }

        packet.release();
    }

    @Nullable
    public static PacketByteBuf receive(ClientPlayPacketListener networkHandler,
                                        Identifier channel,
                                        PacketByteBuf buf)
    {
        return receive(networkHandler, channel, buf, DEFAULT_MAX_RECEIVE_SIZE_S2C);
    }

    @Nullable
    private static PacketByteBuf receive(ClientPlayPacketListener networkHandler,
                                         Identifier channel,
                                         PacketByteBuf buf,
                                         int maxLength)
    {
        Pair<PacketListener, Identifier> key = Pair.of(networkHandler, channel);

        return READING_SESSIONS.computeIfAbsent(key, ReadingSession::new).receive(readPayload(buf), maxLength);
    }

    public static PacketByteBuf readPayload(PacketByteBuf byteBuf)
    {
        PacketByteBuf newBuf = PacketByteBufs.create();
        newBuf.writeBytes(byteBuf.copy());
        byteBuf.skipBytes(byteBuf.readableBytes());
        return newBuf;
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
