package fi.dy.masa.malilib.config.value;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.util.StringUtils;

public enum HudAlignment implements IConfigOptionListEntry<HudAlignment>
{
    TOP_LEFT        ("top_left",        "malilib.label.alignment.top_left"),
    TOP_RIGHT       ("top_right",       "malilib.label.alignment.top_right"),
    BOTTOM_LEFT     ("bottom_left",     "malilib.label.alignment.bottom_left"),
    BOTTOM_RIGHT    ("bottom_right",    "malilib.label.alignment.bottom_right"),
    CENTER          ("center",          "malilib.label.alignment.center");

    public static final ImmutableList<HudAlignment> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String unlocName;

    HudAlignment(String configString, String unlocName)
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
        return StringUtils.translate(this.unlocName);
    }

    @Override
    public HudAlignment cycle(boolean forward)
    {
        return ConfigOptionListEntry.cycleValue(VALUES, this.ordinal(), forward);
    }

    @Override
    public HudAlignment fromString(String name)
    {
        return ConfigOptionListEntry.findValueByName(name, VALUES);
    }
}
