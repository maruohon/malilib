package fi.dy.masa.malilib.config.option;

import java.util.function.Function;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;

public class ItemListConfig extends ValueListConfig<Item>
{
    public ItemListConfig(String name, ImmutableList<Item> defaultValues, Function<Item, String> toStringConverter, Function<String, Item> fromStringConverter)
    {
        super(name, defaultValues, toStringConverter, fromStringConverter);
    }

    public ItemListConfig(String name, ImmutableList<Item> defaultValues, String comment, Function<Item, String> toStringConverter, Function<String, Item> fromStringConverter)
    {
        super(name, defaultValues, comment, toStringConverter, fromStringConverter);
    }
}
