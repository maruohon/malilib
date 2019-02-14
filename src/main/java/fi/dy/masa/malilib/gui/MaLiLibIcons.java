package fi.dy.masa.malilib.gui;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.reference.MaLiLibReference;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.util.ResourceLocation;

public enum MaLiLibIcons implements IGuiIcon
{
    ARROW_UP        (108,   0, 15, 15),
    ARROW_DOWN      (108,  15, 15, 15),
    PLUS            (108,  30, 15, 15),
    MINUS           (108,  45, 15, 15),
    BTN_SLIDER      (153,   0, 16, 16),
    BTN_TXTFIELD    (153,  16, 16, 16);

    public static final ResourceLocation TEXTURE = new ResourceLocation(MaLiLibReference.MOD_ID, "textures/gui/gui_widgets.png");

    private final int u;
    private final int v;
    private final int w;
    private final int h;

    private MaLiLibIcons(int u, int v, int w, int h)
    {
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
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
    public void renderAt(int x, int y, float zLevel, boolean enabled, boolean selected)
    {
        int u = this.u;

        if (enabled)
        {
            u += this.w;
        }

        if (selected)
        {
            u += this.w;
        }

        RenderUtils.drawTexturedRect(x, y, u, this.v, this.w, this.h, zLevel);
    }

    @Override
    public ResourceLocation getTexture()
    {
        return TEXTURE;
    }
}
