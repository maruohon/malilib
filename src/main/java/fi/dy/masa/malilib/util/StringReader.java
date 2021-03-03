package fi.dy.masa.malilib.util;

public class StringReader
{
    protected final String string;
    protected final int length;
    protected int pos;
    protected int storedPos;

    public StringReader(String string)
    {
        this.string = string;
        this.length = string.length();
    }

    public boolean canRead()
    {
        return this.canPeekAt(this.pos);
    }

    public boolean canPeekAt(int pos)
    {
        return pos >= 0 && pos < this.length;
    }

    public char peek()
    {
        return this.peekAt(this.pos);
    }

    public char read()
    {
        return this.peekAt(this.pos++);
    }

    public char peekPrevious()
    {
        return this.peekAt(this.pos - 1);
    }

    public char peekNext()
    {
        return this.peekAt(this.pos + 1);
    }

    public char peekAtOffset(int offset)
    {
        return this.peekAt(this.pos + offset);
    }

    public char peekAt(int pos)
    {
        return this.canPeekAt(pos) ? this.string.charAt(pos) : 0;
    }

    public String subString()
    {
        return this.canRead() ? this.string.substring(this.pos) : "";
    }

    public String subString(Region region)
    {
        return this.subString(region.start, region.end);
    }

    /**
     * Note: The end index is inclusive, in contrast to the Java String#substring()
     */
    public String subString(int start, int end)
    {
        if (start >= 0 && end >= start && start < this.length && end < this.length)
        {
            return this.string.substring(start, end + 1);
        }

        return "";
    }

    public StringReader subReader(Region region)
    {
        return this.subReader(region.start, region.end);
    }

    /**
     * Note: The end index is inclusive, in contrast to the Java String#substring()
     */
    public StringReader subReader(int start, int end)
    {
        return new StringReader(this.subString(start, end));
    }

    public boolean startsWith(String str)
    {
        final int len = str.length();

        if ((this.length - this.pos) < len)
        {
            return false;
        }

        int pos = this.pos;

        for (int i = 0; i < len; ++i, ++pos)
        {
            if (this.string.charAt(pos) != str.charAt(i))
            {
                return false;
            }
        }

        return true;
    }

    public boolean skip()
    {
        return this.skip(1);
    }

    public boolean skip(int amount)
    {
        if (amount > 0 && (this.pos + amount) <= this.length)
        {
            this.pos += amount;
            return true;
        }

        return false;
    }

    public int findNext(char c)
    {
        for (int i = this.pos; i < this.length; ++i)
        {
            if (this.string.charAt(i) == c)
            {
                return i;
            }
        }

        return -1;
    }

    public int findNext(String subStr)
    {
        return this.canRead() ? this.string.substring(this.pos).indexOf(subStr) : -1;
    }

    public int getPos()
    {
        return this.pos;
    }

    public StringReader setPos(int pos)
    {
        if (pos >= 0 && pos <= this.length)
        {
            this.pos = pos;
        }

        return this;
    }

    public int getLength()
    {
        return this.length;
    }

    public void storePos()
    {
        this.storedPos = this.pos;
    }

    public void restorePos()
    {
        this.pos = this.storedPos;
    }

    public String getString()
    {
        return this.string;
    }

    @Override
    public String toString()
    {
        return "StringReader{string='" + this.string + "',length=" + this.length +
               ",pos=" + this.pos + ",storedPos=" + this.storedPos + '}';
    }

    public static class Region
    {
        public final int start;
        public final int end;

        public Region(int start, int end)
        {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString()
        {
            return String.format("Region:{start:%d,end:%d}", this.start, this.end);
        }
    }
}
