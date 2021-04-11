package fi.dy.masa.malilib.overlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.position.EdgeInt;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.overlay.widget.InfoRendererWidget;

public class InfoArea
{
    protected final ArrayList<InfoRendererWidget> allWidgets = new ArrayList<>();
    protected final ArrayList<InfoRendererWidget> enabledInfoWidgets = new ArrayList<>();
    protected final ArrayList<InfoRendererWidget> allEnabledWidgets = new ArrayList<>();
    protected final ScreenLocation location;
    protected final IntSupplier viewportWidthSupplier;
    protected final IntSupplier viewportHeightSupplier;
    @Nullable protected final EventListener enabledWidgetsChangedListener;
    protected boolean needsReLayout;
    protected boolean needsWidgetUpdate;
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public InfoArea(ScreenLocation location,
                    @Nullable EventListener enabledWidgetsChangedListener)
    {
        this(location, GuiUtils::getScaledWindowWidth, GuiUtils::getScaledWindowHeight, enabledWidgetsChangedListener);
    }

    public InfoArea(ScreenLocation location,
                    IntSupplier viewportWidthSupplier,
                    IntSupplier viewportHeightSupplier,
                    @Nullable EventListener enabledWidgetsChangedListener)
    {
        this.location = location;
        this.enabledWidgetsChangedListener = enabledWidgetsChangedListener;
        this.viewportWidthSupplier = viewportWidthSupplier;
        this.viewportHeightSupplier = viewportHeightSupplier;
    }

    /**
     * Returns the first widget that passes the test, if any
     */
    @Nullable
    public <C extends InfoRendererWidget> C findWidget(Class<C> clazz, Predicate<C> predicate)
    {
        return InfoOverlay.findWidget(clazz, predicate, this.allWidgets);
    }

    public void addWidget(InfoRendererWidget widget)
    {
        //System.out.printf("InfoArea(%s)#addWidget() - all: %d, enabled: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size());
        if (this.allWidgets.contains(widget) == false)
        {
            boolean isOverlay = widget.isOverlay();
            this.addWidgetImpl(widget, isOverlay);
            this.notifyEnabledWidgetsChanged(isOverlay == false);
        }
    }

    protected void addWidgetImpl(InfoRendererWidget widget, final boolean isOverlay)
    {
        //System.out.printf("InfoArea(%s)#addWidgetImpl() - all: %d, enabled: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size());
        widget.setViewportSizeSuppliers(this.viewportWidthSupplier, this.viewportHeightSupplier);
        widget.setLocation(this.location);
        widget.setEnabledChangeListener(() -> this.notifyEnabledWidgetsChanged(isOverlay == false));
        widget.setGeometryChangeListener(this::requestReLayout);
        this.allWidgets.add(widget);
        widget.onAdded();
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

        this.notifyEnabledWidgetsChanged(true);
    }

    public void removeWidget(InfoRendererWidget widget)
    {
        //System.out.printf("InfoArea(%s)#removeWidget() - all: %d, enabled: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size());
        if (this.allWidgets.remove(widget))
        {
            this.notifyEnabledWidgetsChanged(widget.isOverlay() == false);
        }
    }

    public void removeWidgets(Collection<InfoRendererWidget> widgets)
    {
        //System.out.printf("InfoArea(%s)#removeWidgets() - all: %d, enabled: %d, removing: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size(), widgets.size());
        HashSet<InfoRendererWidget> set = new HashSet<>(widgets);

        if (this.allWidgets.removeAll(set))
        {
            this.notifyEnabledWidgetsChanged(true);
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
    }

    /**
     * Notifies the InfoArea that the set of enabled widgets has changed.
     * Note that this alone does not cause a re-layout of the widgets,
     * unless the needsReLayout argument is true.
     * <br>
     * This also notifies the listener (usually the InfoOverlay) that
     * the set of enabled widgets has changed and should be re-fetched.
     */
    public void notifyEnabledWidgetsChanged(boolean needsReLayout)
    {
        //System.out.printf("InfoArea(%s)#notifyWidgetChange() - all: %d, enabled: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size());
        this.needsWidgetUpdate = true;

        if (needsReLayout)
        {
            this.requestReLayout();
        }

        if (this.enabledWidgetsChangedListener != null)
        {
            this.enabledWidgetsChangedListener.onEvent();
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

    public void updateState()
    {
        if (this.needsReLayout)
        {
            this.reLayoutWidgets();
        }
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
        int prev = 0;

        for (InfoRendererWidget widget : this.enabledInfoWidgets)
        {
            EdgeInt margin = widget.getMargin();
            int topGap = Math.max(prev, margin.getTop());
            prev = margin.getBottom();

            int ww = (int) Math.ceil(widget.getWidth() * widget.getScale());
            int wh = (int) Math.ceil(widget.getHeight() * widget.getScale());
            width = Math.max(width, ww + margin.getHorizontalTotal());
            height += wh + topGap;
        }

        height += prev;
        this.width = width;
        this.height = height;
    }

    /**
     * Updates both the InfoArea position, and all the contained enabled widgets' positions
     */
    public void updatePositions()
    {
        //System.out.printf("InfoArea(%s)#updatePositions() - all: %d, enabled: %d\n", this.location, this.allWidgets.size(), this.enabledInfoWidgets.size());
        int viewportWidth = this.viewportWidthSupplier.getAsInt();
        int viewportHeight = this.viewportHeightSupplier.getAsInt();

        this.x = this.location.getStartX(this.width, viewportWidth, 0);
        this.y = this.location.getStartY(this.height, viewportHeight, 0);

        int y = this.y;
        int prev = 0;

        for (InfoRendererWidget widget : this.enabledInfoWidgets)
        {
            EdgeInt margin = widget.getMargin();
            int width = (int) Math.ceil(widget.getWidth() * widget.getScale());
            int height = (int) Math.ceil(widget.getHeight() * widget.getScale());
            int x = this.location.getStartX(width + margin.getHorizontalTotal(), viewportWidth, 0);
            int topGap = Math.max(prev, margin.getTop());
            prev = margin.getBottom();
            y += topGap;

            widget.setPosition(x + margin.getLeft(), y);
            y += height;
        }
    }

    public void renderDebug()
    {
        BaseWidget.renderDebugOutline(this.x, this.y, 0, this.width, this.height, false);
    }
}
