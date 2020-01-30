package fi.dy.masa.malilib.gui.interfaces;

import java.io.File;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.util.GuiIconBase;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase.DirectoryEntry;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase.DirectoryEntryType;

public interface IFileBrowserIconProvider
{
    /**
     * Returns the icon to use for the given icon type
     * @return
     */
    default IGuiIcon getIcon(FileBrowserIconType type)
    {
        switch (type)
        {
            case ROOT:                      return GuiIconBase.FILE_BROWSER_DIR_ROOT;
            case UP:                        return GuiIconBase.FILE_BROWSER_DIR_UP;
            case CREATE_DIR:                return GuiIconBase.FILE_BROWSER_CREATE_DIR;
            case SEARCH:                    return GuiIconBase.SEARCH;
            case DIRECTORY:                 return GuiIconBase.FILE_BROWSER_DIR;
            case NAVBAR_ROOT_PATH_CLOSED:   return GuiIconBase.MEDIUM_ARROW_LEFT;
            case NAVBAR_ROOT_PATH_OPEN:     return GuiIconBase.SMALL_ARROW_DOWN;
            case NAVBAR_SUBDIRS_CLOSED:     return GuiIconBase.SMALL_ARROW_RIGHT;
            case NAVBAR_SUBDIRS_OPEN:       return GuiIconBase.SMALL_ARROW_DOWN;
        }

        return GuiIconBase.EMPTY;
    }

    /**
     * Returns the icon that should be used for the given file, if any
     * @param file
     * @return the icon that should be used for the given file, or null if it shouldn't have an icon
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
            return this.getIcon(FileBrowserIconType.DIRECTORY);
        }
        else
        {
            return this.getIconForFile(entry.getFullPath());
        }
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

    public enum FileBrowserIconType
    {
        ROOT,
        UP,
        CREATE_DIR,
        SEARCH,
        DIRECTORY,
        NAVBAR_ROOT_PATH_CLOSED,
        NAVBAR_ROOT_PATH_OPEN,
        NAVBAR_SUBDIRS_CLOSED,
        NAVBAR_SUBDIRS_OPEN;
    }
}
