package fi.dy.masa.malilib.command;

import java.util.List;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.util.GameUtils;

public class ClientCommandHandler extends CommandHandler
{
    public String[] latestAutoComplete = null;

    /**
     * Attempt to execute a command. This method should return the number of times that the command was executed. If the
     * command does not exist or if the player does not have permission, 0 will be returned. A number greater than 1 can
     * be returned if a player selector is used.
     * 
     * @return 1 if successfully executed, -1 if no permission or wrong usage, 0 if it doesn't exist
     */
    @Override
    public int executeCommand(ICommandSender sender, String message)
    {
        message = message.trim();

        boolean usedSlash = message.startsWith("/");

        if (usedSlash)
        {
            message = message.substring(1);
        }

        String[] temp = message.split(" ");
        String[] args = new String[temp.length - 1];
        String commandName = temp[0];
        System.arraycopy(temp, 1, args, 0, args.length);
        ICommand command = this.getCommands().get(commandName);

        try
        {
            if (command == null)
            {
                return 0;
            }

            if (command.checkPermission(this.getServer(), sender))
            {
                this.tryExecute(sender, args, command, message);
                return 1;
            }
            else
            {
                sender.sendMessage(this.format("commands.generic.permission"));
            }
        }
        catch (Throwable t)
        {
            sender.sendMessage(this.format("commands.generic.exception"));
            MaLiLib.LOGGER.error("Command '{}' threw an exception:", message, t);
        }

        return -1;
    }

    private TextComponentTranslation format(String str, Object... args)
    {
        TextComponentTranslation ret = new TextComponentTranslation(str, args);
        ret.getStyle().setColor(TextFormatting.RED);
        return ret;
    }

    public void autoComplete(String leftOfCursor)
    {
        this.latestAutoComplete = null;

        if (leftOfCursor.charAt(0) == '/')
        {
            leftOfCursor = leftOfCursor.substring(1);

            if (GuiUtils.getCurrentScreen() instanceof GuiChat)
            {
                EntityPlayer player = GameUtils.getClientPlayer();
                List<String> commands = this.getTabCompletions(player, leftOfCursor, player.getPosition());

                if (commands.isEmpty() == false)
                {
                    TextFormatting gray = TextFormatting.GRAY;
                    TextFormatting reset = TextFormatting.RESET;

                    if (leftOfCursor.indexOf(' ') == -1)
                    {
                        for (int i = 0; i < commands.size(); i++)
                        {
                            commands.set(i, gray + "/" + commands.get(i) + reset);
                        }
                    }
                    else
                    {
                        for (int i = 0; i < commands.size(); i++)
                        {
                            commands.set(i, gray + commands.get(i) + reset);
                        }
                    }

                    this.latestAutoComplete = commands.toArray(new String[commands.size()]);
                }
            }
        }
    }

    @Override
    protected MinecraftServer getServer()
    {
        return GameUtils.getClient().getIntegratedServer();
    }
}
