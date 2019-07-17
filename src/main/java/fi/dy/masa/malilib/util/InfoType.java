package fi.dy.masa.malilib.util;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;

public enum InfoType implements IConfigOptionListEntry
{
    NONE            ("none",    "malilib.label.info_type.none"),
    CHAT            ("chat",    "malilib.label.info_type.chat"),
    HOTBAR          ("hotbar",  "malilib.label.info_type.hotbar"),
    MESSAGE_OVERLAY ("message", "malilib.label.info_type.message");

    private final String configString;
    private final String translationKey;

    private InfoType(String configString, String translationKey)
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
    public IConfigOptionListEntry cycle(boolean forward)
    {
        int id = this.ordinal();

        if (forward)
        {
            if (++id >= values().length)
            {
                id = 0;
            }
        }
        else
        {
            if (--id < 0)
            {
                id = values().length - 1;
            }
        }

        return values()[id % values().length];
    }

    @Override
    public InfoType fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static InfoType fromStringStatic(String name)
    {
        for (InfoType aligment : InfoType.values())
        {
            if (aligment.configString.equalsIgnoreCase(name))
            {
                return aligment;
            }
        }

        return InfoType.MESSAGE_OVERLAY;
    }
}
