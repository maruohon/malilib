package fi.dy.masa.malilib.gui.position;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;

public class ScreenCorner extends BaseOptionListConfigValue
{
    public static final ScreenCorner TOP_LEFT     = new ScreenCorner("top_left",        "malilib.label.alignment.top_left");
    public static final ScreenCorner TOP_RIGHT    = new ScreenCorner("top_right",       "malilib.label.alignment.top_right");
    public static final ScreenCorner BOTTOM_LEFT  = new ScreenCorner("bottom_left",     "malilib.label.alignment.bottom_left");
    public static final ScreenCorner BOTTOM_RIGHT = new ScreenCorner("bottom_right",    "malilib.label.alignment.bottom_right");

    public static final ImmutableList<ScreenCorner> VALUES = ImmutableList.of(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT);

    private ScreenCorner(String name, String translationKey)
    {
        super(name, translationKey);
    }
}
