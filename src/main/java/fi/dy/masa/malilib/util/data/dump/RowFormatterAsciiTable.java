package fi.dy.masa.malilib.util.data.dump;

public class RowFormatterAsciiTable extends RowFormatterBase
{
    protected final String fmtCenteredString;
    protected final String fmtLeftAlignedPaddedString;

    public RowFormatterAsciiTable(DataDump dump)
    {
        super(dump);

        dump.checkAllHeaders();

        String colSep = dump.useColumnSeparator ? "|" : " ";
        String lineColSep = dump.useColumnSeparator ? "+" : "-";
        StringBuilder sbFmt = new StringBuilder(128);
        StringBuilder sbSep = new StringBuilder(256);
        sbFmt.append(colSep);
        sbSep.append(lineColSep);

        for (int i = 0; i < dump.columns; i++)
        {
            int width = dump.columnLengths[i];

            if (dump.alignment[i] == DataDump.Alignment.LEFT)
            {
                sbFmt.append(String.format(" %%-%ds %s", width, colSep));
            }
            else
            {
                sbFmt.append(String.format(" %%%ds %s", width, colSep));
            }

            for (int j = 0; j < width + 2; j++)
            {
                sbSep.append("-");
            }

            sbSep.append(lineColSep);
        }

        this.formatStringColumns = sbFmt.toString();
        this.lineSeparator = sbSep.toString();
        this.fmtCenteredString = colSep + " %%%ds%%s%%%ds " + colSep;
        this.fmtLeftAlignedPaddedString = colSep + " %%-%ds " + colSep;
    }

    @Override
    public String getFormattedHeaderOrFooter(String header)
    {
        int lineLength = this.dump.maxTotalLineLength;
        boolean isCenter = false;
        String fmt;

        if (this.dump.centerTitle)
        {
            int len = header.length();
            int start = (lineLength - len) / 2;

            if (start > 0)
            {
                fmt = String.format(this.fmtCenteredString, start, lineLength - len - start);
                isCenter = true;
            }
            else
            {
                fmt = String.format(this.fmtLeftAlignedPaddedString, lineLength);
            }
        }
        else
        {
            fmt = String.format(this.fmtLeftAlignedPaddedString, lineLength);
        }

        return isCenter ? String.format(fmt, " ", header, " ") : String.format(fmt, header);
    }
}
