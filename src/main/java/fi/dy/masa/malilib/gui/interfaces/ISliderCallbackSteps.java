package fi.dy.masa.malilib.gui.interfaces;

import fi.dy.masa.malilib.gui.config.SliderCallback;

public interface ISliderCallbackSteps extends SliderCallback
{
    /**
     * Returns the step size the underlying config value should snap to
     * @return
     */
    double getStepSize();

    /**
     * Sets the step size the underlying config value should snap to
     * @param step
     */
    void setStepSize(double step);
}
