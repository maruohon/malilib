package malilib.mixin.access;

import java.net.SocketAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.Connection;

@Mixin(Connection.class)
public interface ConnectionMixin
{
    @Accessor("address")
    SocketAddress malilib_getAddress();
}
