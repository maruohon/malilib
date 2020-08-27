package fi.dy.masa.malilib.config.value;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.util.StringUtils;

public enum ScreenCorner implements ConfigOptionListEntry<ScreenCorner>
{
    TOP_LEFT        ("top_left",        "malilib.label.alignment.top_left"),
    TOP_RIGHT       ("top_right",       "malilib.label.alignment.top_right"),
    BOTTOM_LEFT     ("bottom_left",     "malilib.label.alignment.bottom_left"),
    BOTTOM_RIGHT    ("bottom_right",    "malilib.label.alignment.bottom_right");

    public static final ImmutableList<ScreenCorner> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    ScreenCorner(String configString, String translationKey)
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
    public ScreenCorner cycle(boolean forward)
    {
        return BaseConfigOptionListEntry.cycleValue(VALUES, this.ordinal(), forward);
    }

    @Override
    public ScreenCorner fromString(String name)
    {
        return BaseConfigOptionListEntry.findValueByName(name, VALUES);
    }
}
