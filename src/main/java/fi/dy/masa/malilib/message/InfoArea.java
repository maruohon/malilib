package fi.dy.masa.malilib.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.listener.EventListener;

public class InfoArea
{
    protected final ArrayList<InfoRendererWidget> allWidgets = new ArrayList<>();
    protected final ArrayList<InfoRendererWidget> enabledInfoWidgets = new ArrayList<>();
    protected final ArrayList<InfoRendererWidget> allEnabledWidgets = new ArrayList<>();
    protected final ScreenLocation location;
    protected final IntSupplier viewportWidthSupplier;
    protected final IntSupplier viewportHeightSupplier;
    @Nullable protected final EventListener widgetChangeListener;
    protected boolean needsReLayout;
    protected boolean needsWidgetUpdate;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int offsetX;
    protected int offsetY;

    public InfoArea(ScreenLocation location,
                    @Nullable EventListener widgetChangeListener)
    {
        this(location, GuiUtils::getScaledWindowWidth, GuiUtils::getScaledWindowHeight, widgetChangeListener);
    }

    public InfoArea(ScreenLocation location,
                    IntSupplier viewportWidthSupplier,
                    IntSupplier viewportHeightSupplier,
                    @Nullable EventListener widgetChangeListener)
    {
        this.location = location;
        this.widgetChangeListener = widgetChangeListener;
        this.viewportWidthSupplier = viewportWidthSupplier;
        this.viewportHeightSupplier = viewportHeightSupplier;
    }

    /**
     * Returns the first widget that passes the test, if any
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <C extends InfoRendererWidget> C findWidget(Class<C> clazz, Predicate<InfoRendererWidget> predicate)
    {
        for (InfoRendererWidget widget : this.allWidgets)
        {
            if (clazz.isAssignableFrom(widget.getClass()) && predicate.test(widget))
            {
                return (C) widget;
            }
        }

        return null;
    }

    public void addWidget(InfoRendererWidget widget)
    {
        //System.out.printf("InfoArea(%s)#addWidget() - all: %d, enabled: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size());
        if (this.allWidgets.contains(widget) == false)
        {
            boolean isOverlay = widget.isOverlay();
            this.addWidgetImpl(widget, isOverlay);
            this.notifyWidgetChange(isOverlay == false);
        }
    }

    protected void addWidgetImpl(InfoRendererWidget widget, final boolean isOverlay)
    {
        //System.out.printf("InfoArea(%s)#addWidgetImpl() - all: %d, enabled: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size());
        widget.setLocation(this.location);
        widget.setEnabledChangeListener(() -> this.notifyWidgetChange(isOverlay == false));
        widget.setGeometryChangeListener(this::requestReLayout);

        this.allWidgets.add(widget);

        if (isOverlay)
        {
            this.updateOverlayWidgetPosition(widget);
        }
    }

    public void addWidgets(Collection<InfoRendererWidget> widgets)
    {
        //System.out.printf("InfoArea(%s)#addWidgets() - all: %d, enabled: %d, adding: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size(), widgets.size());
        for (InfoRendererWidget widget : widgets)
        {
            if (this.allWidgets.contains(widget) == false)
            {
                this.addWidgetImpl(widget, widget.isOverlay());
            }
        }

        this.notifyWidgetChange(true);
    }

    public void removeWidget(InfoRendererWidget widget)
    {
        //System.out.printf("InfoArea(%s)#removeWidget() - all: %d, enabled: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size());
        if (this.allWidgets.remove(widget))
        {
            this.notifyWidgetChange(widget.isOverlay() == false);
        }
    }

    public void removeWidgets(Collection<InfoRendererWidget> widgets)
    {
        //System.out.printf("InfoArea(%s)#removeWidgets() - all: %d, enabled: %d, removing: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size(), widgets.size());
        HashSet<InfoRendererWidget> set = new HashSet<>(widgets);

        if (this.allWidgets.removeAll(set))
        {
            this.notifyWidgetChange(true);
        }
    }

    public List<InfoRendererWidget> getEnabledWidgets()
    {
        //System.out.printf("InfoArea(%s)#getEnabledWidgets() - all: %d, enabled: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size());
        if (this.needsWidgetUpdate)
        {
            this.updateEnabledWidgets();
        }

        if (this.needsReLayout)
        {
            this.reLayoutWidgets();
        }

        return this.allEnabledWidgets;
    }

    /**
     * Requests all the widgets to be laid out again, ignoring any widgets that are disabled,
     * and updating the ordering of the widgets and taking into account their current, possibly updated sizes.
     */
    public void requestReLayout()
    {
        this.needsReLayout = true;

        if (this.widgetChangeListener != null)
        {
            this.widgetChangeListener.onEvent();
        }
    }

