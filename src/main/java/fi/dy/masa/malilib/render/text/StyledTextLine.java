package fi.dy.masa.malilib.render.text;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.StringUtils;

public class StyledTextLine
{
    public static final StyledTextLine EMPTY = new StyledTextLine(ImmutableList.of());

    public final ImmutableList<StyledTextSegment> segments;
    public final String displayText;
    public final String originalString;
    public final int glyphCount;
    public final int renderWidth;

    public StyledTextLine(ImmutableList<StyledTextSegment> segments)
    {
        this.segments = segments;

        StringBuilder sbDisplay = new StringBuilder();
        StringBuilder sbOriginal = new StringBuilder();
        int width = 0;
        int glyphCount = 0;

        for (StyledTextSegment segment : segments)
        {
            sbDisplay.append(segment.displayText);
            sbOriginal.append(segment.originalString);
            width += segment.renderWidth;
            glyphCount += segment.glyphCount;
        }

        this.displayText = sbDisplay.toString();
        this.originalString = sbOriginal.toString();
        this.glyphCount = glyphCount;
        this.renderWidth = width;
    }

    /**
     * Returns a sub line of this text Line.
     * @param startIndex the inclusive start index of the sub line
     * @param endIndex the exclusive end index of the sub line
     * @return
     */
    @Nullable
    public StyledTextLine getSubLine(int startIndex, int endIndex)
    {
        if (startIndex < 0 || startIndex >= this.glyphCount || endIndex <= startIndex)
        {
            MaLiLib.LOGGER.warn("StyledTextLine#getSubLine(): Invalid range - start: {}, end: {} - glyphCount: {}",
                                startIndex, endIndex, this.glyphCount);
            return null;
        }

        ImmutableList.Builder<StyledTextSegment> segmentBuilder = ImmutableList.builder();
        int currentIndex = 0;

        for (StyledTextSegment segment : this.segments)
        {
            if (currentIndex + segment.glyphCount <= startIndex)
            {
                currentIndex += segment.glyphCount;
                continue;
            }

            // The segment is entirely within the region, use it as-is
            if (currentIndex >= startIndex && currentIndex + segment.glyphCount <= endIndex)
            {
                segmentBuilder.add(segment);
            }
            else
            {
                int start = Math.max(startIndex - currentIndex, 0);
                int end = Math.min(endIndex - currentIndex, segment.glyphCount);
                segmentBuilder.add(segment.getSubSegment(start, end));
            }

            currentIndex += segment.glyphCount;

            if (currentIndex >= endIndex)
            {
                break;
            }
        }

        return new StyledTextLine(segmentBuilder.build());
    }

    public StyledTextLine append(StyledTextSegment segment)
    {
        ArrayList<StyledTextSegment> segments = new ArrayList<>(this.segments);
        segments.add(segment);
        return new StyledTextLine(ImmutableList.copyOf(segments));
    }

    public StyledTextLine append(StyledTextLine other)
    {
        ArrayList<StyledTextSegment> segments = new ArrayList<>(this.segments);
        segments.addAll(other.segments);
        return new StyledTextLine(ImmutableList.copyOf(segments));
    }

    public TextStyle getLastStyle()
    {
        return this.segments.isEmpty() ? TextStyle.DEFAULT : this.segments.get(this.segments.size() - 1).style;
    }

    @Override
    public String toString()
    {
        return "StyledTextLine{" + this.displayText + "}";
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
     * Creates a styled text line of the translation result of the provided translation key.
     * If the string has line breaks, the separate lines will be joined
     * and the line breaks will be replaced by the string '\n'.
     */
    public static StyledTextLine translatedOf(String translationKey)
    {
        return of(StringUtils.translate(translationKey));
    }

    /**
     * Creates a styled text line of the translation result of the provided translation key.
     * If the string has line breaks, the separate lines will be joined
     * and the line breaks will be replaced by the string '\n'.
     * Uses the given startingStyle as the style builder base style.
     */
    public static StyledTextLine translatedOf(String translationKey, TextStyle startingStyle)
    {
        return of(StringUtils.translate(translationKey), startingStyle);
    }

    /**
     * Creates a styled text line of the provided raw string.
     * If the string has line breaks, the separate lines will be joined
     * and the line breaks will be replaced by the string '\n'.
     */
    public static StyledTextLine of(String str)
    {
        StyledText text = StyledText.of(str);
        return joinLines(text);
    }

    /**
     * Creates a styled text line of the provided raw string.
     * If the string has line breaks, the separate lines will be joined
     * and the line breaks will be replaced by the string '\n'.
     * Uses the given startingStyle as the style builder base style.
     */
    public static StyledTextLine of(String str, TextStyle startingStyle)
    {
        StyledText text = StyledText.of(str, startingStyle);
        return joinLines(text);
    }

    public static StyledTextLine joinLines(StyledText text)
    {
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
