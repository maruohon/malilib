package fi.dy.masa.malilib.message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.listener.EventListener;

public class InfoArea
{
    protected final HashMap<String, InfoRendererWidget> infoWidgetMap = new HashMap<>();
    protected final ArrayList<InfoRendererWidget> enabledInfoWidgetsList = new ArrayList<>();
    protected final ScreenLocation location;
    protected final IntSupplier viewportWidthSupplier;
    protected final IntSupplier viewportHeightSupplier;
    @Nullable protected final EventListener reLayoutRequestListener;
    protected boolean needsReLayout;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int offsetX = 4;
    protected int offsetY = 4;

    public InfoArea(ScreenLocation location, @Nullable EventListener reLayoutRequestListener)
    {
        this(location, GuiUtils::getScaledWindowWidth, GuiUtils::getScaledWindowHeight, reLayoutRequestListener);
    }

    public InfoArea(ScreenLocation location, IntSupplier viewportWidthSupplier, IntSupplier viewportHeightSupplier, @Nullable EventListener reLayoutRequestListener)
    {
        this.location = location;
        this.reLayoutRequestListener = reLayoutRequestListener;
        this.viewportWidthSupplier = viewportWidthSupplier;
        this.viewportHeightSupplier = viewportHeightSupplier;
    }

    @SuppressWarnings("unchecked")
    public <T extends InfoRendererWidget> T getOrCreateInfoWidget(String id, Class<T> clazz, Supplier<T> factory)
    {
        InfoRendererWidget widget = this.infoWidgetMap.get(id);

        if (widget == null || clazz.isInstance(widget) == false)
        {
            widget = factory.get();
            this.putInfoWidget(id, widget);
        }

        return (T) widget;
    }

    @Nullable
    public InfoRendererWidget getInfoWidget(String id)
    {
        return this.infoWidgetMap.get(id);
    }

    public void putInfoWidget(String id, InfoRendererWidget widget)
    {
        widget.setContainer(this);
        widget.setLocation(this.location);
        this.infoWidgetMap.put(id, widget);
        this.requestReLayout();
    }

    public List<InfoRendererWidget> getEnabledWidgets()
    {
        if (this.needsReLayout)
        {
            this.reLayoutWidgets();
            this.needsReLayout = false;
        }

        return this.enabledInfoWidgetsList;
    }

    /**
     * Requests all the widgets to be laid out again, ignoring any widgets that are disabled,
     * and updating the ordering of the widgets and taking into account their current, possibly updated sizes.
     */
    public void requestReLayout()
    {
        this.needsReLayout = true;

        if (this.reLayoutRequestListener != null)
        {
            this.reLayoutRequestListener.onEvent();
        }
    }

    protected void reLayoutWidgets()
    {
        this.enabledInfoWidgetsList.clear();

        for (InfoRendererWidget widget : this.infoWidgetMap.values())
        {
            if (widget.isEnabled())
            {
                this.enabledInfoWidgetsList.add(widget);
            }
        }

        this.enabledInfoWidgetsList.sort(Comparator.comparing(InfoRendererWidget::getSortIndex));
        this.updateSizeAndPosition();
    }

    public void updateSize()
    {
        int width = 0;
        int height = 0;

        for (InfoRendererWidget widget : this.enabledInfoWidgetsList)
        {
            width = Math.max(width, widget.getWidth());
            height += widget.getHeight();
        }

        this.width = width;
        this.height = height;

        for (InfoRendererWidget widget : this.enabledInfoWidgetsList)
        {
            widget.setContainerDimensions(width, height);
        }
    }

    public void updatePosition()
    {
        this.x = this.location.getStartX(this.width, this.viewportWidthSupplier.getAsInt(), this.offsetX);
        this.y = this.location.getStartY(this.height, this.viewportHeightSupplier.getAsInt(), this.offsetY);

        int y = this.y;

        for (InfoRendererWidget widget : this.enabledInfoWidgetsList)
        {
            widget.setPosition(this.x, y);
            y += widget.getHeight();
        }
    }

    public void updateSizeAndPosition()
    {
        this.updateSize();
        this.updatePosition();
    }
}
