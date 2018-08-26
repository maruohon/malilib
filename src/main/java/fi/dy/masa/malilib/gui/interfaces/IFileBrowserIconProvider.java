package fi.dy.masa.malilib.gui.interfaces;

import java.io.File;

public interface IFileBrowserIconProvider
{
    IGuiIcon getIconRoot();

    IGuiIcon getIconUp();

    IGuiIcon getIconDirectory();
    
    IGuiIcon getIconForFile(File file);
}
