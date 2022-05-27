package fi.dy.masa.malilib.config.option.list;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import fi.dy.masa.malilib.config.option.BaseGenericConfig;

public class ValueListConfig<TYPE> extends BaseGenericConfig<ImmutableList<TYPE>>
{
    protected final Function<TYPE, String> toStringConverter;
    protected final Function<String, TYPE> fromStringConverter;
    @Nullable ImmutableSet<TYPE> validValues;

    public ValueListConfig(String name, ImmutableList<TYPE> defaultValues,
                           Function<TYPE, String> toStringConverter,
                           Function<String, TYPE> fromStringConverter)
    {
        this(name, defaultValues, toStringConverter, fromStringConverter, name);
    }

    public ValueListConfig(String name, ImmutableList<TYPE> defaultValues,
                           Function<TYPE, String> toStringConverter,
                           Function<String, TYPE> fromStringConverter,
                           String commentTranslationKey, Object... commentArgs)
    {
        super(name, defaultValues, commentTranslationKey, commentArgs);

        this.toStringConverter = toStringConverter;
        this.fromStringConverter = fromStringConverter;
    }

    public ImmutableList<String> getValuesAsString()
    {
        return getValuesAsStringList(this.getValue(), this.toStringConverter);
    }

    @Nullable
    public ImmutableSet<TYPE> getValidValues()
    {
        return this.validValues;
    }

    public Function<TYPE, String> getToStringConverter()
    {
        return this.toStringConverter;
    }

    public Function<String, TYPE> getFromStringConverter()
    {
        return this.fromStringConverter;
    }

    /**
     * Sets the values that are allowed/valid for this config.
     * A null list or an empty list will disable the validity check.
     * By default all values are valid (a null set).
     */
    public void setValidValues(@Nullable Collection<TYPE> values)
    {
        if (values != null && values.isEmpty() == false)
        {
            this.validValues = ImmutableSet.copyOf(values);
        }
        else
        {
            this.validValues = null;
        }
    }

    @Override
    public boolean setValue(ImmutableList<TYPE> newValue)
    {
        List<TYPE> filteredValues;

        if (this.validValues != null && this.validValues.isEmpty() == false)
        {
            filteredValues = newValue.stream().filter(this.validValues::contains).collect(Collectors.toList());
        }
        else
        {
            filteredValues = newValue;
        }

        return super.setValue(ImmutableList.copyOf(filteredValues));
    }

    public void copyValuesFrom(ValueListConfig<TYPE> other)
    {
        this.nameTranslationKey = other.nameTranslationKey;
        this.prettyNameTranslationKey = other.prettyNameTranslationKey;
        this.commentTranslationKey = other.commentTranslationKey;
        this.commentArgs = other.commentArgs;
        this.modInfo = other.modInfo;
        this.setValidValues(other.validValues);
        this.setValue(other.getValue());
        this.setValueChangeCallback(other.valueChangeCallback);
        this.setValueLoadCallback(other.valueLoadCallback);
    }

    public ValueListConfig<TYPE> copy()
    {
        ValueListConfig<TYPE> config = new ValueListConfig<>(this.name, this.defaultValue,
                                                             this.toStringConverter, this.fromStringConverter);
        config.copyValuesFrom(this);
        return config;
    }

    public static <TYPE> ImmutableList<String> getValuesAsStringList(List<TYPE> values, Function<TYPE, String> converter)
    {
        ImmutableList.Builder<String> builder = ImmutableList.builder();

        for (TYPE value : values)
        {
            String str = converter.apply(value);

            if (str != null)
            {
                builder.add(str);
            }
        }

        return builder.build();
    }

    public static <TYPE> ImmutableList<TYPE> getStringListAsValues(List<String> strings, Function<String, TYPE> converter)
    {
        ImmutableList.Builder<TYPE> builder = ImmutableList.builder();

        for (String str : strings)
        {
            TYPE value = converter.apply(str);

            if (value != null)
            {
                builder.add(value);
            }
        }

        return builder.build();
    }
}
