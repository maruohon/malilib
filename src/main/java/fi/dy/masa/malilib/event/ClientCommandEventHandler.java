package fi.dy.masa.malilib.event;

import java.util.ArrayList;
import java.util.List;

import com.mojang.brigadier.CommandDispatcher;

import fi.dy.masa.malilib.command.ClientCommandManager;
import fi.dy.masa.malilib.interfaces.IClientCommand;
import fi.dy.masa.malilib.interfaces.IClientCommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

public class ClientCommandEventHandler implements IClientCommandDispatcher
{
    private static final ClientCommandEventHandler INSTANCE = new ClientCommandEventHandler();

    private final List<IClientCommand> clientCommands = new ArrayList<>();
    private CommandDispatcher<ServerCommandSource> dispatcher;

    public static IClientCommandDispatcher getInstance() 
    {
        return INSTANCE;
    }

    @Override
    public void registerClientCommand(IClientCommand clientCommand)
    {
        if (this.clientCommands.contains(clientCommand) == false) 
        {
            this.clientCommands.add(clientCommand);
        }
    }
    
    /**
     * NOT A PUBLIC API - DO NOT CALL
     */
    public void onInit(com.mojang.brigadier.CommandDispatcher<net.minecraft.server.command.ServerCommandSource> commandDispatcher)
    {
        ClientCommandManager.clearClientSideCommands();

        if (this.clientCommands.isEmpty() == false)
        {
            for (IClientCommand clientCommand : this.clientCommands)
            {
                clientCommand.registerClientCommands(commandDispatcher);
            }

            setCommandDispatcher(commandDispatcher);
        }
    }

    /**
     * NOT A PUBLIC API - DO NOT CALL
     */
    public void onOnCommandTree(com.mojang.brigadier.CommandDispatcher<net.minecraft.server.command.ServerCommandSource> commandDispatcher)
    {
        if (this.clientCommands.isEmpty() == false)
        {
            for (IClientCommand clientCommand : this.clientCommands)
            {
                clientCommand.registerClientCommands(commandDispatcher);
            }
        }
    }
    
    @Override
    public CommandDispatcher<ServerCommandSource> getCommandDispatcher()
    {
        return dispatcher;
    }

    /**
     * NOT A PUBLIC API - DO NOT CALL
     */
    @Override
    public void setCommandDispatcher(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        this.dispatcher = dispatcher;
    }
}
