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

    public int getElementPositionX(int baseX, int parentSize, int elementSize)
    {
        int position = baseX + this.getXOffset();

        if (this.getCenterHorizontally())
        {
            position += getCenteredElementOffset(parentSize, elementSize);
        }

        return position;
    }

    public int getElementPositionY(int baseY, int parentSize, int elementSize)
    {
        int position = baseY + this.getYOffset();

        if (this.getCenterVertically())
        {
            position += getCenteredElementOffset(parentSize, elementSize);
        }

        return position;
    }

    public ElementOffset setCenterHorizontally(boolean centerHorizontally)
    {
        this.centerHorizontally = centerHorizontally;
        return this;
    }

    public ElementOffset setCenterVertically(boolean centerVertically)
    {
        this.centerVertically = centerVertically;
        return this;
    }

    public ElementOffset setXOffset(int xOffset)
    {
        this.xOffset = xOffset;
        return this;
    }

    public ElementOffset setYOffset(int yOffset)
    {
        this.yOffset = yOffset;
        return this;
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

    public static int getCenteredElementOffset(int parentSize, int elementSize)
    {
        return (parentSize - elementSize) / 2;
    }
}
