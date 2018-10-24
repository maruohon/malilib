package fi.dy.masa.malilib.gui.interfaces;

import java.io.File;
import javax.annotation.Nullable;

public interface IFileBrowserIconProvider
{
    IGuiIcon getIconRoot();

    IGuiIcon getIconUp();

    IGuiIcon getIconDirectory();

    @Nullable
    IGuiIcon getIconForFile(File file);
}
