package fi.dy.masa.malilib.interfaces;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

public interface IClientCommandDispatcher 
{
    void registerClientCommand(IClientCommand clientCommand);

    /**
     * Returns the original ServerCommandSource from the commandDispatcher for commands
     * @return the server command source
     */
    CommandDispatcher<ServerCommandSource> getCommandDispatcher();

    /**
     * Sets the original ServerCommandSource from the commandDispatcher
     * @param dispatcher
     */
    void setCommandDispatcher(CommandDispatcher<ServerCommandSource> dispatcher);
}
