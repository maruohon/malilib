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
import fi.dy.masa.malilib.config.option.BlockListConfig;
import fi.dy.masa.malilib.config.option.ItemListConfig;
import fi.dy.masa.malilib.config.option.StatusEffectListConfig;
import fi.dy.masa.malilib.config.option.ValueListConfig;
import fi.dy.masa.malilib.util.restriction.UsageRestriction.ListType;

public class BlackWhiteList<TYPE, CFG extends ValueListConfig<TYPE>>
{
    protected final ListType type;
    protected final CFG blackList;
    protected final CFG whiteList;
    protected final Function<TYPE, String> toStringConverter;
    protected final Function<String, TYPE> fromStringConverter;

    public BlackWhiteList(ListType type, CFG blackList, CFG whiteList)
    {
        this(type, blackList, whiteList, blackList.getToStringConverter(), blackList.getFromStringConverter());
    }

    public BlackWhiteList(ListType type, CFG blackList, CFG whiteList,
                          RegistryNamespaced<ResourceLocation, TYPE> registry)
    {
        this.type = type;
        this.blackList = blackList;
        this.whiteList = whiteList;
        this.toStringConverter = getRegistryBasedToStringConverter(registry);
        this.fromStringConverter = getRegistryBasedFromStringConverter(registry);
    }

    public BlackWhiteList(ListType type, CFG blackList, CFG whiteList,
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

    public CFG getBlackList()
    {
        return this.blackList;
    }

    public CFG getWhiteList()
    {
        return this.whiteList;
    }

    @Nullable
    public CFG getActiveList()
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
        return ValueListConfig.getValuesAsStringList(this.blackList.getValues(), this.toStringConverter);
    }

    public ImmutableList<String> getWhiteListAsString()
    {
        return ValueListConfig.getValuesAsStringList(this.whiteList.getValues(), this.toStringConverter);
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

    @SuppressWarnings("unchecked")
    public BlackWhiteList<TYPE, CFG> copy()
    {
        return new BlackWhiteList<>(this.type, (CFG) this.blackList.copy(), (CFG) this.whiteList.copy(), this.getToStringConverter(), this.getFromStringConverter());
    }

    public static <TYPE, CFG extends ValueListConfig<TYPE>> BlackWhiteList<TYPE, CFG> of(ListType type, CFG blackList, CFG whiteList, RegistryNamespaced<ResourceLocation, TYPE> registry)
    {
        return new BlackWhiteList<>(type, blackList, whiteList, registry);
    }

    public static BlackWhiteList<Item, ItemListConfig> items(ListType type, List<String> blackList, List<String> whiteList)
    {
        return BlackWhiteList.of(type,
                                 ItemListConfig.create("malilib.label.list_type.blacklist", blackList),
                                 ItemListConfig.create("malilib.label.list_type.whitelist", whiteList),
                                 Item.REGISTRY);
    }

    public static BlackWhiteList<Block, BlockListConfig> blocks(ListType type, List<String> blackList, List<String> whiteList)
    {
        return BlackWhiteList.of(type,
                                 BlockListConfig.create("malilib.label.list_type.blacklist", blackList),
                                 BlockListConfig.create("malilib.label.list_type.whitelist", whiteList),
                                 Block.REGISTRY);
    }

    public static BlackWhiteList<Potion, StatusEffectListConfig> effects(ListType type, List<String> blackList, List<String> whiteList)
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
        BlackWhiteList<TYPE, CFG> that = (BlackWhiteList<TYPE, CFG>) o;

        if (this.type != that.type) { return false; }
        if (!Objects.equals(this.blackList.getValues(), that.blackList.getValues())) { return false; }
        return Objects.equals(this.whiteList.getValues(), that.whiteList.getValues());
    }

    @Override
    public int hashCode()
    {
        int result = this.type != null ? this.type.hashCode() : 0;
        result = 31 * result + (this.blackList != null ? this.blackList.getValues().hashCode() : 0);
        result = 31 * result + (this.whiteList != null ? this.whiteList.getValues().hashCode() : 0);
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
