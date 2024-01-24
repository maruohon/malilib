package malilib.util.data;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import malilib.listener.EventListener;
import malilib.util.StringUtils;

public class EdgeInt
{
    protected int defaultLeft;
    protected int defaultRight;
    protected int defaultTop;
    protected int defaultBottom;
    protected int left;
    protected int right;
    protected int top;
    protected int bottom;
    @Nullable protected EventListener changeListener;

    public EdgeInt()
    {
    }

    public EdgeInt(int all)
    {
        this(all, all, all, all);
    }

    public EdgeInt(int top, int right, int bottom, int left)
    {
        this.defaultTop = top;
        this.defaultRight = right;
        this.defaultBottom = bottom;
        this.defaultLeft = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public int getLeft()
    {
        return this.left;
    }

    public int getRight()
    {
        return this.right;
    }

    public int getTop()
    {
        return this.top;
    }

    public int getBottom()
    {
        return this.bottom;
    }

    public int getHorizontalTotal()
    {
        return this.left + this.right;
    }

    public int getVerticalTotal()
    {
        return this.top + this.bottom;
    }

    public boolean isEmpty()
    {
        return this.left == 0 && this.top == 0 && this.right == 0 && this.bottom == 0;
    }

    public EdgeInt setChangeListener(@Nullable EventListener changeListener)
    {
        this.changeListener = changeListener;
        return this;
    }

    protected void notifyChange()
    {
        if (this.changeListener != null)
        {
            this.changeListener.onEvent();
        }
    }

    public EdgeInt setFrom(EdgeInt other)
    {
        return this.setAll(other.top, other.right, other.bottom, other.left);
    }

    /**
     * Sets the default values used for checking if the values have later been modified,
     * and if the values need to be serialized.
     * Also sets the "normal" values.
     */
    public EdgeInt setAllDefaults(int value)
    {
        return this.setDefaults(value, value, value, value);
    }

    /**
     * Sets the default values used for checking if the values have later been modified,
     * and if the values need to be serialized.
     * Also sets the "normal" values.
     */
    public EdgeInt setDefaults(int top, int right, int bottom, int left)
    {
        this.defaultTop = top;
        this.defaultRight = right;
        this.defaultBottom = bottom;
        this.defaultLeft = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        return this;
    }

    public EdgeInt setAll(int value)
    {
        return this.setAll(value, value, value, value);
    }

    public EdgeInt setAll(int top, int right, int bottom, int left)
    {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        this.notifyChange();
        return this;
    }

    public EdgeInt setLeft(int left)
    {
        this.left = left;
        this.notifyChange();
        return this;
    }

    public EdgeInt setRight(int right)
    {
        this.right = right;
        this.notifyChange();
        return this;
    }

    public EdgeInt setTop(int top)
    {
        this.top = top;
        this.notifyChange();
        return this;
    }

    public EdgeInt setBottom(int bottom)
    {
        this.bottom = bottom;
        this.notifyChange();
        return this;
    }

    public EdgeInt setTopBottom(int value)
    {
        this.bottom = value;
        this.top = value;
        this.notifyChange();
        return this;
    }

    public EdgeInt setLeftRight(int value)
    {
        this.left = value;
        this.right = value;
        this.notifyChange();
        return this;
    }

    public List<String> getHoverTooltip()
    {
        return Collections.singletonList(StringUtils.translate("malilib.hover.button.edge_int_values",
                                         this.top, this.left, this.right, this.bottom));
    }

    public boolean isModified()
    {
        return this.left != this.defaultLeft ||
               this.right != this.defaultRight ||
               this.top != this.defaultTop ||
               this.bottom != this.defaultBottom;
    }

    public void writeToJsonIfModified(JsonObject obj, String keyName)
    {
        if (this.isModified())
        {
            obj.add(keyName, this.toJson());
        }
    }

    public JsonArray toJson()
    {
        JsonArray arr = new JsonArray();

        arr.add(new JsonPrimitive(this.top));
        arr.add(new JsonPrimitive(this.right));
        arr.add(new JsonPrimitive(this.bottom));
        arr.add(new JsonPrimitive(this.left));

        return arr;
    }

    public boolean fromJson(JsonArray arr)
    {
        if (arr.size() == 4)
        {
            try
            {
                // Read to locals first, so that we know that they all were successfully
                // read as int before changing any of the original values.
                int top    = arr.get(0).getAsInt();
                int right  = arr.get(1).getAsInt();
                int bottom = arr.get(2).getAsInt();
                int left   = arr.get(3).getAsInt();

                this.top = top;
                this.right = right;
                this.bottom = bottom;
                this.left = left;

                this.notifyChange();

                return true;
            }
            catch (Exception ignore) {}
        }

        return false;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        EdgeInt edgeInt = (EdgeInt) o;

        /*
        if (this.defaultLeft != edgeInt.defaultLeft) { return false; }
        if (this.defaultRight != edgeInt.defaultRight) { return false; }
        if (this.defaultTop != edgeInt.defaultTop) { return false; }
        if (this.defaultBottom != edgeInt.defaultBottom) { return false; }
        */
        if (this.left != edgeInt.left) { return false; }
        if (this.right != edgeInt.right) { return false; }
        if (this.top != edgeInt.top) { return false; }
        return this.bottom == edgeInt.bottom;
    }

    @Override
    public int hashCode()
    {
        /*
        int result = this.defaultLeft;
        result = 31 * result + this.defaultRight;
        result = 31 * result + this.defaultTop;
        result = 31 * result + this.defaultBottom;
        result = 31 * result + this.left;
        */
        int result = this.left;
        result = 31 * result + this.right;
        result = 31 * result + this.top;
        result = 31 * result + this.bottom;
        return result;
    }
}
