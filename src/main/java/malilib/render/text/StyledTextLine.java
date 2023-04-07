package malilib.render.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;

import malilib.MaLiLib;
import malilib.render.text.StyledText.StyledTextCacheKey;
import malilib.util.StringUtils;

public class StyledTextLine
{
    public static final StyledTextLine EMPTY = new StyledTextLine(ImmutableList.of());

    protected static final Cache<StyledTextCacheKey, ImmutableList<StyledTextLine>> TEXT_CACHE = CacheBuilder.newBuilder().concurrencyLevel(1).initialCapacity(4000).maximumSize(4000).expireAfterAccess(10 * 60, TimeUnit.SECONDS).build();

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
     * @param startIndex the inclusive start index of the sub line
     * @param endIndex the exclusive end index of the sub line
     * @return a slice of this text line between the given indices
     */
    @Nullable
    public StyledTextLine slice(int startIndex, int endIndex)
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
        int last = segments.size() - 1;

        if (last >= 0 && segments.get(last).canAppend(segment))
        {
            segments.set(last, segments.get(last).append(segment));
        }
        else
        {
            segments.add(segment);
        }

        return new StyledTextLine(ImmutableList.copyOf(segments));
    }

    public StyledTextLine append(StyledTextLine other)
    {
        if (other.segments.isEmpty())
        {
            return this;
        }

        ArrayList<StyledTextSegment> segments = new ArrayList<>(this.segments);
        StyledTextSegment first = other.segments.get(0);
        int last = segments.size() - 1;

        if (last >= 0 && segments.get(last).canAppend(first))
        {
            segments.set(last, segments.get(last).append(first));
            segments.addAll(other.segments.subList(1, other.segments.size()));
        }
        else
        {
            segments.addAll(other.segments);
        }

        return new StyledTextLine(ImmutableList.copyOf(segments));
    }

    /**
     * @return the style from the last text segment
     */
    public TextStyle getLastStyle()
    {
        return this.segments.isEmpty() ? TextStyle.DEFAULT : this.segments.get(this.segments.size() - 1).style;
    }

    /**
     * @return a copy of the text with the given style set as the starting style.
     * Any text segments can override properties of the starting style.
     * The way this works is that the style from each segment is merged to the starting style
     * to produce the final style for each segment.
     */
    public StyledTextLine withStartingStyle(TextStyle style)
    {
        ImmutableList.Builder<StyledTextSegment> builder = ImmutableList.builder();

        for (StyledTextSegment segment : this.segments)
        {
            builder.add(segment.withStyle(style.merge(segment.style)));
        }

        return new StyledTextLine(builder.build());
    }

    /**
     * @return a copy of the text with the given style forced to each segment
     */
    public StyledTextLine withStyle(TextStyle style)
    {
        ArrayList<StyledTextSegment> segments = new ArrayList<>(this.segments.size());

        for (StyledTextSegment segment : this.segments)
        {
            segments.add(segment.withStyle(style));
        }

        return new StyledTextLine(ImmutableList.copyOf(segments));
    }

    /**
     * @return a copy of the text without a style (or rather with the default style)
     */
    public StyledTextLine withoutStyle()
    {
        return this.withStyle(TextStyle.DEFAULT);
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
     * Creates styled text lines of the provided string by calling
     * {@link malilib.render.text.StyledTextLine#parseLines(String)}.
     * This is just a convenience method to also add the text lines to the given list.
     */
    public static void parseLines(List<StyledTextLine> linesOut, String str)
    {
        linesOut.addAll(parseLines(str));
    }

    /**
     * Creates styled text lines of the provided string by calling
     * {@link malilib.render.text.StyledTextLine#parseLines(String)}.
     * This is just a convenience method to feed the text lines to the given consumer.
     */
    public static void parseLines(Consumer<StyledTextLine> lineConsumer, String str)
    {
        for (StyledTextLine line : parseLines(str))
        {
            lineConsumer.accept(line);
        }
    }

    /**
     * Creates styled text lines of the provided string.
     */
    public static ImmutableList<StyledTextLine> parseLines(String str)
    {
        return parseLines(str, Optional.empty());
    }

    public static ImmutableList<StyledTextLine> parseLines(String str, TextStyle startingStyle)
    {
        return parseLines(str, Optional.of(startingStyle));
    }

    protected static ImmutableList<StyledTextLine> parseLines(String str, Optional<TextStyle> startingStyle)
    {
        try
        {
            TextStyle style = startingStyle.isPresent() ? startingStyle.get() : null;
            StyledTextCacheKey key = new StyledTextCacheKey(str, style);
            return TEXT_CACHE.get(key, () -> StyledTextParser.parseString(str, startingStyle));
        }
        catch (ExecutionException e)
        {
            MaLiLib.LOGGER.warn("Exception while retrieving StyledText from cache", e);
            return StyledTextParser.parseString(str, startingStyle);
        }
    }

    /**
     * Creates a styled text line of the provided string.
     * If the string has line breaks, the separate lines will be joined
     * and the line breaks will be replaced by the string '\n'.
     */
    public static StyledTextLine parseJoin(String str)
    {
        return joinLines(parseLines(str));
    }

    /**
     * Creates a styled text line of the provided string.
     * If the string has line breaks, the separate lines will be joined
     * and the line breaks will be replaced by the string '\n'.
     * Uses the given startingStyle as the style builder base style.
     */
    public static StyledTextLine parseJoin(String str, TextStyle startingStyle)
    {
        return joinLines(parseLines(str, startingStyle));
    }

    /**
     * Creates a styled text line of the provided string.
     * Only the first line is returned, even if the result has more than one line.
     */
    public static StyledTextLine parseFirstLine(String str)
    {
        List<StyledTextLine> lines = parseLines(str);
        return lines.isEmpty() ? EMPTY : lines.get(0);
    }

    /**
     * Creates a styled text line of the provided string.
     * Only the first line is returned, even if the result has more than one line.
     * Uses the given startingStyle as the style builder base style.
     */
    public static StyledTextLine parseFirstLine(String str, TextStyle startingStyle)
    {
        List<StyledTextLine> lines = parseLines(str, startingStyle);
        return lines.isEmpty() ? EMPTY : lines.get(0);
    }

    /**
     * Creates a styled text line of the translation result of the provided translation key.
     * If the string has line breaks, the separate lines will be joined
     * and the line breaks will be replaced by the string '\n'.
     */
    public static StyledTextLine translateJoin(String translationKey, Object... args)
    {
        return parseJoin(StringUtils.translate(translationKey, args));
    }

    /**
     * Creates a styled text line of the translation result of the provided translation key.
     * If the string has line breaks, the separate lines will be joined
     * and the line breaks will be replaced by the string '\n'.
     * Uses the given startingStyle as the style builder base style.
     */
    public static StyledTextLine translateJoin(TextStyle startingStyle, String translationKey, Object... args)
    {
        return parseJoin(StringUtils.translate(translationKey, args), startingStyle);
    }

    /**
     * Creates styled text lines of the translation result of the provided translation key.
     */
    public static ImmutableList<StyledTextLine> translate(String translationKey, Object... args)
    {
        return parseLines(StringUtils.translate(translationKey, args));
    }

    /**
     * Creates styled text lines of the translation result of the provided translation key.
     * This is just a convenience method to also add the text lines to the given list.
     */
    public static void translate(List<StyledTextLine> linesOut, String translationKey, Object... args)
    {
        linesOut.addAll(translate(translationKey, args));
    }

    /**
     * Creates a styled text line of the translation result of the provided translation key.
     * Only the first line is returned, even if the result has more than one line.
     */
    public static StyledTextLine translateFirstLine(String translationKey, Object... args)
    {
        List<StyledTextLine> lines = parseLines(StringUtils.translate(translationKey, args));
        return lines.isEmpty() ? EMPTY : lines.get(0);
    }

    /**
     * Creates a styled text line of the translation result of the provided translation key.
     * Only the first line is returned, even if the result has more than one line.
     * Uses the given startingStyle as the style builder base style.
     */
    public static StyledTextLine translateFirstLine(TextStyle startingStyle, String translationKey, Object... args)
    {
        List<StyledTextLine> lines = parseLines(StringUtils.translate(translationKey, args), startingStyle);
        return lines.isEmpty() ? EMPTY : lines.get(0);
    }

    /**
     * Creates styled text lines of the provided list of strings.
     * Each string in the input list will be passed through
     * {@link malilib.render.text.StyledTextLine#parseLines(String)}.
     */
    public static ImmutableList<StyledTextLine> parseList(List<String> strings)
    {
        ImmutableList.Builder<StyledTextLine> builder = ImmutableList.builder();

        for (String str : strings)
        {
            builder.addAll(parseLines(str));
        }

        return builder.build();
    }

    /**
     * Creates styled text lines of the provided list of strings.
     * Each string in the input list will first be translated and then passed through
     * {@link malilib.render.text.StyledTextLine#parseLines(String)}.
     */
    public static ImmutableList<StyledTextLine> translateList(List<String> strings)
    {
        ImmutableList.Builder<StyledTextLine> builder = ImmutableList.builder();

        for (String str : strings)
        {
            builder.addAll(parseLines(StringUtils.translate(str)));
        }

        return builder.build();
    }

    /**
     * Joins all the lines of the provided StyledText into one StyledTextLine
     */
    public static StyledTextLine joinLines(StyledText text)
    {
        return joinLines(text.lines);
    }

    public static StyledTextLine joinLines(List<StyledTextLine> textLines)
    {
        final int lineCount = textLines.size();

        if (lineCount == 1)
        {
            return textLines.get(0);
        }
        else if (lineCount > 1)
        {
            List<StyledTextSegment> newSegments = new ArrayList<>();
            Glyph lineBreakGlyph = TextRenderer.INSTANCE.getGlyphFor('\n');
            StyledTextSegment defaultLineBreakSegment = new StyledTextSegment(lineBreakGlyph.texture,
                                                                              TextStyle.DEFAULT,
                                                                              ImmutableList.of(lineBreakGlyph), "", "");

            for (StyledTextLine line : textLines)
            {
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
     * Returns the string as a completely un-parsed raw StyledTextLine with the default TextStyle
     */
    public static StyledTextLine unParsed(String str)
    {
        return unParsedWithStyle(str, TextStyle.DEFAULT);
    }

    /**
     * Returns the string as a completely un-parsed raw StyledTextLine,
     * using the provided TextStyle
     */
    public static StyledTextLine unParsedWithStyle(String str, TextStyle style)
    {
        List<StyledTextSegment> segments = new ArrayList<>();
        StyledTextUtils.generatePerFontTextureSegmentsFor(str, str, style,
                                                          segments::add, TextRenderer.INSTANCE::getGlyphFor);
        return new StyledTextLine(ImmutableList.copyOf(segments));
    }
}
