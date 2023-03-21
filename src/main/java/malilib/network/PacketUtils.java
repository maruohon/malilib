package malilib.network;

import java.util.Objects;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;

public class PacketUtils
{
    /**
     * Wraps the newly created buf from {@code buf.slice()} in a PacketByteBuf.
     *
     * @param buf the original ByteBuf
     * @return a slice of the buffer
     * @see io.netty.buffer.ByteBuf#slice()
     */
    public static FriendlyByteBuf slice(ByteBuf buf)
    {
        Objects.requireNonNull(buf, "PacketUtils#slice(): ByteBuf cannot be null");
        return new FriendlyByteBuf(buf.slice());
    }

    /**
     * Wraps the newly created buf from {@code buf.retainedSlice()} in a PacketByteBuf.
     *
     * @param buf the original ByteBuf
     * @return a slice of the buffer
     * @see io.netty.buffer.ByteBuf#retainedSlice()
     */
    public static FriendlyByteBuf retainedSlice(ByteBuf buf)
    {
        Objects.requireNonNull(buf, "PacketUtils#retainedSlice(): ByteBuf cannot be null");
        return new FriendlyByteBuf(buf.retainedSlice());
    }

    public static void send(ResourceLocation channel, FriendlyByteBuf packet, ClientPacketListener networkHandler)
    {
        networkHandler.send(new ServerboundCustomPayloadPacket(channel, packet));
    }

    public static void sendTag(ResourceLocation channel, CompoundTag tag, ClientPacketListener networkHandler)
    {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeNbt(tag);
        send(channel, buf, networkHandler);
    }
}
