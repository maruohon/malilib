package malilib.gui.icon;

import java.util.Objects;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import malilib.MaLiLibReference;
import malilib.util.StringUtils;
import malilib.util.data.Identifier;
import malilib.util.data.json.JsonUtils;

public class BaseIcon implements Icon
{
    public static final Identifier MALILIB_GUI_WIDGETS_TEXTURE = StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/gui_widgets.png");

    protected final int u;
    protected final int v;
    protected final int w;
    protected final int h;
    protected final int variantOffsetU;
    protected final int variantOffsetV;
    protected final int textureSheetWidth;
    protected final int textureSheetHeight;
    protected final float texturePixelWidth;
    protected final float texturePixelHeight;
    protected Identifier texture;

    BaseIcon(int u, int v, int w, int h)
    {
        this(u, v, w, h, MALILIB_GUI_WIDGETS_TEXTURE);
    }

    public BaseIcon(int u, int v, int w, int h, Identifier texture)
    {
        this(u, v, w, h, 0, 0, texture);
    }

    BaseIcon(int u, int v, int w, int h,
                    int variantOffsetU, int variantOffsetV)
    {
        this(u, v, w, h, variantOffsetU, variantOffsetV, MALILIB_GUI_WIDGETS_TEXTURE);
    }

    public BaseIcon(int u, int v, int w, int h,
                    int variantOffsetU, int variantOffsetV, Identifier texture)
    {
        this(u, v, w, h, variantOffsetU, variantOffsetV, 256, 256, texture);
    }

    public BaseIcon(int u, int v, int w, int h,
                    int variantOffsetU, int variantOffsetV,
                    int textureWidth, int textureHeight, Identifier texture)
    {
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
        this.variantOffsetU = variantOffsetU;
        this.variantOffsetV = variantOffsetV;
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
    public Identifier getTexture()
    {
        return this.texture;
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("texture", this.getTexture().toString());
        obj.addProperty("u", this.u);
        obj.addProperty("v", this.v);
        obj.addProperty("w", this.w);
        obj.addProperty("h", this.h);
        obj.addProperty("var_u", this.variantOffsetU);
        obj.addProperty("var_v", this.variantOffsetV);
        obj.addProperty("tw", this.textureSheetWidth);
        obj.addProperty("th", this.textureSheetHeight);

        return obj;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if ((o instanceof BaseIcon) == false) { return false; }

        BaseIcon other = (BaseIcon) o;

        if (this.u != other.u) { return false; }
        if (this.v != other.v) { return false; }
        if (this.w != other.w) { return false; }
        if (this.h != other.h) { return false; }
        if (Float.compare(other.texturePixelWidth, this.texturePixelWidth) != 0) { return false; }
        if (Float.compare(other.texturePixelHeight, this.texturePixelHeight) != 0) { return false; }
        if (this.variantOffsetU != other.variantOffsetU) { return false; }
        if (this.variantOffsetV != other.variantOffsetV) { return false; }

        return Objects.equals(this.texture, other.texture);
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
        result = 31 * result + this.variantOffsetU;
        result = 31 * result + this.variantOffsetV;
        result = 31 * result + (this.texture != null ? this.texture.hashCode() : 0);
        return result;
    }

    @Nullable
    public static BaseIcon fromJson(JsonElement el)
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
            return new BaseIcon(u, v, w, h, vu, vv, tw, th, new Identifier(textureName));
        }

        return null;
    }
}
