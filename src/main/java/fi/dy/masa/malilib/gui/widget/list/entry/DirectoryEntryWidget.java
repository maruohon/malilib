package fi.dy.masa.malilib.gui.widget.list.entry;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.FileBrowserIconProvider;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget.DirectoryEntry;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget.DirectoryEntryType;
import fi.dy.masa.malilib.gui.widget.list.header.DataColumn;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class DirectoryEntryWidget extends BaseDataListEntryWidget<DirectoryEntry>
{
    public static final DataColumn<DirectoryEntry> NAME_COLUMN =
            new DataColumn<DirectoryEntry>("malilib.gui.label.file_browser_column.name",
                                           DefaultIcons.SMALL_ARROW_UP, DefaultIcons.SMALL_ARROW_DOWN,
                                           Comparator.naturalOrder());

    public static final DataColumn<DirectoryEntry> SIZE_COLUMN =
            new DataColumn<>("malilib.gui.label.file_browser_column.size",
                             DefaultIcons.SMALL_ARROW_UP, DefaultIcons.SMALL_ARROW_DOWN,
                             Comparator.comparingLong((e) -> e.getFullPath().length()));

    public static final DataColumn<DirectoryEntry> TIME_COLUMN =
            new DataColumn<>("malilib.gui.label.file_browser_column.mtime",
                             DefaultIcons.SMALL_ARROW_UP, DefaultIcons.SMALL_ARROW_DOWN,
                             Comparator.comparingLong((e) -> e.getFullPath().lastModified()));

    protected final BaseFileBrowserWidget fileBrowserWidget;
    protected final DirectoryEntry entry;
    protected final StyledTextLine fileSizeText;
    protected final StyledTextLine modificationTimeText;
    protected boolean showSize;
    protected boolean showMTime;
    protected int sizeColumnStartX;
    protected int mTimeColumnStartX;

    public DirectoryEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                DirectoryEntry entry, BaseFileBrowserWidget fileBrowserWidget,
                                @Nullable FileBrowserIconProvider iconProvider)
    {
        super(x, y, width, height, listIndex, originalListIndex, entry, fileBrowserWidget);

        this.entry = entry;
        this.fileBrowserWidget = fileBrowserWidget;
        this.getTextSettings().setTextShadowEnabled(false);

        this.setText(StyledTextLine.raw(this.getDisplayName()));
        this.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, this.isOdd ? 0xFF202020 : 0xFF303030);
        this.getBackgroundRenderer().getHoverSettings().setColor(0xFF404040);

        int textXOffset = 2;
        @Nullable MultiIcon icon = iconProvider != null ? iconProvider.getIconForEntry(entry) : null;

        if (icon != null)
        {
            textXOffset += iconProvider.getEntryIconWidth(entry) + 2;
            this.iconOffset.setXOffset(2);
            this.setIcon(icon);
        }

        this.textOffset.setXOffset(textXOffset);

        String mTimeStr = BaseFileBrowserWidget.DATE_FORMAT.format(new Date(entry.getFullPath().lastModified()));
        this.fileSizeText = StyledTextLine.of(this.getFileSizeStringFor(entry));
        this.modificationTimeText = StyledTextLine.of(mTimeStr);
    }

    public DirectoryEntry getDirectoryEntry()
    {
        return this.entry;
    }

    protected String getFileSizeStringFor(DirectoryEntry entry)
    {
        long fileSize = entry.getFullPath().length();
        //return FileUtils.getPrettyFileSizeText(fileSize, 1);
        //return String.format("%.1f KiB", (double) fileSize / 1024.0);
        return new DecimalFormat("###,###,###.#").format((double) fileSize / 1024.0) + " KiB";
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0 && this.entry.getType() == DirectoryEntryType.DIRECTORY)
        {
            this.fileBrowserWidget.switchToDirectory(new File(this.entry.getDirectory(), this.entry.getName()));
        }
        else
        {
            return super.onMouseClicked(mouseX, mouseY, mouseButton);
        }

        return true;
    }

    protected String getDisplayName()
    {
        if (this.entry.getType() == DirectoryEntryType.DIRECTORY)
        {
            return this.entry.getDisplayName();
        }
        else
        {
            return FileUtils.getNameWithoutExtension(this.entry.getDisplayName());
        }
    }

    @Nullable
    @Override
    public Consumer<DirectoryEntryWidget> createWidgetInitializer(List<DirectoryEntry> dataList)
    {
        final boolean showSize = this.fileBrowserWidget.getShowFileSize();
        final boolean showMTime = this.fileBrowserWidget.getShowFileModificationTime();
        int timeTitleWidth = (TIME_COLUMN.getName().isPresent() ? TIME_COLUMN.getName().get().renderWidth : 0) + 6;
        int mTimeLen = Math.max(StringUtils.getStringWidth("2222-22-22 00:00:00"), timeTitleWidth) + 6;
        int sizeMaxWidth = StringUtils.getStringWidth("2,222,222,222 KiB") + 6;

        final int mTimeStartX = this.getRight() - 2 - mTimeLen;
        final int sizeEndX = showMTime ? mTimeStartX - 8 : this.getRight() - 2;
        int relativeRight = this.getWidth() - 2;

        if (showMTime)
        {
            TIME_COLUMN.setMaxContentWidth(mTimeLen);
            TIME_COLUMN.setWidth(mTimeLen);
            TIME_COLUMN.setRelativeStartX(this.getWidth() - 2 - mTimeLen);
            relativeRight -= mTimeLen + 2;
        }

        if (showSize)
        {
            SIZE_COLUMN.setMaxContentWidth(sizeMaxWidth);
            SIZE_COLUMN.setWidth(sizeMaxWidth);
            SIZE_COLUMN.setRelativeStartX(relativeRight - sizeMaxWidth);
            relativeRight -= sizeMaxWidth + 2;
        }

        NAME_COLUMN.setMaxContentWidth(relativeRight);
        NAME_COLUMN.setWidth(relativeRight);
        NAME_COLUMN.setRelativeStartX(0);

        return (w) -> {
            w.showSize = showSize && w.getDirectoryEntry().getFullPath().isFile();
            w.showMTime = showMTime;
            w.mTimeColumnStartX = mTimeStartX;

            if (w.showSize)
            {
                int width = StringUtils.getStringWidth(this.getFileSizeStringFor(w.entry));
                w.sizeColumnStartX = sizeEndX - width;
            }
        };
    }

    protected void renderInfoColumns(int x, int y, float z, ScreenContext ctx)
    {
        int color = this.getTextSettings().getTextColor();
        int ty = this.getTextPositionY(y);

        if (this.showSize)
        {
            this.renderTextLine(this.sizeColumnStartX, ty, z, color, false, ctx, this.fileSizeText);
        }

        if (this.showMTime)
        {
            this.renderTextLine(this.mTimeColumnStartX, ty, z, color, false, ctx, this.modificationTimeText);
        }
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        this.renderInfoColumns(x, y, z, ctx);
    }
}
