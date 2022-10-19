package malilib.config.option;

import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

public class BaseDualValueConfig<T> extends BaseGenericConfig<T>
{
    @Nullable protected Pair<String, String> labels;
    @Nullable protected Pair<String, String> hoverTexts;

    public BaseDualValueConfig(String name, T defaultValue)
    {
        this(name, defaultValue, name);
    }

    public BaseDualValueConfig(String name, T defaultValue, String commentTranslationKey, Object... commentArgs)
    {
        super(name, defaultValue, commentTranslationKey, commentArgs);
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
