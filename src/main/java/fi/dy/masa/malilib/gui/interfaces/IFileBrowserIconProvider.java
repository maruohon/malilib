package fi.dy.masa.malilib.gui.interfaces;

import java.io.File;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.util.GuiIconBase;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase.DirectoryEntry;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase.DirectoryEntryType;

public interface IFileBrowserIconProvider
{
    /**
     * Returns the icon for the "go to root directory" button
     * @return
     */
    default IGuiIcon getIconRoot()
    {
        return GuiIconBase.FILE_BROWSER_DIR_ROOT;
    }

    /**
     * Returns the icon for the "go up one directory" button
     * @return
     */
    default IGuiIcon getIconUp()
    {
        return GuiIconBase.FILE_BROWSER_DIR_UP;
    }

    /**
     * Returns the icon for the "create a new directory" button
     * @return
     */
    default IGuiIcon getIconCreateDirectory()
    {
        return GuiIconBase.FILE_BROWSER_CREATE_DIR;
    }

    /**
     * Returns the icon for the "show/hide the search bar" button
     * @return
     */
    default IGuiIcon getIconSearch()
    {
        return GuiIconBase.SEARCH;
    }

    /**
     * Returns the expected width of the icons, for proper text alignment
     * @param entry
     * @return
     */
    default int getEntryIconWidth(DirectoryEntry entry)
    {
        IGuiIcon icon = this.getIconForEntry(entry);

        if (icon != null)
        {
            return icon.getWidth();
        }

        return 0;
    }

    /**
     * Returns the icon for a directory
     * @return
     */
    default IGuiIcon getIconDirectory()
    {
        return GuiIconBase.FILE_BROWSER_DIR;
    }

    /**
     * Returns the icon that should be used for the given file, if any
     * @param file
     * @return
     */
    @Nullable
    default IGuiIcon getIconForFile(File file)
    {
        return null;
    }

    /**
     * Returns the icon that should be used for the given directory entry.
     * Usually this would just call either {@link getIconDirectory()} or {@link getIconForFile(File file)}
     * based on whether the entry is a directory or a file.
     * @param entry
     * @return
     */
    @Nullable
    default IGuiIcon getIconForEntry(DirectoryEntry entry)
    {
        if (entry.getType() == DirectoryEntryType.DIRECTORY)
        {
            return this.getIconDirectory();
        }
        else
        {
            return this.getIconForFile(entry.getFullPath());
        }
    }
}
