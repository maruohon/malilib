package fi.dy.masa.malilib.util.data;

public class IntRange
{
    protected final int first;
    protected final int last;
    protected final int length;

    public IntRange(int start, int length)
    {
        this.first = start;
        this.length = length;
        this.last = start + length - 1;
    }

    public int getFirst()
    {
        return this.first;
    }

    public int getLast()
    {
        return this.last;
    }

    public int getLength()
    {
        return this.length;
    }

    public boolean contains(int value)
    {
        return value >= this.first && value <= this.last;
    }

    @Override
    public String toString()
    {
        return String.format("IntRange:{first:%d,last:%d,length:%d}", this.first, this.last, this.length);
    }

    public static IntRange of(int start, int length)
    {
        return new IntRange(start, length);
    }
}
