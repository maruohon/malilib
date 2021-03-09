package fi.dy.masa.malilib.render.text;

import java.util.List;
import java.util.Objects;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;

public class StyledTextSegment
{
    protected final ImmutableList<Glyph> glyphs;
    public final ResourceLocation texture;
    public final TextStyle style;
    public final String displayText;
    public final String originalString;
    public final int renderWidth;

    public StyledTextSegment(ResourceLocation texture, TextStyle style, ImmutableList<Glyph> glyphs, String displayText, String originalString)
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

        this.renderWidth = renderWidth;
    }

    public List<Glyph> getGlyphsForRender()
    {
        if (this.style.random)
        {
            return TextRenderer.INSTANCE.getRandomizedGlyphsFromSameTexture(this.texture, this.glyphs);
        }

        return this.glyphs;
    }

    @Override
    public String toString()
    {
        return "StyledTextSegment{displayText='" + this.displayText + "'}";
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
