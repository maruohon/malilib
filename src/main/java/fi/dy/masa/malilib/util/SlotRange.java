package fi.dy.masa.malilib.util;

public class SlotRange
{
    private final int first;
    private final int last;

    public SlotRange(int start, int numSlots)
    {
        this.first = start;
        this.last = start + numSlots - 1;
    }

    public int getFirst()
    {
        return this.first;
    }

    public int getLast()
    {
        return this.last;
    }

    public int getSlotCount()
    {
        return this.last - this.first + 1;
    }

    public boolean contains(int slot)
    {
        return slot >= this.first && slot <= this.last;
    }

    @Override
    public String toString()
    {
        return String.format("SlotRange: {first: %d, last: %d}", this.first, this.last);
    }

    public static SlotRange of(int start, int numSlots)
    {
        return new SlotRange(start, numSlots);
    }
}
