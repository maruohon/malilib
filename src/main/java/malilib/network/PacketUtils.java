package malilib.network;

import java.util.Objects;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;

public class PacketUtils
{
    /**
     * Wraps the newly created buf from {@code buf.slice()} in a PacketByteBuf.
     *
     * @param buf the original ByteBuf
     * @return a slice of the buffer
     * @see io.netty.buffer.ByteBuf#slice()
     */
    public static PacketBuffer slice(ByteBuf buf)
    {
        Objects.requireNonNull(buf, "PacketUtils#slice(): ByteBuf cannot be null");
        return new PacketBuffer(buf.slice());
    }

    /**
     * Wraps the newly created buf from {@code buf.retainedSlice()} in a PacketByteBuf.
     *
     * @param buf the original ByteBuf
     * @return a slice of the buffer
     * @see io.netty.buffer.ByteBuf#retainedSlice()
     */
    public static PacketBuffer retainedSlice(ByteBuf buf)
    {
        Objects.requireNonNull(buf, "PacketUtils#retainedSlice(): ByteBuf cannot be null");
        return new PacketBuffer(buf.slice());   // TODO 1.8.9 this should be retainedSlice(), which doesn't exist in Netty 4.0.23
    }

    public static void send(ResourceLocation channel, PacketBuffer packet, NetHandlerPlayClient networkHandler)
    {
        networkHandler.addToSendQueue(new C17PacketCustomPayload(channel.toString(), packet));
    }

    public static void sendTag(ResourceLocation channel, NBTTagCompound tag, NetHandlerPlayClient networkHandler)
    {
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        buf.writeNBTTagCompoundToBuffer(tag);
        send(channel, buf, networkHandler);
    }
}
