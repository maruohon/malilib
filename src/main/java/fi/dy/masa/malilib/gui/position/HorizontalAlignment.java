package fi.dy.masa.malilib.gui.position;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.util.Int2IntFunction;

public class HorizontalAlignment extends BaseOptionListConfigValue
{
    public static final HorizontalAlignment LEFT   = new HorizontalAlignment("left",   "malilib.label.horizontal_alignment.left",   (w) -> 0, (cw, vw, o) -> o);
    public static final HorizontalAlignment RIGHT  = new HorizontalAlignment("right",  "malilib.label.horizontal_alignment.right",  (w) -> -w, (cw, vw, o) -> vw - cw - o);
    public static final HorizontalAlignment CENTER = new HorizontalAlignment("center", "malilib.label.horizontal_alignment.center", (w) -> -w / 2, (cw, vw, o) -> vw / 2 - cw / 2 + o);

    public static final ImmutableList<HorizontalAlignment> VALUES = ImmutableList.of(LEFT, RIGHT, CENTER);

    protected final Int2IntFunction xOffsetSource;
    protected final StartXPositionSource startXPositionSource;

    public HorizontalAlignment(String name, String translationKey,
                               Int2IntFunction xOffsetSource, StartXPositionSource startXPositionSource)
    {
        super(name, translationKey);

        this.xOffsetSource = xOffsetSource;
        this.startXPositionSource = startXPositionSource;
    }

    public int getStartX(int contentWidth, int viewportWidth, int offset)
    {
        return this.startXPositionSource.getStartX(contentWidth, viewportWidth, offset);
    }

    public int getStartX(BaseWidget widget, int viewportWidth)
    {
        int offset = this.getMargin(widget.getMargin());
        return this.startXPositionSource.getStartX(widget.getWidth(), viewportWidth, offset);
    }

    public int getMargin(EdgeInt margin)
    {
        return this == LEFT ? margin.getLeft() : (this == RIGHT ? margin.getRight() : 0);
    }

    public int getXStartOffsetForEdgeAlignment(int contentWidth)
    {
        return this.xOffsetSource.apply(contentWidth);
    }

    public interface StartXPositionSource
    {
        int getStartX(int contentWidth, int viewportWidth, int offset);
    }
}
