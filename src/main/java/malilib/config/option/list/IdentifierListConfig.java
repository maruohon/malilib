package malilib.config.option.list;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Identifier;

public class IdentifierListConfig extends ValueListConfig<Identifier>
{
    public IdentifierListConfig(String name, ImmutableList<Identifier> defaultValues,
                                Function<Identifier, String> toStringConverter,
                                Function<String, Identifier> fromStringConverter)
    {
        super(name, defaultValues, toStringConverter, fromStringConverter);
    }

    public IdentifierListConfig(String name, ImmutableList<Identifier> defaultValues,
                                Function<Identifier, String> toStringConverter,
                                Function<String, Identifier> fromStringConverter,
                                String commentTranslationKey, Object... commentArgs)
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

    public static IdentifierListConfig create(String cfgName, @Nullable List<Identifier> validValues, String... names)
    {
        return create(cfgName, Arrays.asList(names), validValues);
    }

    public static IdentifierListConfig create(String cfgName, List<String> names)
    {
        return create(cfgName, names, null);
    }

    public static IdentifierListConfig create(String cfgName, List<String> names, @Nullable List<Identifier> validValues)
    {
        ImmutableList.Builder<Identifier> builder = ImmutableList.builder();

        for (String name : names)
        {
            builder.add(new Identifier(name));
        }

        IdentifierListConfig config = new IdentifierListConfig(cfgName, builder.build(), Identifier::toString, Identifier::new);
        config.setValidValues(validValues);
        return config;
    }
}
