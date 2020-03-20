package fi.dy.masa.malilib.gui.interfaces;

public interface ISliderCallbackSteps extends ISliderCallback
{
    /**
     * Returns the step size the config should snap to
     * @return
     */
    double getStepSize();

    /**
     * Sets the step size the config should snap the value to
     * @param step
     */
    void setStepSize(double step);
}
