package fi.dy.masa.malilib.gui.icon;

import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.render.RenderUtils;

public class BaseMultiIcon extends BaseIcon implements MultiIcon
{
    protected final int variantOffsetU;
    protected final int variantOffsetV;

    BaseMultiIcon(int u, int v, int w, int h)
    {
        this(u, v, w, h, w, 0, BaseIcon.MALILIB_GUI_TEXTURES);
    }

    public BaseMultiIcon(int u, int v, int w, int h, ResourceLocation texture)
    {
        this(u, v, w, h, w, 0, texture);
    }

    BaseMultiIcon(int u, int v, int w, int h, int variantOffsetU, int variantOffsetV)
    {
        super(u, v, w, h, BaseIcon.MALILIB_GUI_TEXTURES);

        this.variantOffsetU = variantOffsetU;
        this.variantOffsetV = variantOffsetV;
    }

    public BaseMultiIcon(int u, int v, int w, int h, int variantOffsetU, int variantOffsetV, ResourceLocation texture)
    {
        super(u, v, w, h, texture);

        this.variantOffsetU = variantOffsetU;
        this.variantOffsetV = variantOffsetV;
    }

    @Override
    public int getVariantU(int variantIndex)
    {
        return this.u + variantIndex * this.variantOffsetU;
    }

    @Override
    public int getVariantV(int variantIndex)
    {
        return this.v + variantIndex * this.variantOffsetV;
    }

    @Override
    public void renderAt(int x, int y, float zLevel, int variantIndex)
    {
        if (this.w == 0 || this.h == 0)
        {
            return;
        }

        int u = this.getVariantU(variantIndex);
        int v = this.getVariantV(variantIndex);

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.bindTexture(this.getTexture());
        RenderUtils.renderTexturedRectangle(x, y, u, v, this.w, this.h, zLevel);
    }
}
