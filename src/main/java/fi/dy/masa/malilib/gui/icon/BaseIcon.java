package fi.dy.masa.malilib.gui.icon;

import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;

public class BaseIcon implements Icon
{
    public static final ResourceLocation MALILIB_GUI_TEXTURES = new ResourceLocation(MaLiLibReference.MOD_ID, "textures/gui/gui_widgets.png");

    protected final int u;
    protected final int v;
    protected final int w;
    protected final int h;
    protected ResourceLocation texture;

    BaseIcon(int u, int v, int w, int h)
    {
        this(u, v, w, h, MALILIB_GUI_TEXTURES);
    }

    public BaseIcon(int u, int v, int w, int h, ResourceLocation texture)
    {
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
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
    public ResourceLocation getTexture()
    {
        return this.texture;
    }

    @Override
    public void renderAt(int x, int y, float z)
    {
        if (this.w == 0 || this.h == 0)
        {
            return;
        }

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.bindTexture(this.getTexture());
        ShapeRenderUtils.renderTexturedRectangle(x, y, z, this.u, this.v, this.w, this.h);
    }
}
