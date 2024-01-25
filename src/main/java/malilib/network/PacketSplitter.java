package malilib.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.ornithemc.osl.networking.api.DataStreams;
import net.ornithemc.osl.networking.impl.client.ClientPlayNetworkingImpl;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.network.handler.ClientNetworkHandler;
import net.minecraft.network.PacketHandler;

import malilib.MaLiLib;

/**
 * Network packet splitter code from QuickCarpet by skyrising
 * @author skyrising
 */
public class PacketSplitter
{
    public static final int MAX_TOTAL_PER_PACKET_S2C = 1048576;
    public static final int MAX_PAYLOAD_PER_PACKET_S2C = MAX_TOTAL_PER_PACKET_S2C - 5;
    public static final int MAX_TOTAL_PER_PACKET_C2S = 32767;
    public static final int MAX_PAYLOAD_PER_PACKET_C2S = MAX_TOTAL_PER_PACKET_C2S - 5;
    public static final int DEFAULT_MAX_RECEIVE_SIZE_C2S = 1048576;
    public static final int DEFAULT_MAX_RECEIVE_SIZE_S2C = 67108864;

    private static final Map<Pair<PacketHandler, String>, ReadingSession> READING_SESSIONS = new HashMap<>();

    public static void send(String channel, ByteBuffer data)
    {
        send(data, MAX_PAYLOAD_PER_PACKET_C2S, arr -> ClientPlayNetworkingImpl.send(channel, arr));
    }

    private static void send(ByteBuffer data, int payloadLimit, Consumer<byte[]> sender)
    {
        try
        {
            int totalSize = data.position();

            for (int offset = 0, index = 0; offset < totalSize; offset += payloadLimit, index += payloadLimit)
            {
                int packetSize = Math.min(totalSize - offset, payloadLimit);
                int start = 0;
                byte[] arr = new byte[packetSize];
                ByteArrayOutputStream bos = new ByteArrayOutputStream(4);
                DataOutputStream dos = new DataOutputStream(bos);

                if (offset == 0)
                {
                    dos.writeInt(totalSize);
                    System.arraycopy(bos.toByteArray(), 0, arr, 0, 4);
                    start = 4;
                }

                data.get(arr, start, packetSize);
                sender.accept(arr);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Exception in PacketSplitter: {}", e);
        }
    }

    @Nullable
    public static DataInputStream receive(ClientNetworkHandler networkHandler,
                                          String channel,
                                          DataInputStream data)
    {
        return receive(networkHandler, channel, data, DEFAULT_MAX_RECEIVE_SIZE_S2C);
    }

    @Nullable
    private static DataInputStream receive(ClientNetworkHandler networkHandler,
                                           String channel,
                                           DataInputStream data,
                                           int maxLength)
    {
        Pair<PacketHandler, String> key = Pair.of(networkHandler, channel);
        return READING_SESSIONS.computeIfAbsent(key, ReadingSession::new).receive(data, maxLength);
    }

    private static class ReadingSession
    {
        private final Pair<PacketHandler, String> key;
        private int expectedSize = -1;
        private int index;
        private byte[] dataBuffer;

        private ReadingSession(Pair<PacketHandler, String> key)
        {
            this.key = key;
        }

        @Nullable
        private DataInputStream receive(DataInputStream data, int maxLength)
        {
            try
            {
                if (this.expectedSize < 0)
                {
                    this.expectedSize = data.readInt();

                    if (this.expectedSize <= 0)
                    {
                        MaLiLib.LOGGER.warn("PacketSplitter.ReadingSession.receive(): Invalid packet size: {}", this.expectedSize);
                        READING_SESSIONS.remove(this.key);
                        return null;
                    }

                    if (this.expectedSize > maxLength)
                    {
                        READING_SESSIONS.remove(this.key);
                        throw new IllegalArgumentException("Payload too large: " + this.expectedSize);
                    }

                    this.dataBuffer = new byte[this.expectedSize];
                }

                int readBytes = data.read(this.dataBuffer, this.index, data.available());
                this.index += readBytes;

                if (readBytes <= 0)
                {
                    MaLiLib.LOGGER.warn("PacketSplitter.ReadingSession.receive(): Failed to read data");
                    READING_SESSIONS.remove(this.key);
                    return null;
                }

                if (this.index >= this.expectedSize)
                {
                    READING_SESSIONS.remove(this.key);
                    return DataStreams.input(this.dataBuffer);
                }
            }
            catch (IOException e)
            {
                MaLiLib.LOGGER.warn("Exception in PacketSplitter.ReadingSession.receive(): {}", e);
            }

            return null;
        }
    }
}
