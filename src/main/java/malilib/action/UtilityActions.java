package malilib.action;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;

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
            ctx.getPlayer().sendChatMessage(arg, Text.literal(arg)); // TODO 1.19+ is this correct?
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult setPlayerFractionalXZ(ActionContext ctx, String arg)
    {
        PlayerEntity player = ctx.getPlayer();

        if (player != null)
        {
            try
            {
                String[] args = arg.split(" ");

                if (args.length == 2)
                {
                    double fx = Math.abs(Double.parseDouble(args[0])) % 1.0;
                    double fz = Math.abs(Double.parseDouble(args[1])) % 1.0;
                    double px = MathHelper.floor(EntityWrap.getX(player));
                    double pz = MathHelper.floor(EntityWrap.getZ(player));
                    double x = px < 0.0 ? px + 1.0 - fx : px + fx;
                    double z = pz < 0.0 ? pz + 1.0 - fz : pz + fz;
                    player.refreshPositionAndAngles(x, EntityWrap.getY(player), z,
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
                    ctx.getPlayer().getInventory().selectedSlot = slot - 1;
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
            GameUtils.getOptions().debugEnabled = ! GameUtils.getOptions().debugEnabled;

            if (GameUtils.getOptions().debugEnabled == false)
            {
                GameUtils.getOptions().debugProfilerEnabled = false;
                GameUtils.getOptions().debugTpsEnabled = false;
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult toggleF3ScreenProfilerPieChart(ActionContext ctx, String arg)
    {
        if (ctx.getWorld() != null)
        {
            GameUtils.getOptions().debugProfilerEnabled = ! GameUtils.getOptions().debugProfilerEnabled;
            boolean state = GameUtils.getOptions().debugProfilerEnabled;
            if (arg.equalsIgnoreCase("on")) state = true;
            else if (arg.equalsIgnoreCase("off")) state = false;
            GameUtils.getOptions().debugEnabled = state;
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult toggleF3ScreenLagometer(ActionContext ctx, String arg)
    {
        if (ctx.getWorld() != null)
        {
            GameUtils.getOptions().debugTpsEnabled = ! GameUtils.getOptions().debugTpsEnabled;
            boolean state = GameUtils.getOptions().debugTpsEnabled;
            if (arg.equalsIgnoreCase("on")) state = true;
            else if (arg.equalsIgnoreCase("off")) state = false;
            GameUtils.getOptions().debugEnabled = state;
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult toggleChunkBorders(ActionContext ctx)
    {
        if (ctx.getWorld() != null)
        {
            boolean enabled = ctx.getClient().debugRenderer.toggleShowChunkBorder();
            translateDebugToggleMessage(enabled ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ActionResult takeScreenshot(ActionContext ctx)
    {
        MinecraftClient mc = ctx.getClient();
        ScreenshotRecorder.saveScreenshot(mc.runDirectory, mc.getFramebuffer(),
                                          message -> mc.execute(() -> mc.inGameHud.getChatHud().addMessage(message)));
        return ActionResult.SUCCESS;
    }

    public static ActionResult dropHeldStack(ActionContext ctx)
    {
        if (ctx.getPlayer() != null && ctx.getPlayer().isSpectator() == false)
        {
            ctx.getPlayer().dropSelectedItem(true);
        }
        return ActionResult.SUCCESS;
    }

    public static ActionResult cycleGameMode(ActionContext ctx, String arg)
    {
        if (ctx.getPlayer() != null && ctx.getClient().getNetworkHandler() != null)
        {
            String[] parts = arg.split(",");

            if (parts.length > 0)
            {
                ArrayList<GameMode> modes = new ArrayList<>();

                for (String part : parts)
                {
                    if (part.equalsIgnoreCase("survival") || part.equals("s") || part.equals("0"))
                    {
                        modes.add(GameMode.SURVIVAL);
                    }
                    else if (part.equalsIgnoreCase("creative") || part.equals("c") || part.equals("1"))
                    {
                        modes.add(GameMode.CREATIVE);
                    }
                    else if (part.equalsIgnoreCase("adventure") || part.equals("a") || part.equals("2"))
                    {
                        modes.add(GameMode.ADVENTURE);
                    }
                    else if (part.equalsIgnoreCase("spectator") || part.equals("sp") || part.equals("3"))
                    {
                        modes.add(GameMode.SPECTATOR);
                    }
                }

                if (modes.isEmpty())
                {
                    return ActionResult.FAIL;
                }

                PlayerListEntry info = ctx.getClient().getNetworkHandler().getPlayerListEntry(ctx.getPlayer().getGameProfile().getId());
                int index = info != null ? modes.indexOf(info.getGameMode()) : -1;

                if (++index >= modes.size())
                {
                    index = 0;
                }

                GameMode mode = modes.get(index);
                GameUtils.sendCommand("gamemode " + mode.getName());

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.FAIL;
    }

    private static void translateDebugToggleMessage(String key, Object... args)
    {
        MutableText text = Text.literal("");
        text.append(Text.translatable("debug.prefix").setStyle(Style.EMPTY
                    .withColor(Formatting.YELLOW).withBold(Boolean.TRUE)))
                    .append(" ").append(Text.translatable(key, args));
        GameUtils.getClient().inGameHud.getChatHud().addMessage(text);
    }
}
