package fi.dy.masa.malilib.message;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import fi.dy.masa.malilib.gui.position.EdgeInt;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.listener.EventListener;

public abstract class InfoRendererWidget extends BaseWidget
{
    protected final EdgeInt padding = new EdgeInt();
    protected ScreenLocation location = ScreenLocation.TOP_LEFT;
    @Nullable protected EventListener geometryChangeListener;
    protected boolean enabled = true;
    protected long previousGeometryUpdateTime = -1;
    protected long geometryShrinkDelay = (long) (4 * 1E9); // 4 seconds
    protected int sortIndex = 100;
    protected int containerWidth;
    protected int containerHeight;
    protected int geometryShrinkThresholdX = 40;
    protected int geometryShrinkThresholdY = 10;
    protected int previousUpdatedWidth;
    protected int previousUpdatedHeight;

    public InfoRendererWidget()
    {
        super(0, 0, 0, 0);
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public int getSortIndex()
    {
        return this.sortIndex;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Sets the sort index of this widget. Lower values come first (higher up).
     * @param index
     */
    public void setSortIndex(int index)
    {
        this.sortIndex = index;
    }

    public void setPadding(EdgeInt padding)
    {
        this.padding.setFrom(padding);
    }

    /**
     * Sets a listener that should be notified if the dimensions of this widget get changed,
     * such as the widget height or width changing due to changes in the displayed contents.
     * @param listener
     */
    public void setGeometryChangeListener(@Nullable EventListener listener)
    {
        this.geometryChangeListener = listener;
    }

    public void setContainerDimensions(int width, int height)
    {
        this.containerWidth = width;
        this.containerHeight = height;
    }

    public void setLocation(ScreenLocation location)
    {
        this.location = location;
    }

    /**
     * Requests the container to re-layout all the info widgets due to
     * this widget's dimensions changing.
     */
    protected void updateContainerLayout()
    {
        if (this.geometryChangeListener != null && this.needsGeometryUpdate())
        {
            this.geometryChangeListener.onEvent();
            this.previousUpdatedWidth = this.getWidth();
            this.previousUpdatedHeight = this.getHeight();
            this.previousGeometryUpdateTime = System.nanoTime();
        }
    }

    protected boolean needsGeometryUpdate()
    {
        int height = this.getHeight();
        int width = this.getWidth();

        if (width > this.previousUpdatedWidth || height > this.previousUpdatedHeight)
        {
            return true;
        }

        if (width < (this.previousUpdatedWidth - this.geometryShrinkThresholdX) ||
            height < (this.previousUpdatedHeight - this.geometryShrinkThresholdY))
        {
            return System.nanoTime() - this.previousGeometryUpdateTime > this.geometryShrinkDelay;
        }

        return false;
    }

    public int getPaddedWidth()
    {
        return this.getWidth() + this.padding.getLeft() + this.padding.getRight();
    }

    public int getPaddedHeight()
    {
        return this.getHeight() + this.padding.getTop() + this.padding.getBottom();
    }

    /**
     * Called to allow the widget to update its state before all the enabled widgets are rendered.
     */
    public void updateState(Minecraft mc)
    {
    }

    public void render()
    {
        if (this.isEnabled())
        {
            int x = this.getX() + this.padding.getLeft();
            int y = this.getY() + this.padding.getTop();
            this.renderAt(x, y, this.getZLevel());
        }
    }

    public abstract void renderAt(int x, int y, float z);
}
