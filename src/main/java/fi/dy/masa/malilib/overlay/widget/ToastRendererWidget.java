package fi.dy.masa.malilib.overlay.widget;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.Queues;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.ToastRendererWidgetEditScreen;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.overlay.widget.sub.ToastWidget;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.util.JsonUtils;

public class ToastRendererWidget extends InfoRendererWidget
{
    protected final List<ToastWidget> activeToasts = new ArrayList<>();
    protected final Deque<ToastWidget> toastQueue = Queues.newArrayDeque();
    protected int defaultLifeTime = 5000;
    protected int defaultFadeInTime = 200;
    protected int defaultFadeOutTime = 200;
    protected int maxToasts = 5;
    protected int messageGap = 4;

    public ToastRendererWidget()
    {
        super();

        this.isOverlay = true;
        this.shouldSerialize = true;
        this.setName("Toast Renderer");
        this.setMaxWidth(240);
        this.padding.setAll(6, 10, 6, 10);
    }

    public int getMessageGap()
    {
        return this.messageGap;
    }

    public void setMessageGap(int messageGap)
    {
        this.messageGap = messageGap;
    }

    public int getDefaultLifeTime()
    {
        return this.defaultLifeTime;
    }

    public void setDefaultLifeTime(int defaultLifeTime)
    {
        this.defaultLifeTime = defaultLifeTime;
    }

    public int getDefaultFadeInTime()
    {
        return this.defaultFadeInTime;
    }

    public void setDefaultFadeInTime(int defaultFadeInTime)
    {
        this.defaultFadeInTime = defaultFadeInTime;
    }

    public int getDefaultFadeOutTime()
    {
        return this.defaultFadeOutTime;
    }

    public void setDefaultFadeOutTime(int defaultFadeOutTime)
    {
        this.defaultFadeOutTime = defaultFadeOutTime;
    }

    public int getMaxToasts()
    {
        return this.maxToasts;
    }

    public void setMaxToasts(int maxToasts)
    {
        this.maxToasts = maxToasts;
    }

    public void addToast(StyledText toastText)
    {
        this.addToast(toastText, this.defaultLifeTime);
    }

    public void addToast(StyledText toastText, int lifeTimeMs)
    {
        this.addToast(toastText, lifeTimeMs, this.defaultFadeInTime, this.defaultFadeOutTime);
    }

    public void addToast(StyledText toastText, int lifeTimeMs, int fadeInTimeMs, int fadeOutTimeMs)
    {
        this.addToast(toastText, lifeTimeMs, fadeInTimeMs, fadeOutTimeMs, null, false);
    }

    /**
     * Adds a toast message
     * @param toastText the message to use
     * @param marker the widget marker, if any. If a marker is set, then the same existing
     *               toast widget can be used for other future messages as well, by using the same marker.
     * @param append if true, and a marker was used and a matching existing toast widget was found,
     *               then the text will be appended to the old toast. If false, then the text
     *               will replace the existing text in the existing toast widget.
     */
    public void addToast(StyledText toastText, @Nullable String marker, boolean append)
    {
        this.addToast(toastText, this.defaultLifeTime, this.defaultFadeInTime, this.defaultFadeOutTime, marker, append);
    }

    /**
     * Adds a toast message
     * @param toastText the message to use
     * @param lifeTimeMs the display time of the toast, in milliseconds
     * @param marker the widget marker, if any. If a marker is set, then the same existing
     *               toast widget can be used for other future messages as well, by using the same marker.
     * @param append if true, and a marker was used and a matching existing toast widget was found,
     *               then the text will be appended to the old toast. If false, then the text
     *               will replace the existing text in the existing toast widget.
     */
    public void addToast(StyledText toastText, int lifeTimeMs, @Nullable String marker, boolean append)
    {
        this.addToast(toastText, lifeTimeMs, this.defaultFadeInTime, this.defaultFadeOutTime, marker, append);
    }

    /**
     * Adds a toast message
     * @param toastText the message to use
     * @param lifeTimeMs the display time of the toast, in milliseconds
     * @param fadeInTimeMs the fade in time (or rather slide in time) in milliseconds
     * @param fadeOutTimeMs the fade out time (or rather slide out time) in milliseconds
     * @param marker the widget marker, if any. If a marker is set, then the same existing
     *               toast widget can be used for other future messages as well, by using the same marker.
     * @param append if true, and a marker was used and a matching existing toast widget was found,
     *               then the text will be appended to the old toast. If false, then the text
     *               will replace the existing text in the existing toast widget.
     */
    public void addToast(StyledText toastText, int lifeTimeMs, int fadeInTimeMs, int fadeOutTimeMs,
                         @Nullable String marker, boolean append)
    {
        if (this.tryAppendTextToExistingToast(toastText, lifeTimeMs, marker, append))
        {
            return;
        }

        ToastWidget widget = new ToastWidget(fadeInTimeMs, fadeOutTimeMs, this.location.horizontalLocation);
        widget.getPadding().setFrom(this.getPadding());
        widget.setMaxWidth(this.getMaxWidth());
        widget.setLineHeight(this.getLineHeight());
        widget.setMessageGap(this.messageGap);
        widget.setZLevel(this.getZLevel() + 1f);
        widget.getTextSettings().setFrom(this.getTextSettings());
        // The text needs to be set after the max width and padding have been set
        widget.replaceText(toastText, lifeTimeMs);

        if (marker != null)
        {
            widget.addMarker(marker);
        }

        this.toastQueue.add(widget);
    }

