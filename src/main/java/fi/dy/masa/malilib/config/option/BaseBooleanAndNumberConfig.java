package fi.dy.masa.malilib.config.option;

import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

public abstract class BaseBooleanAndNumberConfig<T> extends BaseSliderConfig<T> implements BooleanContainingConfig<T>
{
    @Nullable protected Pair<String, String> labels;
    @Nullable protected Pair<String, String> hoverTexts;

    public BaseBooleanAndNumberConfig(String name, T defaultValue)
    {
        this(name, defaultValue, name);
    }

    public BaseBooleanAndNumberConfig(String name, T defaultValue, String comment)
    {
        this(name, defaultValue, comment, false);
    }

    public BaseBooleanAndNumberConfig(String name, T defaultValue, String comment, boolean sliderActive)
    {
        super(name, defaultValue, comment, sliderActive);
    }

    @Nullable
    public Pair<String, String> getLabels()
    {
        return this.labels;
    }

    public void setLabels(@Nullable Pair<String, String> labels)
    {
        this.labels = labels;
    }

    @Nullable
    public Pair<String, String> getHoverTexts()
    {
        return this.hoverTexts;
    }

    public void setHoverTexts(@Nullable Pair<String, String> hoverTexts)
    {
        this.hoverTexts = hoverTexts;
    }
}
