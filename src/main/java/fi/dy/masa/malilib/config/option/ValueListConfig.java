package fi.dy.masa.malilib.config.option;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.JsonUtils;

public abstract class ValueListConfig<TYPE> extends BaseConfig<ImmutableList<TYPE>>
{
    protected final ImmutableList<TYPE> defaultValues;
    protected final Function<TYPE, String> toStringConverter;
    protected final Function<String, TYPE> fromStringConverter;
    protected ImmutableList<TYPE> values;
    protected ImmutableList<TYPE> lastSavedValues;
    @Nullable ImmutableSet<TYPE> validValues;

    public ValueListConfig(String name, ImmutableList<TYPE> defaultValues,
                           Function<TYPE, String> toStringConverter, Function<String, TYPE> fromStringConverter)
    {
        this(name, defaultValues, name, toStringConverter, fromStringConverter);
    }

    public ValueListConfig(String name, ImmutableList<TYPE> defaultValues, String comment,
                           Function<TYPE, String> toStringConverter, Function<String, TYPE> fromStringConverter)
    {
        super(name, comment);

        this.defaultValues = defaultValues;
        this.values = ImmutableList.copyOf(defaultValues);
        this.toStringConverter = toStringConverter;
        this.fromStringConverter = fromStringConverter;

        this.cacheSavedValue();
    }

    public ImmutableList<TYPE> getValues()
    {
        return this.values;
    }

    public ImmutableList<TYPE> getDefaultValues()
    {
        return this.defaultValues;
    }

    public ImmutableList<String> getValuesAsString()
    {
        return getValuesAsStringList(this.values, this.toStringConverter);
    }

    public Function<TYPE, String> getToStringConverter()
    {
        return this.toStringConverter;
    }

    public Function<String, TYPE> getFromStringConverter()
    {
        return this.fromStringConverter;
    }

    @Nullable
    public ImmutableSet<TYPE> getValidValues()
    {
        return this.validValues;
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

    public void setValues(List<TYPE> newValues)
    {
        if (this.values.equals(newValues) == false)
        {
            ImmutableList<TYPE> oldValues = this.values;
            List<TYPE> filteredValues;

            if (this.validValues != null && this.validValues.isEmpty() == false)
            {
                filteredValues = newValues.stream().filter(this.validValues::contains).collect(Collectors.toList());
            }
            else
            {
                filteredValues = newValues;
            }

            this.values = ImmutableList.copyOf(filteredValues);
            this.onValueChanged(this.values, oldValues);
        }
    }

    @Override
    public void resetToDefault()
    {
        this.setValues(this.defaultValues);
    }

    @Override
    public boolean isModified()
    {
        return this.values.equals(this.defaultValues) == false;
    }

    @Override
    public boolean isDirty()
    {
        return this.lastSavedValues.equals(this.values) == false;
    }

    @Override
    public void cacheSavedValue()
    {
        this.lastSavedValues = this.values;
    }

    public abstract ValueListConfig<TYPE> copy();

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonArray())
            {
                ImmutableList.Builder<TYPE> builder = ImmutableList.builder();
                List<String> strings = JsonUtils.arrayAsStringList(element.getAsJsonArray());

                for (TYPE value : getStringListAsValues(strings, this.fromStringConverter))
                {
                    builder.add(value);
                }

                this.values = builder.build();
            }
            else
            {
                // Make sure to clear the old value in any case
                this.values = ImmutableList.of();
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }

            this.onValueLoaded(this.values);
        }
        catch (Exception e)
        {
            // Make sure to clear the old value in any case
            this.values = ImmutableList.of();
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }

        this.cacheSavedValue();
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        JsonArray arr = new JsonArray();

        for (String str : getValuesAsStringList(this.values, this.toStringConverter))
        {
            arr.add(new JsonPrimitive(str));
        }

        return arr;
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
