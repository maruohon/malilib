package malilib.config.option.list;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import net.minecraft.resources.ResourceLocation;

public class IdentifierListConfig extends ValueListConfig<ResourceLocation>
{
    public IdentifierListConfig(String name, ImmutableList<ResourceLocation> defaultValues,
                                Function<ResourceLocation, String> toStringConverter,
                                Function<String, ResourceLocation> fromStringConverter)
    {
        super(name, defaultValues, toStringConverter, fromStringConverter);
    }

    public IdentifierListConfig(String name, ImmutableList<ResourceLocation> defaultValues,
                                Function<ResourceLocation, String> toStringConverter,
                                Function<String, ResourceLocation> fromStringConverter,
                                @Nullable String commentTranslationKey, Object... commentArgs)
    {
        super(name, defaultValues, toStringConverter, fromStringConverter, commentTranslationKey, commentArgs);
    }

    @Override
    public IdentifierListConfig copy()
    {
        IdentifierListConfig config = new IdentifierListConfig(this.name, this.defaultValue, this.toStringConverter, this.fromStringConverter);
        config.copyValuesFrom(this);
        return config;
    }

    public static IdentifierListConfig create(String cfgName, String... names)
    {
        return create(cfgName, null, names);
    }

    public static IdentifierListConfig create(String cfgName, @Nullable List<ResourceLocation> validValues, String... names)
    {
        return create(cfgName, Arrays.asList(names), validValues);
    }

    public static IdentifierListConfig create(String cfgName, List<String> names)
    {
        return create(cfgName, names, null);
    }

    public static IdentifierListConfig create(String cfgName, List<String> names, @Nullable List<ResourceLocation> validValues)
    {
        ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();

        for (String name : names)
        {
            builder.add(new ResourceLocation(name));
        }

        IdentifierListConfig config = new IdentifierListConfig(cfgName, builder.build(), ResourceLocation::toString, ResourceLocation::new);
        config.setValidValues(validValues);
        return config;
    }
}
