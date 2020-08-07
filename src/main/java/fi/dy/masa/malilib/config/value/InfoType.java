package fi.dy.masa.malilib.config.value;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.util.StringUtils;

public enum InfoType implements ConfigOptionListEntry<InfoType>
{
    MESSAGE_OVERLAY ("message", "malilib.label.info_type.message"),
    HOTBAR          ("hotbar",  "malilib.label.info_type.hotbar"),
    CHAT            ("chat",    "malilib.label.info_type.chat"),
    NONE            ("none",    "malilib.label.info_type.none");

    public static final ImmutableList<InfoType> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    InfoType(String configString, String translationKey)
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
    public InfoType cycle(boolean forward)
    {
        return BaseConfigOptionListEntry.cycleValue(VALUES, this.ordinal(), forward);
    }

    @Override
    public InfoType fromString(String name)
    {
        return BaseConfigOptionListEntry.findValueByName(name, VALUES);
    }
}
