package fi.dy.masa.malilib.util.data.dump;

public class RowFormatterSimpleText extends RowFormatterBase
{
    public RowFormatterSimpleText(DataDump dump)
    {
        super(dump);

        StringBuilder sbColumns = new StringBuilder(128);
        StringBuilder sbSeparator = new StringBuilder(256);

        String[] title = dump.title.getValues();
        int titleLength = (dump.columns - 1) * 2;

        for (int i = 0; i < dump.columns; ++i)
        {
            if (i < (dump.columns - 1))
            {
                sbColumns.append("%s, ");
            }
            else
            {
                sbColumns.append("%s");
            }

            titleLength += title[i].length();
        }

        // Generate the separator after the title line, like: --------

        for (int i = 0; i < titleLength; ++i)
        {
            sbSeparator.append("-");
        }

        this.formatStringColumns = sbColumns.toString();
        this.lineSeparator = sbSeparator.toString();
    }

    @Override
    protected String getFormattedHeaderOrFooter(String header)
    {
        return header;
    }
}
