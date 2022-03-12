package fi.dy.masa.malilib.gui.widget.list.entry;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Date;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.FileBrowserIconProvider;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget.DirectoryEntry;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget.DirectoryEntryType;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.ListEntryWidgetInitializer;
import fi.dy.masa.malilib.gui.widget.list.header.DataColumn;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.FileNameUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class DirectoryEntryWidget extends BaseDataListEntryWidget<DirectoryEntry>
{
    public static final DataColumn<DirectoryEntry> NAME_COLUMN =
            new DataColumn<DirectoryEntry>("malilib.label.file_browser.column.file_name",
                                           Comparator.naturalOrder());

    public static final DataColumn<DirectoryEntry> SIZE_COLUMN =
            new DataColumn<>("malilib.label.file_browser.column.file_size",
                             Comparator.comparingLong((e) -> e.getFullPath().length()));

    public static final DataColumn<DirectoryEntry> TIME_COLUMN =
            new DataColumn<>("malilib.label.file_browser.column.last_modified",
                             Comparator.comparingLong((e) -> e.getFullPath().lastModified()));

    protected static final DecimalFormat FILE_SIZE_FORMAT = new DecimalFormat("###,###,###.#");

    protected final BaseFileBrowserWidget fileBrowserWidget;
    protected final StyledTextLine fileSizeText;
    protected final StyledTextLine modificationTimeText;
    protected boolean showSize;
    protected boolean showMTime;
    protected int sizeColumnEndX;
    protected int mTimeColumnEndX;

    public DirectoryEntryWidget(DirectoryEntry entry,
                                DataListEntryWidgetData constructData,
                                BaseFileBrowserWidget fileBrowserWidget,
                                @Nullable FileBrowserIconProvider iconProvider)
    {
        super(entry, constructData);

        this.fileBrowserWidget = fileBrowserWidget;
        this.getTextSettings().setTextShadowEnabled(false);

        this.setText(StyledTextLine.raw(this.getDisplayName()));
        this.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, this.isOdd ? 0xFF202020 : 0xFF303030);
        this.getBackgroundRenderer().getHoverSettings().setColor(0xFF404040);

        int textXOffset = 3;
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

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            this.fileBrowserWidget.closeCurrentContextMenu();

            if (this.data.getType() == DirectoryEntryType.DIRECTORY)
            {
                this.fileBrowserWidget.switchToDirectory(new File(this.data.getDirectory(), this.data.getName()));
                return true;
            }
        }

        if (mouseButton == 1)
        {
            this.fileBrowserWidget.openContextMenuForEntry(mouseX, mouseY, this.originalListIndex);
            return true;
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        this.renderInfoColumns(x, y, z, ctx);
    }

    protected void renderInfoColumns(int x, int y, float z, ScreenContext ctx)
    {
        boolean hovered = this.isHoveredForRender(ctx);
        int color = this.getTextSettings().getEffectiveTextColor(hovered);
        int usableHeight = this.getHeight() - this.padding.getVerticalTotal();
        int ty = this.getTextPositionY(y, usableHeight, this.getLineHeight());

        if (this.showSize)
        {
            this.renderTextLineRightAligned(x + this.sizeColumnEndX, ty, z, color, false, this.fileSizeText, ctx);
        }

        if (this.showMTime)
        {
            this.renderTextLineRightAligned(x + this.mTimeColumnEndX, ty, z, color, false, this.modificationTimeText, ctx);
        }
    }

    protected String getDisplayName()
    {
        if (this.data.getType() == DirectoryEntryType.DIRECTORY)
        {
            return this.data.getDisplayName();
        }
        else
        {
            return FileNameUtils.getFileNameWithoutExtension(this.data.getDisplayName());
        }
    }

    protected String getFileSizeStringFor(DirectoryEntry entry)
    {
        long fileSize = entry.getFullPath().length();
        return FILE_SIZE_FORMAT.format((double) fileSize / 1024.0) + " KiB";
    }

    public static class WidgetInitializer implements ListEntryWidgetInitializer<DirectoryEntry>
    {
        protected final int maxSizeColumnLength = StringUtils.getStringWidth("222,222,222 KiB");
        protected final int maxTimeColumnLength = StringUtils.getStringWidth("2222-22-22 00:00:00");
        protected final int timeTitleWidth = (TIME_COLUMN.getName().isPresent() ? TIME_COLUMN.getName().get().renderWidth : 0);
        protected boolean showFileSize;
        protected boolean showFileMTime;

        @Override
        public void onListContentsRefreshed(DataListWidget<DirectoryEntry> dataListWidget, int entryWidgetWidth)
        {
            BaseFileBrowserWidget fileBrowserWidget = (BaseFileBrowserWidget) dataListWidget; 
            this.showFileSize = fileBrowserWidget.getShowFileSize();
            this.showFileMTime = fileBrowserWidget.getShowFileModificationTime();

            final int padding = 6;
            final int mTimeLen = Math.max(this.maxTimeColumnLength, this.timeTitleWidth) + padding;
            final int sizeMaxWidth = this.maxSizeColumnLength + padding;

            int relativeRight = entryWidgetWidth - 2;

            if (this.showFileMTime)
            {
                TIME_COLUMN.setWidth(mTimeLen);
                TIME_COLUMN.setMaxContentWidth(mTimeLen - padding);
                TIME_COLUMN.setRelativeStartX(relativeRight - mTimeLen);
                relativeRight -= mTimeLen + 2;
            }

            if (this.showFileSize)
            {
                SIZE_COLUMN.setWidth(sizeMaxWidth);
                SIZE_COLUMN.setMaxContentWidth(sizeMaxWidth - padding);
                SIZE_COLUMN.setRelativeStartX(relativeRight - sizeMaxWidth);
                relativeRight -= sizeMaxWidth + 2;
            }

            NAME_COLUMN.setWidth(relativeRight);
            NAME_COLUMN.setMaxContentWidth(relativeRight - padding);
            NAME_COLUMN.setRelativeStartX(0);
        }

        @Override
        public void applyToEntryWidgets(DataListWidget<DirectoryEntry> dataListWidget)
        {
            int timeColumnRight = TIME_COLUMN.getRelativeRight() - 3;
            int sizeColumnRight = SIZE_COLUMN.getRelativeRight() - 3;

            for (BaseListEntryWidget w : dataListWidget.getEntryWidgetList())
            {
                DirectoryEntryWidget widget = (DirectoryEntryWidget) w;

                widget.showSize = this.showFileSize && widget.data.getFullPath().isFile();
                widget.showMTime = this.showFileMTime;
                widget.mTimeColumnEndX = timeColumnRight;
                widget.sizeColumnEndX = sizeColumnRight;
            }
        }
    }
}
