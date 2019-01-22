package fi.dy.masa.malilib.config;

import net.minecraft.client.resource.language.I18n;

public enum HudAlignment implements IConfigOptionListEntry
{
    TOP_LEFT        ("top_left",        "malilib.label.aligment.top_left"),
    TOP_RIGHT       ("top_right",       "malilib.label.aligment.top_right"),
    BOTTOM_LEFT     ("bottom_left",     "malilib.label.aligment.bottom_left"),
    BOTTOM_RIGHT    ("bottom_right",    "malilib.label.aligment.bottom_right"),
    CENTER          ("center",          "malilib.label.aligment.center");

    private final String configString;
    private final String unlocName;

    private HudAlignment(String configString, String unlocName)
    {
        this.configString = configString;
        this.unlocName = unlocName;
    }

    @Override
    public String getStringValue()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
    {
        return I18n.translate(this.unlocName);
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
    public HudAlignment fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static HudAlignment fromStringStatic(String name)
    {
        for (HudAlignment aligment : HudAlignment.values())
        {
            if (aligment.configString.equalsIgnoreCase(name))
            {
                return aligment;
            }
        }

        return HudAlignment.TOP_LEFT;
    }
}
