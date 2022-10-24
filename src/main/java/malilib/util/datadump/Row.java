package malilib.util.datadump;

import javax.annotation.Nonnull;

public class Row implements Comparable<Row>
{
    protected final String[] strings;
    protected final Double[] numbers;
    protected int sortColumn = -1;

    public Row(String[] strings)
    {
        this.strings = strings;
        this.numbers = new Double[strings.length];

        for (int i = 0; i < strings.length; i++)
        {
            try
            {
                this.numbers[i] = Double.parseDouble(strings[i]);
            }
            catch (NumberFormatException ignore) {}
        }
    }

    public Row(String[] strings, int sortColumn)
    {
        this(strings);

        if (sortColumn >= 0 && sortColumn < this.strings.length)
        {
            this.sortColumn = sortColumn;
        }
    }

    public String[] getValues()
    {
        return this.strings;
    }

    @Override
    public int compareTo(@Nonnull Row other)
    {
        int column = this.sortColumn;

        if (column >= 0)
        {
            if (this.numbers[column] != null && other.numbers[column] != null)
            {
                double d1 = this.numbers[column];
                double d2 = other.numbers[column];

                if (d1 < d2)
                {
                    return -1;
                }
                else if (d1 > d2)
                {
                    return 1;
                }
            }
            else
            {
                int res = this.strings[column].compareTo(other.strings[column]);

                if (res != 0)
                {
                    return res;
                }
            }
        }

        for (int i = 0; i < this.strings.length; i++)
        {
            if (this.numbers[i] != null && other.numbers[i] != null)
            {
                double d1 = this.numbers[i];
                double d2 = other.numbers[i];

                if (d1 < d2)
                {
                    return -1;
                }
                else if (d1 > d2)
                {
                    return 1;
                }
            }
            else
            {
                int res = this.strings[i].compareTo(other.strings[i]);

                if (res != 0)
                {
                    return res;
                }
            }
        }

        return 0;
    }
}
