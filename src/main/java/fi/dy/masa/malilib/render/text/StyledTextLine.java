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

    public StyledTextLine withStartingStyle(TextStyle style)
    {
        ImmutableList.Builder<StyledTextSegment> builder = ImmutableList.builder();

        for (StyledTextSegment segment : this.segments)
        {
            builder.add(segment.withStyle(style.merge(segment.style)));
        }

        return new StyledTextLine(builder.build());
    }

    @Override
    public String toString()
    {
        return this.displayText;
    }

    public String getDebugString()
    {
        return String.format("StyledTextLine{displayText='%s', originalString='%s', segmentCount=%d, glyphCount=%d, renderWidth=%d}",
                             this.displayText, this.originalString, this.segments.size(), this.glyphCount, this.renderWidth);
    }

    public String getDebugStringWithSegments()
    {
        StringBuilder sb = new StringBuilder();
        int i = 0;

        for (StyledTextSegment segment : this.segments)
        {
            if (i > 0) { sb.append(", "); }
            sb.append(segment.getDebugString());
            ++i;
        }

        return String.format("StyledTextLine{displayText='%s', originalString='%s', segmentCount=%d, glyphCount=%d, renderWidth=%d, segments=[%s]}",
                             this.displayText, this.originalString, this.segments.size(), this.glyphCount, this.renderWidth, sb);
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
     * This is just a convenience method to also add the text to the given list.
     */
    public static void translate(List<StyledTextLine> lines, String translationKey, Object... args)
    {
        lines.addAll(StyledText.translate(translationKey, args).lines);
    }

    /**
     * Creates a styled text line of the translation result of the provided translation key.
     * If the string has line breaks, the separate lines will be joined
     * and the line breaks will be replaced by the string '\n'.
     */
    public static StyledTextLine translate(String translationKey, Object... args)
    {
        return of(StringUtils.translate(translationKey, args));
    }

    /**
     * Creates a styled text line of the translation result of the provided translation key.
     * If the string has line breaks, the separate lines will be joined
     * and the line breaks will be replaced by the string '\n'.
     * Uses the given startingStyle as the style builder base style.
     */
    public static StyledTextLine translate(String translationKey, TextStyle startingStyle, Object... args)
    {
        return of(StringUtils.translate(translationKey, args), startingStyle);
    }

    /**
     * Creates a styled text line of the provided raw string.
     * If the string has line breaks, the separate lines will be joined
     * and the line breaks will be replaced by the string '\n'.
     * This is just a convenience method to also add the text to the given list.
     */
    public static void of(List<StyledTextLine> lines, String str)
    {
        lines.addAll(StyledText.of(str).lines);
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

    public static ImmutableList<StyledTextLine> ofStrings(List<String> strings)
    {
        ImmutableList.Builder<StyledTextLine> builder = ImmutableList.builder();

        for (String str : strings)
        {
            builder.addAll(StyledText.of(str).lines);
        }

        return builder.build();
    }

    public static ImmutableList<StyledTextLine> translateStrings(List<String> strings)
    {
        ImmutableList.Builder<StyledTextLine> builder = ImmutableList.builder();

        for (String str : strings)
        {
            builder.addAll(StyledText.translate(str).lines);
        }

        return builder.build();
    }

    public static StyledTextLine joinLines(StyledText text)
    {
        final int lineCount = text.lines.size();

        if (lineCount == 1)
        {
            return text.lines.get(0);
        }
        else if (lineCount > 1)
        {
            List<StyledTextSegment> newSegments = new ArrayList<>();
            Glyph lineBreakGlyph = TextRenderer.INSTANCE.getGlyphFor('\n');
            StyledTextSegment defaultLineBreakSegment = new StyledTextSegment(lineBreakGlyph.texture, TextStyle.DEFAULT,
                                                                              ImmutableList.of(lineBreakGlyph), "", "");

            for (int lineIndex = 0; lineIndex < lineCount; ++lineIndex)
            {
                StyledTextLine line = text.lines.get(lineIndex);
                List<StyledTextSegment> oldSegments = line.segments;
                final int segmentCount = oldSegments.size();

                if (segmentCount > 0)
                {
                    StyledTextSegment lineBreakSegment = new StyledTextSegment(lineBreakGlyph.texture,
                                                                               oldSegments.get(segmentCount - 1).style,
                                                                               ImmutableList.of(lineBreakGlyph), "", "");

                    if (oldSegments.get(segmentCount - 1).canAppend(lineBreakSegment))
                    {
                        for (int segmentIndex = 0; segmentIndex < segmentCount - 1; ++segmentIndex)
                        {
                            newSegments.add(oldSegments.get(segmentIndex));
                        }

                        newSegments.add(oldSegments.get(segmentCount - 1).append(lineBreakSegment));
                        continue;
                    }
                }

                newSegments.addAll(oldSegments);
                newSegments.add(defaultLineBreakSegment);
            }

            return new StyledTextLine(ImmutableList.copyOf(newSegments));
        }

        return EMPTY;
    }

    /**
     * Returns the maximum render width of the given StyledTextLines
     */
    public static int getRenderWidth(List<StyledTextLine> lines)
    {
        int width = 0;

        for (StyledTextLine line : lines)
        {
            width = Math.max(width, line.renderWidth);
        }

        return width;
    }

    /**
     * 
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
