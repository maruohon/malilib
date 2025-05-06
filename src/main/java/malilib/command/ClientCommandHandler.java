package malilib.command;

import java.util.List;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import malilib.gui.util.GuiUtils;
import malilib.util.game.wrap.GameWrap;

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
        /* TODO 1.8.9
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
        */

        return -1;
    }

    private ChatComponentTranslation format(String str, Object... args)
    {
        ChatComponentTranslation ret = new ChatComponentTranslation(str, args);
        ret.getChatStyle().setColor(EnumChatFormatting.RED);
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
                EntityPlayer player = GameWrap.getClientPlayer();
                List<String> commands = this.getTabCompletionOptions(player, leftOfCursor, player.getPosition());

                if (commands.isEmpty() == false)
                {
                    EnumChatFormatting gray = EnumChatFormatting.GRAY;
                    EnumChatFormatting reset = EnumChatFormatting.RESET;

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

    /*
    @Override
    protected MinecraftServer getServer()
    {
        return GameWrap.getClient().getIntegratedServer();
    }
    */
}
