package fi.dy.masa.malilib.gui.widget.list.entry;

import java.io.File;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.FileBrowserIconProvider;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget.DirectoryEntry;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget.DirectoryEntryType;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.util.DirectoryNavigator;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.FileUtils;

public class DirectoryEntryWidget extends BaseDataListEntryWidget<DirectoryEntry>
{
    protected final DirectoryNavigator navigator;
    protected final DirectoryEntry entry;
    @Nullable protected final FileBrowserIconProvider iconProvider;

    public DirectoryEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                DirectoryEntry entry, DataListWidget<DirectoryEntry> listWidget,
                                DirectoryNavigator navigator, @Nullable FileBrowserIconProvider iconProvider)
    {
        super(x, y, width, height, listIndex, originalListIndex, entry, listWidget);

        this.entry = entry;
        this.navigator = navigator;
        this.iconProvider = iconProvider;
        this.textShadow = false;

        this.setText(StyledTextLine.raw(this.getDisplayName()));
        this.setNormalBackgroundColor(this.isOdd ? 0xFF202020 : 0xFF303030);
        this.setHoveredBackgroundColor(0xFF404040);
        this.setRenderNormalBackground(true);
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
        int height = this.getHeight();

        this.textOffsetX = 2;

        if (icon != null)
        {
            this.textOffsetX += this.iconProvider.getEntryIconWidth(this.entry) + 2;
            icon.renderAt(x, y + (height - icon.getHeight()) / 2, z + 0.1f, false, false);
        }

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
