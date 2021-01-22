package fi.dy.masa.malilib.util.data.dump;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.MaLiLib;

public class DataDump
{
    public static final String EMPTY_STRING = "";

    protected final ArrayList<Row> lines = new ArrayList<>();
    protected final ArrayList<String> headers = new ArrayList<>();
    protected final ArrayList<String> footers = new ArrayList<>();
    protected final Alignment[] alignment;
    protected final boolean[] columnIsNumeric;
    protected final int[] columnLengths;
    protected final int columnSeparatorsLength;
    protected final int columns;

    protected Row title;
    protected boolean centerTitle;
    protected boolean repeatTitleAtBottom;
    protected boolean useColumnSeparator = true;
    protected boolean sort = true;
    protected boolean sortReverse;
    protected int maxCombinedDataLength;
    protected int maxTotalLineLength;
    protected int sortColumn = -1;
    protected Format format;

    public DataDump(int columns)
    {
        this(columns, Format.ASCII);
    }

    public DataDump(int columns, Format format)
    {
        this.columns = columns;
        this.format = format;
        this.columnSeparatorsLength = Math.max(this.columns - 1, 0) * 3;
        this.alignment = new Alignment[columns];
        this.columnIsNumeric = new boolean[columns];
        this.columnLengths = new int[columns];
        this.repeatTitleAtBottom = format != Format.CSV;

        Arrays.fill(this.alignment, Alignment.LEFT);
        Arrays.fill(this.columnIsNumeric, false);
    }

    public DataDump setColumnProperties(int columnId, Alignment align, boolean isNumeric)
    {
        this.setColumnAlignment(columnId, align);
        this.columnIsNumeric[columnId] = isNumeric;

        return this;
    }

    public DataDump setColumnAlignment(int columnId, Alignment align)
    {
        if (columnId < 0 || columnId >= this.columns)
        {
            throw new IllegalArgumentException("setColumnAlignment(): Invalid column id '" + columnId + "', max is " + (this.columns - 1));
        }

        this.alignment[columnId] = align;
        return this;
    }

    public DataDump setColumnIsNumeric(int columnId, boolean isNumeric)
    {
        if (columnId < 0 || columnId >= this.columns)
        {
            throw new IllegalArgumentException("setColumnIsNumeric(): Invalid column id '" + columnId + "', max is " + (this.columns - 1));
        }

        this.columnIsNumeric[columnId] = isNumeric;
        return this;
    }

    public Format getFormat()
    {
        return this.format;
    }

    public DataDump setFormat(Format format)
    {
        this.format = format;
        return this;
    }

    public DataDump setSort(boolean sort)
    {
        this.sort = sort;
        return this;
    }

    public DataDump setSortReverse(boolean reverse)
    {
        this.sortReverse = reverse;
        return this;
    }

    public DataDump setSortColumn(int column)
    {
        if (column >= 0 && column < this.columns)
        {
            this.sortColumn = column;
        }

        return this;
    }

    public DataDump setCenterTitle(boolean center)
    {
        this.centerTitle = center;
        return this;
    }

    public DataDump setRepeatTitleAtBottom(boolean repeat)
    {
        this.repeatTitleAtBottom = repeat;
        return this;
    }

    public DataDump setUseColumnSeparator(boolean value)
    {
        this.useColumnSeparator = value;
        return this;
    }

    public void addTitle(String... data)
    {
        if (this.checkHeaderData(data))
        {
            this.title = new Row(data);
        }
    }

    public void addHeader(String header)
    {
        this.checkHeaderData(header);
        this.headers.add(header);
    }

    public void addHeader(int index, String header)
    {
        this.checkHeaderData(header);
        this.headers.add(index, header);
    }

    public void addFooter(String header)
    {
        this.checkHeaderData(header);
        this.footers.add(header);
    }

    public void clearHeader()
    {
        this.headers.clear();
    }

    public void clearFooter()
    {
        this.footers.clear();
    }

    public void addData(String... data)
    {
        if (this.updateMaxColumnLengths(data))
        {
            this.lines.add(new Row(data, this.sortColumn));
        }
    }

    protected boolean checkHeaderData(String... data)
    {
        if (data.length != 1 || this.columns == 1)
        {
            return this.updateMaxColumnLengths(data);
        }

        return false;
    }

    protected void checkAllHeaders()
    {
        if ((this.format == Format.ASCII || this.format == Format.COMPACT) && this.columns != 1)
        {
            if (this.title != null)
            {
                this.updateTableLengthForHeader(this.title);
            }

            int size = this.headers.size();

            for (int i = 0; i < size; i++)
            {
                this.updateTableLengthForHeader(this.headers.get(i));
            }

            size = this.footers.size();

            for (int i = 0; i < size; i++)
            {
                this.updateTableLengthForHeader(this.footers.get(i));
            }
        }
    }

    protected void updateTableLengthForHeader(Row row)
    {
        String[] values = row.getValues();

        if (values.length == 1)
        {
            this.updateTableLengthForHeader(values[0]);
        }
    }

    protected void updateTableLengthForHeader(String header)
    {
        int len = header.length();

        // The title is longer than all the columns and padding character put together,
        // so we will add to the last column's width enough to widen the entire table enough to fit the header.
        if (len > this.maxTotalLineLength)
        {
            int diff = len - this.maxTotalLineLength;
            this.columnLengths[this.columns - 1] += diff;
            this.maxCombinedDataLength += diff;
            this.maxTotalLineLength += diff;
        }
    }

