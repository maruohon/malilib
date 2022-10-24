package malilib.gui.icon;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import malilib.render.RenderUtils;
import malilib.render.ShapeRenderUtils;
import malilib.util.data.Identifier;
import malilib.util.data.json.JsonUtils;

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
     * @return the texture sheet width this icons is on
     */
    int getTextureSheetWidth();

    /**
     * @return the texture sheet height this icons is on
     */
    int getTextureSheetHeight();

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
    Identifier getTexture();

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
     * Renders a possibly scaled/stretched version of this icon, with the given rendered width and height,
     * possibly using an icon variant chosen by the given enabled and hover status arguments.
     */
    default void renderScaledAt(int x, int y, float z, int renderWidth, int renderHeight,
                                boolean enabled, boolean hovered)
    {
        this.renderScaledAt(x, y, z, renderWidth, renderHeight);
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
     * Renders this icon at the given position, with a tint color
     */
    default void renderTintedAt(int x, int y, float z, int backgroundTintColor)
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

        ShapeRenderUtils.renderScaledTintedTexturedRectangle(x, y, z, u, v, width, height,
                                                             width, height, pw, ph, backgroundTintColor);
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

    default JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("texture", this.getTexture().toString());
        obj.addProperty("u", this.getU());
        obj.addProperty("v", this.getV());
        obj.addProperty("w", this.getWidth());
        obj.addProperty("h", this.getHeight());
        obj.addProperty("tw", this.getTextureSheetWidth());
        obj.addProperty("th", this.getTextureSheetHeight());

        return obj;
    }

    @Nullable
    static Icon fromJson(JsonElement el)
    {
        if (el.isJsonObject() == false)
        {
            return null;
        }

        JsonObject obj = el.getAsJsonObject();
        String textureName = JsonUtils.getStringOrDefault(obj, "texture", null);

        if (textureName == null)
        {
            return null;
        }

        int u = JsonUtils.getIntegerOrDefault(obj, "u", 0);
        int v = JsonUtils.getIntegerOrDefault(obj, "v", 0);
        int w = JsonUtils.getIntegerOrDefault(obj, "w", 0);
        int h = JsonUtils.getIntegerOrDefault(obj, "h", 0);
        int tw = JsonUtils.getIntegerOrDefault(obj, "tw", 0);
        int th = JsonUtils.getIntegerOrDefault(obj, "th", 0);
        int vu = JsonUtils.getIntegerOrDefault(obj, "var_u", 0);
        int vv = JsonUtils.getIntegerOrDefault(obj, "var_v", 0);

        if (w > 0 && h > 0 && tw > 0 && th > 0)
        {
            Identifier texture = new Identifier(textureName);

            if (vu != 0 || vv != 0)
            {
                return new BaseMultiIcon(u, v, w, h, vu, vv, tw, th, texture);
            }

            return new BaseIcon(u, v, w, h, tw, th, texture);
        }

        return null;
    }
}
