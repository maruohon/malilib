package fi.dy.masa.malilib.config.value;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import fi.dy.masa.malilib.config.option.ValueListConfig;
import fi.dy.masa.malilib.util.restriction.UsageRestriction.ListType;

public class BlackWhiteList<TYPE>
{
    protected final ListType type;
    protected final ImmutableList<TYPE> blackList;
    protected final ImmutableList<TYPE> whiteList;
    protected final Function<TYPE, String> toStringConverter;
    protected final Function<String, TYPE> fromStringConverter;

    public BlackWhiteList(ListType type, ImmutableList<TYPE> blackList, ImmutableList<TYPE> whiteList,
                          RegistryNamespaced<ResourceLocation, TYPE> registry)
    {
        this.type = type;
        this.blackList = blackList;
        this.whiteList = whiteList;
        this.toStringConverter = getRegistryBasedToStringConverter(registry);
        this.fromStringConverter = getRegistryBasedFromStringConverter(registry);
    }

    public BlackWhiteList(ListType type, ImmutableList<TYPE> blackList, ImmutableList<TYPE> whiteList,
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

    public ImmutableList<TYPE> getBlackList()
    {
        return this.blackList;
    }

    public ImmutableList<TYPE> getWhiteList()
    {
        return this.whiteList;
    }

    @Nullable
    public ImmutableList<TYPE> getActiveList()
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
        return ValueListConfig.getValuesAsStringList(this.blackList, this.toStringConverter);
    }

    public ImmutableList<String> getWhiteListAsString()
    {
        return ValueListConfig.getValuesAsStringList(this.whiteList, this.toStringConverter);
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

    public static <TYPE> BlackWhiteList<TYPE> of(ListType type, ImmutableList<TYPE> blackList, ImmutableList<TYPE> whiteList, RegistryNamespaced<ResourceLocation, TYPE> registry)
    {
        return new BlackWhiteList<>(type, blackList, whiteList, registry);
    }

    public static <TYPE> BlackWhiteList<TYPE> fromLists(BlackWhiteList<TYPE> oldList, ListType type, List<String> blackListStr, List<String> whiteListStr)
    {
        ImmutableList<TYPE> blackList = ValueListConfig.getStringListAsValues(blackListStr, oldList.getFromStringConverter());
        ImmutableList<TYPE> whiteList = ValueListConfig.getStringListAsValues(whiteListStr, oldList.getFromStringConverter());
        return new BlackWhiteList<>(type, blackList, whiteList, oldList.getToStringConverter(), oldList.getFromStringConverter());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        BlackWhiteList<TYPE> that = (BlackWhiteList<TYPE>) o;

        if (this.type != that.type) { return false; }
        if (!Objects.equals(this.blackList, that.blackList)) { return false; }
        return Objects.equals(this.whiteList, that.whiteList);
    }

    @Override
    public int hashCode()
    {
        int result = this.type != null ? this.type.hashCode() : 0;
        result = 31 * result + (this.blackList != null ? this.blackList.hashCode() : 0);
        result = 31 * result + (this.whiteList != null ? this.whiteList.hashCode() : 0);
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
