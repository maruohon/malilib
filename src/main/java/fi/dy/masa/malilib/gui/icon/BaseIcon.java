package fi.dy.masa.malilib.gui.icon;

import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.MaLiLibReference;

public class BaseIcon implements Icon
{
    public static final ResourceLocation MALILIB_GUI_WIDGETS_TEXTURE = new ResourceLocation(MaLiLibReference.MOD_ID, "textures/gui/gui_widgets.png");

    protected final int u;
    protected final int v;
    protected final int w;
    protected final int h;
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
}
