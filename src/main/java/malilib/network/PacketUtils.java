package malilib.network;

import java.util.Objects;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

public class PacketUtils
{
    /**
     * Wraps the newly created buf from {@code buf.slice()} in a PacketByteBuf.
     *
     * @param buf the original ByteBuf
     * @return a slice of the buffer
     * @see io.netty.buffer.ByteBuf#slice()
     */
    public static PacketByteBuf slice(ByteBuf buf)
    {
        Objects.requireNonNull(buf, "PacketUtils#slice(): ByteBuf cannot be null");
        return new PacketByteBuf(buf.slice());
    }

    /**
     * Wraps the newly created buf from {@code buf.retainedSlice()} in a PacketByteBuf.
     *
     * @param buf the original ByteBuf
     * @return a slice of the buffer
     * @see io.netty.buffer.ByteBuf#retainedSlice()
     */
    public static PacketByteBuf retainedSlice(ByteBuf buf)
    {
        Objects.requireNonNull(buf, "PacketUtils#retainedSlice(): ByteBuf cannot be null");
        return new PacketByteBuf(buf.retainedSlice());
    }

    public static void send(Identifier channel, PacketByteBuf packet, ClientPlayNetworkHandler networkHandler)
    {
        networkHandler.sendPacket(new CustomPayloadC2SPacket(channel, packet));
    }

    public static void sendTag(Identifier channel, NbtCompound tag, ClientPlayNetworkHandler networkHandler)
    {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeNbt(tag);
        send(channel, buf, networkHandler);
    }
}
