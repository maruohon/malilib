package fi.dy.masa.malilib.gui.position;

import java.util.function.ToIntFunction;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;

public class HorizontalAlignment extends BaseOptionListConfigValue
{
    public static final HorizontalAlignment LEFT   = new HorizontalAlignment("left",   "malilib.label.horizontal_alignment.left",   (w) -> 0, (cw, vw, o) -> o);
    public static final HorizontalAlignment RIGHT  = new HorizontalAlignment("right",  "malilib.label.horizontal_alignment.right",  (w) -> -w, (cw, vw, o) -> vw - cw - o);
    public static final HorizontalAlignment CENTER = new HorizontalAlignment("center", "malilib.label.horizontal_alignment.center", (w) -> -w / 2, (cw, vw, o) -> vw / 2 - cw / 2 + o);

    public static final ImmutableList<HorizontalAlignment> VALUES = ImmutableList.of(LEFT, RIGHT, CENTER);

    protected final ToIntFunction<Integer> xOffsetSource;
    protected final StartXPositionSource startXPositionSource;

    public HorizontalAlignment(String name, String translationKey, ToIntFunction<Integer> xOffsetSource, StartXPositionSource startXPositionSource)
    {
        super(name, translationKey);

        this.xOffsetSource = xOffsetSource;
        this.startXPositionSource = startXPositionSource;
    }

    public int getStartX(int contentWidth, int viewportWidth, int offset)
    {
        return this.startXPositionSource.getStartX(contentWidth, viewportWidth, offset);
    }

    public int getXStartOffset(int contentWidth)
    {
        return this.xOffsetSource.applyAsInt(contentWidth);
    }

    public interface StartXPositionSource
    {
        int getStartX(int contentWidth, int viewportWidth, int offset);
    }
}
