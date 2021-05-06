package fi.dy.masa.malilib.gui.icon;

import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;

public interface Icon
{
    /**
     * @return the width of this icon in pixels
     */
    int getWidth();

    /**
     * @return the height of this icon in pixels
     */
    int getHeight();

    /**
     * @return the texture pixel u-coordinate (x-coordinate) of this icon's top left corner
     */
    int getU();

    /**
     * @return the texture pixel v-coordinate (y-coordinate) of this icon's top left corner
     */
    int getV();

    /**
     * @return the relative width of one pixel in the texture sheet
     */
    float getTexturePixelWidth();

    /**
     * @return the relative height of one pixel in the texture sheet
     */
    float getTexturePixelHeight();

    /**
     * @return the identifier/location of the texture sheet used for this icon
     */
    ResourceLocation getTexture();

    /**
     * Renders the icon at the given location, possibly using an icon variant chosen
     * by the given enabled and hover status arguments.
     */
    default void renderAt(int x, int y, float z, boolean enabled, boolean hovered)
    {
        this.renderAt(x, y, z);
    }

    /**
     * Renders this icon at the given position
     */
    default void renderAt(int x, int y, float z)
    {
        this.renderScaledAt(x, y, z, this.getWidth(), this.getHeight());
    }

    /**
     * Renders a possibly scaled/stretched version of this icon, with the given rendered width and height
     */
    default void renderScaledAt(int x, int y, float z, int renderWidth, int renderHeight)
    {
        int width = this.getWidth();
        int height = this.getHeight();

        if (width == 0 || height == 0)
        {
            return;
        }

        int u = this.getU();
        int v = this.getV();
        float pw = this.getTexturePixelWidth();
        float ph = this.getTexturePixelHeight();

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.bindTexture(this.getTexture());
        RenderUtils.setupBlend();

        ShapeRenderUtils.renderScaledTexturedRectangle(x, y, z, u, v, renderWidth, renderHeight,
                                                       width, height, pw, ph);
    }

    /**
     * Renders a composite (smaller) icon by using a rectangular area
     * of each of the 4 corners of the texture. The width and height
     * arguments define what size texture is going to be rendered.
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
        int uRight = u + textureWidth - w2;
        int vBottom = v + textureHeight - h2;
        float pw = this.getTexturePixelWidth();
        float ph = this.getTexturePixelHeight();

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.bindTexture(this.getTexture());

        ShapeRenderUtils.renderTexturedRectangle(x, y     , z, u, v      , w1, h1, pw, ph); // top left
        ShapeRenderUtils.renderTexturedRectangle(x, y + h1, z, u, vBottom, w1, h2, pw, ph); // bottom left

        ShapeRenderUtils.renderTexturedRectangle(x + w1, y     , z, uRight, v      , w2, h1, pw, ph); // top right
        ShapeRenderUtils.renderTexturedRectangle(x + w1, y + h1, z, uRight, vBottom, w2, h2, pw, ph); // bottom right
    }
}
