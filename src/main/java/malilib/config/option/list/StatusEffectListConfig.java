package malilib.config.option.list;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class StatusEffectListConfig extends ValueListConfig<Potion>
{
    public StatusEffectListConfig(String name, ImmutableList<Potion> defaultValues,
                                  Function<Potion, String> toStringConverter,
                                  Function<String, Potion> fromStringConverter)
    {
        super(name, defaultValues, toStringConverter, fromStringConverter);
    }

    public StatusEffectListConfig(String name, ImmutableList<Potion> defaultValues,
                                  Function<Potion, String> toStringConverter,
                                  Function<String, Potion> fromStringConverter,
                                  @Nullable String commentTranslationKey, Object... commentArgs)
    {
        super(name, defaultValues, toStringConverter, fromStringConverter, commentTranslationKey, commentArgs);
    }

    @Override
    public StatusEffectListConfig copy()
    {
        StatusEffectListConfig config = new StatusEffectListConfig(this.name, this.defaultValue, this.toStringConverter, this.fromStringConverter);
        config.copyValuesFrom(this);
        return config;
    }

    @Nullable
    public static Potion getEffectByRegistryName(String name)
    {
        try
        {
            return Registry.POTION.get(new Identifier(name));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static String getRegistryName(Potion effect)
    {
        try
        {
            return Registry.POTION.getId(effect).toString();
        }
        catch (Exception e)
        {
            return "?";
        }
    }

    public static StatusEffectListConfig create(String cfgName, String... effectNames)
    {
        return create(cfgName, Arrays.asList(effectNames));
    }

    public static StatusEffectListConfig create(String cfgName, List<String> effectNames)
    {
        ImmutableList.Builder<Potion> builder = ImmutableList.builder();

        for (String name : effectNames)
        {
            Potion effect = getEffectByRegistryName(name);

            if (effect != null)
            {
                builder.add(effect);
            }
        }

        return new StatusEffectListConfig(cfgName, builder.build(), StatusEffectListConfig::getRegistryName, StatusEffectListConfig::getEffectByRegistryName);
    }
}
