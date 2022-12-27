package malilib.config.option;

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

    public BaseBooleanAndNumberConfig(String name, T defaultValue,
                                      @Nullable String commentTranslationKey, Object... commentArgs)
    {
        this(name, defaultValue, false, commentTranslationKey, commentArgs);
    }

    public BaseBooleanAndNumberConfig(String name, T defaultValue, boolean sliderActive,
                                      @Nullable String commentTranslationKey, Object... commentArgs)
    {
        super(name, defaultValue, sliderActive, commentTranslationKey, commentArgs);
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