    /**
     * Notifies the InfoArea that the set of enabled widgets has changed.
     * Note that this alone does not cause a re-layout of the widgets,
     * unless the needsReLayout argument is true.
     */
    public void notifyWidgetChange(boolean needsReLayout)
    {
        //System.out.printf("InfoArea(%s)#notifyWidgetChange() - all: %d, enabled: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size());
        this.needsWidgetUpdate = true;

        if (needsReLayout)
        {
            this.requestReLayout();
        }

        if (this.widgetChangeListener != null)
        {
            this.widgetChangeListener.onEvent();
        }
    }

    protected void updateEnabledWidgets()
    {
        //System.out.printf("InfoArea(%s)#updateEnabledWidgets() - PRE  - all: %d, enabled: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size());
        this.allEnabledWidgets.clear();
        this.enabledInfoWidgets.clear();

        for (InfoRendererWidget widget : this.allWidgets)
        {
            if (widget.isEnabled())
            {
                this.allEnabledWidgets.add(widget);

                if (widget.isOverlay() == false)
                {
                    this.enabledInfoWidgets.add(widget);
                }
            }
        }

        this.needsWidgetUpdate = false;
        //System.out.printf("InfoArea(%s)#updateEnabledWidgets() - POST - all: %d, enabled: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size());
    }

    protected void reLayoutWidgets()
    {
        //System.out.printf("InfoArea(%s)#reLayoutWidgets() - all: %d, enabled: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size());
        this.enabledInfoWidgets.sort(Comparator.comparing(InfoRendererWidget::getSortIndex));

        this.updateSize();
        this.updatePositions();

        this.needsReLayout = false;
    }

    /**
     * Updates the size of the InfoArea so that it tightly encloses
     * all the currently enabled widgets.
     */
    public void updateSize()
    {
        //System.out.printf("InfoArea(%s)#updateSize() - all: %d, enabled: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size());
        int width = 0;
        int height = 0;

        for (InfoRendererWidget widget : this.enabledInfoWidgets)
        {
            width = Math.max(width, widget.getWidth());
            height += widget.getHeight();
        }

        this.width = width;
        this.height = height;

        for (InfoRendererWidget widget : this.enabledInfoWidgets)
        {
            widget.setContainerDimensions(width, height);
        }
    }

    /**
     * Updates both the InfoArea position, and all the contained enabled widgets' positions
     */
    public void updatePositions()
    {
        //System.out.printf("InfoArea(%s)#updatePositions() - all: %d, enabled: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size());
        int viewportWidth = this.viewportWidthSupplier.getAsInt();
        int viewportHeight = this.viewportHeightSupplier.getAsInt();

        this.x = this.location.getStartX(this.width, viewportWidth, this.offsetX);
        this.y = this.location.getStartY(this.height, viewportHeight, this.offsetY);

        int y = this.y;

        for (InfoRendererWidget widget : this.enabledInfoWidgets)
        {
            int x = this.location.getStartX(widget.getWidth(), viewportWidth, this.offsetX);
            widget.setPosition(x, y);
            y += widget.getHeight();
        }
    }

    protected void updateOverlayWidgetPosition(InfoRendererWidget widget)
    {
        //System.out.printf("InfoArea(%s)#updateOverlayWidgetPosition() - all: %d, enabled: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size());
        int viewportWidth = this.viewportWidthSupplier.getAsInt();
        int viewportHeight = this.viewportHeightSupplier.getAsInt();
        int x = this.location.getStartX(widget.getWidth(), viewportWidth, this.offsetX);
        int y = this.location.getStartY(widget.getHeight(), viewportHeight, this.offsetY);

        widget.setPosition(x, y);
    }

    public void renderDebug()
    {
        BaseWidget.renderDebugOutline(this.x, this.y, 0, this.width, this.height, false);
    }
}
