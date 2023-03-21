package malilib.render;

import com.mojang.blaze3d.vertex.BufferBuilder;

import malilib.util.data.Color4f;

public class Gradient
{
    public final Color4f topLeft;
    public final Color4f topRight;
    public final Color4f bottomLeft;
    public final Color4f bottomRight;

    public Gradient(Color4f topLeft, Color4f topRight, Color4f bottomLeft, Color4f bottomRight)
    {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    public void putColorForPosition(float x, float y, float totalWidth, float totalHeight, BufferBuilder buffer)
    {
        float relativeX = x / totalWidth;
        float relativeY = y / totalHeight;

        float rLeft = this.topLeft.r + relativeY * (this.bottomLeft.r - this.topLeft.r);
        float gLeft = this.topLeft.g + relativeY * (this.bottomLeft.g - this.topLeft.g);
        float bLeft = this.topLeft.b + relativeY * (this.bottomLeft.b - this.topLeft.b);
        float aLeft = this.topLeft.a + relativeY * (this.bottomLeft.a - this.topLeft.a);
        float rRight = this.topRight.r + relativeY * (this.bottomRight.r - this.topRight.r);
        float gRight = this.topRight.g + relativeY * (this.bottomRight.g - this.topRight.g);
        float bRight = this.topRight.b + relativeY * (this.bottomRight.b - this.topRight.b);
        float aRight = this.topRight.a + relativeY * (this.bottomRight.a - this.topRight.a);
        float r = rLeft + relativeX * (rRight - rLeft);
        float g = gLeft + relativeX * (gRight - gLeft);
        float b = bLeft + relativeX * (bRight - bLeft);
        float a = aLeft + relativeX * (aRight - aLeft);

        buffer.color(r, g, b, a);
    }
}
