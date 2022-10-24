package malilib.gui.icon;

import com.google.gson.JsonObject;

import malilib.util.data.Identifier;

public class BaseMultiIcon extends BaseIcon implements MultiIcon
{
    protected final int variantOffsetU;
    protected final int variantOffsetV;

    BaseMultiIcon(int u, int v, int w, int h)
    {
        this(u, v, w, h, w, 0, MALILIB_GUI_WIDGETS_TEXTURE);
    }

    public BaseMultiIcon(int u, int v, int w, int h, Identifier texture)
    {
        this(u, v, w, h, w, 0, texture);
    }

    BaseMultiIcon(int u, int v, int w, int h, int variantOffsetU, int variantOffsetV)
    {
        super(u, v, w, h, MALILIB_GUI_WIDGETS_TEXTURE);

        this.variantOffsetU = variantOffsetU;
        this.variantOffsetV = variantOffsetV;
    }

    public BaseMultiIcon(int u, int v, int w, int h, int variantOffsetU, int variantOffsetV, Identifier texture)
    {
        this(u, v, w, h, variantOffsetU, variantOffsetV, 256, 256, texture);
    }

    public BaseMultiIcon(int u, int v, int w, int h, int variantOffsetU, int variantOffsetV,
                         int textureWidth, int textureHeight, Identifier texture)
    {
        super(u, v, w, h, textureWidth, textureHeight, texture);

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
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();

        obj.addProperty("var_u", this.variantOffsetU);
        obj.addProperty("var_v", this.variantOffsetV);

        return obj;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }

        BaseMultiIcon that = (BaseMultiIcon) o;

        if (this.variantOffsetU != that.variantOffsetU) { return false; }
        return this.variantOffsetV == that.variantOffsetV;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + this.variantOffsetU;
        result = 31 * result + this.variantOffsetV;
        return result;
    }
}
