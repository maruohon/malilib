package malilib.util.datadump;

public class RowFormatterCompactTable extends RowFormatterBase
{
    public RowFormatterCompactTable(DataDump dump)
    {
        super(dump);

        String colSep = dump.useColumnSeparator ? "|" : " ";
        String lineColSep = dump.useColumnSeparator ? "+" : "-";
        StringBuilder sbColumns = new StringBuilder(128);
        StringBuilder sbSeparator = new StringBuilder(256);

        String[] title = dump.title.getValues();
        int titleLength = dump.columns * 3 + 1 - 2;

        sbColumns.append(colSep);

        for (int i = 0; i < dump.columns; ++i)
        {
            sbColumns.append(" %s ").append(colSep);
            titleLength += title[i].length();
        }

        // Generate the separator after the title line, like: +--------+
        sbSeparator.append(lineColSep);

        for (int i = 0; i < titleLength; ++i)
        {
            sbSeparator.append("-");
        }

        sbSeparator.append(lineColSep);

        this.formatStringColumns = sbColumns.toString();
        this.lineSeparator = sbSeparator.toString();
    }

    @Override
    protected String getFormattedHeaderOrFooter(String header)
    {
        String colSep = this.dump.useColumnSeparator ? "|" : "";
        return colSep + " " + header + " " + colSep;
    }
}
