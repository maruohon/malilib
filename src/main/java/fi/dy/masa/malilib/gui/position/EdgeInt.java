package fi.dy.masa.malilib.gui.position;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.listener.EventListener;

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

    public void setFrom(EdgeInt other)
    {
        this.left = other.left;
        this.right = other.right;
        this.top = other.top;
        this.bottom = other.bottom;
        this.notifyChange();
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

    public EdgeInt setAll(int value)
    {
        this.left = value;
        this.right = value;
        this.bottom = value;
        this.top = value;
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
}
