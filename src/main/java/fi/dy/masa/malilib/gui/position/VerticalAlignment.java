package fi.dy.masa.malilib.gui.position;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;

public class VerticalAlignment extends BaseOptionListConfigValue
{
    public static final VerticalAlignment TOP    = new VerticalAlignment("top",    "malilib.label.vertical_alignment.top",    (ch, vh, o) -> o);
    public static final VerticalAlignment BOTTOM = new VerticalAlignment("bottom", "malilib.label.vertical_alignment.bottom", (ch, vh, o) -> vh - ch - o);
    public static final VerticalAlignment CENTER = new VerticalAlignment("center", "malilib.label.vertical_alignment.center", (ch, vh, o) -> vh / 2 - ch / 2 + o);

    public static final ImmutableList<VerticalAlignment> VALUES = ImmutableList.of(TOP, BOTTOM, CENTER);

    protected final StartYPositionSource startYPositionSource;

    public VerticalAlignment(String name, String translationKey, StartYPositionSource startYPositionSource)
    {
        super(name, translationKey);

        this.startYPositionSource = startYPositionSource;
    }

    public int getStartY(int contentHeight, int viewportHeight, int offset)
    {
        return this.startYPositionSource.getStartY(contentHeight, viewportHeight, offset);
    }

    public interface StartYPositionSource
    {
        int getStartY(int contentHeight, int viewportHeight, int offset);
    }
}
