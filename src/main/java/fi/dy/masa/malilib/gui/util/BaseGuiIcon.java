package fi.dy.masa.malilib.gui.util;

import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;

public class BaseGuiIcon implements IGuiIcon
{
    public static final BaseGuiIcon EMPTY                   = new BaseGuiIcon(0, 0, 0, 0, 0, 0);

    public static final BaseGuiIcon ARROW_UP                = new BaseGuiIcon(108, 0, 15, 15);
    public static final BaseGuiIcon ARROW_DOWN              = new BaseGuiIcon(108, 15, 15, 15);
    public static final BaseGuiIcon PLUS                    = new BaseGuiIcon(108, 30, 15, 15);
    public static final BaseGuiIcon MINUS                   = new BaseGuiIcon(108, 45, 15, 15);
    public static final BaseGuiIcon BTN_SLIDER              = new BaseGuiIcon(153, 0, 16, 16);
    public static final BaseGuiIcon BTN_TXTFIELD            = new BaseGuiIcon(153, 16, 16, 16);
    public static final BaseGuiIcon BTN_PLUSMINUS_16        = new BaseGuiIcon(153, 32, 16, 16);

    public static final BaseGuiIcon SEARCH                  = new BaseGuiIcon(201, 0, 12, 12, 0, 0);
    public static final BaseGuiIcon FILE_BROWSER_DIR        = new BaseGuiIcon(201, 12, 12, 12, 0, 0);
    public static final BaseGuiIcon FILE_BROWSER_DIR_ROOT   = new BaseGuiIcon(201, 24, 12, 12, 0, 0);
    public static final BaseGuiIcon FILE_BROWSER_DIR_UP     = new BaseGuiIcon(201, 36, 12, 12, 0, 0);
    public static final BaseGuiIcon FILE_BROWSER_CREATE_DIR = new BaseGuiIcon(201, 48, 12, 12, 0, 0);

    public static final BaseGuiIcon SMALL_ARROW_UP          = new BaseGuiIcon(213, 0, 8, 8);
    public static final BaseGuiIcon SMALL_ARROW_DOWN        = new BaseGuiIcon(213, 8, 8, 8);
    public static final BaseGuiIcon SMALL_ARROW_RIGHT       = new BaseGuiIcon(213, 16, 8, 8);
    public static final BaseGuiIcon SMALL_ARROW_LEFT        = new BaseGuiIcon(213, 24, 8, 8);
    public static final BaseGuiIcon MEDIUM_ARROW_RIGHT      = new BaseGuiIcon(213, 32, 8, 8);
    public static final BaseGuiIcon MEDIUM_ARROW_LEFT       = new BaseGuiIcon(213, 40, 8, 8);
    public static final BaseGuiIcon THIN_DOUBLE_ARROW_LEFT  = new BaseGuiIcon(213, 48, 8, 8);
    public static final BaseGuiIcon SMALL_DOUBLE_ARROW_LEFT = new BaseGuiIcon(213, 56, 8, 8);

    public static final BaseGuiIcon RADIO_BUTTON_UNSELECTED_NORMAL  = new BaseGuiIcon(237, 0, 8, 8, 0, 0);
    public static final BaseGuiIcon RADIO_BUTTON_SELECTED_NORMAL    = new BaseGuiIcon(237, 8, 8, 8, 0, 0);
    public static final BaseGuiIcon RADIO_BUTTON_UNSELECTED_HOVER   = new BaseGuiIcon(237, 16, 8, 8, 0, 0);
    public static final BaseGuiIcon RADIO_BUTTON_SELECTED_HOVER     = new BaseGuiIcon(237, 24, 8, 8, 0, 0);

    public static final BaseGuiIcon INFO_ICON_11                    = new BaseGuiIcon(245, 0, 11, 11, 0, 0);

    public static final ResourceLocation MALILIB_GUI_TEXTURES = new ResourceLocation(MaLiLibReference.MOD_ID, "textures/gui/gui_widgets.png");

    protected final int u;
    protected final int v;
    protected final int w;
    protected final int h;
    protected final int hoverOffU;
    protected final int hoverOffV;

    protected BaseGuiIcon(int u, int v, int w, int h)
    {
        this(u, v, w, h, w, 0);
    }

    protected BaseGuiIcon(int u, int v, int w, int h, int hoverOffU, int hoverOffV)
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
        RenderUtils.drawTexturedRect(x, y, u, v, this.w, this.h, zLevel);
    }

    @Override
    public ResourceLocation getTexture()
    {
        return MALILIB_GUI_TEXTURES;
    }
}
