package malilib.render.text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.google.common.collect.ImmutableList;

import malilib.util.StringUtils;
import malilib.util.data.Identifier;
import malilib.util.data.LeftRight;

public class StyledTextUtils
{
    public static int getRenderHeight(List<StyledTextLine> lines)
    {
        return getRenderHeight(lines, TextRenderer.INSTANCE.getLineHeight());
    }

    public static int getRenderHeight(List<StyledTextLine> lines, int lineHeight)
    {
        return Math.max(0, lines.size() - 1) * lineHeight + TextRenderer.INSTANCE.getFontHeight();
    }

    public static int getMaxTextRenderWidth(List<StyledTextLine> lines)
    {
        int width = 0;

        for (StyledTextLine line : lines)
        {
            width = Math.max(width, line.renderWidth);
        }

        return width;
    }

    /**
     * Wraps the given StyledText to the given maximum render width.
     * The lines are primarily attempted to be split from spaces, but if there are long
     * lines without any spaces, then they are just hard cut to the maximum width.
     * Any over long lines are split and added to the list, so no characters will be hidden.
     * @return the new StyledText that fit within the given maximum width
     */
    public static StyledText wrapStyledTextToMaxWidth(StyledText text, int maxRenderWidth)
    {
        return StyledText.ofLines(wrapStyledTextToMaxWidth(text.lines, maxRenderWidth));
    }

    /**
     * Wraps the text lines in the given list to the given maximum render width.
     * The lines are primarily attempted to be split from spaces, but if there are long
     * lines without any spaces, then they are just hard cut to the maximum width.
     * Any over long lines are split and added to the list, so no characters will be hidden.
     * @return the new list of text lines that fit within the given maximum width
     */
    public static ImmutableList<StyledTextLine> wrapStyledTextToMaxWidth(List<StyledTextLine> linesIn, int maxRenderWidth)
    {
        boolean needsWrapping = false;

        for (StyledTextLine line : linesIn)
        {
            if (line.renderWidth > maxRenderWidth)
            {
                needsWrapping = true;
                break;
            }
        }

        if (needsWrapping == false)
        {
            return ImmutableList.copyOf(linesIn);
        }

        ImmutableList.Builder<StyledTextLine> builder = ImmutableList.builder();

        for (StyledTextLine line : linesIn)
        {
            wrapStyledTextLineToMaxWidth(line, builder::add, maxRenderWidth);
        }

        return builder.build();
    }

    public static void wrapStyledTextLineToMaxWidth(StyledTextLine line, Consumer<StyledTextLine> lineConsumer, int maxRenderWidth)
    {
        if (line.renderWidth <= maxRenderWidth)
        {
            lineConsumer.accept(line);
            return;
        }

        final int totalGlyphCount = line.glyphCount;
        int remainingWidth = line.renderWidth;
        int lineStartIndex = 0;

        while (lineStartIndex < totalGlyphCount)
        {
            int endIndex;

            if (remainingWidth <= maxRenderWidth)
            {
                endIndex = totalGlyphCount - 1;
            }
            else
            {
                endIndex = getLastWhiteSpace(lineStartIndex, maxRenderWidth, line);
            }

            // No spaces found to split from, just hard split to the maximum length
            if (endIndex == -1)
            {
                endIndex = getLastGlyphIndexWithinWidth(lineStartIndex, maxRenderWidth, line);
            }

            endIndex += 1; // The endIndex is exclusive in the getSubLine() method

            StyledTextLine subLine = line.slice(lineStartIndex, endIndex);

            if (subLine == null || subLine.glyphCount == 0)
            {
                break;
            }

            lineConsumer.accept(subLine);
            lineStartIndex = endIndex;
            remainingWidth -= subLine.renderWidth;
        }
    }

    public static int getLastGlyphIndexWithinWidth(int startIndex, int maxRenderWidth, StyledTextLine line)
    {
        int renderWidth = 0;
        int currentIndex = 0;

        for (StyledTextSegment segment : line.segments)
        {
            if (currentIndex + segment.glyphCount <= startIndex)
            {
                currentIndex += segment.glyphCount;
                continue;
            }

            TextStyle style = segment.style;

            for (Glyph glyph : segment.getOriginalGlyphs())
            {
                if (currentIndex >= startIndex)
                {
                    renderWidth += glyph.getRenderWidthWithStyle(style);

                    if (renderWidth > maxRenderWidth)
                    {
                        return currentIndex - 1;
                    }
                }

                ++currentIndex;
            }
        }

        return currentIndex;
    }

