package fi.dy.masa.malilib.gui.widget.list;

import java.io.File;
import java.io.FileFilter;

public class DirectoryBrowserWidget extends BaseFileBrowserWidget
{
    public static final FileFilter EMPTY_FILTER = (file) -> false;

    public DirectoryBrowserWidget(int x, int y, int width, int height, File defaultDirectory, File rootDirectory)
    {
        super(x, y, width, height, defaultDirectory, rootDirectory, null, null);
    }

    @Override
    protected FileFilter getFileFilter()
    {
        return EMPTY_FILTER;
    }
}
