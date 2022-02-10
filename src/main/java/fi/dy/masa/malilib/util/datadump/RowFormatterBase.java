package fi.dy.masa.malilib.util.datadump;

import java.util.ArrayList;
import java.util.List;

public abstract class RowFormatterBase
{
    protected final DataDump dump;
    protected String formatStringColumns;
    protected String lineSeparator;
    protected boolean addHeaders = true;
    protected boolean addLineSeparators = true;

    protected RowFormatterBase(DataDump dump)
    {
        this.dump = dump;
    }

    public List<String> getFormattedLines()
    {
        ArrayList<String> linesOut = new ArrayList<>();

        this.addHeaders(this.dump.headers, linesOut);
        this.addTopTitle(this.dump.title, linesOut);
        this.addDataRows(this.dump.lines, linesOut);
        this.addBottomTitle(this.dump.title, linesOut);
        this.addFooters(this.dump.footers, linesOut);

        return linesOut;
    }

    public void addHeaders(List<String> headers, List<String> linesOut)
    {
        if (this.addHeaders)
        {
            if (this.addLineSeparators)
            {
                linesOut.add(this.lineSeparator);
            }

            final int len = headers.size();

            if (len > 0)
            {
                for (String header : headers)
                {
                    linesOut.add(this.getFormattedHeaderOrFooter(header));
                }

                if (this.addLineSeparators)
                {
                    linesOut.add(this.lineSeparator);
                }
            }
        }
    }

    protected void addTopTitle(Row title, List<String> linesOut)
    {
        linesOut.add(this.getFormattedTitle(title));

        if (this.addLineSeparators)
        {
            linesOut.add(this.lineSeparator);
        }
    }

    protected void addBottomTitle(Row title, List<String> linesOut)
    {
        if (this.dump.repeatTitleAtBottom)
        {
            if (this.addLineSeparators)
            {
                linesOut.add(this.lineSeparator);
            }

            linesOut.add(this.getFormattedTitle(title));
        }
    }

    protected void addDataRows(List<Row> data, List<String> linesOut)
    {
        for (Row row : data)
        {
            linesOut.add(this.getFormattedData(row));
        }
    }

    protected void addFooters(List<String> footers, List<String> linesOut)
    {
        this.addHeaders(footers, linesOut);
    }

    protected abstract String getFormattedHeaderOrFooter(String header);

    protected String getFormattedTitle(Row title)
    {
        return this.getFormattedData(title);
    }

    protected String getFormattedData(Row data)
    {
        Object[] values = data.getValues();
        return String.format(this.formatStringColumns, values);
    }
}
