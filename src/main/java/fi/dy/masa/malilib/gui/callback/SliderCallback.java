package fi.dy.masa.malilib.gui.callback;

public interface SliderCallback
{
    /**
     * Maximum number of values/steps the underlying data can have.
     * Return Integer.MAX_VALUE for unlimited/non-specified, like double data type ranges.
     * @return
     */
    int getMaxSteps();

    /**
     * Returns the relative value within the min - max range,
     * so relativeValue = (value - minValue) / (maxValue - minValue)
     * @return
     */
    double getRelativeValue();

    /**
     * Sets the value from the provided relative value (0.0 ... 1.0)
     * @param relativeValue
     */
    void setRelativeValue(double relativeValue);

    /**
     * Returns the formatted display string for the current value.
     * This is used in the slider widget in the GUI.
     * @return
     */
    String getFormattedDisplayValue();
}
