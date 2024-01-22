package malilib.action.builtin;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;

import malilib.MaLiLib;
import malilib.action.ActionContext;
import malilib.action.NamedAction;
import malilib.config.ConfigManagerImpl;
import malilib.config.ModConfig;
import malilib.config.category.ConfigOptionCategory;
import malilib.config.option.ConfigOption;
import malilib.input.ActionResult;
import malilib.overlay.message.MessageDispatcher;
import malilib.registry.Registry;
import malilib.util.MathUtils;
import malilib.util.data.ModInfo;
import malilib.util.datadump.DataDump;
import malilib.util.datadump.DataDump.Format;
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

    public static ActionResult setPlayerFractionalXZ(ActionContext ctx, String arg)
    {
        EntityPlayer player = ctx.getPlayer();

        if (player != null)
        {
            try
            {
                String[] args = arg.split(" ");

                if (args.length == 2)
                {
                    double fx = Math.abs(Double.parseDouble(args[0])) % 1.0;
                    double fz = Math.abs(Double.parseDouble(args[1])) % 1.0;
                    double px = MathUtils.floor(EntityWrap.getX(player));
                    double pz = MathUtils.floor(EntityWrap.getZ(player));
                    double x = px < 0.0 ? px + 1.0 - fx : px + fx;
                    double z = pz < 0.0 ? pz + 1.0 - fz : pz + fz;
                    player.setLocationAndAngles(x, EntityWrap.getY(player), z,
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
                EntityWrap.setYaw(ctx.getPlayer(), MathUtils.wrapDegrees(Float.parseFloat(arg)));
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

    public static ActionResult copyScreenshotToClipboard(ActionContext ctx)
    {
        Minecraft mc = ctx.getClient();

        try
        {
            BufferedImage image = ScreenShotHelper.createScreenshot(mc.displayWidth, mc.displayHeight, mc.getFramebuffer());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new BufferedImageTransferable(image), null);
            MessageDispatcher.generic("malilib.message.info.utility_actions.screenshot_copied_to_clipboard");
        }
        catch (Exception e)
        {
            MessageDispatcher.error().console(e).translate("malilib.message.error.utility_actions.failed_to_copy_screenshot_to_clipboard");
        }

        return ActionResult.SUCCESS;
    }

    public static ActionResult takeScreenshot(ActionContext ctx)
    {
        Minecraft mc = ctx.getClient();
        mc.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(mc.gameDir,
                                    mc.displayWidth, mc.displayHeight, mc.getFramebuffer()));
        return ActionResult.SUCCESS;
    }

    public static ActionResult dropOneItem(ActionContext ctx)
    {
        if (ctx.getPlayer() != null && ctx.getPlayer().isSpectator() == false)
        {
            ctx.getPlayer().dropItem(false);
        }
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

    public static ActionResult listAllConfigCategories(ActionContext ctx)
    {
        DataDump dump = new DataDump(2, Format.ASCII);
        dump.addTitle("Mod", "Config Category");

        for (ModConfig mc : ((ConfigManagerImpl) Registry.CONFIG_MANAGER).getAllModConfigsSorted())
        {
            String mod = mc.getModInfo().getModId();

            for (ConfigOptionCategory cat : mc.getConfigOptionCategories())
            {
                dump.addData(mod, cat.getName());
            }
        }

        dump.getLines().forEach(MaLiLib.LOGGER::info);
        MessageDispatcher.generic("malilib.message.info.utility_actions.output_printed_to_console");

        return ActionResult.SUCCESS;
    }

    public static ActionResult listAllConfigs(ActionContext ctx)
    {
        DataDump dump = new DataDump(3, Format.ASCII);
        dump.addTitle("Mod", "Config Category", "Config Name");

        for (ModConfig mc : ((ConfigManagerImpl) Registry.CONFIG_MANAGER).getAllModConfigsSorted())
        {
            String mod = mc.getModInfo().getModId();

            for (ConfigOptionCategory cat : mc.getConfigOptionCategories())
            {
                String categoryName = cat.getName();

                for (ConfigOption<?> cfg : cat.getConfigOptions())
                {
                    dump.addData(mod, categoryName, cfg.getName());
                    //MaLiLib.LOGGER.info("{} -> {} -> {}", mod, categoryName, cfg.getName());
                }
            }
        }

        dump.getLines().forEach(MaLiLib.LOGGER::info);
        MessageDispatcher.generic("malilib.message.info.utility_actions.output_printed_to_console");

        return ActionResult.SUCCESS;
    }

    public static ActionResult listAllBaseActions(ActionContext ctx)
    {
        DataDump dump = new DataDump(2, Format.ASCII);
        dump.addTitle("Mod", "Action Name");

        for (NamedAction action : Registry.ACTION_REGISTRY.getBaseActions())
        {
            ModInfo modInfo = action.getModInfo();
            String modName = modInfo != null ? modInfo.getModId() : "<null>";
            dump.addData(modName, action.getName());
        }

        dump.getLines().forEach(MaLiLib.LOGGER::info);
        MessageDispatcher.generic("malilib.message.info.utility_actions.output_printed_to_console");

        return ActionResult.SUCCESS;
    }

    private static void translateDebugToggleMessage(String key, Object... args)
    {
        ITextComponent text = new TextComponentString("");
        text.appendSibling((new TextComponentTranslation("debug.prefix"))
                                .setStyle((new Style()).setColor(TextFormatting.YELLOW).setBold(Boolean.TRUE)))
                .appendText(" ").appendSibling(new TextComponentTranslation(key, args));
        GameUtils.getClient().ingameGUI.getChatGUI().printChatMessage(text);
    }

    private static class BufferedImageTransferable implements Transferable
    {
        private final BufferedImage image;

        private BufferedImageTransferable(BufferedImage image)
        {
            this.image = image;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors()
        {
            return new DataFlavor[]{ DataFlavor.imageFlavor };
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor)
        {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
        {
            if (DataFlavor.imageFlavor.equals(flavor))
            {
                return this.image;
            }

            throw new UnsupportedFlavorException(flavor);
        }
    }
}
