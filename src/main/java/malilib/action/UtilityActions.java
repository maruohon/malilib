package malilib.action;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;

import malilib.input.ActionResult;
import malilib.util.game.wrap.EntityWrap;
import malilib.util.game.wrap.GameUtils;

public class UtilityActions
{
    public static ActionResult runVanillaCommand(ActionContext ctx, String arg)
    {
        if (arg.length() > 0 && arg.charAt(0) != '/')
        {
            arg = '/' + arg;
        }

        if (ctx.getPlayer() != null)
        {
            ctx.getPlayer().sendChatMessage(arg);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult sendChatMessage(ActionContext ctx, String arg)
    {
        if (ctx.getPlayer() != null)
        {
            ctx.getPlayer().sendChatMessage(arg);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult setPlayerYaw(ActionContext ctx, String arg)
    {
        if (ctx.getPlayer() != null)
        {
            try
            {
                EntityWrap.setYaw(ctx.getPlayer(), MathHelper.wrapDegrees(Float.parseFloat(arg)));
                return ActionResult.SUCCESS;
            }
            catch (Exception ignore) {}
        }
        return ActionResult.FAIL;
    }

    public static ActionResult setPlayerPitch(ActionContext ctx, String arg)
    {
        if (ctx.getPlayer() != null)
        {
            try
            {
                float pitch = Float.parseFloat(arg);

                if (pitch >= -90.0f && pitch <= 90.0f)
                {
                    EntityWrap.setPitch(ctx.getPlayer(), pitch);
                    return ActionResult.SUCCESS;
                }
            }
            catch (Exception ignore) {}
        }
        return ActionResult.FAIL;
    }

    public static ActionResult setSelectedHotbarSlot(ActionContext ctx, String arg)
    {
        if (ctx.getPlayer() != null)
        {
            try
            {
                int slot = Integer.parseInt(arg);
                if (slot >= 1 && slot <= 9)
                {
                    ctx.getPlayer().inventory.currentItem = slot - 1;
                    return ActionResult.SUCCESS;
                }
            }
            catch (Exception ignore) {}
        }
        return ActionResult.FAIL;
    }

    public static ActionResult toggleF3Screen(ActionContext ctx)
    {
        if (ctx.getWorld() != null)
        {
            GameUtils.getOptions().showDebugInfo = ! GameUtils.getOptions().showDebugInfo;

            if (GameUtils.getOptions().showDebugInfo == false)
            {
                GameUtils.getOptions().showDebugProfilerChart = false;
                GameUtils.getOptions().showLagometer = false;
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult toggleF3ScreenProfilerPieChart(ActionContext ctx, String arg)
    {
        if (ctx.getWorld() != null)
        {
            GameUtils.getOptions().showDebugProfilerChart = ! GameUtils.getOptions().showDebugProfilerChart;
            boolean state = GameUtils.getOptions().showDebugProfilerChart;
            if (arg.equalsIgnoreCase("on")) state = true;
            else if (arg.equalsIgnoreCase("off")) state = false;
            GameUtils.getOptions().showDebugInfo = state;
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult toggleF3ScreenLagometer(ActionContext ctx, String arg)
    {
        if (ctx.getWorld() != null)
        {
            GameUtils.getOptions().showLagometer = ! GameUtils.getOptions().showLagometer;
            boolean state = GameUtils.getOptions().showLagometer;
            if (arg.equalsIgnoreCase("on")) state = true;
            else if (arg.equalsIgnoreCase("off")) state = false;
            GameUtils.getOptions().showDebugInfo = state;
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult toggleChunkBorders(ActionContext ctx)
    {
        if (ctx.getWorld() != null)
        {
            boolean enabled = ctx.getClient().debugRenderer.toggleChunkBorders();
            translateDebugToggleMessage(enabled ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult takeScreenshot(ActionContext ctx)
    {
        Minecraft mc = ctx.getClient();
        mc.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(mc.gameDir,
                                    mc.displayWidth, mc.displayHeight, mc.getFramebuffer()));
        return ActionResult.SUCCESS;
    }

    public static ActionResult dropHeldStack(ActionContext ctx)
    {
        if (ctx.getPlayer() != null && ctx.getPlayer().isSpectator() == false)
        {
            ctx.getPlayer().dropItem(true);
        }
        return ActionResult.SUCCESS;
    }

    public static ActionResult cycleGameMode(ActionContext ctx, String arg)
    {
        if (ctx.getPlayer() != null && ctx.getClient().getConnection() != null)
        {
            String[] parts = arg.split(",");

            if (parts.length > 0)
            {
                ArrayList<GameType> modes = new ArrayList<>();

                for (String part : parts)
                {
                    if (part.equalsIgnoreCase("survival") || part.equals("s") || part.equals("0"))
                    {
                        modes.add(GameType.SURVIVAL);
                    }
                    else if (part.equalsIgnoreCase("creative") || part.equals("c") || part.equals("1"))
                    {
                        modes.add(GameType.CREATIVE);
                    }
                    else if (part.equalsIgnoreCase("adventure") || part.equals("a") || part.equals("2"))
                    {
                        modes.add(GameType.ADVENTURE);
                    }
                    else if (part.equalsIgnoreCase("spectator") || part.equals("sp") || part.equals("3"))
                    {
                        modes.add(GameType.SPECTATOR);
                    }
                }

                if (modes.isEmpty())
                {
                    return ActionResult.FAIL;
                }

                NetworkPlayerInfo info = ctx.getClient().getConnection().getPlayerInfo(ctx.getPlayer().getGameProfile().getId());
                int index = info != null ? modes.indexOf(info.getGameType()) : -1;

                if (++index >= modes.size())
                {
                    index = 0;
                }

                GameType mode = modes.get(index);
                ctx.getPlayer().sendChatMessage("/gamemode " + mode.getName());

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.FAIL;
    }

    private static void translateDebugToggleMessage(String key, Object... args)
    {
        ITextComponent text = new TextComponentString("");
        text.appendSibling((new TextComponentTranslation("debug.prefix"))
                                .setStyle((new Style()).setColor(TextFormatting.YELLOW).setBold(Boolean.TRUE)))
                .appendText(" ").appendSibling(new TextComponentTranslation(key, args));
        GameUtils.getClient().ingameGUI.getChatGUI().printChatMessage(text);
    }
}
