package fi.dy.masa.malilib.overlay.message;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.value.InfoType;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.input.Context;
import fi.dy.masa.malilib.overlay.InfoArea;
import fi.dy.masa.malilib.overlay.InfoOverlay;
import fi.dy.masa.malilib.overlay.InfoWidgetManager;
import fi.dy.masa.malilib.overlay.widget.MessageRendererWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class MessageUtils
{
    public static final String CUSTOM_ACTION_BAR_MARKER = "malilib_actionbar";

    public static void info(String translationKey, Object... args)
    {
        info(5000, translationKey, args);
    }

    public static void info(int displayTimeMs, String translationKey, Object... args)
    {
        addMessage(Message.INFO, displayTimeMs, translationKey, args);
    }

    public static void success(String translationKey, Object... args)
    {
        success(5000, translationKey, args);
    }

    public static void success(int displayTimeMs, String translationKey, Object... args)
    {
        addMessage(Message.SUCCESS, displayTimeMs, translationKey, args);
    }

    public static void warning(String translationKey, Object... args)
    {
        warning(5000, translationKey, args);
    }

    public static void warning(int displayTimeMs, String translationKey, Object... args)
    {
        addMessage(Message.WARNING, displayTimeMs, translationKey, args);
    }

    public static void error(String translationKey, Object... args)
    {
        error(5000, translationKey, args);
    }

    public static void error(int displayTimeMs, String translationKey, Object... args)
    {
        addMessage(Message.ERROR, displayTimeMs, translationKey, args);
    }

    public static void errorAndConsole(String translationKey, Object... args)
    {
        errorAndConsole(5000, translationKey, args);
    }

    public static void errorAndConsole(int displayTimeMs, String translationKey, Object... args)
    {
        error(displayTimeMs, translationKey, args);
        MaLiLib.LOGGER.error(StringUtils.translate(translationKey, args));
    }

    public static void addMessage(int color, int displayTimeMs, String translationKey, Object... args)
    {
        addMessage(color, displayTimeMs, MaLiLibConfigs.Generic.MESSAGE_FADE_TIME.getIntegerValue(), translationKey, args);
    }

    public static void addMessage(int color, int displayTimeMs, int fadeTimeMs, String translationKey, Object... args)
    {
        addMessage(ScreenLocation.CENTER, color, displayTimeMs, fadeTimeMs, translationKey, args);
    }

    public static void addMessage(ScreenLocation location, int color, int displayTimeMs, int fadeTimeMs, String translationKey, Object... args)
    {
        getMessageRendererWidget(location, null).addMessage(color, displayTimeMs, fadeTimeMs, translationKey, args);
    }

    public static MessageRendererWidget getMessageRendererWidget(ScreenLocation location, @Nullable final String marker)
    {
        InfoArea area = InfoOverlay.INSTANCE.getOrCreateInfoArea(location);
        MessageRendererWidget widget;

        if (marker != null)
        {
            widget = area.findWidget(MessageRendererWidget.class, w -> w.hasMarker(marker));
        }
        else
        {
            widget = area.findWidget(MessageRendererWidget.class, w -> true);
        }

        if (widget == null)
        {
            widget = new MessageRendererWidget();
            widget.setLocation(location);
            widget.setZLevel(200f);
            widget.setWidth(300);
            widget.setRenderContext(Context.ANY);

            if (marker != null)
            {
                widget.addMarker(marker);
            }

            InfoWidgetManager.INSTANCE.addWidget(widget);
        }

        return widget;
    }

    public static MessageRendererWidget getCustomActionBarMessageRenderer()
    {
        String marker = CUSTOM_ACTION_BAR_MARKER;
        InfoArea area = InfoOverlay.INSTANCE.getOrCreateInfoArea(ScreenLocation.BOTTOM_CENTER);
        MessageRendererWidget widget = area.findWidget(MessageRendererWidget.class, w -> w.hasMarker(marker));

        if (widget == null)
        {
            widget = new MessageRendererWidget();
            widget.setLocation(ScreenLocation.BOTTOM_CENTER);
            widget.addMarker(marker);
            widget.setRenderBackground(false);
            widget.getMargin().setBottom(50);
            widget.setZLevel(200f);
            widget.setAutomaticWidth(true);
            widget.setMaxMessages(MaLiLibConfigs.Generic.ACTION_BAR_MESSAGE_LIMIT.getIntegerValue());
            widget.setMessageGap(2);
            InfoWidgetManager.INSTANCE.addWidget(widget);
        }

        return widget;
    }

    public static void addMessage(InfoType outputType, int color, int displayTimeMs, String translationKey, Object... args)
    {
        if (outputType != InfoType.NONE)
        {
            if (outputType == InfoType.MESSAGE_OVERLAY)
            {
                addMessage(color, displayTimeMs, translationKey, args);
            }
            else if (outputType == InfoType.CUSTOM_HOTBAR)
            {
                printCustomActionbarMessage(color, displayTimeMs, 500, translationKey, args);
            }
            else if (outputType == InfoType.VANILLA_HOTBAR)
            {
                printVanillaActionbarMessage(translationKey, args);
            }
            else if (outputType == InfoType.CHAT)
            {
                Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.CHAT, new TextComponentTranslation(translationKey, args));
            }
        }
    }

    public static void printCustomActionbarMessage(String translationKey, Object... args)
    {
        printCustomActionbarMessage(Message.INFO, 5000, 500, translationKey, args);
    }

    public static void printCustomActionbarMessage(int color, int displayTimeMs, int fadeTimeMs, String translationKey, Object... args)
    {
        getCustomActionBarMessageRenderer().addMessage(color, displayTimeMs, fadeTimeMs, translationKey, args);
    }

    public static void printVanillaActionbarMessage(String translationKey, Object... args)
    {
        Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentTranslation(translationKey, args));
    }

    public static void printBooleanConfigToggleMessage(BooleanConfig config)
    {
        boolean newValue = config.getBooleanValue();
        String msgKey;

        if (config.isOverridden())
        {
            msgKey = newValue ? "malilib.message.config_overridden_on" : "malilib.message.config_overridden_off";
        }
        else if (config.isLocked())
        {
            msgKey = newValue ? "malilib.message.config_locked_on" : "malilib.message.config_locked_off";
        }
        else
        {
            msgKey = newValue ? "malilib.message.toggled_config_on" : "malilib.message.toggled_config_off";
        }

        // TODO add a system for overriding the default output type per-config
        InfoType type = GuiUtils.getCurrentScreen() != null ? InfoType.MESSAGE_OVERLAY : InfoType.CUSTOM_HOTBAR;
        addMessage(type, Message.INFO, 5000, msgKey, config.getPrettyName());
    }
}
