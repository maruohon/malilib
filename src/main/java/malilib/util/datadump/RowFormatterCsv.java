package malilib.util.datadump;

import malilib.MaLiLibConfigs;

public class RowFormatterCsv extends RowFormatterBase
{
    protected String formatStringTitleCSV;
    protected String formatStringSingleLeftCSV;
    protected String columnDelimiter = ",";

    public RowFormatterCsv(DataDump dump)
    {
        super(dump);

        this.addHeaders = false;
        this.addLineSeparators = false;

        this.setColumnDelimiter(MaLiLibConfigs.Generic.DATA_DUMP_CSV_DELIMITER.getValue(), dump);
    }

    public RowFormatterCsv setColumnDelimiter(String delimiter, DataDump dump)
    {
        if (delimiter == null || delimiter.isEmpty())
        {
            delimiter = ",";
        }

        this.columnDelimiter = delimiter;
        this.createFormatStrings(dump);
        return this;
    }

    protected void createFormatStrings(DataDump dump)
    {
        StringBuilder sbFmtTitle = new StringBuilder(128);
        StringBuilder sbFmtColumns = new StringBuilder(128);
        StringBuilder sbFmtSingleLeft = new StringBuilder(128);
        final String fmtNumeric = "%s";
        final String fmtString = "\"%s\"";

        sbFmtSingleLeft.append(dump.columnIsNumeric[0] ? fmtNumeric : fmtString);

        for (int i = 0; i < dump.columns; i++)
        {
            sbFmtTitle.append(fmtString);
            sbFmtColumns.append(dump.columnIsNumeric[i] ? fmtNumeric : fmtString);

            if (i < (dump.columns - 1))
            {
                sbFmtTitle.append(this.columnDelimiter);
                sbFmtColumns.append(this.columnDelimiter);
                sbFmtSingleLeft.append(this.columnDelimiter);
            }
        }

        this.formatStringTitleCSV = sbFmtTitle.toString();
        this.formatStringColumns = sbFmtColumns.toString();
        this.formatStringSingleLeftCSV = sbFmtSingleLeft.toString();
    }

    @Override
    public String getFormattedHeaderOrFooter(String header)
    {
        return DataDump.EMPTY_STRING;
    }

    @Override
    public String getFormattedTitle(Row title)
    {
        Object[] values = title.getValues();
        return String.format(this.formatStringTitleCSV, values);
    }

    @Override
    public String getFormattedData(Row data)
    {
        String[] valuesStr = data.getValues();
        Object[] valuesObj = new Object[valuesStr.length];

        for (int i = 0; i < valuesObj.length; i++)
        {
            String str = valuesStr[i];

            // Fix the values so that they don't break the CSV format,
            // ie. double any quotes (escape quotes with a quote).
            // Note that all non-numeric columns (ie. strings) are already being surrounded
            // in double quotes by the format string.
            str = str.replace("\"", "\"\"");

            // Numeric columns are not surrounded in double quotes by default, so if there are
            // any commas in those, then we need to double quote in those cases.
            if (this.dump.columnIsNumeric[i] && str.contains(this.columnDelimiter))
            {
                str = "\"" + str + "\"";
            }

            valuesObj[i] = str.trim();
        }

        return String.format(this.formatStringColumns, valuesObj);
    }
}
