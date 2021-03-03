package fi.dy.masa.malilib.render.text;

import fi.dy.masa.malilib.util.StringUtils;

public class StyledTextSegment
{
    public final String text;
    public final TextStyle style;
    public final int renderWidth;

    public StyledTextSegment(String text, TextStyle style)
    {
        this.text = text;
        this.style = style;

        int renderWidth = StringUtils.getStringWidth(text);

        // Bold style glyphs are 1 pixel wider per glyph
        if (style.bold)
        {
            renderWidth += text.length();
        }

        this.renderWidth = renderWidth;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        StyledTextSegment that = (StyledTextSegment) o;

        if (!this.text.equals(that.text)) { return false; }
        return this.style.equals(that.style);
    }

    @Override
    public int hashCode()
    {
        int result = this.text.hashCode();
        result = 31 * result + this.style.hashCode();
        return result;
    }
}
