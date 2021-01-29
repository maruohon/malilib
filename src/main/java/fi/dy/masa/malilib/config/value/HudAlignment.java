package fi.dy.masa.malilib.config.value;

import com.google.common.collect.ImmutableList;

public class HudAlignment extends BaseOptionListConfigValue
{
    public static final HudAlignment TOP_LEFT     = new HudAlignment("top_left",        "malilib.label.alignment.top_left");
    public static final HudAlignment TOP_RIGHT    = new HudAlignment("top_right",       "malilib.label.alignment.top_right");
    public static final HudAlignment BOTTOM_LEFT  = new HudAlignment("bottom_left",     "malilib.label.alignment.bottom_left");
    public static final HudAlignment BOTTOM_RIGHT = new HudAlignment("bottom_right",    "malilib.label.alignment.bottom_right");
    public static final HudAlignment CENTER       = new HudAlignment("center",          "malilib.label.alignment.center");

    public static final ImmutableList<HudAlignment> VALUES = ImmutableList.of(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER);

    private HudAlignment(String name, String translationKey)
    {
        super(name, translationKey);
    }
}
