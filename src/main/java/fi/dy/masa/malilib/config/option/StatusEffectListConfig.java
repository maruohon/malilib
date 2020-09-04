package fi.dy.masa.malilib.config.option;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class StatusEffectListConfig extends ValueListConfig<Potion>
{
    public StatusEffectListConfig(String name, ImmutableList<Potion> defaultValues, Function<Potion, String> toStringConverter, Function<String, Potion> fromStringConverter)
    {
        super(name, defaultValues, toStringConverter, fromStringConverter);
    }

    public StatusEffectListConfig(String name, ImmutableList<Potion> defaultValues, String comment, Function<Potion, String> toStringConverter, Function<String, Potion> fromStringConverter)
    {
        super(name, defaultValues, comment, toStringConverter, fromStringConverter);
    }

    @Override
    public StatusEffectListConfig copy()
    {
        return new StatusEffectListConfig(this.name, this.defaultValues, this.commentTranslationKey, this.toStringConverter, this.fromStringConverter);
    }

    @Nullable
    public static Potion nameToEffect(String name)
    {
        try
        {
            return Potion.REGISTRY.getObject(new ResourceLocation(name));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static String effectToName(Potion effect)
    {
        try
        {
            return Potion.REGISTRY.getNameForObject(effect).toString();
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
            Potion effect = nameToEffect(name);

            if (effect != null)
            {
                builder.add(effect);
            }
        }

        return new StatusEffectListConfig(cfgName, builder.build(), StatusEffectListConfig::effectToName, StatusEffectListConfig::nameToEffect);
    }
}
