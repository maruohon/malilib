package fi.dy.masa.malilib.gui.icon;

import java.io.File;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget.DirectoryEntry;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget.DirectoryEntryType;

public interface FileBrowserIconProvider
{
    /**
     * Returns the icon to use for the given icon type
     * @return
     */
    default MultiIcon getIcon(FileBrowserIconType type)
    {
        switch (type)
        {
            case ROOT:                      return DefaultIcons.FILE_BROWSER_DIR_ROOT;
            case UP:                        return DefaultIcons.FILE_BROWSER_DIR_UP;
            case CREATE_DIR:                return DefaultIcons.FILE_BROWSER_CREATE_DIR;
            case SEARCH:                    return DefaultIcons.SEARCH;
            case DIRECTORY:                 return DefaultIcons.FILE_BROWSER_DIR;
            case NAVBAR_ROOT_PATH_CLOSED:   return DefaultIcons.MEDIUM_ARROW_LEFT;
            case NAVBAR_SUBDIRS_CLOSED:     return DefaultIcons.SMALL_ARROW_RIGHT;
            case NAVBAR_ROOT_PATH_OPEN:
            case NAVBAR_SUBDIRS_OPEN:       return DefaultIcons.SMALL_ARROW_DOWN;
        }

        return DefaultIcons.EMPTY;
    }

    /**
     * Returns the icon that should be used for the given file, if any
     * @param file
     * @return the icon that should be used for the given file, or null if it shouldn't have an icon
     */
    @Nullable
    default MultiIcon getIconForFile(File file)
    {
        return null;
    }

    /**
     * Returns the icon that should be used for the given directory entry.
     * Usually this would just call either {@link #getIcon(FileBrowserIconType)} with
     * the <b>{@code FileBrowserIconType.DIRECTORY}</b> argument if the entry is a directory,
     * or {@link #getIconForFile(File)} if the entry is a file.
     * @param entry
     * @return
     */
    @Nullable
    default MultiIcon getIconForEntry(DirectoryEntry entry)
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
        MultiIcon icon = this.getIconForEntry(entry);

        if (icon != null)
        {
            return icon.getWidth();
        }

        return 0;
    }

    enum FileBrowserIconType
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
