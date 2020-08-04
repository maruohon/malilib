package fi.dy.masa.malilib.gui.config;

public interface SliderCallback
{
    /**
     * Maximum number of values/steps the underlying data can have.
     * Return Integer.MAX_VALUE for unlimited/non-specified, like double data type ranges.
     * @return
     */
    int getMaxSteps();

    /**
     * Returns the relative value (within the min - max range)
     * @return
     */
    double getValueRelative();

    /**
     * Sets the value from the provided relative value (0.0 ... 1.0)
     * @param relativeValue
     */
    void setValueRelative(double relativeValue);

    /**
     * Returns the formatted display string for the current value
     * @return
     */
    String getFormattedDisplayValue();
}
