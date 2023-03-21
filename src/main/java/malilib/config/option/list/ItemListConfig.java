package malilib.config.option.list;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import net.minecraft.world.item.Item;

import malilib.util.game.wrap.RegistryUtils;

public class ItemListConfig extends ValueListConfig<Item>
{
    public ItemListConfig(String name, ImmutableList<Item> defaultValues)
    {
        this(name, defaultValues, RegistryUtils::getItemIdStr, RegistryUtils::getItemByIdStr);
    }

    public ItemListConfig(String name, ImmutableList<Item> defaultValues,
                          Function<Item, String> toStringConverter,
                          Function<String, Item> fromStringConverter)
    {
        super(name, defaultValues, toStringConverter, fromStringConverter);
    }

    public ItemListConfig(String name, ImmutableList<Item> defaultValues,
                          Function<Item, String> toStringConverter,
                          Function<String, Item> fromStringConverter,
                          @Nullable String commentTranslationKey, Object... commentArgs)
    {
        super(name, defaultValues, toStringConverter, fromStringConverter, commentTranslationKey, commentArgs);
    }

    @Override
    public ItemListConfig copy()
    {
        ItemListConfig config = new ItemListConfig(this.name, this.defaultValue, this.toStringConverter, this.fromStringConverter);
        config.copyValuesFrom(this);
        return config;
    }

    public static ItemListConfig fromNames(String cfgName, String... itemNames)
    {
        return fromNames(cfgName, Arrays.asList(itemNames));
    }

    public static ItemListConfig fromNames(String cfgName, List<String> itemNames)
    {
        ImmutableList.Builder<Item> builder = ImmutableList.builder();

        for (String name : itemNames)
        {
            Item item = RegistryUtils.getItemByIdStr(name);

            if (item != null)
            {
                builder.add(item);
            }
        }

        return create(cfgName, builder.build());
    }

    public static ItemListConfig create(String cfgName, ImmutableList<Item> items)
    {
        return new ItemListConfig(cfgName, items, RegistryUtils::getItemIdStr, RegistryUtils::getItemByIdStr);
    }
}
