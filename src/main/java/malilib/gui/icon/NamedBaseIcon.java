package malilib.gui.icon;

import java.util.Objects;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import malilib.util.data.Identifier;
import malilib.util.data.json.JsonUtils;

public class NamedBaseIcon extends BaseIcon implements NamedIcon
{
    protected final String name;

    public NamedBaseIcon(int u, int v, int w, int h, Identifier texture, String name)
    {
        super(u, v, w, h, texture);

        this.name = name;
    }

    public NamedBaseIcon(int u, int v, int w, int h, int variantOffsetU, int variantOffsetV,
                         Identifier texture, String name)
    {
        super(u, v, w, h, variantOffsetU, variantOffsetV, texture);

        this.name = name;
    }

    public NamedBaseIcon(int u, int v, int w, int h, int variantOffsetU, int variantOffsetV,
                         int textureWidth, int textureHeight, Identifier texture, String name)
    {
        super(u, v, w, h, variantOffsetU, variantOffsetV, textureWidth, textureHeight, texture);

        this.name = name;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();
        obj.addProperty("name", this.name);
        return obj;
    }

    @Override
    public boolean equals(Object o)
    {
        return super.equals(o) && Objects.equals(this.name, ((NamedBaseIcon) o).name);
    }

    @Override
    public int hashCode()
    {
        return super.hashCode() * 31 + (this.name != null ? this.name.hashCode() : 0);
    }

    @Nullable
    public static NamedBaseIcon namedBaseIconFromJson(JsonElement el)
    {
        BaseIcon baseIcon = BaseIcon.baseIconFromJson(el);

        if (baseIcon != null)
        {
            JsonObject obj = el.getAsJsonObject();

            if (JsonUtils.hasString(obj, "name"))
            {
                return new NamedBaseIcon(baseIcon.u, baseIcon.v,
                                         baseIcon.w, baseIcon.h,
                                         baseIcon.variantOffsetU, baseIcon.variantOffsetV,
                                         baseIcon.textureSheetWidth, baseIcon.textureSheetHeight,
                                         baseIcon.texture, JsonUtils.getString(obj, "name"));
            }
        }

        return null;
    }
}
