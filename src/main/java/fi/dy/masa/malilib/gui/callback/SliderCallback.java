package fi.dy.masa.malilib.gui.callback;

import fi.dy.masa.malilib.render.text.StyledTextLine;

public interface SliderCallback
{
    /**
     * Maximum number of values/steps the underlying data can have.
     * Return Integer.MAX_VALUE for unlimited/non-specified, like double data type ranges.
     */
    int getMaxSteps();

    /**
     * Returns the relative value within the min - max range,
     * so relativeValue = (value - minValue) / (maxValue - minValue)
     */
    double getRelativeValue();

    /**
     * Sets the value from the provided relative value (0.0 ... 1.0)
     */
    void setRelativeValue(double relativeValue);

    /**
     * @return the formatted display text for the current value. This is used in the slider widget in the GUI.
     */
    StyledTextLine getDisplayText();

    /**
     * Updates the display text, if the value changes externally
     */
    void updateDisplayText();
}
