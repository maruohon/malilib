package fi.dy.masa.malilib.gui.icon;

import java.util.Objects;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.util.StringUtils;

public class BaseIcon implements Icon
{
    public static final ResourceLocation MALILIB_GUI_WIDGETS_TEXTURE = StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/gui_widgets.png");

    protected final int u;
    protected final int v;
    protected final int w;
    protected final int h;
    protected final int textureSheetWidth;
    protected final int textureSheetHeight;
    protected final float texturePixelWidth;
    protected final float texturePixelHeight;
    protected ResourceLocation texture;

    BaseIcon(int u, int v, int w, int h)
    {
        this(u, v, w, h, MALILIB_GUI_WIDGETS_TEXTURE);
    }

    public BaseIcon(int u, int v, int w, int h, ResourceLocation texture)
    {
        this(u, v, w, h, 256, 256, texture);
    }

    public BaseIcon(int u, int v, int w, int h, int textureWidth, int textureHeight, ResourceLocation texture)
    {
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
        this.textureSheetWidth = textureWidth;
        this.textureSheetHeight = textureHeight;
        this.texturePixelWidth = 1.0F / (float) textureWidth;
        this.texturePixelHeight = 1.0F / (float) textureHeight;
        this.texture = texture;
    }

    @Override
    public int getWidth()
    {
        return this.w;
    }

    @Override
    public int getHeight()
    {
        return this.h;
    }

    @Override
    public int getU()
    {
        return this.u;
    }

    @Override
    public int getV()
    {
        return this.v;
    }

    @Override
    public int getTextureSheetWidth()
    {
        return this.textureSheetWidth;
    }

    @Override
    public int getTextureSheetHeight()
    {
        return this.textureSheetHeight;
    }

    @Override
    public float getTexturePixelWidth()
    {
        return this.texturePixelWidth;
    }

    @Override
    public float getTexturePixelHeight()
    {
        return this.texturePixelHeight;
    }

    @Override
    public ResourceLocation getTexture()
    {
        return this.texture;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if ((o instanceof BaseIcon) == false) { return false; }

        BaseIcon baseIcon = (BaseIcon) o;

        if (this.u != baseIcon.u) { return false; }
        if (this.v != baseIcon.v) { return false; }
        if (this.w != baseIcon.w) { return false; }
        if (this.h != baseIcon.h) { return false; }
        if (Float.compare(baseIcon.texturePixelWidth, this.texturePixelWidth) != 0) { return false; }
        if (Float.compare(baseIcon.texturePixelHeight, this.texturePixelHeight) != 0) { return false; }
        return Objects.equals(this.texture, baseIcon.texture);
    }

    @Override
    public int hashCode()
    {
        int result = this.u;
        result = 31 * result + this.v;
        result = 31 * result + this.w;
        result = 31 * result + this.h;
        result = 31 * result + (this.texturePixelWidth != +0.0f ? Float.floatToIntBits(this.texturePixelWidth) : 0);
        result = 31 * result + (this.texturePixelHeight != +0.0f ? Float.floatToIntBits(this.texturePixelHeight) : 0);
        result = 31 * result + (this.texture != null ? this.texture.hashCode() : 0);
        return result;
    }
}
