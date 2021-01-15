package fi.dy.masa.malilib.config.option;

import java.util.function.Function;
import fi.dy.masa.malilib.config.SliderConfig;
import fi.dy.masa.malilib.gui.callback.SliderCallback;
import fi.dy.masa.malilib.listener.EventListener;

public abstract class BaseSliderConfig<T> extends BaseGenericConfig<T> implements SliderConfig
{
    protected Function<EventListener, SliderCallback> sliderCallbackFactory;
    protected boolean sliderActive;
    protected boolean allowSlider = true;

    public BaseSliderConfig(String name, T defaultValue, String comment)
    {
        this(name, defaultValue, comment, false);
    }

    public BaseSliderConfig(String name, T defaultValue, String comment, boolean sliderActive)
    {
        super(name, defaultValue, comment);

        this.sliderActive = sliderActive;
    }

    @Override
    public boolean isSliderActive()
    {
        return this.sliderActive && this.allowSlider;
    }

    @Override
    public void toggleSliderActive()
    {
        this.sliderActive = ! this.sliderActive;
    }

    @Override
    public boolean allowSlider()
    {
        return this.allowSlider;
    }

    @Override
    public void setAllowSlider(boolean allowSlider)
    {
        this.allowSlider = allowSlider;
    }

    @Override
    public SliderCallback getSliderCallback(EventListener changeListener)
    {
        return this.sliderCallbackFactory.apply(changeListener);
    }

    @Override
    public void setSliderCallbackFactory(Function<EventListener, SliderCallback> sliderCallbackFactory)
    {
        this.sliderCallbackFactory = sliderCallbackFactory;
    }
}
