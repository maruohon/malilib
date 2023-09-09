package malilib.config.option;

import java.util.function.Function;
import javax.annotation.Nullable;

import malilib.gui.callback.SliderCallbackWithText;
import malilib.listener.EventListener;

public abstract class BaseSliderConfig<T> extends BaseGenericConfig<T> implements SliderConfig
{
    protected Function<EventListener, SliderCallbackWithText> sliderCallbackFactory;
    protected boolean sliderActive;
    protected boolean allowSlider = true;

    public BaseSliderConfig(String name, T defaultValue, @Nullable String commentTranslationKey)
    {
        this(name, defaultValue, false, commentTranslationKey);
    }

    public BaseSliderConfig(String name, T defaultValue, boolean sliderActive,
                            @Nullable String commentTranslationKey, Object... commentArgs)
    {
        super(name, defaultValue, commentTranslationKey, commentArgs);

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

    public void setSliderActive(boolean sliderActive)
    {
        this.sliderActive = sliderActive;
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
    public SliderCallbackWithText getSliderCallback(@Nullable EventListener changeListener)
    {
        return this.sliderCallbackFactory.apply(changeListener);
    }

    @Override
    public void setSliderCallbackFactory(Function<EventListener, SliderCallbackWithText> sliderCallbackFactory)
    {
        this.sliderCallbackFactory = sliderCallbackFactory;
    }
}
