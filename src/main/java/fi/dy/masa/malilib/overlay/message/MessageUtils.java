package fi.dy.masa.malilib.overlay.message;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.value.InfoType;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.overlay.InfoOverlay;
import fi.dy.masa.malilib.overlay.InfoWidgetManager;
import fi.dy.masa.malilib.overlay.widget.MessageRendererWidget;
import fi.dy.masa.malilib.overlay.widget.ToastRendererWidget;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.util.StringUtils;

public class MessageUtils
{
    protected static final Pattern PATTERN_TIME_MSG = Pattern.compile("time=(?<time>[0-9]+);(?<msg>.*)");

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

    public static void addMessage(@Nullable ScreenLocation location, int color, int displayTimeMs, int fadeTimeMs, String translationKey, Object... args)
    {
        getMessageRendererWidget(location, null).addMessage(color, displayTimeMs, fadeTimeMs, translationKey, args);
    }

    public static void addToastMessage(StyledText text, @Nullable final String marker, boolean append)
    {
        addToastMessage(text, marker, append, null);
    }

    public static void addToastMessage(StyledText text, int lifeTimeMs, @Nullable final String marker, boolean append)
    {
        addToastMessage(text, lifeTimeMs, marker, append, null);
    }

    public static void addToastMessage(StyledText text, @Nullable final String marker, boolean append, @Nullable ScreenLocation location)
    {
        addToastMessage(text, 5000, marker, append, location);
    }

    public static void addToastMessage(StyledText text, int lifeTimeMs, @Nullable final String marker, boolean append, final @Nullable ScreenLocation location)
    {
        Predicate<ToastRendererWidget> predicateLocation = location != null ? w -> w.getScreenLocation() == location : w -> true;
        ToastRendererWidget widget = InfoOverlay.INSTANCE.findWidget(ToastRendererWidget.class, predicateLocation);

        if (widget == null)
        {
            widget = new ToastRendererWidget();
            widget.setLocation(location != null ? location : ScreenLocation.TOP_RIGHT);
            widget.setZLevel(310f);
            InfoWidgetManager.INSTANCE.addWidget(widget);
        }

        widget.addToast(text, lifeTimeMs, marker, append);
    }

    public static MessageRendererWidget getMessageRendererWidget(final @Nullable ScreenLocation location, @Nullable final String marker)
    {
        Predicate<MessageRendererWidget> predicateLocation = location != null ? w -> w.getScreenLocation() == location : w -> true;
        Predicate<MessageRendererWidget> predicateMarker = marker != null ? w -> w.hasMarker(marker) : w -> true;
        MessageRendererWidget widget = InfoOverlay.INSTANCE.findWidget(MessageRendererWidget.class, predicateLocation.and(predicateMarker));

        if (widget == null)
        {
            widget = new MessageRendererWidget();
            widget.setLocation(location != null ? location : ScreenLocation.CENTER);
            widget.setZLevel(300f);
            widget.setWidth(300);
            widget.setRenderAboveScreen(true);

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
        MessageRendererWidget widget = InfoOverlay.INSTANCE.findWidget(MessageRendererWidget.class, w -> w.hasMarker(CUSTOM_ACTION_BAR_MARKER));

        if (widget == null)
        {
            widget = new MessageRendererWidget();
            widget.setLocation(ScreenLocation.BOTTOM_CENTER);
            widget.addMarker(CUSTOM_ACTION_BAR_MARKER);
            widget.setRenderBackground(false);
            widget.getMargin().setBottom(50);
            widget.setZLevel(300f);
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
            else if (outputType == InfoType.TOAST)
            {
                addToastMessage(StyledText.translatedOf(translationKey, args), displayTimeMs, null, false, null);
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
        printBooleanConfigToggleMessage(config, null);
    }

    public static void printBooleanConfigToggleMessage(BooleanConfig config,
                                                       @Nullable Function<BooleanConfig, String> messageFactory)
    {
        printBooleanConfigToggleMessage(InfoType.CUSTOM_HOTBAR, config, messageFactory);
    }

    public static void printBooleanConfigToggleMessage(InfoType messageType, BooleanConfig config,
                                                       @Nullable Function<BooleanConfig, String> messageFactory)
    {
        String message = getBooleanConfigToggleMessage(config, messageFactory);

        if (org.apache.commons.lang3.StringUtils.isBlank(message) == false)
        {
            addMessage(messageType, Message.INFO, 5000, message);
        }
    }

    public static ActionResult addMessageAction(ActionContext ctx, String msg)
    {
        return addMessageAction(InfoType.MESSAGE_OVERLAY, msg);
    }

    public static ActionResult addToastAction(ActionContext ctx, String msg)
    {
        return addMessageAction(InfoType.TOAST, msg);
    }

    public static ActionResult addMessageAction(InfoType type, String msg)
    {
        int displayTimeMs = 5000;
        Matcher matcher = PATTERN_TIME_MSG.matcher(msg);

        try
        {
            if (matcher.matches())
            {
                displayTimeMs = Integer.parseInt(matcher.group("time"));
                msg = matcher.group("msg");
            }
        }
        catch (Exception ignore) {}

        addMessage(type, Message.INFO, displayTimeMs, msg);

        return ActionResult.SUCCESS;
    }

    public static String getBooleanConfigToggleMessage(BooleanConfig config, @Nullable Function<BooleanConfig, String> messageFactory)
    {
        boolean newValue = config.getBooleanValue();
        String message;

        if (config.isOverridden())
        {
            String msgKey = newValue ? "malilib.message.config_overridden_on" : "malilib.message.config_overridden_off";
            message = StringUtils.translate(msgKey, config.getPrettyName());
        }
        else if (config.isLocked())
        {
            String msgKey = newValue ? "malilib.message.config_locked_on" : "malilib.message.config_locked_off";
            message = StringUtils.translate(msgKey, config.getPrettyName());
        }
        else if (messageFactory != null)
        {
            message = messageFactory.apply(config);
        }
        else
        {
            message = getBasicBooleanConfigToggleMessage(config);
        }

        return message;
    }

    public static String getBasicBooleanConfigToggleMessage(BooleanConfig config)
    {
        String msgKey = config.getBooleanValue() ? "malilib.message.toggled_config_on" : "malilib.message.toggled_config_off";
        return StringUtils.translate(msgKey, config.getPrettyName());
    }
}
