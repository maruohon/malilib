package malilib.render.text;

import java.util.List;
import java.util.Objects;
import com.google.common.collect.ImmutableList;
import malilib.util.ListUtils;
import malilib.util.data.Identifier;

public class StyledTextSegment
{
    protected final ImmutableList<Glyph> glyphs;
    public final Identifier texture;
    public final TextStyle style;
    public final String displayText;
    public final String originalString;
    public final int glyphCount;
    public final int renderWidth;

    public StyledTextSegment(Identifier texture,
                             TextStyle style,
                             ImmutableList<Glyph> glyphs,
                             String displayText,
                             String originalString)
    {
        this.texture = texture;
        this.style = style;
        this.glyphs = glyphs;
        this.displayText = displayText;
        this.originalString = originalString;

        int renderWidth = 0;

        for (Glyph glyph : glyphs)
        {
            renderWidth += glyph.renderWidth;
        }

        // Bold style glyphs are 1 pixel wider per glyph
        if (style.bold)
        {
            renderWidth += glyphs.size();
        }

        this.glyphCount = glyphs.size();
        this.renderWidth = renderWidth;
    }

    /**
     * Returns the Glyphs for rendering.
     * These will be randomized, if this segment has the random/obfuscated style set.
     */
    public List<Glyph> getGlyphsForRender()
    {
        if (this.style.random)
        {
            return TextRenderer.INSTANCE.getRandomizedGlyphsFromSameTexture(this.texture, this.glyphs);
        }

        return this.glyphs;
    }

    public List<Glyph> getOriginalGlyphs()
    {
        return this.glyphs;
    }

    /**
     * Returns a sub segment of this text segment.
     * @param startIndex the inclusive start index of the sub segment
     * @param endIndex the exclusive end index of the sub segment
     */
    public StyledTextSegment getSubSegment(int startIndex, int endIndex)
    {
        if (startIndex < 0 || startIndex >= this.glyphCount)
        {
            throw new IllegalArgumentException(String.format("start index out of bounds - startIndex: %d, endIndex: %d, glyphCount: %d",
                                                             startIndex, endIndex, this.glyphCount));
        }

        if (endIndex <= startIndex || endIndex > this.glyphCount)
        {
            throw new IllegalArgumentException(String.format("end index out of bounds - startIndex: %d, endIndex: %d, glyphCount: %d",
                                                             startIndex, endIndex, this.glyphCount));
        }

        ImmutableList.Builder<Glyph> glyphs = ImmutableList.builder();

        for (int i = startIndex; i < endIndex; ++i)
        {
            glyphs.add(this.glyphs.get(i));
        }

        int stylePrefixLength = startIndex == 0 ? this.originalString.length() - this.displayText.length() : 0;
        int originalStringStart = startIndex == 0 ? 0 : stylePrefixLength + startIndex;
        int originalStringEnd = originalStringStart + stylePrefixLength + (endIndex - startIndex);
        String originalStringSegment = this.originalString.substring(originalStringStart, originalStringEnd);
        String displayStringSegment = this.displayText.substring(startIndex, endIndex);

        return new StyledTextSegment(this.texture, this.style, glyphs.build(), displayStringSegment, originalStringSegment);
    }

    /**
     * Checks if the texture sheet and style of the given segment are identical
     * to this segment, and thus the segments could be merged.
     */
    public boolean canAppend(StyledTextSegment other)
    {
        return this.texture.equals(other.texture) && this.style.equals(other.style);
    }

    /**
     * Appends the other segment to this segment, if both of them use the same texture sheet and style.
     * Otherwise returns this segment as-is.
     */
    public StyledTextSegment append(StyledTextSegment other)
    {
        if (this.canAppend(other))
        {
            ImmutableList<Glyph> glyphs = ListUtils.getAppendedList(this.glyphs, other.glyphs);
            String displayText = this.displayText + other.displayText;
            String originalString = this.originalString + other.originalString;
            return new StyledTextSegment(this.texture, this.style, glyphs, displayText, originalString);
        }

        return this;
    }

    public StyledTextSegment withStyle(TextStyle style)
    {
        return new StyledTextSegment(this.texture, style, this.glyphs, this.displayText, this.originalString);
    }

    @Override
    public String toString()
    {
        return this.displayText;
    }

    public String getDebugString()
    {
        return String.format("StyledTextSegment{displayText='%s', originalString='%s', style=%s, glyphCount=%d, renderWidth=%d}",
                             this.displayText, this.originalString, this.style.toString(), this.glyphCount, this.renderWidth);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        StyledTextSegment that = (StyledTextSegment) o;

        if (!Objects.equals(this.displayText, that.displayText)) { return false; }
        if (!Objects.equals(this.texture, that.texture)) { return false; }
        return Objects.equals(this.style, that.style);
    }

    @Override
    public int hashCode()
    {
        int result = this.displayText.hashCode();
        result = 31 * result + this.texture.hashCode();
        result = 31 * result + this.style.hashCode();
        return result;
    }
}
