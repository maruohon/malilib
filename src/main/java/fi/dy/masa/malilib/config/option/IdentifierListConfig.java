package fi.dy.masa.malilib.config.option;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
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
        return new IdentifierListConfig(this.name, this.defaultValues, this.commentTranslationKey, this.toStringConverter, this.fromStringConverter);
    }

    public static IdentifierListConfig create(String cfgName, String... names)
    {
        return create(cfgName, Arrays.asList(names));
    }

    public static IdentifierListConfig create(String cfgName, List<String> names)
    {
        ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();

        for (String name : names)
        {
            builder.add(new ResourceLocation(name));
        }

        return new IdentifierListConfig(cfgName, builder.build(), ResourceLocation::toString, ResourceLocation::new);
    }
}
