package malilib.mixin.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.network.handler.ClientNetworkHandler;
import net.minecraft.network.Connection;

@Mixin(ClientNetworkHandler.class)
public interface ClientNetworkHandlerMixin
{
    @Accessor("connection")
    Connection malilib_getConnection();
}
