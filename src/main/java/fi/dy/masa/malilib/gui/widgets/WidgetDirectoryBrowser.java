package fi.dy.masa.malilib.gui.widgets;

import java.io.File;
import java.io.FileFilter;

public class WidgetDirectoryBrowser extends WidgetFileBrowserBase
{
    public static final FileFilter EMPTY_FILTER = (file) -> false;

    public WidgetDirectoryBrowser(int x, int y, int width, int height, File defaultDirectory, File rootDirectory)
    {
        super(x, y, width, height, defaultDirectory, rootDirectory, null, null, null);
    }

    @Override
    protected FileFilter getFileFilter()
    {
        return EMPTY_FILTER;
    }
}
