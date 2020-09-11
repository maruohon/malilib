package fi.dy.masa.malilib.gui.widget.list.entry;

import java.io.File;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.widget.util.DirectoryNavigator;
import fi.dy.masa.malilib.gui.icon.FileBrowserIconProvider;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget.DirectoryEntry;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget.DirectoryEntryType;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.FileUtils;

public class DirectoryEntryWidget extends BaseDataListEntryWidget<DirectoryEntry>
{
    protected final DirectoryNavigator navigator;
    protected final DirectoryEntry entry;
    @Nullable protected final FileBrowserIconProvider iconProvider;

    public DirectoryEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex, DirectoryEntry entry,
                                DirectoryNavigator navigator, @Nullable FileBrowserIconProvider iconProvider)
    {
        super(x, y, width, height, listIndex, originalListIndex, entry);

        this.entry = entry;
        this.navigator = navigator;
        this.iconProvider = iconProvider;
    }

    public DirectoryEntry getDirectoryEntry()
    {
        return this.entry;
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.entry.getType() == DirectoryEntryType.DIRECTORY)
        {
            this.navigator.switchToDirectory(new File(this.entry.getDirectory(), this.entry.getName()));
        }
        else
        {
            return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
        }

        return true;
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId, boolean selected)
    {
        @Nullable Icon icon = this.iconProvider != null ? this.iconProvider.getIconForEntry(this.entry) : null;
        int xOffset = 0;
        int width = this.getWidth();
        int height = this.getHeight();

        // Draw a lighter background for the hovered and the selected entry
        if (selected)
        {
            RenderUtils.renderRectangle(x, y, width, height, 0x70FFFFFF, z);
        }
        else if (isActiveGui && this.getId() == hoveredWidgetId)
        {
            RenderUtils.renderRectangle(x, y, width, height, 0x60FFFFFF, z);
        }
        else if (this.isOdd)
        {
            RenderUtils.renderRectangle(x, y, width, height, 0x20FFFFFF, z);
        }
        // Draw a slightly lighter background for even entries
        else
        {
            RenderUtils.renderRectangle(x, y, width, height, 0x38FFFFFF, z);
        }

        // Draw an outline if this is the currently selected entry
        if (selected)
        {
            RenderUtils.renderOutline(x, y, width, height, 1, 0xEEEEEEEE, z);
        }

        if (icon != null)
        {
            xOffset += this.iconProvider.getEntryIconWidth(this.entry) + 2;
            icon.renderAt(x, y + (height - icon.getHeight()) / 2, z + 0.1f, false, false);
        }

        int yOffset = (height - this.fontHeight) / 2 + 1;
        this.drawString(x + xOffset + 2, y + yOffset, z, 0xFFFFFFFF, this.getDisplayName());

        super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hoveredWidgetId, selected);
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
