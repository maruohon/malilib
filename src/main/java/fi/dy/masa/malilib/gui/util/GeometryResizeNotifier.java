package fi.dy.masa.malilib.gui.util;

import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.position.Vec2i;

public class GeometryResizeNotifier
{
    protected final IntSupplier widthSupplier;
    protected final IntSupplier heightSupplier;
    @Nullable protected EventListener geometryChangeListener;
    protected boolean useDelayedGrow;
    protected boolean useDelayedShrink = true;
    protected boolean delayedGeometryUpdate;
    protected long activeGeometryUpdateDelay = -1;
    protected long previousGeometryUpdateTime = -1;
    protected long geometryGrowDelay = (long) (2 * 1E9); // 2 seconds
    protected long geometryShrinkDelay = (long) (2 * 1E9); // 2 seconds
    protected Vec2i geometryGrowThreshold = Vec2i.ZERO;
    protected Vec2i geometryShrinkThreshold = new Vec2i(40, 10);
    protected Vec2i previousUpdatedSize = Vec2i.ZERO;

    public GeometryResizeNotifier(IntSupplier widthSupplier, IntSupplier heightSupplier)
    {
        this.widthSupplier = widthSupplier;
        this.heightSupplier = heightSupplier;
    }

    /**
     * Sets a listener that should be notified if the dimensions of the tracked object change,
     * such as the widget height or width changing due to changes in the displayed contents.
     */
    public void setGeometryChangeListener(@Nullable EventListener listener)
    {
        this.geometryChangeListener = listener;
    }

    public void setUseDelayedGrow(boolean useDelayedGrow)
    {
        this.useDelayedGrow = useDelayedGrow;
    }

    public void setUseDelayedShrink(boolean useDelayedShrink)
    {
        this.useDelayedShrink = useDelayedShrink;
    }

    public void setGeometryGrowDelay(long geometryGrowDelay)
    {
        this.geometryGrowDelay = geometryGrowDelay;
    }

    public void setGeometryShrinkDelay(long geometryShrinkDelay)
    {
        this.geometryShrinkDelay = geometryShrinkDelay;
    }

    public void setGeometryGrowThreshold(int xThreshold, int yThreshold)
    {
        this.geometryGrowThreshold = new Vec2i(xThreshold, yThreshold);
    }

    public void setGeometryShrinkThreshold(int xThreshold, int yThreshold)
    {
        this.geometryShrinkThreshold = new Vec2i(xThreshold, yThreshold);
    }

    protected int getWidth()
    {
        return this.widthSupplier.getAsInt();
    }

    protected int getHeight()
    {
        return this.heightSupplier.getAsInt();
    }

    public void updateState()
    {
        if (this.needsDelayedGeometryUpdateNow())
        {
            this.notifyContainerOfChanges();
        }
    }

    /**
     * Requests the container to re-layout all the info widgets due to
     * this widget's dimensions changing.
     */
    public void checkAndNotifyContainerOfChanges(boolean forceNotify)
    {
        if (forceNotify ||
            this.checkNeedsImmediateGeometryUpdate() ||
            this.needsDelayedGeometryUpdateNow())
        {
            this.notifyContainerOfChanges();
        }
        else
        {
            this.checkDelayedGeometryUpdate();
        }
    }

    public void notifyContainerOfChanges()
    {
        if (this.geometryChangeListener != null)
        {
            this.geometryChangeListener.onEvent();
            this.previousUpdatedSize = new Vec2i(this.getWidth(), this.getHeight());
            this.previousGeometryUpdateTime = System.nanoTime();
            this.delayedGeometryUpdate = false;
            this.activeGeometryUpdateDelay = -1L;
        }
    }

    protected boolean needsDelayedGeometryUpdateNow()
    {
        return this.delayedGeometryUpdate &&
               System.nanoTime() - this.previousGeometryUpdateTime > this.activeGeometryUpdateDelay;
    }

    protected void setDelayedUpdate(long newDelay)
    {
        long oldDelay = this.activeGeometryUpdateDelay;

        if (oldDelay > 0)
        {
            newDelay = Math.min(oldDelay, newDelay);
        }

        this.activeGeometryUpdateDelay = newDelay;
        this.delayedGeometryUpdate = true;
    }

    protected boolean shouldGrowImmediately(int width, int height)
    {
        return this.useDelayedGrow == false &&
               (width  > this.previousUpdatedSize.x ||
                height > this.previousUpdatedSize.y);
    }

    protected boolean shouldShrinkImmediately(int width, int height)
    {
        return this.useDelayedShrink == false &&
               (width  < this.previousUpdatedSize.x ||
                height < this.previousUpdatedSize.y);
    }

    protected boolean needsToGrowDelayed(int width, int height)
    {
        return width  > (this.previousUpdatedSize.x + this.geometryGrowThreshold.x) ||
               height > (this.previousUpdatedSize.y + this.geometryGrowThreshold.y);
    }

    protected boolean needsToShrinkDelayed(int width, int height)
    {
        return width  < (this.previousUpdatedSize.x - this.geometryGrowThreshold.x) ||
               height < (this.previousUpdatedSize.y - this.geometryGrowThreshold.y);
    }

    protected boolean checkNeedsImmediateGeometryUpdate()
    {
        int height = this.getHeight();
        int width = this.getWidth();

        return this.shouldGrowImmediately(width, height) ||
               this.shouldShrinkImmediately(width, height);
    }

    protected void checkDelayedGeometryUpdate()
    {
        int height = this.getHeight();
        int width = this.getWidth();

        if (this.needsToGrowDelayed(width, height))
        {
            this.setDelayedUpdate(this.geometryGrowDelay);
        }

        if (this.needsToShrinkDelayed(width, height))
        {
            this.setDelayedUpdate(this.geometryShrinkDelay);
        }
    }
}
