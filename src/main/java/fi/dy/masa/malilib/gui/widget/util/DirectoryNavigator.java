package fi.dy.masa.malilib.gui.widget.util;

import java.io.File;

public interface DirectoryNavigator
{
    File getCurrentDirectory();

    void switchToDirectory(File dir);

    void switchToParentDirectory();

    void switchToRootDirectory();
}
