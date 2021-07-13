package fi.dy.masa.malilib.overlay.message;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.overlay.InfoOverlay;
import fi.dy.masa.malilib.overlay.InfoWidgetManager;
import fi.dy.masa.malilib.overlay.widget.InfoRendererWidget;
import fi.dy.masa.malilib.overlay.widget.MessageRendererWidget;
import fi.dy.masa.malilib.overlay.widget.ToastRendererWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class MessageUtils
{
    protected static final Pattern PATTERN_TIME_MSG = Pattern.compile("time=(?<time>[0-9]+);(?<msg>.*)");

    public static final String CUSTOM_ACTION_BAR_MARKER = "malilib_actionbar";


    public static MessageRendererWidget getMessageRendererWidget(@Nullable final ScreenLocation location,
                                                                 @Nullable final String marker)
    {
        MessageRendererWidget widget = findInfoWidget(MessageRendererWidget.class, location, marker);

        if (widget == null)
        {
            widget = new MessageRendererWidget();
            widget.setLocation(location != null ? location : ScreenLocation.CENTER);
            widget.setZ(300f);
            widget.setWidth(300);
            widget.setRenderAboveScreen(true);

            if (marker != null)
            {
                widget.getMarkerManager().addMarker(marker);
            }

            InfoWidgetManager.INSTANCE.addWidget(widget);
        }

        return widget;
    }

    public static MessageRendererWidget getCustomActionBarMessageRenderer()
    {
        MessageRendererWidget widget = findInfoWidget(MessageRendererWidget.class, null, CUSTOM_ACTION_BAR_MARKER);

        if (widget == null)
        {
            widget = new MessageRendererWidget();
            widget.setLocation(ScreenLocation.BOTTOM_CENTER);
            widget.getMarkerManager().addMarker(CUSTOM_ACTION_BAR_MARKER);
            widget.setZ(300f);
            widget.getMargin().setBottom(50);
            widget.setMessageGap(2);
            widget.setAutomaticWidth(true);
            widget.setName(StringUtils.translate("malilib.label.default_custom_hotbar_message_renderer"));
            widget.setMaxMessages(MaLiLibConfigs.Generic.ACTION_BAR_MESSAGE_LIMIT.getIntegerValue());
            InfoWidgetManager.INSTANCE.addWidget(widget);
        }

        return widget;
    }

    public static ToastRendererWidget getToastRendererWidget(@Nullable final ScreenLocation location,
                                                             @Nullable final String marker)
    {
        ToastRendererWidget widget = findInfoWidget(ToastRendererWidget.class, location, marker);

        if (widget == null)
        {
            widget = new ToastRendererWidget();
            widget.setLocation(location != null ? location : ScreenLocation.TOP_RIGHT);
            widget.setZ(310f);

            if (marker != null)
            {
                widget.getMarkerManager().addMarker(marker);
            }

            InfoWidgetManager.INSTANCE.addWidget(widget);
        }

        return widget;
    }

    @Nullable
    public static <T extends InfoRendererWidget> T findInfoWidget(Class<T> widgetClass,
                                                                  @Nullable final ScreenLocation location,
                                                                  @Nullable final String marker)
    {
        Predicate<T> predicateLocation = location != null ? w -> w.getScreenLocation() == location : w -> true;
        Predicate<T> predicateMarker = w -> w.getMarkerManager().matchesMarker(marker);
        Predicate<T> filter = predicateLocation.and(predicateMarker);
        return InfoOverlay.INSTANCE.findWidget(widgetClass, filter);
    }

    public static void printCustomActionbarMessage(String translationKey, Object... args)
    {
        MessageDispatcher.generic().type(MessageOutput.CUSTOM_HOTBAR).time(5000).fadeOut(500).translate(translationKey, args);
    }

    public static void printBooleanConfigToggleMessage(MessageOutput type, BooleanConfig config,
                                                       @Nullable Function<BooleanConfig, String> messageFactory)
    {
        String msg = MessageHelpers.getBooleanConfigToggleMessage(config, messageFactory);

        if (org.apache.commons.lang3.StringUtils.isBlank(msg) == false)
        {
            MessageDispatcher.generic().type(type).time(5000).send(msg);
        }
    }

    public static ActionResult addMessageAction(ActionContext ctx, String msg)
    {
        return addMessageAction(MessageOutput.MESSAGE_OVERLAY, msg);
    }

    public static ActionResult addToastAction(ActionContext ctx, String msg)
    {
        return addMessageAction(MessageOutput.TOAST, msg);
    }

    public static ActionResult addMessageAction(MessageOutput type, String msg)
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

        MessageDispatcher.generic().type(type).time(displayTimeMs).send(msg);

        return ActionResult.SUCCESS;
    }

}
