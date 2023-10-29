package malilib.gui.widget;

import malilib.gui.callback.SliderCallback;

public abstract class BaseSliderWidget<CB extends SliderCallback> extends InteractableWidget
{
    protected final CB callback;
    protected int lastMouseX;
    protected int lastMouseY;
    protected boolean canScrollAdjust = true;
    protected boolean dragging;

    public BaseSliderWidget(int width, int height, CB callback)
    {
        super(width, height);

        this.callback = callback;
        this.canReceiveMouseClicks = true;
        this.canReceiveMouseScrolls = true;
        this.canReceiveMouseMoves = true;

        // This is for receiving release events when the mouse is outside the widget
        this.setShouldReceiveOutsideClicks(true);
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isEnabled())
        {
            this.callback.setRelativeValue(this.getRelativePosition(mouseX, mouseY));
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            this.dragging = true;

            return true;
        }

        return false;
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        this.dragging = false;
    }

    @Override
    protected boolean onMouseScrolled(int mouseX, int mouseY, double verticalWheelDelta, double horizontalWheelDelta)
    {
        if (this.isEnabled() && this.canScrollAdjust)
        {
            double relPos = this.callback.getRelativeValue();
            double delta = 1.0 / (double) this.getSliderTravelDistance();

            if (verticalWheelDelta < 0)
            {
                delta = -delta;
            }

            this.callback.setRelativeValue(relPos + delta);

            return true;
        }

        return false;
    }

    @Override
    public boolean onMouseMoved(int mouseX, int mouseY)
    {
        if (this.dragging)
        {
            double relPos = this.getRelativePosition(mouseX, mouseY);

            if (relPos != this.callback.getRelativeValue())
            {
                this.callback.setRelativeValue(relPos);
                this.lastMouseX = mouseX;
                this.lastMouseY = mouseY;
                return true;
            }
        }

        return false;
    }

    public void setCanScrollAdjust(boolean canScrollAdjust)
    {
        this.canScrollAdjust = canScrollAdjust;
    }

    protected abstract int getSliderTravelDistance();

    protected abstract double getRelativePosition(int mouseX, int mouseY);
}
