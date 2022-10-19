package malilib.render.text;

import malilib.util.data.Identifier;

public class Glyph
{
    public final Identifier texture;
    public final float u1;
    public final float u2;
    public final float v1;
    public final float v2;
    public final int width;
    public final int height;
    public final int renderWidth;
    public final boolean whiteSpace;
    public final char c;

    public Glyph(Identifier texture,
                 float u1, float v1,
                 float u2, float v2,
                 int width, int height,
                 boolean whiteSpace, char c)
    {
        this(texture, u1, v1, u2, v2, width, height, width, whiteSpace, c);
    }

    public Glyph(Identifier texture,
                 float u1, float v1,
                 float u2, float v2,
                 int width, int height,
                 int renderWidth, boolean whiteSpace, char c)
    {
        this.texture = texture;
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
        this.width = width;
        this.height = height;
        this.renderWidth = renderWidth;
        this.whiteSpace = whiteSpace;
        this.c = c;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("Glyph{");

        sb.append("c=").append(this.c);
        sb.append(",texture=").append(this.texture);
        sb.append(", u1=").append(this.u1);
        sb.append(", u2=").append(this.u2);
        sb.append(", v1=").append(this.v1);
        sb.append(", v2=").append(this.v2);
        sb.append(", width=").append(this.width);
        sb.append(", height=").append(this.height);
        sb.append(", renderWidth=").append(this.renderWidth);
        sb.append(", whiteSpace=").append(this.whiteSpace);
        sb.append('}');

        return sb.toString();
    }
}
