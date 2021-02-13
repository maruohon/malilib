package fi.dy.masa.malilib.message;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.widget.BaseWidget;

public abstract class InfoRendererWidget extends BaseWidget
{
    @Nullable protected InfoArea infoArea;
    protected ScreenLocation location = ScreenLocation.TOP_LEFT;
    protected boolean enabled = true;
    protected int sortIndex = 100;
    protected int containerWidth;
    protected int containerHeight;

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

    public void setContainer(InfoArea infoArea)
    {
        this.infoArea = infoArea;
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
        if (this.infoArea != null)
        {
            this.infoArea.requestReLayout();
        }
    }

    /**
     * Called to allow the widget to update its state before all the enabled widgets are rendered.
     */
    public void updateState()
    {
    }

    public void render()
    {
        if (this.isEnabled())
        {
            this.renderAt(this.getX(), this.getY(), this.getZLevel());
        }
    }

    public abstract void renderAt(int x, int y, float z);
}
