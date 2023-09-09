package malilib.gui.callback;

public interface SteppedSliderCallback extends SliderCallback
{
    /**
     * @return the step size the underlying config value should snap to
     */
    double getStepSize();

    /**
     * Sets the step size the underlying config value should snap to
     */
    void setStepSize(double step);
}
