package fi.dy.masa.malilib.gui.util;

public class Padding
{
    protected int leftPadding;
    protected int rightPadding;
    protected int topPadding;
    protected int bottomPadding;

    public Padding()
    {
    }

    public Padding(int topPadding, int rightPadding, int bottomPadding, int leftPadding)
    {
        this.topPadding = topPadding;
        this.rightPadding = rightPadding;
        this.bottomPadding = bottomPadding;
        this.leftPadding = leftPadding;
    }

    public int getLeftPadding()
    {
        return leftPadding;
    }

    public int getRightPadding()
    {
        return rightPadding;
    }

    public int getTopPadding()
    {
        return topPadding;
    }

    public int getBottomPadding()
    {
        return bottomPadding;
    }

    public Padding setLeftPadding(int leftPadding)
    {
        this.leftPadding = leftPadding;
        return this;
    }

    public Padding setRightPadding(int rightPadding)
    {
        this.rightPadding = rightPadding;
        return this;
    }

    public Padding setTopPadding(int topPadding)
    {
        this.topPadding = topPadding;
        return this;
    }

    public Padding setBottomPadding(int bottomPadding)
    {
        this.bottomPadding = bottomPadding;
        return this;
    }
}
