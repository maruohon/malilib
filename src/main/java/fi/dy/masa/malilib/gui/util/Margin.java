package fi.dy.masa.malilib.gui.util;

public class Margin
{
    protected int leftMargin;
    protected int rightMargin;
    protected int topMargin;
    protected int bottomMargin;

    public int getLeftMargin()
    {
        return leftMargin;
    }

    public int getRightMargin()
    {
        return rightMargin;
    }

    public int getTopMargin()
    {
        return topMargin;
    }

    public int getBottomMargin()
    {
        return bottomMargin;
    }

    public Margin setLeftMargin(int leftMargin)
    {
        this.leftMargin = leftMargin;
        return this;
    }

    public Margin setRightMargin(int rightMargin)
    {
        this.rightMargin = rightMargin;
        return this;
    }

    public Margin setTopMargin(int topMargin)
    {
        this.topMargin = topMargin;
        return this;
    }

    public Margin setBottomMargin(int bottomMargin)
    {
        this.bottomMargin = bottomMargin;
        return this;
    }
}
