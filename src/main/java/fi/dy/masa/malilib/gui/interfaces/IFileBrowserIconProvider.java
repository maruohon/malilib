package fi.dy.masa.malilib.gui.interfaces;

import java.io.File;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.util.BaseGuiIcon;
import fi.dy.masa.malilib.gui.widget.WidgetFileBrowserBase.DirectoryEntry;
import fi.dy.masa.malilib.gui.widget.WidgetFileBrowserBase.DirectoryEntryType;

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
            case ROOT:                      return BaseGuiIcon.FILE_BROWSER_DIR_ROOT;
            case UP:                        return BaseGuiIcon.FILE_BROWSER_DIR_UP;
            case CREATE_DIR:                return BaseGuiIcon.FILE_BROWSER_CREATE_DIR;
            case SEARCH:                    return BaseGuiIcon.SEARCH;
            case DIRECTORY:                 return BaseGuiIcon.FILE_BROWSER_DIR;
            case NAVBAR_ROOT_PATH_CLOSED:   return BaseGuiIcon.MEDIUM_ARROW_LEFT;
            case NAVBAR_SUBDIRS_CLOSED:     return BaseGuiIcon.SMALL_ARROW_RIGHT;
            case NAVBAR_ROOT_PATH_OPEN:
            case NAVBAR_SUBDIRS_OPEN:       return BaseGuiIcon.SMALL_ARROW_DOWN;
        }

        return BaseGuiIcon.EMPTY;
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
     * Usually this would just call either {@link #getIcon(FileBrowserIconType)} with
     * the <b>{@code FileBrowserIconType.DIRECTORY}</b> argument if the entry is a directory,
     * or {@link #getIconForFile(File)} if the entry is a file.
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
