package fi.dy.masa.malilib.config.value;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import fi.dy.masa.malilib.config.option.list.BlockListConfig;
import fi.dy.masa.malilib.config.option.list.ItemListConfig;
import fi.dy.masa.malilib.config.option.list.StatusEffectListConfig;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;
import fi.dy.masa.malilib.util.restriction.UsageRestriction.ListType;

public class BlackWhiteList<TYPE>
{
    protected final ListType type;
    protected final ValueListConfig<TYPE> blackList;
    protected final ValueListConfig<TYPE> whiteList;
    protected final Function<TYPE, String> toStringConverter;
    protected final Function<String, TYPE> fromStringConverter;

    public BlackWhiteList(ListType type, ValueListConfig<TYPE> blackList, ValueListConfig<TYPE> whiteList)
    {
        this(type, blackList, whiteList, blackList.getToStringConverter(), blackList.getFromStringConverter());
    }

    public BlackWhiteList(ListType type, ValueListConfig<TYPE> blackList, ValueListConfig<TYPE> whiteList,
                          RegistryNamespaced<ResourceLocation, TYPE> registry)
    {
        this.type = type;
        this.blackList = blackList;
        this.whiteList = whiteList;
        this.toStringConverter = getRegistryBasedToStringConverter(registry);
        this.fromStringConverter = getRegistryBasedFromStringConverter(registry);
    }

    public BlackWhiteList(ListType type, ValueListConfig<TYPE> blackList, ValueListConfig<TYPE> whiteList,
                          Function<TYPE, String> toStringConverter, Function<String, TYPE> fromStringConverter)
    {
        this.type = type;
        this.blackList = blackList;
        this.whiteList = whiteList;
        this.toStringConverter = toStringConverter;
        this.fromStringConverter = fromStringConverter;
    }

    public ListType getListType()
    {
        return this.type;
    }

    public ValueListConfig<TYPE> getBlackList()
    {
        return this.blackList;
    }

    public ValueListConfig<TYPE> getWhiteList()
    {
        return this.whiteList;
    }

    @Nullable
    public ValueListConfig<TYPE> getActiveList()
    {
        if (this.type == ListType.BLACKLIST)
        {
            return this.blackList;
        }
        else if (this.type == ListType.WHITELIST)
        {
            return this.whiteList;
        }

        return null;
    }

    public ImmutableList<String> getBlackListAsString()
    {
        return ValueListConfig.getValuesAsStringList(this.blackList.getValue(), this.toStringConverter);
    }

    public ImmutableList<String> getWhiteListAsString()
    {
        return ValueListConfig.getValuesAsStringList(this.whiteList.getValue(), this.toStringConverter);
    }

    @Nullable
    public ImmutableList<String> getActiveListAsString()
    {
        if (this.type == ListType.BLACKLIST)
        {
            return this.getBlackListAsString();
        }
        else if (this.type == ListType.WHITELIST)
        {
            return this.getWhiteListAsString();
        }

        return null;
    }

    public Function<TYPE, String> getToStringConverter()
    {
        return this.toStringConverter;
    }

    public Function<String, TYPE> getFromStringConverter()
    {
        return this.fromStringConverter;
    }

    public BlackWhiteList<TYPE> copy()
    {
        return new BlackWhiteList<>(this.type, this.blackList.copy(), this.whiteList.copy(), this.getToStringConverter(), this.getFromStringConverter());
    }

    public static <TYPE> BlackWhiteList<TYPE> of(ListType type, ValueListConfig<TYPE> blackList, ValueListConfig<TYPE> whiteList)
    {
        return new BlackWhiteList<>(type, blackList, whiteList);
    }

    public static <TYPE> BlackWhiteList<TYPE> of(ListType type, ValueListConfig<TYPE> blackList, ValueListConfig<TYPE> whiteList, RegistryNamespaced<ResourceLocation, TYPE> registry)
    {
        return new BlackWhiteList<>(type, blackList, whiteList, registry);
    }

    public static BlackWhiteList<Item> items(ListType type, ImmutableList<Item> blackList, ImmutableList<Item> whiteList)
    {
        return BlackWhiteList.of(type,
                                 ItemListConfig.create("malilib.label.list_type.blacklist", blackList),
                                 ItemListConfig.create("malilib.label.list_type.whitelist", whiteList));
    }

    public static BlackWhiteList<Item> itemNames(ListType type, List<String> blackList, List<String> whiteList)
    {
        return BlackWhiteList.of(type,
                                 ItemListConfig.fromNames("malilib.label.list_type.blacklist", blackList),
                                 ItemListConfig.fromNames("malilib.label.list_type.whitelist", whiteList));
    }

    public static BlackWhiteList<Block> blocks(ListType type, ImmutableList<Block> blackList, ImmutableList<Block> whiteList)
    {
        return BlackWhiteList.of(type,
                                 BlockListConfig.create("malilib.label.list_type.blacklist", blackList),
                                 BlockListConfig.create("malilib.label.list_type.whitelist", whiteList));
    }

    public static BlackWhiteList<Potion> effects(ListType type, List<String> blackList, List<String> whiteList)
    {
        return BlackWhiteList.of(type,
                                 StatusEffectListConfig.create("malilib.label.list_type.blacklist", blackList),
                                 StatusEffectListConfig.create("malilib.label.list_type.whitelist", whiteList),
                                 Potion.REGISTRY);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        @SuppressWarnings("unchecked")
        BlackWhiteList<TYPE> that = (BlackWhiteList<TYPE>) o;

        if (this.type != that.type) { return false; }
        if (!Objects.equals(this.blackList.getValue(), that.blackList.getValue())) { return false; }
        return Objects.equals(this.whiteList.getValue(), that.whiteList.getValue());
    }

    @Override
    public int hashCode()
    {
        int result = this.type != null ? this.type.hashCode() : 0;
        result = 31 * result + (this.blackList != null ? this.blackList.getValue().hashCode() : 0);
        result = 31 * result + (this.whiteList != null ? this.whiteList.getValue().hashCode() : 0);
        return result;
    }

    public static <TYPE> Function<TYPE, String> getRegistryBasedToStringConverter(RegistryNamespaced<ResourceLocation, TYPE> registry)
    {
        return (t) -> {
            ResourceLocation id = registry.getNameForObject(t);
            return id != null ? id.toString() : "<N/A>";
        };
    }

    public static <TYPE> Function<String, TYPE> getRegistryBasedFromStringConverter(RegistryNamespaced<ResourceLocation, TYPE> registry)
    {
        return (str) -> {
            try
            {
                ResourceLocation id = new ResourceLocation(str);
                return registry.getObject(id);
            }
            catch (Exception e)
            {
                return null;
            }
        };
    }
}
