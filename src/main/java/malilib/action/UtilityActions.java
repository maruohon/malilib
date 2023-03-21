package malilib.action;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import malilib.input.ActionResult;
import malilib.util.game.wrap.EntityWrap;
import malilib.util.game.wrap.GameUtils;

public class UtilityActions
{
    public static ActionResult runVanillaCommand(ActionContext ctx, String arg)
    {
        if (ctx.getPlayer() != null)
        {
            GameUtils.sendCommand(arg);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult sendChatMessage(ActionContext ctx, String arg)
    {
        if (ctx.getPlayer() != null)
        {
            arg = StringUtils.normalizeSpace(arg.trim());
            ctx.getPlayer().chat(arg);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult setPlayerFractionalXZ(ActionContext ctx, String arg)
    {
        Player player = ctx.getPlayer();

        if (player != null)
        {
            try
            {
                String[] args = arg.split(" ");

                if (args.length == 2)
                {
                    double fx = Math.abs(Double.parseDouble(args[0])) % 1.0;
                    double fz = Math.abs(Double.parseDouble(args[1])) % 1.0;
                    double px = Mth.floor(EntityWrap.getX(player));
                    double pz = Mth.floor(EntityWrap.getZ(player));
                    double x = px < 0.0 ? px + 1.0 - fx : px + fx;
                    double z = pz < 0.0 ? pz + 1.0 - fz : pz + fz;
                    player.moveTo(x, EntityWrap.getY(player), z,
                                                    EntityWrap.getYaw(player), EntityWrap.getPitch(player));
                }

                return ActionResult.SUCCESS;
            }
            catch (Exception ignore) {}
        }
        return ActionResult.FAIL;
    }

    public static ActionResult setPlayerYaw(ActionContext ctx, String arg)
    {
        if (ctx.getPlayer() != null)
        {
            try
            {
                EntityWrap.setYaw(ctx.getPlayer(), Mth.wrapDegrees(Float.parseFloat(arg)));
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
                    ctx.getPlayer().getInventory().selected = slot - 1;
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
            GameUtils.getOptions().renderDebug = ! GameUtils.getOptions().renderDebug;

            if (GameUtils.getOptions().renderDebug == false)
            {
                GameUtils.getOptions().renderDebugCharts = false;
                GameUtils.getOptions().renderFpsChart = false;
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult toggleF3ScreenProfilerPieChart(ActionContext ctx, String arg)
    {
        if (ctx.getWorld() != null)
        {
            GameUtils.getOptions().renderDebugCharts = ! GameUtils.getOptions().renderDebugCharts;
            boolean state = GameUtils.getOptions().renderDebugCharts;
            if (arg.equalsIgnoreCase("on")) state = true;
            else if (arg.equalsIgnoreCase("off")) state = false;
            GameUtils.getOptions().renderDebug = state;
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult toggleF3ScreenLagometer(ActionContext ctx, String arg)
    {
        if (ctx.getWorld() != null)
        {
            GameUtils.getOptions().renderFpsChart = ! GameUtils.getOptions().renderFpsChart;
            boolean state = GameUtils.getOptions().renderFpsChart;
            if (arg.equalsIgnoreCase("on")) state = true;
            else if (arg.equalsIgnoreCase("off")) state = false;
            GameUtils.getOptions().renderDebug = state;
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult toggleChunkBorders(ActionContext ctx)
    {
        if (ctx.getWorld() != null)
        {
            boolean enabled = ctx.getClient().debugRenderer.switchRenderChunkborder();
            translateDebugToggleMessage(enabled ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult takeScreenshot(ActionContext ctx)
    {
        Minecraft mc = ctx.getClient();
        Screenshot.grab(mc.gameDirectory, mc.getMainRenderTarget(),
                                          message -> mc.execute(() -> mc.gui.getChat().addMessage(message)));
        return ActionResult.SUCCESS;
    }

    public static ActionResult dropHeldStack(ActionContext ctx)
    {
        if (ctx.getPlayer() != null && ctx.getPlayer().isSpectator() == false)
        {
            ctx.getPlayer().drop(true);
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

                PlayerInfo info = ctx.getClient().getConnection().getPlayerInfo(ctx.getPlayer().getGameProfile().getId());
                int index = info != null ? modes.indexOf(info.getGameMode()) : -1;

                if (++index >= modes.size())
                {
                    index = 0;
                }

                GameType mode = modes.get(index);
                GameUtils.sendCommand("gamemode " + mode.getName());

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.FAIL;
    }

    private static void translateDebugToggleMessage(String key, Object... args)
    {
        MutableComponent text = new TextComponent("");
        text.append(new TranslatableComponent("debug.prefix").setStyle(Style.EMPTY
                    .withColor(ChatFormatting.YELLOW).withBold(Boolean.TRUE)))
                    .append(" ").append(new TranslatableComponent(key, args));
        GameUtils.getClient().gui.getChat().addMessage(text);
    }
}
