package fi.dy.masa.malilib.config.option;

import fi.dy.masa.malilib.config.SliderConfig;

public abstract class BaseSliderConfig<T> extends BaseConfig<T> implements SliderConfig
{
    protected boolean sliderActive;
    protected boolean allowSlider = true;

    public BaseSliderConfig(String name, String comment)
    {
        super(name, comment);
    }

    public BaseSliderConfig(String name, String comment, boolean sliderActive)
    {
        super(name, comment);

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
}
