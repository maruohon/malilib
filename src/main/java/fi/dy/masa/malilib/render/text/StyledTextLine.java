package fi.dy.masa.malilib.render.text;

import com.google.common.collect.ImmutableList;

public class StyledTextLine
{
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

    public static StyledTextLine of(String str)
    {
        StyledText text = StyledText.of(str);
        return text.lines.size() > 0 ? text.lines.get(0) : new StyledTextLine(ImmutableList.of());
    }
}
