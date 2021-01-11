package fi.dy.masa.malilib.config;

import java.util.function.Function;
import fi.dy.masa.malilib.gui.callback.SliderCallback;
import fi.dy.masa.malilib.listener.EventListener;

public interface SliderConfig
{
    /**
     * Returns true when the slider is currently active/visible,
     * so in other words it has been toggled on by the user using the toggle button.
     * @return
     */
    boolean isSliderActive();

    /**
     * Toggle the slider active/inactive state, so in other words
     * whether the slider or the text input fields is visible.
     */
    void toggleSliderActive();

    /**
     * Returns true if using the slider is allowed at all for this config option.
     * @return
     */
    boolean allowSlider();

    /**
     * Sets whether or not the slider is allowed at all for this config option.
     * A mod could for example disable the slider when some condition is true,
     * to disallow the user from enabling the slider at that time.
     * @param allowSlider
     */
    void setAllowSlider(boolean allowSlider);

    /**
     * Returns the slider callback used to read and write values from/to this config
     * @param changeListener
     * @return
     */
    SliderCallback getSliderCallback(EventListener changeListener);

    /**
     * Sets the slider callback factory. If a mod wants to use a non-default callback implementation,
     * it can change the factory using this method.
     * @param callbackFactory
     */
    void setSliderCallbackFactory(Function<EventListener, SliderCallback> callbackFactory);
}
