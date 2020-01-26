package fi.dy.masa.malilib.gui.util;

import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;

public class GuiIconBase implements IGuiIcon
{
    public static final GuiIconBase ARROW_UP                = new GuiIconBase(108,   0, 15, 15);
    public static final GuiIconBase ARROW_DOWN              = new GuiIconBase(108,  15, 15, 15);
    public static final GuiIconBase PLUS                    = new GuiIconBase(108,  30, 15, 15);
    public static final GuiIconBase MINUS                   = new GuiIconBase(108,  45, 15, 15);
    public static final GuiIconBase BTN_SLIDER              = new GuiIconBase(153,   0, 16, 16);
    public static final GuiIconBase BTN_TXTFIELD            = new GuiIconBase(153,  16, 16, 16);
    public static final GuiIconBase BTN_PLUSMINUS_16        = new GuiIconBase(153,  32, 16, 16);

    public static final GuiIconBase SEARCH                  = new GuiIconBase(201,   0, 12, 12, 0, 0);
    public static final GuiIconBase FILE_BROWSER_DIR        = new GuiIconBase(201,  12, 12, 12, 0, 0);
    public static final GuiIconBase FILE_BROWSER_DIR_ROOT   = new GuiIconBase(201,  24, 12, 12, 0, 0);
    public static final GuiIconBase FILE_BROWSER_DIR_UP     = new GuiIconBase(201,  36, 12, 12, 0, 0);
    public static final GuiIconBase FILE_BROWSER_CREATE_DIR = new GuiIconBase(201,  48, 12, 12, 0, 0);

    public static final GuiIconBase SMALL_ARROW_UP          = new GuiIconBase(213,   0,  8,  8);
    public static final GuiIconBase SMALL_ARROW_DOWN        = new GuiIconBase(213,   8,  8,  8);

    public static final GuiIconBase RADIO_BUTTON_UNSELECTED_NORMAL = new GuiIconBase(213,  16, 8, 8, 0, 0);
    public static final GuiIconBase RADIO_BUTTON_SELECTED_NORMAL   = new GuiIconBase(213,  24, 8, 8, 0, 0);
    public static final GuiIconBase RADIO_BUTTON_UNSELECTED_HOVER  = new GuiIconBase(213,  32, 8, 8, 0, 0);
    public static final GuiIconBase RADIO_BUTTON_SELECTED_HOVER    = new GuiIconBase(213,  40, 8, 8, 0, 0);

    public static final ResourceLocation MALILIB_GUI_TEXTURES = new ResourceLocation(MaLiLibReference.MOD_ID, "textures/gui/gui_widgets.png");

    protected final int u;
    protected final int v;
    protected final int w;
    protected final int h;
    protected final int hoverOffU;
    protected final int hoverOffV;

    protected GuiIconBase(int u, int v, int w, int h)
    {
        this(u, v, w, h, w, 0);
    }

    protected GuiIconBase(int u, int v, int w, int h, int hoverOffU, int hoverOffV)
    {
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
        this.hoverOffU = hoverOffU;
        this.hoverOffV = hoverOffV;
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
        int v = this.v;

        if (enabled)
        {
            u += this.hoverOffU;
            v += this.hoverOffV;
        }

        if (selected)
        {
            u += this.hoverOffU;
            v += this.hoverOffV;
        }

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.bindTexture(this.getTexture());
        RenderUtils.drawTexturedRect(x, y, u, v, this.w, this.h, zLevel);
    }

    @Override
    public ResourceLocation getTexture()
    {
        return MALILIB_GUI_TEXTURES;
    }
}