    protected boolean updateMaxColumnLengths(String... data)
    {
        if (data.length != this.columns && data.length != 1)
        {
            throw new IllegalArgumentException("Invalid number of columns, you must add exactly " + this.columns + " columns for this type of DataDump");
        }

        int totalDataLength = 0;
        boolean valid = true;

        for (int i = 0; i < data.length; i++)
        {
            if (data[i] == null)
            {
                MaLiLib.LOGGER.warn("null value at column index {} on row '{}'", i, this.rowDataToString(data));
                valid = false;
            }
            else if (this.format == Format.ASCII)
            {
                int len = data[i].length();

                if (len > this.columnLengths[i])
                {
                    this.columnLengths[i] = len;
                }

                totalDataLength += this.columnLengths[i];
            }
        }

        if (this.format == Format.ASCII && totalDataLength > this.maxCombinedDataLength)
        {
            this.maxCombinedDataLength = totalDataLength;
            this.maxTotalLineLength = totalDataLength + this.columnSeparatorsLength;
        }

        return valid;
    }

    protected String rowDataToString(String... data)
    {
        return String.join(", ", data);
    }

    public List<String> getLines()
    {
        if (this.sort)
        {
            if (this.sortReverse)
            {
                this.lines.sort(Comparator.reverseOrder());
            }
            else
            {
                this.lines.sort(Row::compareTo);
            }
        }

        return this.format.getRowFormatter(this).getFormattedLines();
    }

    @Nullable
    public static File dumpDataToFile(File outputDir, String fileNameBase, List<String> lines, Format format)
    {
        String extension = format == Format.CSV ? ".csv" : ".txt";
        return dumpDataToFile(outputDir, fileNameBase, extension, lines);
    }

    @Nullable
    public static File dumpDataToFile(File outputDir, String fileNameBase, List<String> lines)
    {
        return dumpDataToFile(outputDir, fileNameBase, ".txt", lines);
    }

    @Nullable
    public static File dumpDataToFile(File outputDir, String fileNameBase, String fileNameExtension, List<String> lines)
    {
        if (outputDir.exists() == false && outputDir.mkdirs() == false)
        {
            MaLiLib.LOGGER.error("dumpDataToFile(): Failed to create the dump output directory '{}'", outputDir);
            return null;
        }

        File outFile = getAvailableTimestampedFileName(outputDir, fileNameBase, fileNameExtension);

        if (outFile == null)
        {
            return null;
        }

        try
        {
            if (outFile.createNewFile() == false)
            {
                MaLiLib.LOGGER.error("dumpDataToFile(): Failed to create data dump file '{}'", outFile.getName());
            }
        }
        catch (IOException e)
        {
            MaLiLib.LOGGER.error("dumpDataToFile(): Failed to create data dump file '{}'", outFile.getName(), e);
            return null;
        }

        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

            for (String line : lines)
            {
                writer.write(line);
                writer.newLine();
            }

            writer.close();
        }
        catch (IOException e)
        {
            MaLiLib.LOGGER.error("dumpDataToFile(): Exception while writing data dump to file '{}'", outFile.getName(), e);
        }

        return outFile;
    }

    @Nullable
    public static File getAvailableTimestampedFileName(File outputDir, String fileNameBase, String fileNameExtension)
    {
        String fileNameBaseWithDate = fileNameBase + "_" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date(System.currentTimeMillis()));
        String fileName = fileNameBaseWithDate + fileNameExtension;
        File outFile = new File(outputDir, fileName);
        int postFix = 1;

        while (outFile.exists() && postFix < 100)
        {
            fileName = fileNameBaseWithDate + "_" + postFix + fileNameExtension;
            outFile = new File(outputDir, fileName);
            postFix++;
        }

        if (outFile.exists())
        {
            MaLiLib.LOGGER.error("dumpDataToFile(): Failed to create data dump file '{}', one already exists", fileName);
            return null;
        }

        return outFile;
    }

    public static void printDataToLogger(List<String> lines)
    {
        for (String line : lines)
        {
            MaLiLib.LOGGER.info(line);
        }
    }

    public enum Alignment
    {
        LEFT,
        RIGHT;
    }

    public enum Format
    {
        ASCII   ("ascii-table",     RowFormatterAsciiTable::new),
        COMPACT ("compact-table",   RowFormatterCompactTable::new),
        CSV     ("csv",             RowFormatterCsv::new),
        SIMPLE  ("simple",          RowFormatterSimpleText::new);

        private final String arg;
        private final Function<DataDump, RowFormatterBase> rowFormatterFactory;

        Format(String arg, Function<DataDump, RowFormatterBase> rowFormatterFactory)
        {
            this.arg = arg;
            this.rowFormatterFactory = rowFormatterFactory;
        }

        public String getArgument()
        {
            return this.arg;
        }

        public RowFormatterBase getRowFormatter(DataDump dump)
        {
            return this.rowFormatterFactory.apply(dump);
        }

        @Nullable
        public static Format fromArg(String arg)
        {
            for (Format type : Format.values())
            {
                if (type.arg.equals(arg))
                {
                    return type;
                }
            }

            return null;
        }
    }
}
