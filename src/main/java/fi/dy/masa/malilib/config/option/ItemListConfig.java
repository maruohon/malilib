package fi.dy.masa.malilib.config.option;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;
import fi.dy.masa.malilib.util.ItemUtils;

public class ItemListConfig extends ValueListConfig<Item>
{
    public ItemListConfig(String name, ImmutableList<Item> defaultValues)
    {
        this(name, defaultValues, ItemUtils::getItemRegistryName, ItemUtils::getItemByRegistryName);
    }

    public ItemListConfig(String name, ImmutableList<Item> defaultValues, Function<Item, String> toStringConverter, Function<String, Item> fromStringConverter)
    {
        super(name, defaultValues, toStringConverter, fromStringConverter);
    }

    public ItemListConfig(String name, ImmutableList<Item> defaultValues, String comment, Function<Item, String> toStringConverter, Function<String, Item> fromStringConverter)
    {
        super(name, defaultValues, comment, toStringConverter, fromStringConverter);
    }

    @Override
    public ItemListConfig copy()
    {
        ItemListConfig config = new ItemListConfig(this.name, this.defaultValues, this.toStringConverter, this.fromStringConverter);
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
            Item item = ItemUtils.getItemByRegistryName(name);

            if (item != null)
            {
                builder.add(item);
            }
        }

        return create(cfgName, builder.build());
    }

    public static ItemListConfig create(String cfgName, ImmutableList<Item> items)
    {
        return new ItemListConfig(cfgName, items, ItemUtils::getItemRegistryName, ItemUtils::getItemByRegistryName);
    }
}
