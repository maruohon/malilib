package fi.dy.masa.malilib.config.option;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;

public class IdentifierListConfig extends ValueListConfig<ResourceLocation>
{
    public IdentifierListConfig(String name, ImmutableList<ResourceLocation> defaultValues, Function<ResourceLocation, String> toStringConverter, Function<String, ResourceLocation> fromStringConverter)
    {
        super(name, defaultValues, toStringConverter, fromStringConverter);
    }

    public IdentifierListConfig(String name, ImmutableList<ResourceLocation> defaultValues, String comment, Function<ResourceLocation, String> toStringConverter, Function<String, ResourceLocation> fromStringConverter)
    {
        super(name, defaultValues, comment, toStringConverter, fromStringConverter);
    }

    @Override
    public IdentifierListConfig copy()
    {
        IdentifierListConfig config = new IdentifierListConfig(this.name, this.defaultValues, this.commentTranslationKey, this.toStringConverter, this.fromStringConverter);
        config.setValidValues(this.validValues);
        config.setValues(this.getValues());
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
