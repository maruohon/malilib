package fi.dy.masa.malilib.gui.widget.list.entry;

import java.io.File;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.FileBrowserIconProvider;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.widget.ScreenContext;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget.DirectoryEntry;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget.DirectoryEntryType;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.util.DirectoryNavigator;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.FileUtils;

public class DirectoryEntryWidget extends BaseDataListEntryWidget<DirectoryEntry>
{
    protected final DirectoryNavigator navigator;
    protected final DirectoryEntry entry;
    protected final StyledTextLine displayText;
    @Nullable protected final FileBrowserIconProvider iconProvider;

    public DirectoryEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                DirectoryEntry entry, DataListWidget<DirectoryEntry> listWidget,
                                DirectoryNavigator navigator, @Nullable FileBrowserIconProvider iconProvider)
    {
        super(x, y, width, height, listIndex, originalListIndex, entry, listWidget);

        this.entry = entry;
        this.navigator = navigator;
        this.iconProvider = iconProvider;
        this.displayText = StyledTextLine.raw(this.getDisplayName());
    }

    public DirectoryEntry getDirectoryEntry()
    {
        return this.entry;
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.entry.getType() == DirectoryEntryType.DIRECTORY)
        {
            this.navigator.switchToDirectory(new File(this.entry.getDirectory(), this.entry.getName()));
        }
        else
        {
            return super.onMouseClicked(mouseX, mouseY, mouseButton);
        }

        return true;
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        @Nullable MultiIcon icon = this.iconProvider != null ? this.iconProvider.getIconForEntry(this.entry) : null;
        int xOffset = 0;
        int width = this.getWidth();
        int height = this.getHeight();
        boolean selected = this.isSelected();

        // Draw a lighter background for the hovered and the selected entry
        if (selected)
        {
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, 0x70FFFFFF);
        }
        else if (ctx.isActiveScreen && this.getId() == ctx.hoveredWidgetId)
        {
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, 0x60FFFFFF);
        }
        else if (this.isOdd)
        {
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, 0x20FFFFFF);
        }
        // Draw a slightly lighter background for even entries
        else
        {
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, 0x38FFFFFF);
        }

        // Draw an outline if this is the currently selected entry
        if (selected)
        {
            ShapeRenderUtils.renderOutline(x, y, z, width, height, 1, 0xEEEEEEEE);
        }

        if (icon != null)
        {
            xOffset += this.iconProvider.getEntryIconWidth(this.entry) + 2;
            icon.renderAt(x, y + (height - icon.getHeight()) / 2, z + 0.1f, false, false);
        }

        int yOffset = (height - this.fontHeight) / 2 + 1;
        this.renderTextLine(x + xOffset + 2, y + yOffset, z, 0xFFFFFFFF, false, ctx, this.displayText);

        super.renderAt(x, y, z, ctx);
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
}
