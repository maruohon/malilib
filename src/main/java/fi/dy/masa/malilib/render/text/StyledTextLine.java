package fi.dy.masa.malilib.render.text;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;

public class StyledTextLine
{
    public static final StyledTextLine EMPTY = new StyledTextLine(ImmutableList.of());

    public final ImmutableList<StyledTextSegment> segments;
    public final String displayText;
    public final String originalString;
    public final int renderWidth;

    public StyledTextLine(ImmutableList<StyledTextSegment> segments)
    {
        this.segments = segments;

        StringBuilder sbDisplay = new StringBuilder();
        StringBuilder sbOriginal = new StringBuilder();
        int width = 0;

        for (StyledTextSegment segment : segments)
        {
            sbDisplay.append(segment.displayText);
            sbOriginal.append(segment.originalString);
            width += segment.renderWidth;
        }

        this.displayText = sbDisplay.toString();
        this.originalString = sbOriginal.toString();
        this.renderWidth = width;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        StyledTextLine styledTextLine = (StyledTextLine) o;
        return this.segments.equals(styledTextLine.segments);
    }

    @Override
    public int hashCode()
    {
        return this.segments.hashCode();
    }

    /**
     * Creates a styled text line of the provided raw string.
     * If the string has line breaks, the separate lines will be joined
     * and the line breaks will be replaced by the string '\n'.
     */
    public static StyledTextLine of(String str)
    {
        StyledText text = StyledText.of(str);
        final int size = text.lines.size();

        if (size == 1)
        {
            return text.lines.get(0);
        }
        else if (size > 1)
        {
            List<StyledTextSegment> segments = new ArrayList<>();
            String lineBreak = "\\n";

            for (StyledTextLine line : text.lines)
            {
                segments.addAll(line.segments);
                TextRendererUtils.generatePerFontTextureSegmentsFor(lineBreak, lineBreak, TextStyle.DEFAULT,
                                                                    segments::add, TextRenderer.INSTANCE::getGlyphFor);
            }

            return new StyledTextLine(ImmutableList.copyOf(segments));
        }

        return EMPTY;
    }

    /**
     * Returns the string as a completely un-parsed raw StyledTextLine with the default TextStyle
     */
    public static StyledTextLine raw(String str)
    {
        return rawWithStyle(str, TextStyle.DEFAULT);
    }

    /**
     * Returns the string as a completely un-parsed raw StyledTextLine,
     * using the provided TextStyle
     */
    public static StyledTextLine rawWithStyle(String str, TextStyle style)
    {
        List<StyledTextSegment> segments = new ArrayList<>();
        TextRendererUtils.generatePerFontTextureSegmentsFor(str, str, style,
                                                            segments::add, TextRenderer.INSTANCE::getGlyphFor);
        return new StyledTextLine(ImmutableList.copyOf(segments));
    }
}
