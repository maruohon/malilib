package malilib.network;

import net.ornithemc.osl.networking.api.CustomPayload;
import net.ornithemc.osl.networking.impl.client.ClientPlayNetworkingImpl;

import malilib.util.data.Identifier;

public class PacketUtils
{
    /*
     * Wraps the newly created buf from {@code buf.slice()} in a PacketByteBuf.
     *
     * @param buf the original ByteBuf
     * @return a slice of the buffer
     * @see io.netty.buffer.ByteBuf#slice()
     */
    /*
    public static PacketBuffer slice(ByteBuf buf)
    {
        Objects.requireNonNull(buf, "PacketUtils#slice(): ByteBuf cannot be null");
        return new PacketBuffer(buf.slice());
    }
    */

    /*
     * Wraps the newly created buf from {@code buf.retainedSlice()} in a PacketByteBuf.
     *
     * @param buf the original ByteBuf
     * @return a slice of the buffer
     * @see io.netty.buffer.ByteBuf#retainedSlice()
     */
    /*
    public static PacketBuffer retainedSlice(ByteBuf buf)
    {
        Objects.requireNonNull(buf, "PacketUtils#retainedSlice(): ByteBuf cannot be null");
        return new PacketBuffer(buf.retainedSlice());
    }
    */

    public static void send(Identifier channel, CustomPayload packet)
    {
        ClientPlayNetworkingImpl.send(channel.toString(), packet);
    }

    /*
    public static void sendTag(Identifier channel, NbtCompound tag)
    {
        DataOutputStream stream = new DataOutputStream(new ByteArrayOutputStream());
        NbtElement.serialize(tag, stream);
        send(channel, stream);
    }
    */
}
