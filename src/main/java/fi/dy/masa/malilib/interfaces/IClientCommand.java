package fi.dy.masa.malilib.interfaces;

public interface IClientCommand 
{
    /**
     * Adds all commands to the client
     * @param commandDispatcher
     */
    default void registerClientCommands(com.mojang.brigadier.CommandDispatcher<net.minecraft.server.command.ServerCommandSource> commandDispatcher) {}
}