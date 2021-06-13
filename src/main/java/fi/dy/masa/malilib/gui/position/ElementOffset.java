package fi.dy.masa.malilib.gui.position;

import com.google.gson.JsonObject;
import fi.dy.masa.malilib.util.JsonUtils;

public class ElementOffset
{
    protected boolean centerHorizontally;
    protected boolean centerVertically = true;
    protected int xOffset;
    protected int yOffset;

    public boolean getCenterHorizontally()
    {
        return this.centerHorizontally;
    }

    public boolean getCenterVertically()
    {
        return this.centerVertically;
    }

    public int getXOffset()
    {
        return this.xOffset;
    }

    public int getYOffset()
    {
        return this.yOffset;
    }

    public void setCenterHorizontally(boolean centerHorizontally)
    {
        this.centerHorizontally = centerHorizontally;
    }

    public void setCenterVertically(boolean centerVertically)
    {
        this.centerVertically = centerVertically;
    }

    public void setXOffset(int xOffset)
    {
        this.xOffset = xOffset;
    }

    public void setYOffset(int yOffset)
    {
        this.yOffset = yOffset;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("center_h", this.centerHorizontally);
        obj.addProperty("center_v", this.centerVertically);
        obj.addProperty("off_x", this.xOffset);
        obj.addProperty("off_y", this.yOffset);

        return obj;
    }

    public void fromJson(JsonObject obj)
    {
        this.centerHorizontally = JsonUtils.getBooleanOrDefault(obj, "center_h", false);
        this.centerVertically = JsonUtils.getBooleanOrDefault(obj, "center_v", true);
        this.xOffset = JsonUtils.getIntegerOrDefault(obj, "off_x", 4);
        this.yOffset = JsonUtils.getIntegerOrDefault(obj, "off_y", 0);
    }
}
