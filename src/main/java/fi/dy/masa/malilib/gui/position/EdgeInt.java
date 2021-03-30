package fi.dy.masa.malilib.gui.position;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import com.google.gson.JsonArray;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;

public class EdgeInt
{
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
        return Collections.singletonList(StringUtils.translate("malilib.gui.button.hover.edge_int_values",
                                         this.top, this.left, this.right, this.bottom));
    }

    public JsonArray toJson()
    {
        JsonArray arr = new JsonArray();

        arr.add(this.getTop());
        arr.add(this.getRight());
        arr.add(this.getBottom());
        arr.add(this.getLeft());

        return arr;
    }

    public boolean fromJson(JsonArray arr)
    {
        if (arr.size() == 4)
        {
            try
            {
                // Read to locals first, so that we know that they all were able to be read as ints
                // before changing any of the original values.
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
}
