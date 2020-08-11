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

    public void setListType(ListType type)
    {
        this.type = type;
    }

    public ListType getListType()
    {
        return this.type;
    }

    /**
     * Sets both the black- and whitelist contents based on the provided names, replacing any old values.
     * If this method is used, then the class should be extended and the
     * {@link #setValuesForList(Set, List)}
     * method should be overridden to handle the names to objects conversion and adding to the Set.
     * @param namesBlacklist
     * @param namesWhitelist
     */
    public void setListContents(List<String> namesBlacklist, List<String> namesWhitelist)
    {
        this.setValuesForList(ListType.BLACKLIST, namesBlacklist);
        this.setValuesForList(ListType.WHITELIST, namesWhitelist);
    }

    /**
     * Sets both the black- and whitelist contents based on the provided names, replacing any old values,
     * using the provided registry to fetch the values.
     * @param namesBlacklist
     * @param namesWhitelist
     * @param registry
     */
    public void setListContentsBasedOnRegistry(List<String> namesBlacklist, List<String> namesWhitelist,
                                               RegistryNamespaced<ResourceLocation, TYPE> registry, String errorTranslationKey)
    {
        this.setValuesForListBasedOnRegistry(ListType.BLACKLIST, namesBlacklist, registry, errorTranslationKey);
        this.setValuesForListBasedOnRegistry(ListType.WHITELIST, namesWhitelist, registry, errorTranslationKey);
    }

    /**
     * Returns either the blacklist or the whitelist Set for the given {@link ListType}.
     * Note: The method returns the whitelist for {@link ListType#WHITELIST}, otherwise the Set for the blacklist
     * (meaning for {@link ListType#NONE} the blacklist Set is returned).
     * @param type
     * @return
     */
    public Set<TYPE> getListForType(ListType type)
    {
        return type == ListType.WHITELIST ? this.whiteList : this.blackList;
    }

    /**
     * Clears the old values for the given  {@link ListType} and then populates them from the provided list of names
     * @param type
     * @param names
     */
    public void setValuesForList(ListType type, List<String> names)
    {
        Set<TYPE> set = this.getListForType(type);
        set.clear();

        this.setValuesForList(set, names);
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
     * Should set the values for the given Set based on the names in the provided List.
     * This should be overridden for any custom restriction types that are not based on
     * objects in the vanilla registries.
     * @param set
     * @param names
     */
    protected void setValuesForList(Set<TYPE> set, List<String> names)
    {
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
