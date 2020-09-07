package fi.dy.masa.malilib.config.option;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.util.ItemUtils;

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

    @Override
    public ItemListConfig copy()
    {
        return new ItemListConfig(this.name, this.defaultValues, this.commentTranslationKey, this.toStringConverter, this.fromStringConverter);
    }

    @Nullable
    public static Item nameToItem(String name)
    {
        try
        {
            return Item.REGISTRY.getObject(new ResourceLocation(name));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static ItemListConfig create(String cfgName, String... itemNames)
    {
        return create(cfgName, Arrays.asList(itemNames));
    }

    public static ItemListConfig create(String cfgName, List<String> itemNames)
    {
        ImmutableList.Builder<Item> builder = ImmutableList.builder();

        for (String name : itemNames)
        {
            Item item = nameToItem(name);

            if (item != null)
            {
                builder.add(item);
            }
        }

        return new ItemListConfig(cfgName, builder.build(), ItemUtils::getItemRegistryName, ItemListConfig::nameToItem);
    }
}
