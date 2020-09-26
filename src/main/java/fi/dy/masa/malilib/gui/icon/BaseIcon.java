package fi.dy.masa.malilib.gui.icon;

import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.render.RenderUtils;

public class BaseIcon implements Icon
{
    public static final BaseIcon EMPTY                      = new BaseIcon(0, 0, 0, 0, 0, 0);

    public static final BaseIcon ARROW_UP                   = new BaseIcon(108,   0, 15, 15);
    public static final BaseIcon ARROW_DOWN                 = new BaseIcon(108,  15, 15, 15);
    public static final BaseIcon ARROW_RIGHT                = new BaseIcon(108,  30, 15, 15);
    public static final BaseIcon ARROW_LEFT                 = new BaseIcon(108,  45, 15, 15);
    public static final BaseIcon LIST_ADD_PLUS              = new BaseIcon(108,  60, 15, 15);
    public static final BaseIcon LIST_REMOVE_MINUS          = new BaseIcon(108,  75, 15, 15);
    public static final BaseIcon BTN_SLIDER                 = new BaseIcon(153,   0, 16, 16);
    public static final BaseIcon BTN_TXTFIELD               = new BaseIcon(153,  16, 16, 16);
    public static final BaseIcon BTN_PLUSMINUS_16           = new BaseIcon(153,  32, 16, 16);

    public static final BaseIcon SEARCH                     = new BaseIcon(201,   0, 12, 12, 0, 0);
    public static final BaseIcon FILE_BROWSER_DIR           = new BaseIcon(201,  12, 12, 12, 0, 0);
    public static final BaseIcon FILE_BROWSER_DIR_ROOT      = new BaseIcon(201,  24, 12, 12, 0, 0);
    public static final BaseIcon FILE_BROWSER_DIR_UP        = new BaseIcon(201,  36, 12, 12, 0, 0);
    public static final BaseIcon FILE_BROWSER_CREATE_DIR    = new BaseIcon(201,  48, 12, 12, 0, 0);

    public static final BaseIcon SMALL_ARROW_UP             = new BaseIcon(213,   0,  8,  8);
    public static final BaseIcon SMALL_ARROW_DOWN           = new BaseIcon(213,   8,  8,  8);
    public static final BaseIcon SMALL_ARROW_RIGHT          = new BaseIcon(213,  16,  8,  8);
    public static final BaseIcon SMALL_ARROW_LEFT           = new BaseIcon(213,  24,  8,  8);
    public static final BaseIcon MEDIUM_ARROW_RIGHT         = new BaseIcon(213,  32,  8,  8);
    public static final BaseIcon MEDIUM_ARROW_LEFT          = new BaseIcon(213,  40,  8,  8);
    public static final BaseIcon THIN_DOUBLE_ARROW_LEFT     = new BaseIcon(213,  48,  8,  8);
    public static final BaseIcon SMALL_DOUBLE_ARROW_LEFT    = new BaseIcon(213,  56,  8,  8);
    public static final BaseIcon GROUP_EXPAND_PLUS          = new BaseIcon(213, 120, 12, 12);
    public static final BaseIcon GROUP_COLLAPSE_MINUS       = new BaseIcon(213, 132, 12, 12);

    public static final BaseIcon RADIO_BUTTON_UNSELECTED_NORMAL     = new BaseIcon(237,   0,  8,  8, 0, 0);
    public static final BaseIcon RADIO_BUTTON_SELECTED_NORMAL       = new BaseIcon(237,   8,  8,  8, 0, 0);
    public static final BaseIcon RADIO_BUTTON_UNSELECTED_HOVER      = new BaseIcon(237,  16,  8,  8, 0, 0);
    public static final BaseIcon RADIO_BUTTON_SELECTED_HOVER        = new BaseIcon(237,  24,  8,  8, 0, 0);
    public static final BaseIcon SLIDER_RED                         = new BaseIcon(244, 216,  6, 40, 0, 0);
    public static final BaseIcon SLIDER_GREEN                       = new BaseIcon(250, 216,  6, 40, 0, 0);

    public static final BaseIcon INFO_ICON_11                       = new BaseIcon(245,   0, 11, 11, 0, 0);

    public static final ResourceLocation MALILIB_GUI_TEXTURES = new ResourceLocation(MaLiLibReference.MOD_ID, "textures/gui/gui_widgets.png");

    protected final int u;
    protected final int v;
    protected final int w;
    protected final int h;
    protected final int hoverOffU;
    protected final int hoverOffV;

    protected BaseIcon(int u, int v, int w, int h)
    {
        this(u, v, w, h, w, 0);
    }

    protected BaseIcon(int u, int v, int w, int h, int hoverOffU, int hoverOffV)
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
    public void renderAt(int x, int y, float zLevel, boolean enabled, boolean hovered)
    {
        if (this.w == 0 || this.h == 0)
        {
            return;
        }

        int u = this.u;
        int v = this.v;

        if (enabled)
        {
            u += this.hoverOffU;
            v += this.hoverOffV;
        }

        if (hovered)
        {
            u += this.hoverOffU;
            v += this.hoverOffV;
        }

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.bindTexture(this.getTexture());
        RenderUtils.renderTexturedRectangle(x, y, u, v, this.w, this.h, zLevel);
    }

    @Override
    public ResourceLocation getTexture()
    {
        return MALILIB_GUI_TEXTURES;
    }
}