    public static int getFirstGlyphIndexWithinWidthFromEnd(int maxRenderWidth, StyledTextLine line)
    {
        final int segmentCount = line.segments.size();
        int renderWidth = 0;
        int currentIndex = line.glyphCount - 1;

        for (int segmentIndex = segmentCount - 1; segmentIndex >= 0; --segmentIndex)
        {
            StyledTextSegment segment = line.segments.get(segmentIndex);
            TextStyle style = segment.style;
            List<Glyph> glyphs = segment.getOriginalGlyphs();
            final int glyphCount = glyphs.size();

            for (int glyphIndex = glyphCount - 1; glyphIndex >= 0; --glyphIndex)
            {
                Glyph glyph = glyphs.get(glyphIndex);
                renderWidth += glyph.getRenderWidthWithStyle(style);

                if (renderWidth > maxRenderWidth)
                {
                    return currentIndex;
                }

                --currentIndex;
            }
        }

        return currentIndex;
    }

    /**
     * Returns the last index of a space, tab or line break within the given maximum render width
     * in the provided StyledTextLine, starting from the Glyph at startIndex. Returns -1 if no
     * whitespace was found.
     */
    public static int getLastWhiteSpace(int startIndex, int maxRenderWidth, StyledTextLine line)
    {
        int currentIndex = 0;
        int renderWidth = 0;
        int lastIndex = -1;

        for (StyledTextSegment segment : line.segments)
        {
            if (currentIndex + segment.glyphCount <= startIndex)
            {
                currentIndex += segment.glyphCount;
                continue;
            }

            TextStyle style = segment.style;

            for (Glyph glyph : segment.getOriginalGlyphs())
            {
                if (currentIndex >= startIndex)
                {
                    renderWidth += glyph.getRenderWidthWithStyle(style);

                    if (renderWidth > maxRenderWidth)
                    {
                        return lastIndex;
                    }

                    if (glyph.whiteSpace)
                    {
                        lastIndex = currentIndex;
                    }
                }

                ++currentIndex;
            }
        }

        return lastIndex;
    }

    /**
     * Shrinks the given text line until it can fit into the provided maximum width,
     * and adds the provided clamping indicator to indicate that the string is longer
     * than what is shown.
     * @param side the side from which to shrink the string
     * @param clampIndicator the appended shrinkage indicator, for example " ..."
     */
    public static StyledTextLine clampStyledTextToMaxWidth(StyledTextLine line, int maxRenderWidth,
                                                           LeftRight side, String clampIndicator)
    {
        // The entire string fits, just return it as-is
        if (line.renderWidth <= maxRenderWidth)
        {
            return line;
        }

        int indicatorWidth = StringUtils.getStringWidth(clampIndicator);
        int usableWidth = maxRenderWidth - indicatorWidth;

        if (usableWidth <= 16)
        {
            return StyledTextLine.unParsed(clampIndicator);
        }

        int startIndex;
        int endIndex;

        // Shrink from the left
        if (side == LeftRight.LEFT)
        {
            startIndex = getFirstGlyphIndexWithinWidthFromEnd(usableWidth, line);
            endIndex = line.glyphCount;
        }
        else
        {
            startIndex = 0;
            endIndex = getLastGlyphIndexWithinWidth(0, usableWidth, line) + 1;
        }

        return line.slice(startIndex, endIndex).append(StyledTextLine.parseJoin(clampIndicator, line.getLastStyle()));
    }

    public static void generatePerFontTextureSegmentsFor(String displayString, String originalString, TextStyle style,
                                                         Consumer<StyledTextSegment> consumer, GlyphSource glyphSource)
    {
        List<Glyph> glyphs = new ArrayList<>();
        Identifier texture = null;
        final int len = displayString.length();
        int displayStringStart = 0;
        int originalStringStart = 0;
        int stylePrefixLength = originalString.length() - displayString.length();
        int segmentLength = 0;

        for (int i = 0; i < len; ++i, ++segmentLength)
        {
            char c = displayString.charAt(i);
            Glyph glyph = glyphSource.getGlyphFor(c);

            // font sheet change, add the segment
            if (texture != null && glyph.texture != texture)
            {
                int endIndex = originalStringStart + stylePrefixLength + segmentLength;
                String originalStringSegment = originalString.substring(originalStringStart, endIndex);
                String displayStringSegment = displayString.substring(displayStringStart, i);

                consumer.accept(new StyledTextSegment(texture, style, ImmutableList.copyOf(glyphs), displayStringSegment, originalStringSegment));

                displayStringStart += segmentLength;
                originalStringStart += segmentLength + stylePrefixLength;
                stylePrefixLength = 0;
                segmentLength = 0;
                glyphs.clear();
            }

            glyphs.add(glyph);
            texture = glyph.texture;
        }

        if (texture != null)
        {
            String displayStringSegment = displayString.substring(displayStringStart, len);
            String originalStringSegment = originalString.substring(originalStringStart);

            consumer.accept(new StyledTextSegment(texture, style, ImmutableList.copyOf(glyphs), displayStringSegment, originalStringSegment));
        }
    }

    public interface GlyphSource
    {
        Glyph getGlyphFor(char character);
    }
}
