package fi.dy.masa.malilib.util.restriction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.value.BaseConfigOptionListEntry;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.config.value.ConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public class UsageRestriction<TYPE>
{
    protected final HashSet<TYPE> blackList = new HashSet<>();
    protected final HashSet<TYPE> whiteList = new HashSet<>();
    protected final Predicate<TYPE> valueValidator;
    protected ListType type = ListType.NONE;

    public UsageRestriction()
    {
        this((v) -> true);
    }

    public UsageRestriction(Predicate<TYPE> valueValidator)
    {
        this.valueValidator = valueValidator;
    }

    public ListType getListType()
    {
        return this.type;
    }

    public void setListType(ListType type)
    {
        this.type = type;
    }

    /**
     * Returns either the blacklist or the whitelist Set for the given {@link ListType}.
     * Note: The method returns the whitelist for {@link ListType#WHITELIST}, otherwise the Set for the blacklist
     * (meaning for {@link ListType#NONE} the blacklist Set is returned).
     * @param type
     * @return
     */
    protected Set<TYPE> getListForType(ListType type)
    {
        return type == ListType.WHITELIST ? this.whiteList : this.blackList;
    }

    /**
     * Sets both the black- and whitelist contents based on the provided names, replacing any old values.
     * @param list
     */
    public void setListContents(BlackWhiteList<TYPE> list)
    {
        this.type = list.getListType();
        this.setValuesForList(this.getListForType(ListType.BLACKLIST), list.getBlackList());
        this.setValuesForList(this.getListForType(ListType.WHITELIST), list.getWhiteList());
    }

    /**
     * Should set the values for the given Set based on the values in the provided List.
     * @param set
     * @param values
     */
    protected void setValuesForList(Set<TYPE> set, List<TYPE> values)
    {
        set.clear();

        for (TYPE value : values)
        {
            if (value != null && this.valueValidator.test(value))
            {
                set.add(value);
            }
        }
    }

    /**
     * Sets both the black- and whitelist contents based on the provided names, replacing any old values,
     * using the provided registry to fetch the values.
     * @param list
     * @param registry
     * @param errorTranslationKey
     */
    public void setValuesBasedOnRegistry(BlackWhiteList<String> list,
                                         RegistryNamespaced<ResourceLocation, TYPE> registry, String errorTranslationKey)
    {
        this.type = list.getListType();
        this.setValuesForListBasedOnRegistry(ListType.BLACKLIST, list.getBlackList(), registry, errorTranslationKey);
        this.setValuesForListBasedOnRegistry(ListType.WHITELIST, list.getWhiteList(), registry, errorTranslationKey);
    }

    /**
     * Clears the old values for the given  {@link ListType} and then populates them from the provided list of names
     * fetching the values from the provided Registry
     * @param type
     * @param names
     * @param registry
     * @param errorTranslationKey
     */
    protected void setValuesForListBasedOnRegistry(ListType type, List<String> names,
                                                   RegistryNamespaced<ResourceLocation, TYPE> registry, String errorTranslationKey)
    {
        Set<TYPE> set = this.getListForType(type);
        set.clear();

        for (String name : names)
        {
            try
            {
                ResourceLocation key = new ResourceLocation(name);
                TYPE value = registry.getObject(key);

                if (value != null && this.valueValidator.test(value))
                {
                    set.add(value);
                }
                else
                {
                    MaLiLib.LOGGER.warn(StringUtils.translate(errorTranslationKey, name));
                }
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.warn(StringUtils.translate(errorTranslationKey, name));
            }
        }
    }

    /**
     * Check if the given object is currently allowed by this restriction
     * @param value
     * @return
     */
    public boolean isAllowed(TYPE value)
    {
        if (this.type == ListType.BLACKLIST)
        {
            return this.blackList.contains(value) == false;
        }
        else if (this.type == ListType.WHITELIST)
        {
            return this.whiteList.contains(value);
        }
        else
        {
            return true;
        }
    }

    public enum ListType implements ConfigOptionListEntry<ListType>
    {
        NONE        ("none",        "malilib.label.list_type.none"),
        BLACKLIST   ("blacklist",   "malilib.label.list_type.blacklist"),
        WHITELIST   ("whitelist",   "malilib.label.list_type.whitelist");

        public static final ImmutableList<ListType> VALUES = ImmutableList.copyOf(values());

        private final String configString;
        private final String translationKey;

        ListType(String configString, String translationKey)
        {
            this.configString = configString;
            this.translationKey = translationKey;
        }

        @Override
        public String getStringValue()
        {
            return this.configString;
        }

        @Override
        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }

        @Override
        public ListType cycle(boolean forward)
        {
            return BaseConfigOptionListEntry.cycleValue(VALUES, this.ordinal(), forward);
        }

        @Override
        public ListType fromString(String name)
        {
            return BaseConfigOptionListEntry.findValueByName(name, VALUES);
        }
    }
}
