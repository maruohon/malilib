package fi.dy.masa.malilib.overlay.widget;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.Queues;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.overlay.widget.sub.ToastWidget;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.util.JsonUtils;

public class ToastRendererWidget extends InfoRendererWidget
{
    protected final List<ToastWidget> activeToasts = new ArrayList<>();
    protected final Deque<ToastWidget> toastQueue = Queues.newArrayDeque();
    protected int defaultLifeTime = 5000;
    protected int fadeInTime = 250;
    protected int fadeOutTime = 250;
    protected int maxToastWidth = 320;
    protected int maxToasts = 5;

    public ToastRendererWidget()
    {
        this.isOverlay = true;
    }

    public void addToast(StyledText toastText)
    {
        this.addToast(toastText, this.defaultLifeTime);
    }

    public void addToast(StyledText toastText, int lifeTimeMs)
    {
        this.addToast(toastText, lifeTimeMs, this.fadeInTime, this.fadeOutTime);
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
        this.addToast(toastText, this.defaultLifeTime, this.fadeInTime, this.fadeOutTime, marker, append);
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
        this.addToast(toastText, lifeTimeMs, this.fadeInTime, this.fadeOutTime, marker, append);
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
        widget.setMaxWidth(this.maxToastWidth);
        widget.setZLevel(this.getZLevel() + 1f);
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
        if (marker != null && this.activeToasts.isEmpty() == false)
        {
            for (ToastWidget widget : this.activeToasts)
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

        width += this.getPadding().getHorizontalTotal();

        this.setWidth(width);
    }

    @Override
    public void updateHeight()
    {
        this.setHeight(this.getTotalToastHeight() + this.getPadding().getVerticalTotal());
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
        int x;
        int y = this.getY();
        int width = this.viewportWidthSupplier.getAsInt();
        HorizontalAlignment align = this.location.horizontalLocation;

        for (ToastWidget widget : this.activeToasts)
        {
            x = align.getStartX(widget.getWidth(), width, 0);
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
    public void updateState()
    {
        this.addToastsFromQueue();
        super.updateState();
    }

    @Override
    protected void renderContents(int x, int y, float z)
    {
        this.drawMessages(x, y, z);
    }

    public void drawMessages(int x, int y, float z)
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
                    widget.render(currentTime);
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
        obj.addProperty("toast_lifetime", this.defaultLifeTime);
        obj.addProperty("toast_fade_in", this.fadeInTime);
        obj.addProperty("toast_fade_out", this.fadeOutTime);
        obj.addProperty("max_toast_width", this.maxToastWidth);

        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);

        this.maxToasts = JsonUtils.getIntegerOrDefault(obj, "max_toasts", this.maxToasts);
        this.defaultLifeTime = JsonUtils.getIntegerOrDefault(obj, "toast_lifetime", this.defaultLifeTime);
        this.fadeInTime = JsonUtils.getIntegerOrDefault(obj, "toast_fade_in", this.fadeInTime);
        this.fadeOutTime = JsonUtils.getIntegerOrDefault(obj, "toast_fade_out", this.fadeOutTime);
        this.maxToastWidth = JsonUtils.getIntegerOrDefault(obj, "max_toast_width", this.maxToastWidth);
    }
}