    protected boolean tryAppendTextToExistingToast(StyledText toastText, int lifeTimeMs,
                                                   @Nullable String marker, boolean append)
    {
        if (marker != null)
        {
            List<ToastWidget> list = new ArrayList<>(this.activeToasts);
            list.addAll(this.toastQueue);

            for (ToastWidget widget : list)
            {
                if (widget.hasMarker(marker))
                {
                    if (append && widget.getRelativeAge() <= 0.25f)
                    {
                        widget.addText(toastText, -1);
                        this.updateSizeAndPosition();
                        return true;
                    }
                    else if (append == false)
                    {
                        widget.replaceText(toastText, lifeTimeMs);
                        this.updateSizeAndPosition();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void onAdded()
    {
        this.updateSizeAndPosition();
    }

    protected void updateSizeAndPosition()
    {
        this.updateSize();
        this.updateWidgetPosition();
        this.updateSubWidgetPositions();
    }

    @Override
    public void updateWidth()
    {
        int width = 0;

        for (ToastWidget widget : this.activeToasts)
        {
            width = Math.max(width, widget.getWidth());
        }

        this.setWidth(width);
    }

    @Override
    public void updateHeight()
    {
        this.setHeight(this.getTotalToastHeight());
    }

    protected int getTotalToastHeight()
    {
        int height = 0;

        for (ToastWidget widget : this.activeToasts)
        {
            height += widget.getHeight();
        }

        return height;
    }

    @Override
    public void updateSubWidgetPositions()
    {
        int width = this.viewportWidthSupplier.getAsInt();
        HorizontalAlignment align = this.location.horizontalLocation;
        int x;
        int y = this.getY() + this.location.verticalLocation.getMargin(this.margin);

        for (ToastWidget widget : this.activeToasts)
        {
            x = align.getStartX(widget.getWidth(), width, align.getMargin(this.margin));
            widget.setPosition(x, y);
            y += widget.getHeight();
        }
    }

    protected void addToastsFromQueue()
    {
        if (this.activeToasts.size() < this.maxToasts && this.toastQueue.isEmpty() == false)
        {
            int countToAdd = Math.min(this.maxToasts - this.activeToasts.size(), this.toastQueue.size());
            long currentTime = System.nanoTime();

            for (int i = 0; i < countToAdd; ++i)
            {
                ToastWidget widget = this.toastQueue.remove();
                widget.initialize(currentTime);
                this.activeToasts.add(widget);
            }

            this.updateSizeAndPosition();
        }
    }

    @Override
    public void openEditScreen()
    {
        ToastRendererWidgetEditScreen screen = new ToastRendererWidgetEditScreen(this);
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }

    @Override
    public void updateState()
    {
        this.addToastsFromQueue();
        super.updateState();
    }

    @Override
    protected void renderContents(int x, int y, float z, ScreenContext ctx)
    {
        this.drawMessages(x, y, z, ctx);
    }

    public void drawMessages(int x, int y, float z, ScreenContext ctx)
    {
        if (this.activeToasts.isEmpty() == false)
        {
            long currentTime = System.nanoTime();
            int countBefore = this.activeToasts.size();

            for (int i = 0; i < this.activeToasts.size(); ++i)
            {
                ToastWidget widget = this.activeToasts.get(i);

                if (widget.hasExpired(currentTime))
                {
                    this.activeToasts.remove(i);
                    --i;
                }
                else
                {
                    widget.render(currentTime, ctx);
                }

                // Always offset the position to prevent a flicker from the later
                // messages jumping over the fading message when it disappears,
                // before the entire widget gets resized and the messages possibly moving
                // (if the widget is bottom-aligned).
                y += widget.getHeight();
            }

            if (this.activeToasts.size() < countBefore)
            {
                this.updateSizeAndPosition();
            }
        }
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();

        obj.addProperty("max_toasts", this.maxToasts);
        obj.addProperty("max_width", this.maxWidth);
        obj.addProperty("message_gap", this.messageGap);
        obj.addProperty("toast_lifetime", this.defaultLifeTime);
        obj.addProperty("toast_fade_in", this.defaultFadeInTime);
        obj.addProperty("toast_fade_out", this.defaultFadeOutTime);

        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);

        this.maxToasts = JsonUtils.getIntegerOrDefault(obj, "max_toasts", this.maxToasts);
        this.messageGap = JsonUtils.getIntegerOrDefault(obj, "message_gap", this.messageGap);
        this.setMaxWidth(JsonUtils.getIntegerOrDefault(obj, "max_width", this.maxWidth));
        this.defaultLifeTime = JsonUtils.getIntegerOrDefault(obj, "toast_lifetime", this.defaultLifeTime);
        this.defaultFadeInTime = JsonUtils.getIntegerOrDefault(obj, "toast_fade_in", this.defaultFadeInTime);
        this.defaultFadeOutTime = JsonUtils.getIntegerOrDefault(obj, "toast_fade_out", this.defaultFadeOutTime);
    }
}
