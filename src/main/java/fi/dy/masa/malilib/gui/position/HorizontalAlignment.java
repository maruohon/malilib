package fi.dy.masa.malilib.gui.position;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;

public class HorizontalAlignment extends BaseOptionListConfigValue
{
    public static final HorizontalAlignment LEFT   = new HorizontalAlignment("left",   "malilib.label.horizontal_alignment.left",   (cw, vw, o) -> o);
    public static final HorizontalAlignment RIGHT  = new HorizontalAlignment("right",  "malilib.label.horizontal_alignment.right",  (cw, vw, o) -> vw - cw - o);
    public static final HorizontalAlignment CENTER = new HorizontalAlignment("center", "malilib.label.horizontal_alignment.center", (cw, vw, o) -> vw / 2 - cw / 2 + o);

    public static final ImmutableList<HorizontalAlignment> VALUES = ImmutableList.of(LEFT, RIGHT, CENTER);

    protected final StartXPositionSource startXPositionSource;

    public HorizontalAlignment(String name, String translationKey, StartXPositionSource startXPositionSource)
    {
        super(name, translationKey);

        this.startXPositionSource = startXPositionSource;
    }

    public int getStartX(int contentWidth, int viewportWidth, int offset)
    {
        return this.startXPositionSource.getStartX(contentWidth, viewportWidth, offset);
    }

    public interface StartXPositionSource
    {
        int getStartX(int contentWidth, int viewportWidth, int offset);
    }
}
