package fi.dy.masa.malilib.config.option;

import java.util.List;
import java.util.function.Function;
import com.google.common.collect.ImmutableList;
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

    public void setValues(List<TYPE> newValues)
    {
        if (this.values.equals(newValues) == false)
        {
            ImmutableList<TYPE> oldValues = this.values;
            this.values = ImmutableList.copyOf(newValues);
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
            builder.add(converter.apply(value));
        }

        return builder.build();
    }

    public static <TYPE> ImmutableList<TYPE> getStringListAsValues(List<String> strings, Function<String, TYPE> converter)
    {
        ImmutableList.Builder<TYPE> builder = ImmutableList.builder();

        for (String str : strings)
        {
            builder.add(converter.apply(str));
        }

        return builder.build();
    }
}
