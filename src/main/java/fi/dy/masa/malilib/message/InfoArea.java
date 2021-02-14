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
    protected int offsetX;
    protected int offsetY;

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

    @Nullable
    public InfoRendererWidget getWidget(String id)
    {
        return this.infoWidgetMap.get(id);
    }

    public InfoRendererWidget getOrCreateWidget(String id, Supplier<? extends InfoRendererWidget> factory)
    {
        InfoRendererWidget widget = this.infoWidgetMap.get(id);

        if (widget == null)
        {
            widget = factory.get();
            this.putWidget(id, widget);
        }

        return widget;
    }

    public void putWidget(String id, InfoRendererWidget widget)
    {
        widget.setGeometryChangeListener(this::requestReLayout);
        widget.setLocation(this.location);
        this.infoWidgetMap.put(id, widget);
        this.requestReLayout();
    }

    public void removeWidget(String id)
    {
        if (this.infoWidgetMap.remove(id) != null)
        {
            this.requestReLayout();
        }
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

        this.updateSize();
        this.updatePositions();
    }

    /**
     * Updates the size of the InfoArea so that it tightly encloses
     * all the currently enabled widgets.
     */
    public void updateSize()
    {
        int width = 0;
        int height = 0;

        for (InfoRendererWidget widget : this.enabledInfoWidgetsList)
        {
            width = Math.max(width, widget.getPaddedWidth());
            height += widget.getPaddedHeight();
        }

        this.width = width;
        this.height = height;

        for (InfoRendererWidget widget : this.enabledInfoWidgetsList)
        {
            widget.setContainerDimensions(width, height);
        }
    }

    /**
     * Updates both the InfoArea position, and all the contained enabled widgets' positions
     */
    public void updatePositions()
    {
        this.x = this.location.getStartX(this.width, this.viewportWidthSupplier.getAsInt(), this.offsetX);
        this.y = this.location.getStartY(this.height, this.viewportHeightSupplier.getAsInt(), this.offsetY);

        int y = this.y;

        for (InfoRendererWidget widget : this.enabledInfoWidgetsList)
        {
            widget.setPosition(this.x, y);
            y += widget.getPaddedHeight();
        }
    }
}
