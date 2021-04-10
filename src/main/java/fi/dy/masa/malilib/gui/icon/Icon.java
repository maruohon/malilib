package fi.dy.masa.malilib.gui.icon;

import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;

public interface Icon
{
    /**
     * @return the width of this icon
     */
    int getWidth();

    /**
     * @return the height of this icon
     */
    int getHeight();

    /**
     * @return the texture U-coordinate (x-coordinate) of this icon
     */
    int getU();

    /**
     * @return the texture V-coordinate (y-coordinate) of this icon
     */
    int getV();

    /**
     * @return the identifier/location of the texture used for this icon
     */
    ResourceLocation getTexture();

    /**
     * Renders this icon at the given position
     * @param x
     * @param y
     * @param z
     */
    void renderAt(int x, int y, float z);

    /**
     * Renders a composite (smaller) icon by using a rectangular area
     * of each of the 4 corners of the texture. The width and height
     * arguments define what size texture is going to be rendered.
     * @param x
     * @param y
     * @param z
     * @param width the width of the icon to render
     * @param height the height of the icon to render
     */
    default void renderFourSplicedAt(int x, int y, float z, int width, int height)
    {
        this.renderFourSplicedAt(x, y, z, this.getU(), this.getV(), width, height);
    }

    default void renderFourSplicedAt(int x, int y, float z, int u, int v, int width, int height)
    {
        int textureWidth = this.getWidth();
        int textureHeight = this.getHeight();

        if (textureWidth == 0 || textureHeight == 0)
        {
            return;
        }

        int w1 = width / 2;
        int w2 = (width & 0x1) != 0 ? w1 + 1 : w1;
        int h1 = height / 2;
        int h2 = (height & 0x1) != 0 ? h1 + 1 : h1;

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.bindTexture(this.getTexture());

        ShapeRenderUtils.renderTexturedRectangle(x, y     , z, u, v                     , w1, h1); // top left
        ShapeRenderUtils.renderTexturedRectangle(x, y + h1, z, u, v + textureHeight - h2, w1, h2); // bottom left

        ShapeRenderUtils.renderTexturedRectangle(x + w1, y     , z, u + textureWidth - w2, v                     , w2, h1); // top right
        ShapeRenderUtils.renderTexturedRectangle(x + w1, y + h1, z, u + textureWidth - w2, v + textureHeight - h2, w2, h2); // bottom right
    }
}
